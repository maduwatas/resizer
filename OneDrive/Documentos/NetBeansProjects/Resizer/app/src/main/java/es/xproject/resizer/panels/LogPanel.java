/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.xproject.resizer.panels;

import java.awt.BorderLayout;
import static java.awt.Component.CENTER_ALIGNMENT;
import java.awt.Insets;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author jsolis
 */
public class LogPanel extends JPanel {

    public LogPanel(JTextArea text, String title) {
        text.setMargin(new Insets(5, 5, 5, 5));
        text.setEditable(false);
        
        JPanel pForm = new JPanel();
        pForm.setLayout(new BorderLayout());
        JScrollPane logScrollPane = new JScrollPane(text);
        pForm.add(logScrollPane, BorderLayout.CENTER);
        JLabel pLabel = new JLabel();
        //nPanel.setLayout(new BoxLayout(nPanel, BoxLayout.Y_AXIS));
        pLabel.setText(title);
        pLabel.setAlignmentX(CENTER_ALIGNMENT);
        
        add(pLabel);
        add(pForm);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
    }
    
  
    
    
}
