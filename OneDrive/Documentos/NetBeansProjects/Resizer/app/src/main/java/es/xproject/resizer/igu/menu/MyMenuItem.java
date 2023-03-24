/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.xproject.resizer.igu.menu;

import static es.xproject.resizer.App.rb;
import java.awt.Dimension;
import javax.swing.JMenuItem;

/**
 *
 * @author jsolis
 */
public class MyMenuItem extends JMenuItem {

    public MyMenuItem(String label) {
        super(rb.getString(label));
        Dimension menuD = getPreferredSize();
        if (menuD.getWidth() < 150) {
            menuD.setSize(150, menuD.getHeight());
        }
        setPreferredSize(menuD);
    }

}
