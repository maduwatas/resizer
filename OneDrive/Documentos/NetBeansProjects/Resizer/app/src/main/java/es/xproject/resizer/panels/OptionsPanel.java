/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.xproject.resizer.panels;

import es.xproject.resizer.Ventana;
import es.xproject.resizer.resize.Resize;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 *
 * @author jsolis
 */
public class OptionsPanel extends JPanel {

    public JButton launchButton, cancelButton, pdfButton, mvButton;
    public JSlider zoom, quality, scale;
    public JComboBox algorithm, fileType, poolSize, pdfPoolSize;
    public JCheckBox pdfCheck, scalePdf, recursivePdf;
    public JLabel labelZoom, labelQuality, labelScale;
    public JTextField separatorChar;
    
    private static final int FORM_WIDTH = 380;

    //  public JPanel form;
    public OptionsPanel(Ventana ventana) {
        super();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(new JLabel("Opciones"));

        createCombos(ventana);
        createCompressionSettings(ventana);
       
        add(createPdfCheck(ventana));
        add(createPoolSize(ventana));
        launchPanel(ventana);

    }

    public void setScale(int scale) {
        labelScale.setText("Escalar PDF " + scale + "%");
    }

    public void setZoom(int ppp) {
        labelZoom.setText("Resolución " + ppp + " ppp");
    }

    public void setQuality(int quality) {
        labelQuality.setText("Calidad " + quality + "%");
    }

    private JPanel createZoom(Ventana ventana) {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        int defaultPpp = 200;
        labelZoom = new JLabel();
        setZoom(defaultPpp);
        panel.add(labelZoom);
        add(panel);

        zoom = new JSlider(JSlider.HORIZONTAL, 50, FORM_WIDTH, defaultPpp);
        zoom.addChangeListener(ventana);

        //Turn on labels at major tick marks.
        zoom.setMajorTickSpacing(50);
        zoom.setMinorTickSpacing(10);
        zoom.setPaintTicks(true);
        zoom.setPaintLabels(true);

        panel.add(zoom);
        return panel;
    }

    private JPanel createQuality(Ventana ventana) {

        labelQuality = new JLabel("Calidad 80%", SwingConstants.RIGHT);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(labelQuality);

        quality = new JSlider(JSlider.HORIZONTAL, 20, 100, 80);
        quality.addChangeListener(ventana);

        //Turn on labels at major tick marks.
        quality.setMajorTickSpacing(10);
        quality.setMinorTickSpacing(1);
        quality.setPaintTicks(true);
        quality.setPaintLabels(true);
        panel.add(quality);
        return panel;
    }

    private JPanel createAlgorithm(Ventana ventana) {
        String[] aStrings = Resize.getSupportedAlgorithms();

        algorithm = new JComboBox(aStrings);
        algorithm.setPreferredSize(new Dimension(150, 30));
        algorithm.setSelectedIndex(0);
        algorithm.addActionListener(ventana);

        JPanel panelAlgorithm = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel cLabel = new JLabel();
        cLabel.setText("Algoritmo de escalado");

        panelAlgorithm.add(cLabel);
        panelAlgorithm.add(algorithm);

        JPanel f = new JPanel();
        f.setLayout(new BoxLayout(f, BoxLayout.X_AXIS));
        f.add(panelAlgorithm);

        return f;
    }

    private JPanel createFileType(Ventana ventana) {
        fileType = new JComboBox(new String[]{"jpeg", "jpg"});
        fileType.setPreferredSize(new Dimension(150, 30));
        fileType.setSelectedIndex(0);
        fileType.addActionListener(ventana);

        JPanel panelFileType = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel cLabel = new JLabel();
        cLabel.setText("Tipo de fichero destino");

        panelFileType.add(cLabel);
        panelFileType.add(fileType);

        JPanel f = new JPanel();
        f.setLayout(new BoxLayout(f, BoxLayout.X_AXIS));
        f.add(panelFileType);
        return f;
    }

    private JPanel createPdfCheck(Ventana ventana) {

        pdfCheck = new JCheckBox();
        scalePdf = new JCheckBox();
        recursivePdf = new JCheckBox();

        JPanel panelPdf = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel cLabel = new JLabel();
        cLabel.setText("Crear PDF");
        panelPdf.add(cLabel);
        panelPdf.add(pdfCheck);

        JPanel panelRecursivePdf = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        cLabel = new JLabel();
        cLabel.setText("Recursivo");
        panelRecursivePdf.add(cLabel);
        panelRecursivePdf.add(recursivePdf);

        JPanel checkBoxes = new JPanel(new GridLayout(2, 2));

        checkBoxes.add(panelPdf);
        checkBoxes.add(panelRecursivePdf);
       
        
        JPanel panelSeparator = new JPanel(new FlowLayout(FlowLayout.LEFT));
        separatorChar = new JTextField(2);
        separatorChar.setPreferredSize(new Dimension(2, 30));
        separatorChar.setDocument(new JTextFieldLimit(1));
        separatorChar.setText("_");

        cLabel = new JLabel();
        cLabel.setText("Separador");
        panelSeparator.add(cLabel);
        panelSeparator.add(separatorChar);
       
        checkBoxes.add(panelSeparator);
        
         JPanel panelScalePdf = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        cLabel = new JLabel();
        cLabel.setText("Escalar PDF");
        panelScalePdf.add(cLabel);
        panelScalePdf.add(scalePdf);
        checkBoxes.add(panelScalePdf);
        
        
        pdfCheck.addChangeListener(ventana);
        scalePdf.addChangeListener(ventana);
        recursivePdf.addChangeListener(ventana);

        labelScale = new JLabel("Escalar PDF 80%", SwingConstants.RIGHT);

        JPanel panel = new JPanel(new GridLayout(2, 1));
        javax.swing.border.Border blackline = BorderFactory.createTitledBorder("Exportar pdf");

        panel.setBorder(blackline);

        panel.setPreferredSize(new Dimension(FORM_WIDTH, 80));

        panel.add(checkBoxes);
        JPanel scalePanel = new JPanel();
        scalePanel.setLayout(new BoxLayout(scalePanel, BoxLayout.Y_AXIS));

        scalePanel.add(labelScale);

        scale = new JSlider(JSlider.HORIZONTAL, 20, 100, 80);
        scale.addChangeListener(ventana);

        //Turn on labels at major tick marks.
        scale.setMajorTickSpacing(10);
        scale.setMinorTickSpacing(1);
        scale.setPaintTicks(true);
        scale.setPaintLabels(true);
        scalePanel.add(scale);
        panel.add(scalePanel);
        scalePanel.setVisible(false);

        panel.setBorder(blackline);

        return panel;
    }

    private void createCombos(Ventana ventana) {
        JPanel panelFileType = createAlgorithm(ventana);
        JPanel panelAlgorithm = createFileType(ventana);

        JPanel combos = new JPanel(new GridLayout(2, 1));

        javax.swing.border.Border blackline = BorderFactory.createTitledBorder("Generación de imágenes");
        combos.setBorder(blackline);
        combos.add(panelFileType);
        combos.add(panelAlgorithm);
        combos.setPreferredSize(new Dimension(FORM_WIDTH, 40));
        add(combos);

    }

    private void launchPanel(Ventana ventana) {
        JPanel launchPanel = new JPanel();
        launchButton = new JButton("Procesar");

        launchButton.addActionListener(ventana);

        cancelButton = new JButton("Cancelar");

        cancelButton.addActionListener(ventana);
        cancelButton.setVisible(false);

        pdfButton = new JButton("Generar pdf");

        pdfButton.addActionListener(ventana);
        pdfButton.setVisible(false);
        
        mvButton = new JButton("Mover imágenes");

        mvButton.addActionListener(ventana);
        mvButton.setVisible(false);

        launchPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        launchPanel.add(launchButton);
        launchPanel.add(cancelButton);
        launchPanel.add(pdfButton);
        launchPanel.add(mvButton);
        add(launchPanel);
    }

    private JPanel createPoolSize(Ventana ventana) {

        pdfPoolSize = new JComboBox(new String[]{"1", "2", "3", "4"});
        pdfPoolSize.setPreferredSize(new Dimension(100, 30));
        pdfPoolSize.setSelectedIndex(1);
        pdfPoolSize.addActionListener(ventana);

        poolSize = new JComboBox(new String[]{"1", "2", "3", "4", "5","6","7","8"});
        poolSize.setSelectedIndex(3);
        poolSize.setPreferredSize(new Dimension(100, 30));
        poolSize.addActionListener(ventana);

        JPanel panelPoolSize = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel cLabel = new JLabel();
        cLabel.setText("Hilos Imagen");

        panelPoolSize.add(cLabel);
        panelPoolSize.add(poolSize);

        JPanel panelPdfPoolSize = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        cLabel = new JLabel();
        cLabel.setText("Hilos Pdf");
        panelPdfPoolSize.add(cLabel);
        panelPdfPoolSize.add(pdfPoolSize);

        JPanel f = new JPanel();
        f.setLayout(new BoxLayout(f, BoxLayout.X_AXIS));
        f.add(panelPoolSize);
        f.add(panelPdfPoolSize);

        javax.swing.border.Border blackline = BorderFactory.createTitledBorder("Ajustes multihilo");

        f.setBorder(blackline);
        f.setPreferredSize(new Dimension(FORM_WIDTH, 15));
        return f;
    }

    public void enableFields() {
        launchButton.setEnabled(true);
        launchButton.setVisible(true);
        cancelButton.setVisible(false);
        cancelButton.setEnabled(true);
        pdfButton.setEnabled(true);
        zoom.setEnabled(true);
        quality.setEnabled(true);
        algorithm.setEnabled(true);
        fileType.setEnabled(true);
        poolSize.setEnabled(true);
        pdfPoolSize.setEnabled(true);
        pdfCheck.setEnabled(true);
        scalePdf.setEnabled(true);
        recursivePdf.setEnabled(true);
        scale.setEnabled(true);
        mvButton.setEnabled(true);
        separatorChar.setEnabled(true);
    }

    public void disableFields() {
        launchButton.setEnabled(false);
        launchButton.setVisible(false);
        cancelButton.setVisible(true);
        cancelButton.setEnabled(true);
        pdfButton.setEnabled(false);
        zoom.setEnabled(false);
        quality.setEnabled(false);
        algorithm.setEnabled(false);
        fileType.setEnabled(false);
        poolSize.setEnabled(false);
        pdfPoolSize.setEnabled(false);
        pdfCheck.setEnabled(false);
        scalePdf.setEnabled(false);
        mvButton.setEnabled(false);
        scale.setEnabled(false);
        recursivePdf.setEnabled(false);
        separatorChar.setEnabled(false);
    }

    private void createCompressionSettings(Ventana ventana) {
        javax.swing.border.Border blackline = BorderFactory.createTitledBorder("Calidad y resolución");
        JPanel f = new JPanel(new GridLayout(2,1));
        f.setBorder(blackline);
        f.setPreferredSize(new Dimension(FORM_WIDTH, 80));
        f.add(createZoom(ventana));

        f.add(createQuality(ventana));
        
        add(f);

    }

}
