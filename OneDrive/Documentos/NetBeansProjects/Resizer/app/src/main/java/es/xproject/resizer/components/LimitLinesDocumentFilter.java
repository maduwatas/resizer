/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.xproject.resizer.components;


import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.Element;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 *
 * @author jsolis
 */
public class LimitLinesDocumentFilter extends DocumentFilter {
   private int maxLineCount;

   private static final Logger log = LogManager.getLogger();
   
   public LimitLinesDocumentFilter(int maxLineCount) {
      this.maxLineCount = maxLineCount;
   }

   @Override
   public void insertString(FilterBypass fb, int offset, String string,
            AttributeSet attr) throws BadLocationException {
      super.insertString(fb, offset, string, attr);

      removeFromStart(fb);
   }

   @Override
   public void replace(FilterBypass fb, int offset, int length, String text,
            AttributeSet attrs) throws BadLocationException {
      super.replace(fb, offset, length, text, attrs);

      removeFromStart(fb);
   }

   private void removeFromStart(FilterBypass fb) {
      Document doc = fb.getDocument();
      Element root = doc.getDefaultRootElement();
      while (root.getElementCount() > maxLineCount) {
         removeLineFromStart(doc, root);
      }
   }

   private void removeLineFromStart(Document document, Element root) {
      Element line = root.getElement(0);
      int end = line.getEndOffset();

      try {
         document.remove(0, end);
      } catch (BadLocationException ble) {
         log.error("error remove end " + ble);
      }
   }

}