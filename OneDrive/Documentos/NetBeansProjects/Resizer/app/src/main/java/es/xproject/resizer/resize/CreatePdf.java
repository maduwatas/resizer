/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.xproject.resizer.resize;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import static es.xproject.resizer.App.rb;
import es.xproject.resizer.igu.Ventana;
import es.xproject.resizer.util.FileList;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import javax.imageio.ImageIO;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author jsolis
 */
public class CreatePdf implements Runnable {

    public static final String FORMAT_PNG = "png";
    public static final String FORMAT_PDF = "pdf";
    public static final String FORMAT_JPG = "jpg";
    public static final String FORMAT_JPEG = "jpeg";
    public static final String FORMAT_TIFF = "tiff";
    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger();
    private final File dir, destination;

    public CreatePdf(File dir, File destination) {
        this.dir = dir;
        this.destination = destination;
    }

    @Override
    public void run() {

        FileList fileList = new FileList().build(dir, false);

        try {
            Ventana.traceProcessed(rb.getString("initProcess") + " " + dir.getName());

            log.debug("CreatePdf file in dir " + dir.getAbsolutePath().replace(FORMAT_PNG, FORMAT_PNG));

            log.trace("ScalePdf  " + Ventana.scalePdf);

            String destinationFileName = dir.getAbsolutePath() + ".pdf";

            if (fileList.hasFiles()) {

                String name = fileList.files.get(0).getName();
                int i = name.lastIndexOf(Ventana.separator);

                if (i < 0) {
                    i = FilenameUtils.removeExtension(name).length();
                }

                destinationFileName = name.substring(0, i) + ".pdf";

                File pdfFile = new File(destination + File.separator + destinationFileName);

                if (Ventana.replaceStrategy.isRemain()) {

                    if (pdfFile.exists()) {
                        Ventana.skipPdfCount.getAndIncrement();
                        Ventana.traceProcessed(Ventana.replaceStrategy + ": " + pdfFile.getName());
                        return;
                    }
                }

                Document document = new Document();
                FileOutputStream fos = new FileOutputStream(pdfFile);
                PdfWriter writer = PdfWriter.getInstance(document, fos);
                writer.open();
                document.open();
                document.setPageCount(fileList.files.size());

                for (File imageFile : fileList.files) {

                    Rectangle frame;
                    Image image1;
                    if (Ventana.scalePdf && Ventana.scale < 1) {
                        java.awt.Image awtImage = ImageIO.read(new File(imageFile.getAbsolutePath()));

                        // TODO funciona, pero hay que preguntar por la escala
                        int scaledWidth = (int) (awtImage.getWidth(null) * Ventana.scale);
                        int scaledHeight = (int) (awtImage.getHeight(null) * Ventana.scale);

                        BufferedImage scaledAwtImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB);
                        Graphics2D g = scaledAwtImage.createGraphics();
                        g.drawImage(awtImage, 0, 0, scaledWidth, scaledHeight, null);
                        g.dispose();

                        ByteArrayOutputStream bout = new ByteArrayOutputStream();
                        ImageIO.write(scaledAwtImage, FORMAT_JPEG, bout);
                        byte[] imageBytes = bout.toByteArray();

                        image1 = Image.getInstance(imageBytes);

                        frame = new Rectangle(image1.getWidth(), image1.getHeight());
                    } else {
                        image1 = Image.getInstance(imageFile.getAbsolutePath());
                        frame = new Rectangle(image1.getWidth(), image1.getHeight());
                    }
                    document.setPageSize(frame);
                    document.setMargins(0, 0, 0, 0);
                    document.newPage();
                    document.add(image1);

                    log.trace("adding page " + imageFile.getName());

                }

                document.close();
                writer.close();
            }
            Ventana.mask.increment(1);
            Ventana.pdfCount.incrementAndGet();
            Ventana.traceProcessed(dir + File.separator + destinationFileName);
            log.trace("pdf created  " + dir + File.separator + destinationFileName);
            // debo controlar el outOfMemmory
        } catch (Throwable ex) {

            log.error("Resize file exception " + ex);
            Ventana.traceProcessed("Error processing file " + dir + File.separator + dir.getAbsolutePath());

        }
    }

}
