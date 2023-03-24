/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.xproject.resizer.igu.panels;

import static es.xproject.resizer.App.rb;
import es.xproject.resizer.igu.Ventana;
import es.xproject.resizer.components.TextReadonly;
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

        add(new JLabel(rb.getString("dirs")));

        createButtons(ventana);

    }

    private void buttons(Ventana ventana) {
        sourceButton = new JButton(rb.getString("srcDir"));
        sourceButton.addActionListener(ventana);
        sourceButton.setPreferredSize(new Dimension(140,30));
        //Create the save button.  We use the image from the JLF
        //Graphics Repository (but we extracted it from the jar).
        destinationButton = new JButton(rb.getString("destinationDir"));
        destinationButton.addActionListener(ventana);
        destinationButton.setPreferredSize(new Dimension(140,30));
        imagesButton = new JButton(rb.getString("imgDir"));
        imagesButton.addActionListener(ventana);
        imagesButton.setPreferredSize(new Dimension(140,30));
    }

    private void createButtons(Ventana ventana) {

        JPanel buttons = new JPanel(new GridLayout(3, 2));

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        buttons(ventana);
        panel.add(sourceButton);
        
        sourceButtonText = new TextReadonly(rb.getString("selectSrc"));
        
        panel.add(sourceButtonText);
        sourceButtonText.setEditable(false);
        buttons.add(panel);

        panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        panel.add(destinationButton);
        destinationButtonText = new TextReadonly(rb.getString("selectDestination"));
        panel.add(destinationButtonText);

        buttons.add(panel);

        panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(imagesButton);
        imagesButtonText = new TextReadonly(rb.getString("selectImg"));

        panel.add(imagesButtonText);
        buttons.add(panel);

        add(buttons);

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
