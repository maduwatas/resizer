/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.xproject.resizer.task;

import es.xproject.resizer.Ventana;
import es.xproject.resizer.util.FileList;
import java.awt.List;
import java.io.File;
import javax.swing.SwingWorker;

/**
 *
 * @author jsolis
 */
public class CleanTask extends BaseTask {

    public CleanTask(Ventana ventana) {
        super(ventana);
    }

    @Override
    public void execute() {
        SwingWorker sw1 = new SwingWorker() {
            // Method
            @Override
            protected String doInBackground()
                    throws Exception {

                System.out.println("clean");

                FileList cleanList = new FileList().build(Ventana.destinationFile, true);

                for (File imageFile : cleanList.files) {
                    imageFile.delete();
                    System.out.println("delete " + imageFile);

                }

                return null;

            }

            // Method.e¡
            protected void process(List chunks) {

            }

            // Method
            @Override
            protected void done() {
            }

        };

        // Executes the swingworker on worker thread
        sw1.execute();
    }

}
