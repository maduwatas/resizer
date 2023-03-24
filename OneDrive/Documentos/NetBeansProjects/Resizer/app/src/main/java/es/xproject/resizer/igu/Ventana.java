/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.xproject.resizer.igu;

import static es.xproject.resizer.App.rb;
import es.xproject.resizer.base.Constants;
import es.xproject.resizer.base.ReplaceStrategyType;
import es.xproject.resizer.igu.panels.FilesPanel;
import es.xproject.resizer.util.FileList;
import es.xproject.resizer.igu.panels.LogPanel;
import es.xproject.resizer.components.Mask;
import es.xproject.resizer.igu.menu.MenuVentana;
import es.xproject.resizer.igu.panels.OptionsPanel;
import es.xproject.resizer.task.CreatePdfTask;
import es.xproject.resizer.task.MoveTask;
import es.xproject.resizer.task.ResizeTask;
import es.xproject.resizer.util.StringHelper;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author jsolis
 */
public class Ventana extends JFrame implements ActionListener, ChangeListener {

    public static JTextArea processed;
    public static JTextArea pending;
    public static boolean pdfCheck, scalePdf, recursivePdf;
    private Calendar initCal;

    private JFileChooser fc;

    public static final int MODE_OPEN = 1;
    public static final int MODE_SAVE = 2;

    public static File sourceFile;
    public static File destinationFile;
    public static File imagesDir;
    public static int zoom;

    public static int zoomMin, dimMin;
    public static String algorithm, fileType, typeDimMin;
    public static FileList fileList;
    public OptionsPanel uPanel;
    public static Mask mask;
    public static float quality, scale, qualityMin;
    public int poolSize, pdfPoolSize;
    public FilesPanel filesPanel;
    public static String separator;
    public static final AtomicInteger skipCount = new AtomicInteger(0);
    public static final AtomicInteger skipPdfCount = new AtomicInteger(0);
    public static final AtomicInteger imageCount = new AtomicInteger(0);
    public static final AtomicInteger pdfCount = new AtomicInteger(0);
    public static final AtomicInteger errorCount = new AtomicInteger(0);
    public static final AtomicInteger srcCount = new AtomicInteger(0);

    public ThreadPoolExecutor executorService;

    private static final Logger log = LogManager.getLogger();
    public static ReplaceStrategyType replaceStrategy;

    public Ventana() {
        log.info("Opening " + Constants.APPNAME);

        setVentana();
        init();

    }

    private void setVentana() {
        log.trace("setVentana");
        this.setTitle(Constants.APPNAME);

        try {
            Image icon = ImageIO.read(getClass().getClassLoader().getResource("icon_small.png"));
            setIconImage(icon);
         
          

            
        } catch (IOException e) {
            log.error("Icon image load fails " + e);
        }
 
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(1024, 768));
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.getContentPane().setBackground(Color.white);

        setJMenuBar(new MenuVentana(this));

        
    }

    private void init() {

        log.debug("init");
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

        //Create a file chooser
        fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        JPanel data = new JPanel();
        data.setLayout(new BoxLayout(data, BoxLayout.X_AXIS));

        filesPanel = new FilesPanel(this);

        uPanel = new OptionsPanel(this);
        mask = new Mask(this);
        uPanel.add(mask);

        data.add(uPanel);

        pending = new JTextArea(5, 20);
        processed = new JTextArea(5, 20);

        JPanel logPanel = new JPanel();

        logPanel.setLayout(new BoxLayout(logPanel, BoxLayout.Y_AXIS));

        logPanel.add(filesPanel);
        logPanel.add(new LogPanel(pending, rb.getString("sourceFiles")));
        logPanel.add(new LogPanel(processed, rb.getString("destinationFiles")));

        data.add(logPanel);
        this.getContentPane().add(data);

        algorithm = (String) uPanel.algorithm.getSelectedItem();
        fileType = (String) uPanel.fileType.getSelectedItem();
        zoom = uPanel.zoom.getValue();

        zoomMin = uPanel.zoomMin.getValue();
        dimMin = Integer.parseInt(uPanel.dimMin.getText());
        typeDimMin = (String) uPanel.typeDimMin.getSelectedItem();
        qualityMin = uPanel.qualityMin.getValue() / 100f;

        quality = uPanel.quality.getValue() / 100f;
        poolSize = Integer.parseInt((String) uPanel.poolSize.getSelectedItem());
        pdfPoolSize = Integer.parseInt((String) uPanel.pdfPoolSize.getSelectedItem());
        scale = 1f;
        separator = uPanel.separatorChar.getText();
        replaceStrategy = (ReplaceStrategyType) uPanel.replaceStrategy.getSelectedItem();
        
        log.debug("init done");
    }

    private boolean errorDimMin() {
        JOptionPane.showMessageDialog(this,
                rb.getString("dimenError"),
                Constants.APPNAME,
                JOptionPane.ERROR_MESSAGE);
        log.debug("invalid dimension");
        return false;
    }

    private boolean estaListo() {

        if (!validatePdf()) {
            return false;
        }

        try {
            int value = Integer.parseInt(uPanel.dimMin.getText());

            if (value < 0) {
                return errorDimMin();
            }

            dimMin = value;

        } catch (NumberFormatException e) {
            return errorDimMin();
        }

        if (sourceFile != null && sourceFile.exists()
                && destinationFile != null && destinationFile.exists()) {
            if (sourceFile.equals(destinationFile)) {
                JOptionPane.showMessageDialog(this,
                        rb.getString("errorSrcDest"),
                        Constants.APPNAME,
                        JOptionPane.ERROR_MESSAGE);
                log.debug("Los directorios origen y destino no pueden ser los mismos");
                return false;
            }
            return true;
        }

        log.trace("Elige directorio origen y destino");
        JOptionPane.showMessageDialog(this,
                rb.getString("selectSrcDest"),
                Constants.APPNAME,
                JOptionPane.WARNING_MESSAGE);

        return false;

    }

    private static void appendLine(JTextArea textArea, String msg) {
        textArea.append(msg + "\n");
    }

    public static void tracePendings(String msg) {
        log.debug("add pending: " + msg);
        appendLine(pending, msg);
        pending.setCaretPosition(pending.getDocument().getLength());
    }

    public static void traceProcessed(String msg) {
        log.debug("add processed: " + msg);
        appendLine(processed, msg);
        processed.setCaretPosition(processed.getDocument().getLength());
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == uPanel.zoomMin) {
            JSlider source = (JSlider) e.getSource();
            zoomMin = (int) source.getValue();
            uPanel.setZoomMin(source.getValue());
        } else if (e.getSource() == uPanel.qualityMin) {
            JSlider source = (JSlider) e.getSource();
            qualityMin = (int) source.getValue() / 100f;
            uPanel.setQualityMin(source.getValue());
        } else if (e.getSource() == uPanel.zoom) {
            JSlider source = (JSlider) e.getSource();
            zoom = (int) source.getValue();
            uPanel.setZoom(source.getValue());
        } else if (e.getSource() == uPanel.scale) {
            JSlider source = (JSlider) e.getSource();
            scale = source.getValue() / 100f;
            uPanel.setScale(source.getValue());
        } else if (e.getSource() == uPanel.quality) {
            JSlider source = (JSlider) e.getSource();
            quality = (int) source.getValue() / 100f;
            uPanel.setQuality(source.getValue());
        } else if (e.getSource() == uPanel.pdfCheck) {
            JCheckBox check = (JCheckBox) e.getSource();
            pdfCheck = check.isSelected();
        } else if (e.getSource() == uPanel.recursivePdf) {
            JCheckBox check = (JCheckBox) e.getSource();
            recursivePdf = check.isSelected();
        } else if (e.getSource() == uPanel.scalePdf) {
            JCheckBox check = (JCheckBox) e.getSource();
            scalePdf = check.isSelected();

            if (scalePdf) {
                uPanel.scale.getParent().setVisible(true);
                scale = uPanel.scale.getValue() / 100f;
            } else {
                uPanel.scale.getParent().setVisible(false);
                scale = 1f;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //Handle open button action.

        if (e.getSource() == filesPanel.sourceButton) {
            int returnVal = fc.showOpenDialog(Ventana.this);
            srcCount.set(0);
            pending.setText("");

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                sourceFile = fc.getSelectedFile();
                //This is where a real application would open the file.
                tracePendings(rb.getString("source") + ": " + sourceFile.getAbsolutePath() + ".");
                filesPanel.sourceButtonText.setText(sourceFile.getAbsolutePath());
            }

            fileList = new FileList().build(sourceFile, true);

            srcCount.addAndGet(fileList.files.size());

            //Handle save button action.
        } else if (e.getSource() == filesPanel.destinationButton) {
            int returnVal = fc.showOpenDialog(Ventana.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {

                destinationFile = fc.getSelectedFile();
                traceProcessed(rb.getString("destination") + ": " + destinationFile.getAbsolutePath() + ".");
                filesPanel.destinationButtonText.setText(destinationFile.getAbsolutePath());

                if (FileList.anyImageFile(destinationFile)) {
                    uPanel.pdfButton.setVisible(true);
                } else {
                    uPanel.pdfButton.setVisible(false);
                }
            }

        } else if (e.getSource() == filesPanel.imagesButton) {
            filesPanel.imagesButtonText.setText(rb.getString("imgDir") + ": ");
            int returnVal = fc.showOpenDialog(Ventana.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                imagesDir = fc.getSelectedFile();
                traceProcessed(rb.getString("preImage") + ": " + imagesDir.getAbsolutePath() + ".");
                filesPanel.imagesButtonText.setText(imagesDir.getAbsolutePath());
            }

            if (FileList.anyImageFile(imagesDir)) {
                uPanel.mvButton.setVisible(true);
            } else {
                uPanel.mvButton.setVisible(false);
            }

        } else if (e.getSource() == uPanel.launchButton) {

            if (estaListo()) {

                if (pdfCheck && !recursivePdf) {
                    uPanel.recursivePdf.doClick();
                }
                initCal = Calendar.getInstance();
                startResizeThread();
            }
        } else if (e.getSource() == uPanel.cancelButton) {

            if (executorService != null) {
                uPanel.cancelButton.setEnabled(false);
                traceProcessed(rb.getString("canceling"));
                executorService.shutdownNow();
            }

        } else if (e.getSource() == uPanel.mvButton) {

            if (validateMv()) {

                if (pdfCheck && !recursivePdf) {
                    uPanel.recursivePdf.doClick();
                }
                initCal = Calendar.getInstance();
                new MoveTask(this).execute();
            }

        } else if (e.getSource() == uPanel.pdfButton) {

            if (validatePdf()) {
                processed.setText("");
                traceProcessed(rb.getString("destination") + ": " + destinationFile.getAbsolutePath() + ".");
                pending.setText("");
                initCal = Calendar.getInstance();
                startPdfCreationsThread();
            }

        } else if (e.getSource() == uPanel.algorithm) {
            algorithm = (String) uPanel.algorithm.getSelectedItem();
        } else if (e.getSource() == uPanel.replaceStrategy) {
            replaceStrategy = (ReplaceStrategyType) uPanel.replaceStrategy.getSelectedItem();
        } else if (e.getSource() == uPanel.fileType) {
            fileType = (String) uPanel.fileType.getSelectedItem();
        } else if (e.getSource() == uPanel.typeDimMin) {
            typeDimMin = (String) uPanel.typeDimMin.getSelectedItem();
        } else if (e.getSource() == uPanel.fileType) {
            fileType = (String) uPanel.fileType.getSelectedItem();
        } else if (e.getSource() == uPanel.poolSize) {
            poolSize = Integer.parseInt((String) uPanel.poolSize.getSelectedItem());

        } else if (e.getSource() == uPanel.pdfPoolSize) {
            pdfPoolSize = Integer.parseInt((String) uPanel.pdfPoolSize.getSelectedItem());
        }

    }

    private boolean validateMv() {
        if (estaListo()) {
            if (imagesDir.equals(destinationFile)) {
                log.debug(rb.getString("errorImgDest"));
                JOptionPane.showMessageDialog(this,
                        "",
                        Constants.APPNAME,
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
            return true;
        }
        return false;
    }

    public void startPdfCreationsThread() {
        pdfCount.set(0);
        zoomMin = uPanel.zoomMin.getValue();
        dimMin = Integer.parseInt(uPanel.dimMin.getText());
        typeDimMin = (String) uPanel.typeDimMin.getSelectedItem();
        qualityMin = uPanel.qualityMin.getValue() / 100f;
        log.debug("startPdfCreationsThread");
        new CreatePdfTask(this).execute();
    }

    public void startResizeThread() {
        log.debug("startResizeThread");
        new ResizeTask(this).execute();
    }

    public void showFinalDialog() {
        String msg;

        if (uPanel.cancelButton.isEnabled()) {
            msg = rb.getString("endProcess");
        } else {
            msg = rb.getString("cancelProcess");
        }

        if (srcCount.get() > 0) {
            msg += "\n" + srcCount.get() + " " + rb.getString("srcImages");
        }

        if (imageCount.get() > 0) {
            msg += "\n" + imageCount.get() + " " + rb.getString("successImages");
            imageCount.set(0);
        }
        
         if (skipCount.get() > 0) {
            msg += "\n" + skipCount.get() + " " + rb.getString("skipImages");
            skipCount.set(0);
        }
         
        if (skipPdfCount.get() > 0) {
            msg += "\n" + skipPdfCount.get() + " " + rb.getString("skipPdf");
            skipPdfCount.set(0);
        }

        if (errorCount.get() > 0) {
            msg += "\n" + errorCount.get() + " " + rb.getString("errors");
            errorCount.set(0);
        }

        if (pdfCount.get() > 0) {
            msg += "\n" + pdfCount.get() + " " + rb.getString("pdfCreated");
            pdfCount.set(0);
        }

        if (initCal != null) {
            Calendar cal = Calendar.getInstance();
            long timeDiff = cal.getTimeInMillis() - initCal.getTimeInMillis();
            msg += "\n\n" + rb.getString("processingTime")+": " + StringHelper.formatChronoFromSeconds(timeDiff / 1000);
        }

        JOptionPane.showMessageDialog(Ventana.this,
                msg,
                Constants.APPNAME,
                JOptionPane.INFORMATION_MESSAGE);

        log.debug("showFinalDialog " + msg);

        traceProcessed(msg);

        pdfButton();
    }

    public void pdfButton() {
        if (FileList.anyImageFile(destinationFile)) {
            uPanel.pdfButton.setVisible(true);
        } else {
            uPanel.pdfButton.setVisible(false);
        }
    }

    private boolean validatePdf() {
        separator = uPanel.separatorChar.getText();

        if (separator.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    rb.getString("errorSeparator"),
                    Constants.APPNAME,
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

}
