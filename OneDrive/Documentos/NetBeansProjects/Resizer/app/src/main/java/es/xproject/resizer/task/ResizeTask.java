/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.xproject.resizer.task;

import es.xproject.resizer.Ventana;
import static es.xproject.resizer.Ventana.destinationFile;
import static es.xproject.resizer.Ventana.errorCount;
import static es.xproject.resizer.Ventana.imageCount;
import static es.xproject.resizer.Ventana.mask;
import static es.xproject.resizer.Ventana.newline;
import static es.xproject.resizer.Ventana.pdfCheck;
import static es.xproject.resizer.Ventana.processed;
import static es.xproject.resizer.Ventana.sourceFile;
import es.xproject.resizer.resize.Resize;
import java.awt.List;
import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author jsolis
 */
public class ResizeTask extends BaseTask {

    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger();

    public ResizeTask(Ventana ventana) {
        super(ventana);
    }

    @Override
    public void execute() {

        Ventana.imageCount.set(0);
        Ventana.errorCount.set(0);

        processed.setText("Destino: " + destinationFile.getAbsolutePath() + "." + newline);
        processed.setCaretPosition(processed.getDocument().getLength());

        lockButtons();

        SwingWorker sw1 = new SwingWorker() {
            // Method
            @Override
            protected String doInBackground()
                    throws Exception {

                processed.append("Inicando procesos con " + ventana.poolSize + " hilos" + newline);

                if (ventana.executorService == null || ventana.executorService.getPoolSize() != ventana.poolSize) {
                    ventana.executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(ventana.poolSize);
                }

                mask.showMask(Ventana.fileList.files.size());

                for (File file : Ventana.fileList.files) {
                    String filePath = file.getParent().replace(sourceFile.getPath(), "");
                    File dir = new File(destinationFile.getPath() + File.separator + filePath);
                    if (!dir.exists()) {
                        dir.mkdirs();
                        processed.append("Creating dir: " + dir.getAbsolutePath() + newline);
                        processed.setCaretPosition(processed.getDocument().getLength());

                    }
                }
                int counter = 0;
                for (File file : Ventana.fileList.files) {
                    log.debug(file.getAbsolutePath());
                    ventana.executorService.submit(new Resize(file));
                    counter++;

                }
                log.debug(counter + " procesos en cola. ");
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

                String res = "Finished Execution";
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

        };

        // Executes the swingworker on worker thread
        sw1.execute();
    }
}
