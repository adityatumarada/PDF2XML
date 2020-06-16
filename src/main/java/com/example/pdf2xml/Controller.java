package com.example.pdf2xml;

/**
 * Author : Jui, Aditya, Eshita
 */

import com.example.pdf2xml.models.Details;
import com.example.pdf2xml.models.HTMLobject;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    //Variables
    private String pdfPath = "";
    private String xmlPath = "";
    private  String folderPath="";
    @FXML
    private Label PDFPath;
    @FXML
    private Label XMLPath;
     //Used to choose pdf
    @FXML
    public void handleChoosePDF() throws IOException{
       choosePDF();
    }

    //Used to choose xml
    @FXML
    private void handleChooseXML() throws IOException{
        chooseXML();
    }

    //Used for conversion for button convert
    @FXML
    private void handleConvert() throws IOException {
        convert();
    }

    //Selects pdffile path using filechooser and stores it
    public void choosePDF() {
        FileChooser fileChooser=new FileChooser();
        fileChooser.setTitle(Constant.OPENPDF);
        File file=fileChooser.showOpenDialog(null);
        pdfPath=file.getAbsolutePath();
        System.out.println(pdfPath);
        pdfPath.replace("\\", "/");
        PDFPath.setText(PDFPath.getText()+" "+pdfPath);
    }

    // Selects folderpath using directorychooser and stores it
    public void chooseXML(){
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(Constant.OPENXMLFOLDER);
        File selectedDirectory = chooser.showDialog(null);
        folderPath=selectedDirectory.getAbsolutePath();
        folderPath.replace("\\", "/");
        XMLPath.setText(XMLPath.getText()+" "+folderPath);
    }

    //Converts pdf to xml
    public void convert(){
        try {
            
            System.out.println("Converting...");

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Please don't close the main window, this will take a while!");
            alert.setWidth(50);
            alert.setHeight(50);
            alert.showAndWait();

            Text2XML text2XML=new Text2XML();
            
            PDDocument pdf = PDDocument.load(new File(pdfPath));

            //extracting tables from pdf
            Details[] tableDetails = PDFTableStripper.getDetails(pdf);

            //generates HTMLString
            String htmlString = HTMLformatter.generateHTMLFromPDF(pdf);

            //generates List of HTML objects.
            ArrayList<List<HTMLobject>> htmlObjectList = HTMLformatter.parseHTML(htmlString);

            //remove tables
            for(Details table : tableDetails)
            {
                int pgNo = table.getPageNo();
                List<HTMLobject> textList = removeTable(htmlObjectList.get(pgNo),table.getTableAllPoints());
                htmlObjectList.set(pgNo,textList);
            }

            //converts tables to XML
            List<String> XMLtable = Table2XML.convertToXML(tableDetails);

            String xmlPath = getXMLPath(pdfPath,folderPath);

            //use XMLtable and htmlObjectList for text2XML
            text2XML.XMLGenerationCombined(htmlObjectList,xmlPath,XMLtable);

            //extracting images
            ImageExtractor.extractImages(pdf,folderPath);

            Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
            alert2.setHeaderText("The extraction is complete");
            alert2.setWidth(50);
            alert2.setHeight(50);
            alert2.showAndWait();

            //change status (Eshita add popup here)

        } catch (IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }


    }

    private String getXMLPath(String pdfPath,String folderPath) {

        String[] path = pdfPath.split("/|\\\\");
        String pdfName = path[path.length-1];
        return folderPath+"/"+pdfName.substring(0,pdfName.length()-4)+Constant.XMLFILEEXTENSION;
    }

    //Removes table from data to be processed to give text
    private static List<HTMLobject> removeTable(List<HTMLobject> formattedHTMLList, double[] tableCoodinates) {
        List<HTMLobject> textList = new ArrayList<>();

        for (HTMLobject htmLobject : formattedHTMLList) {
            if(!istable(htmLobject,tableCoodinates))
            {
                textList.add(htmLobject);
            }
        }
        return textList;
    }

    //checks if data is present in table or not
    private static boolean istable(HTMLobject htmLobject, double[] coodinates) {

        double top = htmLobject.getTop();
        double left = htmLobject.getLeft();

        if( top>coodinates[0] && top<coodinates[1] && left>coodinates[2] && left<coodinates[3])
            return true;


        return false;
    }

}
