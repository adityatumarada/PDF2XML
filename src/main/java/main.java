import org.apache.pdfbox.pdmodel.PDDocument;
import org.fit.pdfdom.PDFDomTree;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

public class main {
    public static void main(String[] args) {
        String inputPDFpath = "/Users/aditya/Documents/Projects/PDFtoXML-me/Invoice.pdf";
        try {
            String htmlString = generateHTMLFromPDF(inputPDFpath);
            parseHTML(htmlString);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

    }

    public static String generateHTMLFromPDF(String filename) throws IOException, ParserConfigurationException {
        PDDocument pdf = PDDocument.load(new File(filename));
        Writer output = new StringWriter();
        new PDFDomTree().writeText(pdf,output);
        output.close();
        pdf.close();
        return output.toString();
    }

    public static void parseHTML(String htmlString) {
        Document doc = Jsoup.parse(htmlString);
        Elements divs = doc.getElementsByClass("p");
        for(Element div:divs)
        {
            System.out.println(div);
        }
    }
}
