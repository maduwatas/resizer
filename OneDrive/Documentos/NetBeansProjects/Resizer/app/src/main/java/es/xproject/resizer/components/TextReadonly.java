/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.xproject.resizer.components;

import java.awt.Dimension;
import javax.swing.JTextField;

/**
 *
 * @author jsolis
 */
public class TextReadonly extends JTextField {

    public TextReadonly(String text) {
        setText(text);
        setPreferredSize(new Dimension(450, 30));
        setEditable(false);
    }

}
