/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.xproject.resizer.igu.menu;

import es.xproject.resizer.App;
import static es.xproject.resizer.App.rb;
import es.xproject.resizer.base.Constants;
import es.xproject.resizer.components.ImagePanel;
import es.xproject.resizer.igu.Ventana;
import es.xproject.resizer.igu.panels.LogPanel;
import es.xproject.resizer.task.LogFileTask;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.logging.Level;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author jsolis
 */
public class MenuVentana extends JMenuBar implements ActionListener {

    private final JMenuItem itemHelp;
    private final JMenuItem itemLogFile, warnLog, errorLog;
    private final JMenuItem itemAbout;
    private final JMenuItem itemExit;
    private final Ventana frame;
    private final JDialog modelDialog, modelLog;
    private static final Logger log = LogManager.getLogger();
    //private JCheckBox errorCheck;
    private JTextArea logText;
    private JDialog dialog;

    public MenuVentana(Ventana frame) {
        this.frame = frame;
        
        

        itemHelp = new MyMenuItem("manual");
        itemAbout = new MyMenuItem("about");
        itemLogFile = new MyMenuItem("log");
        itemExit = new MyMenuItem("exit");

        
        // File
        MyMenu menu = new MyMenu("file");
        MyMenu itemLog = new MyMenu("errorMessages");
        
        warnLog = new MyMenuItem("menu.warns");
        errorLog = new MyMenuItem("menu.errors");
        
        itemLog.add(warnLog);
        itemLog.add(errorLog);
        
        menu.add(itemLog);
        menu.add(itemLogFile);
        //   File logFile = new File(new File(".").getCanonicalPath() + File.separator + "logs" + File.separator + "codimage2.log").getAbsoluteFile();
        menu.addSeparator();
        menu.add(itemExit);
        
        this.add(menu);
                
          //Help
        add(Box.createHorizontalGlue());
        menu = new MyMenu("help");
        menu.add(itemHelp);
        menu.add(itemAbout);
        
       
        this.add(menu);
        
        setActionListeners();

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                int result = JOptionPane.showConfirmDialog(frame,
                        rb.getString("reallyClose") + " " + Constants.APPNAME + "?",
                        Constants.APPNAME,
                        JOptionPane.YES_NO_OPTION);

                if (result == JOptionPane.YES_OPTION) {
                    closeApp();
                } else if (result == JOptionPane.NO_OPTION) {
                    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                }
            }
        });

        modelDialog = createDialog(frame);
        modelLog = createLog(frame);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == itemHelp) {
            openHelp();
        } 
        else if (e.getSource() == itemLogFile) {
            openLog();
        } else if (e.getSource() == warnLog) {
            loadLogFile(Constants.LOG_WARN);
            modelLog.setVisible(true);
        }
        else if (e.getSource() == errorLog) {
            loadLogFile(Constants.LOG_ERROR);
            modelLog.setVisible(true);
        } 
        else if (e.getSource() == itemAbout) {
            modelDialog.setVisible(true);
        } else if (e.getSource() == itemExit) {

            int result = JOptionPane.showConfirmDialog(frame,
                    rb.getString("reallyClose") + " " + Constants.APPNAME + "?",
                    Constants.APPNAME,
                    JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                closeApp();
            }

        }

    }

    private void setActionListeners() {
        itemHelp.addActionListener(this);
        itemAbout.addActionListener(this);
        itemExit.addActionListener(this);
        warnLog.addActionListener(this);
        errorLog.addActionListener(this);
        itemLogFile.addActionListener(this);
    }

    private static JDialog createDialog(final JFrame frame) {

        final JDialog modelDialog = new JDialog(frame, Constants.APPNAME,
                Dialog.ModalityType.DOCUMENT_MODAL);

        modelDialog.setLayout(new BorderLayout());
        modelDialog.setBounds(132, 132, 520, 740);
        JLabel left = new JLabel("      ");
        modelDialog.add(left, BorderLayout.WEST);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        // imagen
        ImagePanel image = new ImagePanel("icon.png");

        panel.add(image);

        // about
        String msg = rb.getString("about.app");
        JLabel optionPane = new NarrowOptionPane(msg);

        panel.add(optionPane);

        msg = "<p>V" + App.version + " " + App.code + "</p>" + rb.getString("copyright");

        JLabel south = new NarrowOptionPane(msg);
        panel.add(south);

        msg = "<p>" + rb.getString("developer") + " XProject <font size=2><a href=mailto:nesitarm@gmail.com>nesitarm@gmail.com</a></font></p>";
        JLabel email = new NarrowOptionPane(msg);

        email.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        email.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    URI uri = new URI("mailto", "nesitarm@gmail.com?subject=" + Constants.APPNAME, null);
                    Desktop.getDesktop().mail(uri);
                } catch (IOException | URISyntaxException ex) {
                    log.error("error parsing email " + e);
                }
            }
        });
        panel.add(email);

        modelDialog.add(panel, BorderLayout.CENTER);

        JPanel panel1 = new JPanel();
        panel1.setLayout(new FlowLayout());
        JButton okButton = new JButton(rb.getString("close"));

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modelDialog.setVisible(false);
            }
        });

        panel1.add(okButton);
        modelDialog.add(panel1, BorderLayout.SOUTH);

        return modelDialog;
    }

    private void openHelp() {

        try {
            URL resource = getClass().getClassLoader().getResource("manual.pdf");
            File pdfFile = Paths.get(resource.toURI()).toFile();

            if (pdfFile.exists()) {

                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().open(pdfFile);
                    } catch (IOException ex) {
                        java.util.logging.Logger.getLogger(MenuVentana.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    log.error("Awt Desktop is not supported!");
                }

            } else {
                log.error("File is not exists!");
            }

            log.trace("Done");
        } catch (URISyntaxException ex) {
            log.error("URISyntaxException");

        }
    }

    private void closeApp() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        if (frame.executorService != null) {
            frame.executorService.shutdownNow();
        }
        frame.dispose();
        System.exit(0);

    }

    private JDialog createLog(Ventana frame) {

        dialog = new JDialog(frame, Constants.APPNAME,
                Dialog.ModalityType.DOCUMENT_MODAL);

        dialog.setLayout(new BorderLayout());
//        dialog.setBounds(132, 132, 1024, 768);

        dialog.setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds());

        JLabel left = new JLabel("      ");
        dialog.add(left, BorderLayout.WEST);
        logText = new JTextArea();
        logText.setEditable(true);
        LogPanel logPanel = new LogPanel(logText, rb.getString("log"));
        dialog.add(logPanel, BorderLayout.CENTER);

        JPanel panel1 = new JPanel();
        panel1.setLayout(new FlowLayout());
        JButton okButton = new JButton(rb.getString("close"));

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logText.setText("");
                dialog.setVisible(false);
            }
        });

        panel1.add(okButton);
        dialog.add(panel1, BorderLayout.SOUTH);

        return dialog;
    }

    private void loadLogFile(String level) {
        LogFileTask logFileTask = new LogFileTask(level, logText);
        logFileTask.execute();
    }

    private void openLog() {
        try {
            File logFile = new File(new File(".").getCanonicalPath() + File.separator + "logs" + File.separator + "codimage2.log").getAbsoluteFile();

            if (logFile.exists()) {

                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().open(logFile);
                    } catch (IOException ex) {
                        java.util.logging.Logger.getLogger(MenuVentana.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    log.error("Awt Desktop is not supported!");
                }

            } else {
                log.error("File is not exists!");
            }

            log.trace("Done");
        } catch (IOException ex) {
            log.error("IOException reading log file");

        }
    }

    static class NarrowOptionPane extends JLabel {

        NarrowOptionPane(String msg) {
            super("<html>" + msg + "<br></html>");
        }

    }
}
