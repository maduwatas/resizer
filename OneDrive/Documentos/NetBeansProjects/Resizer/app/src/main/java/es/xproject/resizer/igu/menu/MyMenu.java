/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.xproject.resizer.igu.menu;

import static es.xproject.resizer.App.rb;
import java.awt.Dimension;
import javax.swing.JMenu;

/**
 *
 * @author jsolis
 */
public class MyMenu extends JMenu {

    public MyMenu(String label) {
        super(rb.getString(label));
        /*Dimension menuD = getPreferredSize();
        if (menuD.getWidth() < 50)
            menuD.setSize(100, menuD.getHeight());
        setPreferredSize(menuD);*/
    }

}
