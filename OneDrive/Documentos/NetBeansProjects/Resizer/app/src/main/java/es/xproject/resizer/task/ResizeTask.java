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
import static es.xproject.resizer.igu.Ventana.pdfCheck;
import static es.xproject.resizer.igu.Ventana.processed;
import static es.xproject.resizer.igu.Ventana.sourceFile;
import es.xproject.resizer.resize.Resize;
import java.awt.List;
import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingWorker;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author jsolis
 */
public class ResizeTask extends BaseTask {

    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger();
    // private ArrayList<Future<String>> futureList;

    public ResizeTask(Ventana ventana) {
        super(ventana);
    }

    @Override
    public void execute() {

        Ventana.imageCount.set(0);
        Ventana.skipCount.set(0);
        Ventana.errorCount.set(0);

        processed.setText("");
        Ventana.traceProcessed("Destino: " + destinationFile.getAbsolutePath() + ".");

        lockButtons();

        SwingWorker sw1;
        sw1 = new SwingWorker() {
            // Method
            @Override
            protected String doInBackground()
                    throws Exception {

                traceHeap();

                Ventana.traceProcessed(rb.getString("initProcess.1") + " " + ventana.poolSize + " " + rb.getString("initProcess.4"));
              
                if (ventana.executorService == null || ventana.executorService.getPoolSize() != ventana.poolSize) {
                    ventana.executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(ventana.poolSize);
                }

                mask.showMask(Ventana.fileList.files.size());

                for (File file : Ventana.fileList.files) {
                    String filePath = file.getParent().replace(sourceFile.getPath(), "");
                    
                    createDir(filePath, Constants.JPG_DIR);
                    
                    if (Ventana.dimMin > 0)
                        createDir(filePath, Constants.JPGMIN_DIR);
                    
                    if (Ventana.pdfCheck)
                        createDir(filePath, Constants.PDF_DIR);
                }
                int counter = 0;

                //futureList = new ArrayList();
                for (File file : Ventana.fileList.files) {
                    log.debug(file.getAbsolutePath());
                    //    futureList.add((Future<String>) ventana.executorService.submit(new Resize(file)));
                    ventana.executorService.submit(new Resize(file));
                    counter++;
                }
                log.debug(counter + " queued process. ");
                traceHeap();
                // wait for all of the executor threads to finish
                ventana.executorService.shutdown();
                try {
                    log.debug("init await loop. ");
                    while (!ventana.executorService.awaitTermination(15, TimeUnit.SECONDS)) {
                        log.debug("waiting for termination loop...");
                        traceHeap();
                    }
                    log.debug("end await loop. ");
                } catch (InterruptedException ex) {
                    log.debug("shutdownNow threads");
                    ventana.executorService.shutdownNow();
                    Thread.currentThread().interrupt();
                }

                String res = "Finished Execution";

                log.debug("Finished Execution");

                traceHeap();

                System.gc();

                traceHeap();

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

                if (pdfCheck) {
                    ventana.startPdfCreationsThread();
                } else {
                    ventana.showFinalDialog();

                    unlockButtons();
                }
            }

        

            private void createDir(String filePath, String parent) {
                File dir = new File(destinationFile.getPath() + File.separator + parent + File.separator + filePath);
                if (!dir.exists()) {
                    dir.mkdirs();
                    Ventana.traceProcessed("Creating dir: " + dir.getAbsolutePath());
                }
            }

        };

        // Executes the swingworker on worker thread
        sw1.execute();
    }
}
