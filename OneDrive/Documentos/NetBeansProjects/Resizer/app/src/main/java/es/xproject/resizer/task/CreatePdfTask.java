/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.xproject.resizer.task;

import static es.xproject.resizer.App.rb;
import es.xproject.resizer.base.Constants;
import es.xproject.resizer.igu.Ventana;
import static es.xproject.resizer.igu.Ventana.destinationFile;
import static es.xproject.resizer.igu.Ventana.mask;
import static es.xproject.resizer.igu.Ventana.recursivePdf;
import es.xproject.resizer.resize.CreatePdf;
import es.xproject.resizer.util.FileList;
import java.awt.List;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingWorker;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author jsolis
 */
public class CreatePdfTask extends BaseTask {

    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger();

    public CreatePdfTask(Ventana ventana) {
        super(ventana);
    }

    @Override
    public void execute() {

        SwingWorker sw1;
        sw1 = new SwingWorker() {
            // Method
            @Override
            protected String doInBackground()
                    throws Exception {
                log.debug("startPdfCreationsThread " + destinationFile.getName());

                Ventana.pdfCount.set(0);
                Ventana.skipPdfCount.set(0);
                
                traceHeap();

                lockButtons();

                if (ventana.executorService == null || ventana.executorService.getPoolSize() != ventana.pdfPoolSize) {
                    ventana.executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(ventana.pdfPoolSize);
                }

                log.debug("recursive " + recursivePdf);
                FileList destinationList = new FileList().build(destinationFile, recursivePdf, true);

                ArrayList<File> dirs = new ArrayList();

                for (File dir : destinationList.dirs) {
                    if (dir.getAbsolutePath().contains(Constants.JPGMIN_DIR)) {
                        log.warn("dir must be ignored " + dir.getAbsolutePath());
                        continue;
                    }
                    FileList dirList = new FileList().build(dir, recursivePdf, false);
                    if (dirList.hasFiles()) {
                        dirs.add(dir);
                        log.debug("add dir " + dir.getAbsolutePath());
                    }
                }

                
                Ventana.traceProcessed(rb.getString("initProcess.1") + " " + ventana.pdfPoolSize 
                        + " " + rb.getString("initProcess.1") + " " + dirs.size() + " " +rb.getString("initProcess.3"));

                mask.showMask(dirs.size());

                log.debug("directories " + dirs);
                int counter = 0;
                for (File dir : dirs) {

                    if (dir.getAbsolutePath().contains(Constants.JPGMIN_DIR)) {
                        log.warn("dir must be ignored " + dir.getAbsolutePath());
                        continue;
                    }

                    FileList dirList = new FileList().build(dir, false);

                    log.debug("dir........ " + dir.getName());
                    log.debug("files...... " + dirList.files.size());

                    if (dirList.hasFiles()) {
                        log.debug("executeCreatePdf in dir " + dir.getAbsolutePath());
                        File destination;

                        if (dir.getAbsolutePath().contains(Constants.JPG_DIR)) {
                            destination = new File(dir.getAbsolutePath().replaceFirst(Constants.JPG_DIR, Constants.PDF_DIR));
                        } else {
                            destination = new File(dir.getAbsolutePath());
                        }

                        if (!destination.exists()) {
                            destination.mkdirs();
                            Ventana.traceProcessed("Creating dir: " + destination.getParentFile().getAbsolutePath());
                        }

                        log.debug("destination file " + destination.getAbsolutePath());
                        ventana.executorService.submit(new CreatePdf(dir, destination));
                        counter++;
                    } else {
                        log.warn("no files in " + dir.getName() + " skip");
                    }
                }
                log.debug(counter + " procesos en cola ");

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
                    log.debug("shutdownNow. ");
                    ventana.executorService.shutdownNow();
                    Thread.currentThread().interrupt();
                }

                String res = "Finished Execution";

                traceHeap();
                System.gc();

                return res;
            }

            // Method.e¡
            protected void process(List chunks) {

            }

            // Method
            @Override
            protected void done() {
                // this method is called when the background
                // thread finishes execution
                mask.hideMask();
                ventana.showFinalDialog();
                unlockButtons();
            }

        };

        // Executes the swingworker on worker thread
        sw1.execute();

    }

}
