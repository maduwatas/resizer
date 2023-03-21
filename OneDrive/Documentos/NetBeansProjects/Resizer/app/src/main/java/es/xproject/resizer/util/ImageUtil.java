/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.xproject.resizer.util;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import org.apache.logging.log4j.LogManager;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 *
 * @author jsolis
 */
public class ImageUtil {

    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger();
    
    public static IIOMetadata readAndDisplayMetadata(File file) {
        IIOMetadata metadata = null;
        try {

            ImageInputStream iis = ImageIO.createImageInputStream(file);
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);

            if (readers.hasNext()) {

                // pick the first available ImageReader
                ImageReader reader = readers.next();

                // attach source to the reader
                reader.setInput(iis, true);

                // read metadata of first image
                metadata = reader.getImageMetadata(0);

                String[] names = metadata.getMetadataFormatNames();
                int length = names.length;
                for (int i = 0; i < length; i++) {
                    log.debug("Format name: " + names[i]);
                    displayMetadata(metadata.getAsTree(names[i]));
                }

            }

        } catch (IOException e) {

            log.debug("error reading metadata " + file.getName());
        }

        return metadata;
    }

    private static void displayMetadata(Node root) {
        displayMetadata(root, 0);
    }

    private static void indent(int level) {
        for (int i = 0; i < level; i++) {
            System.out.print("    ");
        }
    }

    private static void displayMetadata(Node node, int level) {
        // print open tag of element
        indent(level);
        System.out.print("<" + node.getNodeName());
        NamedNodeMap map = node.getAttributes();
        if (map != null) {

            // print attribute values
            int length = map.getLength();
            for (int i = 0; i < length; i++) {
                Node attr = map.item(i);
                System.out.print(" " + attr.getNodeName()
                        + "=\"" + attr.getNodeValue() + "\"");
            }
        }

        Node child = node.getFirstChild();
        if (child == null) {
            // no children, so close element and return
            log.debug("/>");
            return;
        }

        // children, so close current tag
        log.debug(">");
        while (child != null) {
            // print children recursively
            displayMetadata(child, level + 1);
            child = child.getNextSibling();
        }

        // print close tag of element
        indent(level);
        log.debug("</" + node.getNodeName() + ">");
    }
}
