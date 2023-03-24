/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.xproject.resizer.base;

import es.xproject.resizer.App;
import es.xproject.resizer.igu.Ventana;

/**
 *
 * @author jsolis
 */
public class ReplaceStrategyType extends BaseType<ReplaceStrategyType>{
    
    public static ReplaceStrategyType REMAIN;
    public static ReplaceStrategyType OVERWRITE;
    
     static {
        REMAIN = new ReplaceStrategyType("REMAIN");
        OVERWRITE = new ReplaceStrategyType("OVERWRITE");
    }

    public ReplaceStrategyType(String value) {
        super(value);
    }
    
    @Override
    public String toString() {
       return App.rb.getString("replaceStrategyType." + value());
    }

    public boolean isRemain() {
        return REMAIN.equals(this);
    }
    
     public boolean isOverwrite() {
        return OVERWRITE.equals(this);
    }
    
}
