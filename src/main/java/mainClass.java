import Models.HTMLobject;
import org.apache.pdfbox.pdmodel.PDDocument;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MainClass {
    public static void main(String[] args) {
        try {
            FileReader reader=new FileReader("config.properties");
            Properties p=new Properties();
            p.load(reader);
            String inputPDFpath = p.getProperty("inputPDFpath");
            PDDocument pdf = PDDocument.load(new File(inputPDFpath));

            ImageExtractor.extractImages(pdf);

            //generates HTMLString
            String htmlString = HTMLformatter.generateHTMLFromPDF(pdf);

            //generates List of HTML objects.
            List<HTMLobject> htmlObjectList = HTMLformatter.parseHTML(htmlString);

            //concats characters of same line
            List<HTMLobject> formattedHTMLList = HTMLformatter.formatHTMLList(htmlObjectList);
            htmlObjectList.clear();

            //removes tables from text
            List<HTMLobject> textList = removeTable(formattedHTMLList);
            formattedHTMLList.clear();

            for(int i=0;i<textList.size();i++)
            {
                System.out.println(textList.get(i).getValue());
            }

        } catch (ParserConfigurationException | IOException e) {
            e.printStackTrace();
        }


    }

    public static List<HTMLobject> removeTable(List<HTMLobject> formattedHTMLList) {
        List<HTMLobject> textList = new ArrayList<>();
        boolean flag = false;
        for (HTMLobject htmLobject : formattedHTMLList) {
            if (htmLobject.getValue().equals("Sl."))
                flag = true;
            else if (htmLobject.getValue().equals("TOTAL:"))
                flag = false;
            if (!flag) {
                textList.add(htmLobject);
            }
        }
        return textList;
    }
}
