/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.xproject.resizer.task;

import es.xproject.resizer.App;
import es.xproject.resizer.base.Constants;
import java.awt.List;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author jsolis
 */
public class LogFileTask {

    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger();

    private final JTextArea logText;
    private final String level;

    public LogFileTask(String level, JTextArea text) {
        this.logText = text;
        this.level = level;
        logText.append(App.rb.getString("loading") + "\n");
    }

    public void execute() {
        SwingWorker sw1 = new SwingWorker() {
            // Method
            @Override
            protected String doInBackground()
                    throws Exception {

                log.debug("LogFileTask");

                try {
                    File logFile = new File(new File(".").getCanonicalPath() + File.separator + "logs" + File.separator + Constants.LOG_FILE).getAbsoluteFile();

                    try ( // ESTO TIENE QUE SER ASÍNCRONO, TASK
                            Scanner myReader = new Scanner(logFile)) {
                        logText.setText("");
                        while (myReader.hasNextLine()) {
                            String data = myReader.nextLine();
                            if (data.contains(level)) {
                                logText.append(data + "\n");
                            }
                        }
                        // TODO cargar datos
                    }
                } catch (IOException ex) {
                    log.error("error reading log file");
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
