/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.xproject.resizer.resize;

import es.xproject.resizer.Ventana;
import com.twelvemonkeys.image.ResampleOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author jsolis
 */
public class Resize implements Runnable {

    public static final String FORMAT_PNG = "png";
    public static final String FORMAT_PDF = "pdf";
    public static final String FORMAT_JPG = "jpg";
    public static final String FORMAT_TIFF = "tiff";
    
    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger();
    
    public static String[] getSupportedAlgorithms() {
        String[] aStrings = new String[aMap.size()];
        int i = 0;
        for (Object key : Resize.aMap.keySet()) {
            aStrings[i++] = key.toString();
        }
        return aStrings;
    }

    private final File file;
    private final String sourcePath, destinationPath;

    private final String format;

    public static final HashMap aMap = new HashMap<String, Integer>();

    static {

        aMap.put("Gaussian", ResampleOp.FILTER_GAUSSIAN);

        aMap.put("Cubic", ResampleOp.FILTER_CUBIC);
        aMap.put("Blackman-Bessel", ResampleOp.FILTER_BLACKMAN_BESSEL);
        aMap.put("Box", ResampleOp.FILTER_BOX);
        aMap.put("Lanzos", ResampleOp.FILTER_LANCZOS);
        aMap.put("Quadratic", ResampleOp.FILTER_QUADRATIC);
    }

    public Resize(File file) {
        this.file = file;
        this.sourcePath = Ventana.sourceFile.getPath();
        this.destinationPath = Ventana.destinationFile.getPath();
        this.format = Ventana.fileType;

    }

    @Override
    public void run() {

        String filePath = file.getParent().replace(sourcePath, "");

        boolean pdf = FORMAT_PDF.equals(format);

        String typeFormat = pdf ? FORMAT_JPG : format;
        String destinationFileName = FilenameUtils.removeExtension(file.getName()) + "." + typeFormat;

        try {
            log.debug("Resize file " + file.getName() + " format " + typeFormat);

            BufferedImage bimg = ImageIO.read(file);

            int width = bimg.getWidth();
            int height = bimg.getHeight();

            width = width * Ventana.zoom / 400;
            height = height * Ventana.zoom / 400;

            int filter = ResampleOp.FILTER_GAUSSIAN;

            if (aMap.get(Ventana.algorithm) != null) {
                filter = (Integer) aMap.get(Ventana.algorithm);
            }

            log.debug("resample file " + file.getName() + width + "x" + height + " filter " + filter);

            BufferedImageOp resampler = new ResampleOp(width, height, filter); // A good default filter, see class documentation for more info

            BufferedImage output = resampler.filter(bimg, null);

            File dir = new File(destinationPath + File.separator + filePath);

            if (!dir.exists()) {
                log.debug("dir " + dir.getAbsolutePath() + " mkdir");
                dir.mkdir();
            }

            File outputfile = new File(destinationPath + File.separator + filePath + File.separator + destinationFileName);

            write(output, typeFormat, outputfile);
            log.debug("output " + outputfile.getName() + " created");
            Ventana.imageCount.getAndIncrement();
            Ventana.mask.increment(1);

            Ventana.traceProcessed(destinationPath + File.separator + destinationFileName);

        } catch (IOException e) {
            Ventana.errorCount.getAndIncrement();
            log.debug("Resize file exception " + e);
            Ventana.traceProcessed("Error processing file " + destinationPath + File.separator + destinationFileName);
            Logger.getLogger(Resize.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void setDPI(String metadataFormat, IIOMetadata metadata) {
        try {
            log.debug("setDPI");

            IIOMetadataNode root = new IIOMetadataNode(metadataFormat);
            IIOMetadataNode jpegVariety = new IIOMetadataNode("JPEGvariety");
            IIOMetadataNode markerSequence = new IIOMetadataNode("markerSequence");

            IIOMetadataNode app0JFIF = new IIOMetadataNode("app0JFIF");
            app0JFIF.setAttribute("majorVersion", "1");
            app0JFIF.setAttribute("minorVersion", "2");
            app0JFIF.setAttribute("thumbWidth", "0");
            app0JFIF.setAttribute("thumbHeight", "0");
            app0JFIF.setAttribute("resUnits", "1");
            app0JFIF.setAttribute("Xdensity", String.valueOf(Ventana.zoom));
            app0JFIF.setAttribute("Ydensity", String.valueOf(Ventana.zoom));

            root.appendChild(jpegVariety);
            root.appendChild(markerSequence);
            jpegVariety.appendChild(app0JFIF);

            metadata.mergeTree(metadataFormat, root);

            log.debug("setDPI done");
        } catch (Exception e) {
            log.debug("error change metadata: " + e);
        }
    }

    private void write(BufferedImage image, String typeFormat, File outputfile) throws IOException {
        // Get the writer
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(typeFormat);

        if (!writers.hasNext()) {
            throw new IllegalArgumentException("No writer for: " + typeFormat);
        }

        ImageWriter writer = writers.next();

        try {
            // Create output stream (in try-with-resource block to avoid leaks)
            try (ImageOutputStream output = ImageIO.createImageOutputStream(outputfile)) {
                writer.setOutput(output);

                // Optionally, listen to progress, warnings, etc.
                ImageWriteParam iwp = writer.getDefaultWriteParam();

                iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);

                iwp.setCompressionQuality(Ventana.quality);

                ImageTypeSpecifier typeSpecifier
                        = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);
                IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, iwp);

                String[] names = metadata.getMetadataFormatNames();
                setDPI(names[0], metadata);

                log.debug("quality " + Ventana.quality);

                //IIOMetadata meta = readAndDisplayMetadata();
                IIOImage first_IIOImage = new IIOImage(image, null, metadata);

                // Optionally, control format specific settings of param (requires casting), or
                // control generic write settings like sub sampling, source region, output type etc.
                // Optionally, provide thumbnails and image/stream metadata
                writer.write(metadata, first_IIOImage, iwp);
                log.debug("done ");
            }
        } finally {
            // Dispose writer in finally block to avoid memory leaks
            writer.dispose();
        }
    }

}
