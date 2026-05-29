package service;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.File;

public class PDFReader {

    public static String readPDF(String path) {
        try {
            PDDocument doc = Loader.loadPDF(new File(path));
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(doc);
            doc.close();
            return text;
        } catch (Exception e) {
            System.out.println("PDF Error: " + e);
            return "";
        }
    }
}
