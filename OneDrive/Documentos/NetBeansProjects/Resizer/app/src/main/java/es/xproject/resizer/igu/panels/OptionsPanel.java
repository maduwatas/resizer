/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.xproject.resizer.igu.panels;

import static es.xproject.resizer.App.rb;
import es.xproject.resizer.components.JTextFieldLimit;
import es.xproject.resizer.base.Constants;
import es.xproject.resizer.base.ReplaceStrategyType;
import es.xproject.resizer.igu.Ventana;
import es.xproject.resizer.resize.Resize;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
    public JSlider zoom, quality, scale, zoomMin, qualityMin;
    public JComboBox algorithm, fileType, poolSize, pdfPoolSize, typeDimMin, replaceStrategy;
    public JCheckBox pdfCheck, scalePdf, recursivePdf;
    public JLabel labelZoom, labelZoomMin,labelQualityMin, labelQuality, labelScale;
    public JTextField separatorChar;
    public JTextField dimMin;

    private static final int FORM_WIDTH = 400;

    //  public JPanel form;
    public OptionsPanel(Ventana ventana) {
        super();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        createCombos(ventana);
        createCompressionSettings(ventana);

        createMinOptions(ventana);

        add(createPdfCheck(ventana));
        add(createPoolSize(ventana));
        add(replaceStrategy(ventana));
        launchPanel(ventana);

    }

    public void setScale(int scale) {
        labelScale.setText(rb.getString("scalePdf") + " " + scale + "%");
    }

    public void setZoom(int ppp) {
        labelZoom.setText(rb.getString("resolution") + " " + ppp + " " + rb.getString("ppp"));
    }
    
     public void setZoomMin(int ppp) {
        labelZoomMin.setText(rb.getString("resolution") + " " + ppp + " " + rb.getString("ppp"));
    }

    public void setQualityMin(int quality) {
        labelQualityMin.setText(rb.getString("quality") + " " + quality + "%");
    }
     
    public void setQuality(int quality) {
        labelQuality.setText(rb.getString("quality") + " " + quality + "%");
    }

    private JPanel createZoom(Ventana ventana) {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        int defaultPpp = 200;
        labelZoom = new JLabel();
        setZoom(defaultPpp);
        panel.add(labelZoom);
        add(panel);

        zoom = new JSlider(JSlider.HORIZONTAL, 50, 400, defaultPpp);
        zoom.addChangeListener(ventana);

        //Turn on labels at major tick marks.
        zoom.setMajorTickSpacing(50);
        zoom.setMinorTickSpacing(10);
        zoom.setPaintTicks(true);
        zoom.setPaintLabels(true);

        panel.add(zoom);
        return panel;
    }
    
     private JPanel createZoomMin(Ventana ventana) {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        int defaultPpp = 76;
        labelZoomMin = new JLabel();
        setZoomMin(defaultPpp);
        panel.add(labelZoomMin);
        add(panel);

        zoomMin = new JSlider(JSlider.HORIZONTAL, 50, 200, defaultPpp);
        zoomMin.addChangeListener(ventana);

        //Turn on labels at major tick marks.
        zoomMin.setMajorTickSpacing(50);
        zoomMin.setMinorTickSpacing(10);
        zoomMin.setPaintTicks(true);
        zoomMin.setPaintLabels(true);

        panel.add(zoomMin);
        return panel;
    }

    private JPanel createQualityMin(Ventana ventana) {

        labelQualityMin = new JLabel(rb.getString("quality") + " 80%", SwingConstants.RIGHT);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(labelQualityMin);

        qualityMin = new JSlider(JSlider.HORIZONTAL, 20, 100, 80);
        qualityMin.addChangeListener(ventana);

        //Turn on labels at major tick marks.
        qualityMin.setMajorTickSpacing(10);
        qualityMin.setMinorTickSpacing(1);
        qualityMin.setPaintTicks(true);
        qualityMin.setPaintLabels(true);
        panel.add(qualityMin);
        return panel;
    }

    
    private JPanel createQuality(Ventana ventana) {

        labelQuality = new JLabel(rb.getString("quality") + " 80%", SwingConstants.RIGHT);

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
        algorithm.setSelectedIndex(2);
        algorithm.addActionListener(ventana);

        JPanel panelAlgorithm = new JPanel(new GridLayout(2,1));

        JLabel cLabel = new JLabel();
        cLabel.setText(rb.getString("algorithm"));

        panelAlgorithm.add(cLabel);
        panelAlgorithm.add(algorithm);

        JPanel f = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        f.add(panelAlgorithm);

        return f;
    }

    private JPanel createFileType(Ventana ventana) {
        fileType = new JComboBox(new String[]{"jpg","jpeg"});
        fileType.setPreferredSize(new Dimension(150, 30));
        fileType.setSelectedIndex(0);
        fileType.addActionListener(ventana);

        JPanel panelFileType =new JPanel(new GridLayout(2,1));

        JLabel cLabel = new JLabel();
        cLabel.setText(rb.getString("fileType"));

        panelFileType.add(cLabel);
        panelFileType.add(fileType);

        JPanel f =  new JPanel(new FlowLayout(FlowLayout.RIGHT));
   
        f.add(panelFileType);
        return f;
    }
    
   
    
    private JPanel createPdfCheck(Ventana ventana) {

        pdfCheck = new JCheckBox();
        scalePdf = new JCheckBox();
        recursivePdf = new JCheckBox();

        JPanel panelPdf = new JPanel(new GridLayout(2,1));
       
        JLabel cLabel = new JLabel();
        cLabel.setText(rb.getString("createPdf"));
        panelPdf.add(cLabel);
        panelPdf.add(pdfCheck);

        JPanel panelRecursivePdf = new JPanel(new GridLayout(2,1));
       
        
        cLabel = new JLabel();
        cLabel.setText(rb.getString("recursive"));
        panelRecursivePdf.add(cLabel);
        panelRecursivePdf.add(recursivePdf);

        JPanel panelSeparator = new JPanel(new GridLayout(2,1));
      
        
        separatorChar = new JTextField(2);
        separatorChar.setPreferredSize(new Dimension(1, 30));
        separatorChar.setDocument(new JTextFieldLimit(1));
        separatorChar.setText("_");

        cLabel = new JLabel();
        cLabel.setText(rb.getString("separator"));
        panelSeparator.add(cLabel);
        panelSeparator.add(separatorChar);

       

        JPanel panelScalePdf = new JPanel(new GridLayout(2,1));
        
        
        cLabel = new JLabel();
        cLabel.setText(rb.getString("scalePdf"));
        panelScalePdf.add(cLabel);
        panelScalePdf.add(scalePdf);
        
        
        JPanel checkBoxes = new JPanel(new GridBagLayout());
        checkBoxes.setPreferredSize(new Dimension(FORM_WIDTH, 1));
        
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 0.5;
        
        checkBoxes.add(panelPdf,c);
        checkBoxes.add(panelRecursivePdf,c);
        checkBoxes.add(panelSeparator,c);
        checkBoxes.add(panelScalePdf,c);

        pdfCheck.addChangeListener(ventana);
        scalePdf.addChangeListener(ventana);
        recursivePdf.addChangeListener(ventana);

        labelScale = new JLabel(rb.getString("scalePdf") + " 80%", SwingConstants.RIGHT);

        JPanel panel = new JPanel(new GridLayout(2, 1));
        javax.swing.border.Border blackline = BorderFactory.createTitledBorder(rb.getString("exportPdf"));

        panel.setBorder(blackline);

        panel.setPreferredSize(new Dimension(FORM_WIDTH, 100));
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

        JPanel combos = new JPanel(new GridLayout(1, 2));

        javax.swing.border.Border blackline = BorderFactory.createTitledBorder(rb.getString("imageGeneration"));
        combos.setBorder(blackline);
        combos.add(panelFileType);
        combos.add(panelAlgorithm);
        combos.setPreferredSize(new Dimension(FORM_WIDTH, 50));
        add(combos);

    }

    private void launchPanel(Ventana ventana) {
        JPanel launchPanel = new JPanel();
        launchButton = new JButton(rb.getString("process"));

        launchButton.addActionListener(ventana);

        cancelButton = new JButton(rb.getString("cancel"));

        cancelButton.addActionListener(ventana);
        cancelButton.setVisible(false);

        pdfButton = new JButton(rb.getString("createPdf"));

        pdfButton.addActionListener(ventana);
        pdfButton.setVisible(false);

        mvButton = new JButton(rb.getString("moveImg"));

        mvButton.addActionListener(ventana);
        mvButton.setVisible(false);

        launchPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        launchPanel.add(launchButton);
        launchPanel.add(cancelButton);
        launchPanel.add(pdfButton);
        launchPanel.add(mvButton);
        add(launchPanel);
    }

    private JPanel replaceStrategy(Ventana ventana) {
         replaceStrategy = new JComboBox(new ReplaceStrategyType[]{ReplaceStrategyType.OVERWRITE, ReplaceStrategyType.REMAIN});
         replaceStrategy.setSelectedIndex(1);
         replaceStrategy.addActionListener(ventana);
         JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
         panel.add(replaceStrategy);
     
         javax.swing.border.Border blackline = BorderFactory.createTitledBorder(rb.getString("replaceStrategy"));
         JPanel f = new JPanel(new FlowLayout(FlowLayout.LEFT));
         f.add(panel);
         f.setBorder(blackline);
         f.setPreferredSize(new Dimension(FORM_WIDTH, 25));
         return f;
         
    }
    private JPanel createPoolSize(Ventana ventana) {

        pdfPoolSize = new JComboBox(new String[]{"1", "2", "3", "4"});
        pdfPoolSize.setPreferredSize(new Dimension(150, 30));
        pdfPoolSize.setSelectedIndex(1);
        pdfPoolSize.addActionListener(ventana);

        poolSize = new JComboBox(new String[]{"1", "2", "3", "4", "5", "6", "7", "8"});
        poolSize.setSelectedIndex(3);
        poolSize.setPreferredSize(new Dimension(150, 30));
        poolSize.addActionListener(ventana);

        JPanel panelPoolSize = new JPanel(new FlowLayout(FlowLayout.LEFT));

        
        JPanel f = new JPanel(new GridLayout(2,1));
        JLabel cLabel = new JLabel();
        cLabel.setText(rb.getString("imgTh"));

        f.add(cLabel);
        f.add(poolSize);
        panelPoolSize.add(f);

        JPanel panelPdfPoolSize = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        f = new JPanel(new GridLayout(2,1));
        cLabel = new JLabel();
        cLabel.setText(rb.getString("pdfTh"));
        f.add(cLabel);
        f.add(pdfPoolSize);
        panelPdfPoolSize.add(f);
       
        f = new JPanel(new GridLayout(1,2));
    
        f.add(panelPoolSize);
        f.add(panelPdfPoolSize);

        javax.swing.border.Border blackline = BorderFactory.createTitledBorder(rb.getString("thSettings"));

        f.setBorder(blackline);
        f.setPreferredSize(new Dimension(FORM_WIDTH, 50));
        return f;
    }

  
    private void createCompressionSettings(Ventana ventana) {
        javax.swing.border.Border blackline = BorderFactory.createTitledBorder(rb.getString("qualityResolution"));
        JPanel f = new JPanel(new GridLayout(1, 2));
        f.setBorder(blackline);
        f.setPreferredSize(new Dimension(FORM_WIDTH, 60));
        f.add(createZoom(ventana));

        f.add(createQuality(ventana));

        add(f);

    }
    

    private void createMinOptions(Ventana ventana) {
        
       
        javax.swing.border.Border blackline = BorderFactory.createTitledBorder(rb.getString("qualityResolutionMin"));
        JPanel f = new JPanel(new GridLayout(2, 2));
        f.setBorder(blackline);
        f.setPreferredSize(new Dimension(FORM_WIDTH, 120));
        f.add(createZoomMin(ventana));
        f.add(createQualityMin(ventana));
        
        //JPanel dimPanel = new JPanel(new GridLayout(1, 2));
        f.add(createTypeDim(ventana));
        f.add(createDim());
        //f.add(dimPanel);
        
        add(f);
        
        
        
        

    }

     private JPanel createTypeDim(Ventana ventana) {
        typeDimMin = new JComboBox(new String[]{Constants.WIDTH, Constants.HEIGHT});
        typeDimMin.setPreferredSize(new Dimension(150, 30));
        typeDimMin.setSelectedIndex(0);
        typeDimMin.addActionListener(ventana);

        JPanel panelType = new JPanel(new GridLayout(2, 1));

        JLabel cLabel = new JLabel();
        cLabel.setText(rb.getString("fixDimension"));

        panelType.add(cLabel);
        panelType.add(typeDimMin);

        JPanel f = new JPanel(new FlowLayout(FlowLayout.LEFT));
        f.add(panelType);
        
        return f;
    }
     
    private JPanel createDim() {
   
        dimMin = new JTextField();

        JPanel panel = new JPanel(new GridLayout(2, 1));
        dimMin = new JTextField();
        dimMin.setPreferredSize(new Dimension(5, 30));
        dimMin.setDocument(new JTextFieldLimit(5));
        dimMin.setText(Constants.DEFAULT_DIMMIN);

        JLabel cLabel = new JLabel();
        cLabel.setText(rb.getString("dimension"));
        panel.add(cLabel);
        panel.add(dimMin);
        
        JPanel f = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        f.add(panel);
        
        
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
        replaceStrategy.setEnabled(true);
        dimMin.setEnabled(true);
        typeDimMin.setEnabled(true);
        qualityMin.setEnabled(true);
        zoomMin.setEnabled(true);
    }

    public void disableFields() {
        launchButton.setEnabled(false);
        launchButton.setVisible(false);
        cancelButton.setVisible(true);
        cancelButton.setEnabled(true);
        pdfButton.setEnabled(false);
        zoom.setEnabled(false);
        quality.setEnabled(false);
        replaceStrategy.setEnabled(false);
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
        
        dimMin.setEnabled(false);
        qualityMin.setEnabled(false);
        zoomMin.setEnabled(false);
        typeDimMin.setEnabled(false);
    }

}
