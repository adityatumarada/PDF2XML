import Models.HTMLobject;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


class Text2XML {

    // joining the relevant fields together which are separated on different lines
    public static List<HTMLobject> joinHTMLObjectList(List<HTMLobject> textList) {

        List<HTMLobject> shrinkedList = new ArrayList<>();
        shrinkedList.add(textList.get(0));
        for (int i = 1; i < textList.size(); i++) {
            /*checking some font parameters & distance between two lines
               If the font is equal and they are not distant then will get appended
               Else if font is equal but they are distant then will not get appended
               and if fonts are different will be considered as different valued components*/
            boolean isFontequal = checkFont(textList.get(i - 1), textList.get(i));
            if (isFontequal) {
                boolean appendable = checkDistanceBetween(textList.get(i - 1), textList.get(i));
                if (appendable) {
                    HTMLobject object = shrinkedList.get(shrinkedList.size() - 1);
                    object.setValue(object.getValue() + " " + textList.get(i).getValue());
                } else {
                    shrinkedList.add(textList.get(i));
                }
            } else {
                shrinkedList.add(textList.get(i));
            }
        }
        return shrinkedList;
    }

    //Generates key value pairs required for xml file generation
    public static void getKeyValuePairs(List<HTMLobject> shrinkedList) {
        //Initialization of array for keeping track of visited elements
        List<Integer> ProcessedElement = new ArrayList<>();
        for (int i = 0; i < shrinkedList.size(); i++) {
            ProcessedElement.add(-1);
        }
        /*
        Logic behind key value pair generation:
         If the element is not visited then process it as follows:
         1. If current element and next element are having same y coordinates & current element is bold then it will result into key value pair.
         2. Else if line spacing between current and next element is between 15 & 14.34
                 if font is same then -----key value
                 else key will be text-entry
         3. Else if line spacing is greater than 15 then key will be text-entry
         4.If line spacing is less than 14.34 then ------key value
         */
        for (int i = 0; i < shrinkedList.size() - 1; i++) {
            if (ProcessedElement.get(i) == -1) {
                if (shrinkedList.get(i).getTop() == shrinkedList.get(i + 1).getTop()) {
                    if (shrinkedList.get(i).getFont_weight().equals("bold")) {
                        Headings.add(shrinkedList.get(i).getValue());
                        textData.add(shrinkedList.get(i + 1).getValue());
                        ProcessedElement.set(i, 1);
                        ProcessedElement.set(i + 1, 1);
                    }
                } else if (Math.abs(shrinkedList.get(i).getTop() - shrinkedList.get(i + 1).getTop()) > 14.34
                        && Math.abs(shrinkedList.get(i).getTop() - shrinkedList.get(i + 1).getTop()) < 15.00) {
                    if (shrinkedList.get(i).getFont_weight().equals(shrinkedList.get(i + 1).getFont_weight())) {
                        Headings.add(shrinkedList.get(i).getValue());
                        textData.add(shrinkedList.get(i + 1).getValue());
                        ProcessedElement.set(i, 1);
                        ProcessedElement.set(i + 1, 1);
                    } else {
                        Headings.add("text-entry");
                        textData.add(shrinkedList.get(i).getValue());
                        ProcessedElement.set(i, 1);
                    }
                } else if (Math.abs(shrinkedList.get(i).getTop() - shrinkedList.get(i + 1).getTop()) > 15.00) {
                    Headings.add("text-entry");
                    textData.add(shrinkedList.get(i).getValue());
                    ProcessedElement.set(i, 1);
                } else if (Math.abs(shrinkedList.get(i).getTop() - shrinkedList.get(i + 1).getTop()) < 14.34) {
                    if (shrinkedList.get(i).getFont_weight().equals("bold")) {
                        Headings.add(shrinkedList.get(i).getValue());
                        textData.add(shrinkedList.get(i + 1).getValue());
                        ProcessedElement.set(i, 1);
                        ProcessedElement.set(i + 1, 1);
                    }
                }
            }
        }
    }

    //Generates XML file & stores it at xmlFilePath location
    public static void XMLGenerator(String xmlFilePath) {
        //Enables  to obtain DOM parser
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        //For obtaining document from XML
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = documentFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document document = documentBuilder.newDocument();
        //root elemnt creation
        Element root = document.createElement("non-tabular-data");
        document.appendChild(root);
        //Creates temp node with required values and attributes and appends them as a child to root
        for (int i = 0; i < Headings.size(); i++) {
            Headings.set(i, Headings.get(i).replace(":", "").trim());
            if (!Headings.get(i).equals("TOTAL")) {
                Element temp = document.createElement("text");
                //for attribute creation
                if (!Headings.get(i).equals("text-entry")) {
                    Attr attr = document.createAttribute("key");
                    attr.setValue(Headings.get(i));
                    temp.setAttributeNode(attr);
                }
                //gives entry value
                temp.appendChild(document.createTextNode(textData.get(i)));
                root.appendChild(temp);
            }
        }

        //Transform DOM document to XML
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(new File(xmlFilePath));
        try {
            transformer.transform(domSource, streamResult);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        //check statement
        System.out.println("Done creating XML File");
    }

    //main function which will call all the required functions & will be called in main
    public static void XMLGenerationCombined(List<HTMLobject> textList, String XMLPath,String tableString) {
        List<HTMLobject> shrinkedList = Text2XML.joinHTMLObjectList(textList);
        Text2XML.getKeyValuePairs(shrinkedList);
        XMLGenerator(XMLPath);
    }

    //This method checks the color, fontfamily, fontweight of Elements
    public static boolean checkFont(HTMLobject element1, HTMLobject element2) {
        if (element1.getColor().equals(element2.getColor()) & element1.getFont_family().equals(element2.getFont_family())
                & element1.getFont_weight() == element2.getFont_weight()) {
            return true;
        }
        return false;
    }

    //This method checks the distance between two elements: if less distance returns false else true
    public static boolean checkDistanceBetween(HTMLobject element1, HTMLobject element2) {
        if (Math.abs(element1.getTop() - element2.getTop()) > 14.34 ||
                (Math.abs(element1.getTop() - element2.getTop()) < 7 && (Math.abs(element1.getTop() - element2.getTop()) > 3))) {
            return false;
        }
        return true;
    }

    private static List<String> Headings = new ArrayList<>();
    private static List<String> textData = new ArrayList<>();
}
