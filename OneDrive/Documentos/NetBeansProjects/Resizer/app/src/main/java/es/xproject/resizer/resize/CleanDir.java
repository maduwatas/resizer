/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.xproject.resizer.resize;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import es.xproject.resizer.igu.Ventana;
import es.xproject.resizer.components.Mask;
import es.xproject.resizer.util.FileList;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author jsolis
 */
public class CleanDir implements Runnable {

    public static final String FORMAT_PNG = "png";
    public static final String FORMAT_PDF = "pdf";
    public static final String FORMAT_JPG = "jpg";
    public static final String FORMAT_TIFF = "tiff";
    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger();
    private final File dir;

    private final Mask mask;

    public CleanDir(File dir, Mask mask) {
        this.dir = dir;
        this.mask = mask;
    }

    @Override
    public void run() {

        String destinationFileName = dir.getName() + ".pdf";
        FileList fileList = new FileList().build(dir, false);

        try {
            log.debug("CreatePdf file in dir " + dir.getAbsolutePath());

            File pdfFile = new File(dir + File.separator + destinationFileName);
            Document document = new Document();
            FileOutputStream fos = new FileOutputStream(pdfFile);
            PdfWriter writer = PdfWriter.getInstance(document, fos);
            writer.open();
            document.open();
            document.setPageCount(fileList.files.size());

            for (File imageFile : fileList.files) {
                Image image1 = Image.getInstance(imageFile.getAbsolutePath());
                image1.setAbsolutePosition((PageSize.A4.getWidth() - image1.getScaledWidth()) / 2, (PageSize.A4.getHeight() - image1.getScaledHeight()) / 2);

                document.newPage();
                document.add(image1);

            }

            document.close();
            writer.close();
            mask.increment(1);

            Ventana.traceProcessed(dir + File.separator + destinationFileName);

       

        } catch (DocumentException | IOException e) {
            log.error("Resize file exception " + e);
            Ventana.traceProcessed("Error processing file " + dir + File.separator + destinationFileName);
        } finally {
            log.debug("Clean image files in dir " + dir.getAbsolutePath());

            for (File imageFile : fileList.files) {
                imageFile.delete();
            }

        }
    }

}
