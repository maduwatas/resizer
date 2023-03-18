/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.xproject.resizer.task;

import es.xproject.resizer.Ventana;
import static es.xproject.resizer.Ventana.destinationFile;
import static es.xproject.resizer.Ventana.mask;
import static es.xproject.resizer.Ventana.recursivePdf;
import es.xproject.resizer.resize.CreatePdf;
import es.xproject.resizer.util.FileList;
import java.awt.List;
import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;

/**
 *
 * @author jsolis
 */
public class CreatePdfTask extends BaseTask {

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
                System.out.println("startPdfCreationsThread " + destinationFile.getName());

                lockButtons();
                

                if (ventana.executorService == null || ventana.executorService.getPoolSize() != ventana.pdfPoolSize) {
                    ventana.executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(ventana.pdfPoolSize);
                }
                
                
                System.out.println("recursive " + recursivePdf);
                FileList destinationList = new FileList().build(destinationFile, recursivePdf, true);
                
                Ventana.processed.append("Inicando procesos con " + ventana.poolSize + " hilos para " +destinationList.dirs.size() + " directorios"+ Ventana.newline);
                
                mask.showMask(destinationList.dirs.size());

                System.out.println("directories " + destinationList.dirs.size());
                int counter = 0;
                for (File dir : destinationList.dirs) {

                    FileList dirList = new FileList().build(dir, false);
                    
                    System.out.println("dir........ " + dir.getName());
                    System.out.println("files...... " + dirList.files.size());

                    if (dirList.hasFiles()) {
                        System.out.println("executeCreatePdf in dir " + dir.getAbsolutePath());
                        ventana.executorService.submit(new CreatePdf(dir));
                        counter++;
                    } else {
                        System.out.println("no files in " + dir.getName() + " skip");
                    }
                }
                System.out.println(counter + " procesos en cola ");

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
                ventana.showFinalDialog();
                unlockButtons();
            }

        };

        // Executes the swingworker on worker thread
        sw1.execute();

    }

}
