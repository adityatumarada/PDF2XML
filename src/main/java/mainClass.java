import Models.HTMLobject;
import org.apache.pdfbox.pdmodel.PDDocument;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class mainClass {
    public static void main(String[] args) {
        try {
            FileReader reader=new FileReader("config.properties");
            Properties p=new Properties();
            p.load(reader);
            String inputPDFpath = p.getProperty("inputPDFpath");
            PDDocument pdf = PDDocument.load(new File(inputPDFpath));

            //generates HTMLString
            String htmlString = HTMLformatter.generateHTMLFromPDF(pdf);

            //generates List of HTML objects.
            List<HTMLobject> htmlObjectList = HTMLformatter.parseHTML(htmlString);

            //concats characters of same line
            List<HTMLobject> formattedHTMLList = HTMLformatter.formatHTMLList(htmlObjectList);

            //remove table content- to be completed by me
            //List<HTMLobject>

            // call jui function

            // call eshita functiom
            // convert table to XML

            //concat

        } catch (ParserConfigurationException | IOException e) {
            e.printStackTrace();
        }


    }
}
