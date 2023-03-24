/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.xproject.resizer.task;

import es.xproject.resizer.igu.Ventana;
import es.xproject.resizer.util.FileList;
import java.awt.List;
import java.io.File;
import javax.swing.SwingWorker;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author jsolis
 */
public class CleanTask extends BaseTask {

    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger();

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

                log.debug("clean");

                FileList cleanList = new FileList().build(Ventana.destinationFile, true);

                for (File imageFile : cleanList.files) {
                    imageFile.delete();
                    log.debug("delete " + imageFile);

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
