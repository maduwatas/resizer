/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.xproject.resizer.panels;

import es.xproject.resizer.Ventana;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author jsolis
 */
public class FilesPanel extends JPanel {

    public JButton sourceButton, destinationButton, imagesButton;

    public JTextField destinationButtonText, sourceButtonText, imagesButtonText;

    //  public JPanel form;
    public FilesPanel(Ventana ventana) {
        super();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(new JLabel("Directorios"));

        createButtons(ventana);

    }

    private void buttons(Ventana ventana) {
        sourceButton = new JButton("Directorio origen");
        sourceButton.addActionListener(ventana);
        sourceButton.setPreferredSize(new Dimension(150,30));
        //Create the save button.  We use the image from the JLF
        //Graphics Repository (but we extracted it from the jar).
        destinationButton = new JButton("Directorio destino");
        destinationButton.addActionListener(ventana);
        destinationButton.setPreferredSize(new Dimension(150,30));
        imagesButton = new JButton("Imágenes");
        imagesButton.addActionListener(ventana);
        imagesButton.setPreferredSize(new Dimension(150,30));
    }

    private void createButtons(Ventana ventana) {

        JPanel buttons = new JPanel(new GridLayout(3, 2));

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        buttons(ventana);
        panel.add(sourceButton);
        
        sourceButtonText = new TextReadonly("Selecciona origen");
        
        panel.add(sourceButtonText);
        sourceButtonText.setEditable(false);
        buttons.add(panel);

        panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        panel.add(destinationButton);
        destinationButtonText = new TextReadonly("Selecciona directorio destino");
        panel.add(destinationButtonText);

        buttons.add(panel);

        panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(imagesButton);
        imagesButtonText = new TextReadonly("Imágenes preprocesadas (opcional)");

        panel.add(imagesButtonText);
        buttons.add(panel);

        add(buttons);

    }

    private static class TextReadonly extends JTextField {

        public TextReadonly(String text) {
            setText(text);
            setPreferredSize(new Dimension(450, 30));
            setEditable(false);
        }

    }

    public void enableFields() {
        sourceButton.setEnabled(true);
        destinationButton.setEnabled(true);
        imagesButton.setEnabled(true);
    }

    public void disableFields() {
        sourceButton.setEnabled(false);
        destinationButton.setEnabled(false);
        imagesButton.setEnabled(false);
    }

}
