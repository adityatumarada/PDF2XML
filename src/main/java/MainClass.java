//Author: Aditya
import Models.Details;
import Models.HTMLobject;
import org.apache.pdfbox.pdmodel.PDDocument;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

class MainClass {
    public static void main(String[] args) {
        try {
            FileReader reader = new FileReader("config.properties");
            Properties p = new Properties();
            p.load(reader);
            String xmlPath = p.getProperty("xmlPath");
            String inputPDFpath = p.getProperty("inputPDFpath");
            PDDocument pdf = PDDocument.load(new File(inputPDFpath));

            Details tableDetails = PDFTableStripper.getDetails(pdf);
            List<String[][]> tables = tableDetails.getTables();
            List<double[]> tableCoodinates = tableDetails.getTableAllPoints();
            String XMLTable = Table2XML.convertToXML(tables);

            //generates HTMLString
            String htmlString = HTMLformatter.generateHTMLFromPDF(pdf);

            //generates List of HTML objects.
            List<HTMLobject> htmlObjectList = HTMLformatter.parseHTML(htmlString);

            //concats characters of same line
            List<HTMLobject> formattedHTMLList = HTMLformatter.formatHTMLList(htmlObjectList);
            htmlObjectList.clear();

            //removes tables from text
            List<HTMLobject> textList = removeTable(formattedHTMLList,tableCoodinates);
            formattedHTMLList.clear();

            //generates XML from unstructured data
            Text2XML.XMLGenerationCombined(textList, xmlPath, XMLTable);

        } catch (ParserConfigurationException | IOException e) {
            e.printStackTrace();
        }


    }

    //Removes table from data to give text
    private static List<HTMLobject> removeTable(List<HTMLobject> formattedHTMLList, List<double[]> tableCoodinates) {
        List<HTMLobject> textList = new ArrayList<>();

        for (HTMLobject htmLobject : formattedHTMLList) {
            if(!istable(htmLobject,tableCoodinates))
            {
                textList.add(htmLobject);
            }
        }
        return textList;
    }

    //checks if data is present in table
    private static boolean istable(HTMLobject htmLobject, List<double[]> tableCoodinates) {

        double top = htmLobject.getTop();
        double left = htmLobject.getLeft();
        for( double[] coodinates : tableCoodinates)
        {

            if( top>coodinates[0] && top<coodinates[2] && left>coodinates[1] && left<coodinates[3])
                return true;
        }

        return false;
    }
}