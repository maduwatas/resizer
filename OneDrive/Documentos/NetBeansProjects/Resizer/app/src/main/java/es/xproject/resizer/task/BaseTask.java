/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.xproject.resizer.task;

import es.xproject.resizer.Ventana;

/**
 *
 * @author jsolis
 */
abstract class BaseTask {
    protected final Ventana ventana;
    
    public BaseTask(Ventana ventana) {
        this.ventana = ventana;
    }
    
    public abstract void execute();
    
    protected void lockButtons() {
        ventana.filesPanel.disableFields();
        ventana.uPanel.disableFields();

    }

    protected void unlockButtons() {
        ventana.filesPanel.enableFields();
        ventana.uPanel.enableFields();
    }
    

}
