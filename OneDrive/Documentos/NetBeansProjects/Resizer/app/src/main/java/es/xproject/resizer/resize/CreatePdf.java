/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.xproject.resizer.resize;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import es.xproject.resizer.Ventana;
import es.xproject.resizer.util.FileList;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.commons.io.FilenameUtils;

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

    private final File dir;

    public CreatePdf(File dir) {
        this.dir = dir;
    }

    @Override
    public void run() {

        FileList fileList = new FileList().build(dir, false);

        try {
            Ventana.processed.append("Inicando proceso para para " + dir.getName() + Ventana.newline);
                
            System.out.println("CreatePdf file in dir " + dir.getAbsolutePath());
            System.out.println("ScalePdf  " + Ventana.scalePdf);

            String destinationFileName = dir.getAbsolutePath() + ".pdf";
            

            if (fileList.hasFiles()) {

                String name = fileList.files.get(0).getName();
                int i = name.lastIndexOf(Ventana.separator);

                if (i < 0) {
                    i = FilenameUtils.removeExtension(name).length();
                }

                destinationFileName = name.substring(0, i) + ".pdf";

                File pdfFile = new File(dir + File.separator + destinationFileName);
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

                        frame = new Rectangle(image1.getWidth() + 10, image1.getHeight() + 10);
                    } else {
                        image1 = Image.getInstance(imageFile.getAbsolutePath());
                        frame = new Rectangle(image1.getWidth() + 10, image1.getHeight() + 10);
                    }
                    document.setPageSize(frame);
                    document.setMargins(2, 2, 2, 2);
                    document.newPage();
                    document.add(image1);

                    System.out.println("adding page " + imageFile.getName());

                }

                document.close();
                writer.close();
            }
            Ventana.mask.increment(1);

            Ventana.traceProcessed(dir + File.separator + destinationFileName);

        } catch (DocumentException | IOException e) {
            System.out.println("Resize file exception " + e);
            Ventana.traceProcessed("Error processing file " + dir + File.separator + dir.getAbsolutePath());
            Logger.getLogger(CreatePdf.class.getName()).log(Level.SEVERE, null, e);
        }
    }

}
