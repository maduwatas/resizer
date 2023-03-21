/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.xproject.resizer;

import es.xproject.resizer.panels.FilesPanel;
import es.xproject.resizer.util.FileList;
import es.xproject.resizer.panels.LogPanel;
import es.xproject.resizer.panels.Mask;
import es.xproject.resizer.panels.OptionsPanel;
import es.xproject.resizer.task.CreatePdfTask;
import es.xproject.resizer.task.MoveTask;
import es.xproject.resizer.task.ResizeTask;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

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

    public static final String newline = "\n";

    public static JTextArea processed;
    public static JTextArea pending;
    public static boolean pdfCheck, scalePdf, recursivePdf;

    private JFileChooser fc;

    public static final int MODE_OPEN = 1;
    public static final int MODE_SAVE = 2;

    public static File sourceFile;
    public static File destinationFile;
    public static File imagesDir;
    public static int zoom;
    public static String algorithm, fileType;
    public static FileList fileList;
    public OptionsPanel uPanel;
    public static Mask mask;
    public static float quality, scale;
    public int poolSize, pdfPoolSize;
    public FilesPanel filesPanel;
    public static String separator;
    public static final AtomicInteger imageCount = new AtomicInteger(0);
    public static final AtomicInteger errorCount = new AtomicInteger(0);

    public ThreadPoolExecutor executorService;
    
    private static final Logger logger = LogManager.getLogger();

    public Ventana() {
        logger.info("Opening XProject Resizer...");
        setVentana();
        init();
        
        // TODO crear botón cancelar
    }

    private void setVentana() {
        logger.debug("setVentana");
        this.setSize(1024, 768);
        this.setTitle("XProject Resizer");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(1024, 768));

        this.getContentPane().setBackground(Color.white);
    }

    private void init() {
        
        
        
        logger.debug("init");
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

        JPanel log = new JPanel();

        log.setLayout(new BoxLayout(log, BoxLayout.Y_AXIS));

        log.add(filesPanel);
        log.add(new LogPanel(pending, "Ficheros y directorios origen"));
        log.add(new LogPanel(processed, "Ficheros y directorios creados"));

        data.add(log);
        this.getContentPane().add(data);

        algorithm = (String) uPanel.algorithm.getSelectedItem();
        fileType = (String) uPanel.fileType.getSelectedItem();
        zoom = uPanel.zoom.getValue();
        quality = uPanel.quality.getValue() / 100f;
        poolSize = Integer.parseInt((String) uPanel.poolSize.getSelectedItem());
        pdfPoolSize = Integer.parseInt((String) uPanel.pdfPoolSize.getSelectedItem());
        scale = 1f;
        separator = uPanel.separatorChar.getText();
        logger.debug("init done");
    }

    private boolean estaListo() {

        if (!validatePdf()) {
            return false;
        }

        if (sourceFile != null && sourceFile.exists()
                && destinationFile != null && destinationFile.exists()) {
            if (sourceFile.equals(destinationFile)) {
                JOptionPane.showMessageDialog(this,
                        "Los directorios origen y destino no pueden ser los mismos.",
                        "Resizer",
                        JOptionPane.WARNING_MESSAGE);
                logger.debug("Los directorios origen y destino no pueden ser los mismos");
                return false;
            }
            return true;
        }

        logger.debug("Elige directorio origen y destino");
        JOptionPane.showMessageDialog(this,
                "Elige directorio origen y destino.",
                "Resizer",
                JOptionPane.WARNING_MESSAGE);

        return false;

    }

    public static void tracePendings(String msg) {
        logger.debug("add pending: " + msg);
        pending.append(msg + newline);
    }

    public static void traceProcessed(String msg) {
        logger.debug("add processed: " + msg);
        processed.append(msg + newline);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == uPanel.zoom) {
            JSlider source = (JSlider) e.getSource();
            zoom = (int) source.getValue();
            uPanel.setZoom(source.getValue());
        }
        if (e.getSource() == uPanel.scale) {
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

            pending.setText("");

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                sourceFile = fc.getSelectedFile();
                //This is where a real application would open the file.
                pending.append("Fuente: " + sourceFile.getAbsolutePath() + "." + newline);
                filesPanel.sourceButtonText.setText(sourceFile.getAbsolutePath());
            }

            pending.setCaretPosition(pending.getDocument().getLength());

            fileList = new FileList().build(sourceFile, true);

            //Handle save button action.
        } else if (e.getSource() == filesPanel.destinationButton) {
            int returnVal = fc.showOpenDialog(Ventana.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                destinationFile = fc.getSelectedFile();
                processed.append("Destino: " + destinationFile.getAbsolutePath() + "." + newline);
                filesPanel.destinationButtonText.setText(destinationFile.getAbsolutePath());

                if (FileList.anyImageFile(destinationFile)) {
                    uPanel.pdfButton.setVisible(true);
                } else {
                    uPanel.pdfButton.setVisible(false);
                }
            }
            processed.setCaretPosition(processed.getDocument().getLength());
        } else if (e.getSource() == filesPanel.imagesButton) {
            filesPanel.imagesButtonText.setText("Imágenes preprocesadas (opcional)");
            int returnVal = fc.showOpenDialog(Ventana.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                imagesDir = fc.getSelectedFile();
                processed.append("Imágenes preprocesadas: " + imagesDir.getAbsolutePath() + "." + newline);
                filesPanel.imagesButtonText.setText(imagesDir.getAbsolutePath());
            }

            if (FileList.anyImageFile(imagesDir)) {
                uPanel.mvButton.setVisible(true);
            } else {
                uPanel.mvButton.setVisible(false);
            }

            processed.setCaretPosition(processed.getDocument().getLength());
        } else if (e.getSource() == uPanel.launchButton) {

            if (estaListo()) {

                if (pdfCheck && !recursivePdf) {
                    uPanel.recursivePdf.doClick();
                }

                startResizeThread();
            }
        } else if (e.getSource() == uPanel.cancelButton) {

            if (executorService != null) {
                uPanel.cancelButton.setEnabled(false);
                processed.append("Intentando cancelar procesos..." + newline);
                executorService.shutdownNow();
            }

        } else if (e.getSource() == uPanel.mvButton) {

            if (validateMv()) {

                if (pdfCheck && !recursivePdf) {
                    uPanel.recursivePdf.doClick();
                }

                new MoveTask(this).execute();
            }

        } else if (e.getSource() == uPanel.pdfButton) {

            if (validatePdf()) {
                processed.setText("Destino: " + destinationFile.getAbsolutePath() + "." + newline);
                pending.setText("");
                startPdfCreationsThread();
            }

        } else if (e.getSource() == uPanel.algorithm) {
            algorithm = (String) uPanel.algorithm.getSelectedItem();
        } else if (e.getSource() == uPanel.fileType) {
            fileType = (String) uPanel.fileType.getSelectedItem();
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
                logger.debug("El directorio destino no puede ser el mismo que el de imágenes " );
                JOptionPane.showMessageDialog(this,
                        "El directorio destino no puede ser el mismo que el de imágenes.",
                        "Resizer",
                        JOptionPane.WARNING_MESSAGE);
                return false;
            }
            return true;
        }
        return false;
    }

    public void startPdfCreationsThread() {
        logger.debug("startPdfCreationsThread" );
        new CreatePdfTask(this).execute();
    }

    public void startResizeThread() {
        logger.debug("startResizeThread" );
        new ResizeTask(this).execute();
    }

    public void showFinalDialog() {
        String msg;

        if (uPanel.cancelButton.isEnabled()) {
            msg = "Proceso finalizado.";
        } else {
            msg = "Proceso cancelado.";
        }

        if (imageCount.get() > 0) {
            msg += "\n" + imageCount.get() + " imágenes procesadas con éxito.";
        }

        if (errorCount.get() > 0) {
            msg += "\n" + errorCount.get() + " errores de proceso.";
        }

        JOptionPane.showMessageDialog(Ventana.this,
                msg,
                "Resizer",
                JOptionPane.INFORMATION_MESSAGE);
        
        logger.debug("showFinalDialog " + msg);
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
                    "El separador no puede estar vacío",
                    "Resizer",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
}
