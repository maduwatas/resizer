/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.xproject.resizer.resize;

import es.xproject.resizer.igu.Ventana;
import com.twelvemonkeys.image.ResampleOp;
import es.xproject.resizer.base.Constants;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author jsolis
 */
public class Resize implements Callable<String> {

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
    private int filter;
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
    private String typeFormat;
    private String destinationFileName;

    public Resize(File file) {
        this.file = file;
        this.sourcePath = Ventana.sourceFile.getPath();
        this.destinationPath = Ventana.destinationFile.getPath();
        this.format = Ventana.fileType;

        filter = ResampleOp.FILTER_GAUSSIAN;

        if (aMap.get(Ventana.algorithm) != null) {
            filter = (Integer) aMap.get(Ventana.algorithm);
        }

    }

    private void setDPI(String metadataFormat, IIOMetadata metadata, int ppp) {
        try {
            log.trace("setDPI");

            IIOMetadataNode root = new IIOMetadataNode(metadataFormat);
            IIOMetadataNode jpegVariety = new IIOMetadataNode("JPEGvariety");
            IIOMetadataNode markerSequence = new IIOMetadataNode("markerSequence");

            IIOMetadataNode app0JFIF = new IIOMetadataNode("app0JFIF");
            app0JFIF.setAttribute("majorVersion", "1");
            app0JFIF.setAttribute("minorVersion", "2");
            app0JFIF.setAttribute("thumbWidth", "0");
            app0JFIF.setAttribute("thumbHeight", "0");
            app0JFIF.setAttribute("resUnits", "1");
            app0JFIF.setAttribute("Xdensity", String.valueOf(ppp));
            app0JFIF.setAttribute("Ydensity", String.valueOf(ppp));

            root.appendChild(jpegVariety);
            root.appendChild(markerSequence);
            jpegVariety.appendChild(app0JFIF);

            metadata.mergeTree(metadataFormat, root);

            log.trace("setDPI done");
        } catch (IIOInvalidTreeException e) {
            log.error("error change metadata: " + e);
        }
    }

    private void write(BufferedImage image, String typeFormat, File outputfile, float quality, int ppp) throws IOException {
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

                iwp.setCompressionQuality(quality);

                ImageTypeSpecifier typeSpecifier
                        = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);
                IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, iwp);

                String[] names = metadata.getMetadataFormatNames();
                setDPI(names[0], metadata, ppp);

                log.trace("quality " + quality);

                //IIOMetadata meta = readAndDisplayMetadata();
                IIOImage first_IIOImage = new IIOImage(image, null, metadata);

                // Optionally, control format specific settings of param (requires casting), or
                // control generic write settings like sub sampling, source region, output type etc.
                // Optionally, provide thumbnails and image/stream metadata
                writer.write(metadata, first_IIOImage, iwp);
                log.trace("done ");
            }
        } finally {
            // Dispose writer in finally block to avoid memory leaks
            writer.dispose();
        }
    }

    @Override
    public String call() throws Exception {
        String filePath = file.getParent().replace(sourcePath, "");

        boolean pdf = Constants.FORMAT_PDF.equals(format);

        typeFormat = pdf ? Constants.FORMAT_JPG : format;
        destinationFileName = FilenameUtils.removeExtension(file.getName()) + "." + typeFormat;

        try {
            
            if (Ventana.replaceStrategy.isRemain()) {
                File outputFile = new File(destinationPath + File.separator + Constants.JPG_DIR + File.separator + filePath + File.separator + destinationFileName);
                if (outputFile.exists()) {
                    log.debug("Existing file " + destinationFileName + " skip");
                    Ventana.skipCount.getAndIncrement();
                    Ventana.mask.increment(1);
                    Ventana.traceProcessed(Ventana.replaceStrategy + ": " + destinationPath + File.separator + destinationFileName);
                    return Constants.SUCCESS;    
                }
            }
            
            log.debug("Resize file " + file.getName() + " format " + typeFormat);

            BufferedImage bimg = ImageIO.read(file);

            log.trace("bimg read ");

            createJpg(bimg, filePath);

            if (Ventana.dimMin > 0) {
                createJpgMin(bimg, filePath);
            }

            Ventana.imageCount.getAndIncrement();
            Ventana.mask.increment(1);

            log.trace("done");

            Ventana.traceProcessed(destinationPath + File.separator + destinationFileName);
            return Constants.SUCCESS;

        } catch (Throwable e) {
            log.error("Resize file exception " + e);
            Ventana.errorCount.getAndIncrement();

            Ventana.traceProcessed("Error processing file " + destinationPath + File.separator + destinationFileName);
            Logger.getLogger(Resize.class.getName()).log(Level.SEVERE, null, e);
        }

        return Constants.ERROR;
    }

    private void createJpg(BufferedImage bimg, String filePath) throws IOException {

        log.trace("createJpg ");

        int width = bimg.getWidth();
        int height = bimg.getHeight();

        width = width * Ventana.zoom / 400;
        height = height * Ventana.zoom / 400;

        if (width > Constants.MAX_JPGDIM) {
            float factor = Constants.MAX_JPGDIM / width;
            width = (int) Constants.MAX_JPGDIM;
            height = (int) (height * factor);
        }
        if (height > Constants.MAX_JPGDIM) {
            float factor = Constants.MAX_JPGDIM / height;
            height = (int) Constants.MAX_JPGDIM;
            width = (int) (width * factor);
        }
        
        log.trace("size " + width + "x" + height);

        log.debug("resample file " + file.getName() + ": " + width + "x" + height + " filter " + filter);

        BufferedImageOp resampler = new ResampleOp(width, height, filter); // A good default filter, see class documentation for more info

        log.trace("resampled");

        BufferedImage output = resampler.filter(bimg, null);

        log.trace("output filtered");

        File dir = new File(destinationPath + File.separator + Constants.JPG_DIR + File.separator + filePath);

        File outputFile = new File(destinationPath + File.separator + Constants.JPG_DIR + File.separator + filePath + File.separator + destinationFileName);

        if (!dir.exists()) {
            log.debug("dir " + dir.getAbsolutePath() + " mkdir");
            dir.mkdir();
        }
        log.trace("output creation");

        log.trace("output write");
        write(output, typeFormat, outputFile, Ventana.quality, Ventana.zoom);
        log.debug("output " + outputFile.getName() + " created");
    }

    private void createJpgMin(BufferedImage bimg, String filePath) throws IOException {

        log.trace("createJpgMin ");

        int width = bimg.getWidth();
        int height = bimg.getHeight();

        float factor;

        if (Constants.WIDTH.equals(Ventana.typeDimMin)) {
            factor = Ventana.dimMin / (float) width;
            width = Ventana.dimMin;
            height = (int) (height * factor);
        } else {
            factor = Ventana.dimMin / (float) height;
            width = (int) (width * factor);
            height = Ventana.dimMin;
        }
        if (width > Constants.MAX_JPGDIM) {
            factor = Constants.MAX_JPGDIM / width;
            width = (int) Constants.MAX_JPGDIM;
            height = (int) (height * factor);
        }
        if (height > Constants.MAX_JPGDIM) {
            factor = Constants.MAX_JPGDIM / height;
            height = (int) Constants.MAX_JPGDIM;
            width = (int) (width * factor);
        }
        
        log.debug("resample MIN file " + file.getName() + width + "x" + height + " filter " + filter);

        BufferedImageOp resampler = new ResampleOp(width, height, filter); // A good default filter, see class documentation for more info

        log.trace("MIN resampled");

        BufferedImage output = resampler.filter(bimg, null);

        log.trace("output MIN filtered");

        File dir = new File(destinationPath + File.separator + Constants.JPGMIN_DIR + File.separator + filePath);

        File outputFile = new File(destinationPath + File.separator + Constants.JPGMIN_DIR + File.separator + filePath + File.separator + destinationFileName);

        if (!dir.exists()) {
            log.debug("dir " + dir.getAbsolutePath() + " mkdir");
            dir.mkdir();
        }
        log.trace("output MIN creation");

        log.trace("output MIN write");
        write(output, typeFormat, outputFile, Ventana.qualityMin, Ventana.zoomMin);
        log.debug("output MIN " + outputFile.getName() + " created");
    }

}
