/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.xproject.resizer.task;

import es.xproject.resizer.App;
import static es.xproject.resizer.App.rb;
import es.xproject.resizer.base.Constants;
import es.xproject.resizer.igu.Ventana;
import static es.xproject.resizer.igu.Ventana.destinationFile;
import static es.xproject.resizer.igu.Ventana.imagesDir;
import static es.xproject.resizer.igu.Ventana.pdfCheck;
import static es.xproject.resizer.igu.Ventana.processed;
import static es.xproject.resizer.igu.Ventana.sourceFile;
import es.xproject.resizer.util.FileList;
import java.awt.List;
import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingWorker;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author jsolis
 */
public class MoveTask extends BaseTask {

    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger();

    private int counter;

    public MoveTask(Ventana ventana) {
        super(ventana);
    }

    @Override
    public void execute() {
        SwingWorker sw1 = new SwingWorker() {
            // Method
            @Override
            protected String doInBackground()
                    throws Exception {

                traceHeap();

                counter = 0;

                if (ventana.executorService == null || ventana.executorService.getPoolSize() != ventana.pdfPoolSize) {
                    ventana.executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(ventana.pdfPoolSize);
                }

                Ventana.imageCount.set(0);
                Ventana.skipCount.set(0);
                Ventana.errorCount.set(0);
                processed.setText("");

                Ventana.traceProcessed(rb.getString("mvImage") + " " + imagesDir.getAbsolutePath() + ".");
                Ventana.traceProcessed(rb.getString("destination") + " " + destinationFile.getAbsolutePath() + ".");

                lockButtons();

                if (ventana.executorService == null || ventana.executorService.getPoolSize() != ventana.poolSize) {
                    ventana.executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(ventana.poolSize);
                }

                // ficheros origen 
                for (File dir : Ventana.fileList.dirs) {
                    Ventana.traceProcessed(rb.getString("srcDir") + ": " + dir.getAbsolutePath() + ".");

                    try {
                        log.debug("src dir: " + dir.getAbsolutePath());
                        FileList files = new FileList().build(dir, false);
                        if (files.hasFiles()) {
                            log.debug("src dir has " + files.files.size() + " files");
                            move(files);
                        } else {
                            log.warn("src dir is empty");
                        }
                    } catch (Throwable e) {
                        log.error("error dir " + dir.getAbsolutePath() + ": " + e);
                    }
                }

                log.debug(counter + " queued process. ");
                // wait for all of the executor threads to finish
                ventana.executorService.shutdown();
                try {
                    log.debug("init await loop. ");
                    while (!ventana.executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                        log.info("Still processing...");
                        traceHeap();
                    }
                    log.debug("end await loop. ");
                } catch (InterruptedException ex) {
                    log.debug("shutdownNow.");
                    ventana.executorService.shutdownNow();
                    Thread.currentThread().interrupt();
                }

                traceHeap();
                System.gc();
                return null;

            }

            // Method.e¡
            protected void process(List chunks) {

            }

            // Method
            @Override
            protected void done() {

                if (pdfCheck) {
                    ventana.startPdfCreationsThread();
                } else {
                    ventana.showFinalDialog();
                    unlockButtons();
                }

            }

            private void move(FileList fileList) {
                for (File file : fileList.files) {
                    String filePath = file.getParent().replace(sourceFile.getPath(), "");
                    File dir = new File(destinationFile.getPath() + File.separator + Constants.JPG_DIR + File.separator + filePath);
                    if (!dir.exists()) {
                        dir.mkdirs();
                        Ventana.traceProcessed("Creating dir: " + dir.getAbsolutePath());
                    }
                }

                for (File file : fileList.files) {
                    String filePath = file.getParent().replace(sourceFile.getPath(), "");
                    File destinationDir = new File(destinationFile.getPath() + File.separator + Constants.JPG_DIR + File.separator + filePath);
                    log.debug("mv dir " + sourceFile.getAbsolutePath() + " to " + destinationDir.getAbsolutePath());
                    ventana.executorService.submit(new MoveFile(file, destinationDir));
                    counter++;
                }

            }

        };

        // Executes the swingworker on worker thread
        sw1.execute();
    }

    private static class MoveFile implements Runnable {

        private final File sourceFile;
        private final File destinationDir;

        public MoveFile(File sourceFile, File destinationDir) {
            this.sourceFile = sourceFile;
            this.destinationDir = destinationDir;
        }

        @Override
        public void run() {
            try {

                String fileName = FilenameUtils.removeExtension(sourceFile.getName());

                FileFilter fileFilter = (file) -> {
                    return FilenameUtils.removeExtension(file.getName()).equals(fileName);
                };
                File[] match = imagesDir.listFiles(fileFilter);

                if (match.length == 0) {
                    Ventana.errorCount.getAndIncrement();
                    log.warn("File not found " + fileName);
                    Ventana.traceProcessed("Fichero no encontrado " + fileName);
                } else if (match.length > 1) {
                    Ventana.errorCount.getAndIncrement();
                    log.warn("More than one file " + fileName);
                    Ventana.traceProcessed("Más de un fichero " + fileName);
                } else {

                    String extension = FilenameUtils.getExtension(match[0].getName());
                    
                    String destinationName = destinationDir.getAbsolutePath() + File.separator + fileName + "." + extension;
                    Path destinationPath = Paths.get(destinationName);

                    log.debug("mv " + match[0].getAbsolutePath() + " " + destinationName);

                    if (Ventana.replaceStrategy.isRemain()) {
                        File outputFile = destinationPath.toFile();
                        if (outputFile.exists()) {
                            Ventana.skipCount.getAndIncrement();
                            Ventana.traceProcessed(Ventana.replaceStrategy + ": " + sourceFile.getName());
                            return;
                        }
                    }
                    Files.move(Paths.get(match[0].getAbsolutePath()), destinationPath, REPLACE_EXISTING);
                    Ventana.imageCount.getAndIncrement();
                    Ventana.traceProcessed(App.rb.getString("fileMoved") + " " + sourceFile.getName());

                }

                // debo controlar el outOfMemmory
            } catch (Throwable ex) {
                log.error(App.rb.getString("errorMoving") + ": " + sourceFile.getName() + " " + ex);
                Ventana.errorCount.getAndIncrement();
                Ventana.traceProcessed(": " + sourceFile.getName() + " " + ex);

            }
        }
    }

}
