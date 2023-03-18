/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.xproject.resizer.task;

import es.xproject.resizer.Ventana;
import static es.xproject.resizer.Ventana.destinationFile;
import static es.xproject.resizer.Ventana.imagesDir;
import static es.xproject.resizer.Ventana.newline;
import static es.xproject.resizer.Ventana.pdfCheck;
import static es.xproject.resizer.Ventana.processed;
import static es.xproject.resizer.Ventana.sourceFile;
import es.xproject.resizer.util.FileList;
import java.awt.List;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author jsolis
 */
public class MoveTask extends BaseTask {

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

                processed.setText("Copiando imágenes de " + imagesDir.getAbsolutePath() + "." + newline);
                processed.setText("Destino " + destinationFile.getAbsolutePath() + "." + newline);
                processed.setCaretPosition(processed.getDocument().getLength());

                lockButtons();

                if (ventana.executorService == null || ventana.executorService.getPoolSize() != ventana.poolSize) {
                    ventana.executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(ventana.poolSize);
                }

                // ficheros origen 
                for (File dir : Ventana.fileList.dirs) {

                    FileList files = new FileList().build(dir, false);
                    if (files.hasFiles()) {
                        move(files);
                    }
                }

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
                
                ventana.pdfButton();
            }

            private void move(FileList fileList) {
                for (File file : fileList.files) {
                    String filePath = file.getParent().replace(sourceFile.getPath(), "");
                    File dir = new File(destinationFile.getPath() + File.separator + filePath);
                    if (!dir.exists()) {
                        dir.mkdirs();
                        processed.append("Creating dir: " + dir.getAbsolutePath() + newline);
                        processed.setCaretPosition(processed.getDocument().getLength());
                    }
                }
                int counter = 0;

                for (File file : fileList.files) {
                    String filePath = file.getParent().replace(sourceFile.getPath(), "");
                    File destinationDir = new File(destinationFile.getPath() + File.separator + filePath);
                    System.out.println("mv " + sourceFile.getAbsolutePath() + " to " + destinationDir.getAbsolutePath());
                    ventana.executorService.submit(new MoveFile(file, destinationDir));
                    counter++;
                }
                System.out.println(counter + " procesos en cola. ");
                // wait for all of the executor threads to finish
                ventana.executorService.shutdown();
                try {
                    while (!ventana.executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                        Logger.getLogger(Ventana.class.getName()).log(Level.INFO, "Procesos aún en curso...");
                    }
                } catch (InterruptedException ex) {
                    ventana.executorService.shutdownNow();
                    Thread.currentThread().interrupt();
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
                    processed.append("Fichero no encontrado " + fileName + newline);
                } else if (match.length > 1) {
                    processed.append("Más de un fichero " + fileName + newline);
                } else {

                    String destinationName = destinationDir.getAbsolutePath() + File.separator + fileName + ".jpg";
                    Path destinationPath = Paths.get(destinationName);

                    System.out.append("mv " + match[0].getAbsolutePath() + " " + destinationName + newline);

                    Files.move(Paths.get(match[0].getAbsolutePath()), destinationPath, REPLACE_EXISTING);

                    processed.append("Fichero movido " + sourceFile.getName() + newline);

                }
                processed.setCaretPosition(processed.getDocument().getLength());

            } catch (IOException ex) {
                processed.append("Error moviendo: " + sourceFile.getName() + " " + ex + newline);
                processed.setCaretPosition(processed.getDocument().getLength());
            }
        }
    }

}
