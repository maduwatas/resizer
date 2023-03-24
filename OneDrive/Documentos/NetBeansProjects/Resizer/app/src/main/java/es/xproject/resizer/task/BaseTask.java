/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.xproject.resizer.task;

import es.xproject.resizer.igu.Ventana;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author jsolis
 */
abstract class BaseTask {

    

    private static final Logger log = LogManager.getLogger();
    
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

    protected void traceHeap() {
        // Get current size of heap in bytes
        long heapSize = Runtime.getRuntime().totalMemory();

        // Get maximum size of heap in bytes. The heap cannot grow beyond this size.// Any attempt will result in an OutOfMemoryException.
        long heapMaxSize = Runtime.getRuntime().maxMemory();

        // Get amount of free memory within the heap in bytes. This size will increase // after garbage collection and decrease as new objects are created.
        long heapFreeSize = Runtime.getRuntime().freeMemory();
        
        log.info("############ Memmory ussagge ############");
        log.info("heapSize.......... " + heapSize / (1024*1024) + " MB");
        log.info("heapMaxSize....... " + heapMaxSize / (1024*1024) + " MB");
        log.info("heapFreeSize...... " + heapFreeSize / (1024*1024) + " MB");
    }

}
