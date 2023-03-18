/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.xproject.resizer.panels;

import es.xproject.resizer.Ventana;
import javax.swing.JProgressBar;

/**
 *
 * @author jsolis
 */
public class Mask extends JProgressBar {

    public Mask(Ventana ventana) {

        setBounds(102, 40, 150, 16);
        setStringPainted(true);
        setVisible(false);

    }

    public void showMask(int max) {
        setMaximum(max);
        setValue(0);
        setVisible(true);

    }
    
    public void hideMask() {
        setValue(0);
        setVisible(false);

    }

    public void increment(int i) {
        setValue(getValue() + i);
    }

}
