package com.example.pdf2xml;

import com.example.pdf2xml.Models.HTMLobject;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

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
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author-Jui
 **/

class Text2XML {
    //variables
    private static List<String> headings = new ArrayList<>();
    private static List<String> textData = new ArrayList<>();
    private static Double lineSpace = 0.00;
    private static Map<Double, Integer> linespacing = new HashMap<Double, Integer>();
    private static List<Integer> processedElement = new ArrayList<>();
    private static String XMLString = "";
    private static Integer totalPages = 0;


    //determines line spacing between lines
    //Taking differences between two values of top and ignoring 0
    public static Double determineLineSpacing(List<HTMLobject> textList) {
        for (int index = 1; index < textList.size(); index++) {
            Double currLineSpacing = Math.abs(Double.valueOf(Math.round(((textList.get(index).getTop() - textList.get(index - 1).getTop()) * 100.00) / 100.00)));
            if (!linespacing.isEmpty()) {
                if (linespacing.containsKey(currLineSpacing)) {
                    linespacing.replace(currLineSpacing, linespacing.get(currLineSpacing), linespacing.get(currLineSpacing) + 1);
                } else {
                    linespacing.put(currLineSpacing, 1);
                }
            } else {
                linespacing.put(currLineSpacing, 1);
            }
        }
        linespacing.remove(0.0);
        Double determinedLineSpace = linespacing.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getKey();
        return determinedLineSpace;
    }

    //Join the respective blocks using font style and linespacing
    public static List<HTMLobject> joinHTMLObjectList(List<HTMLobject> textList) {

        List<HTMLobject> shrinkedList = new ArrayList<>();
        shrinkedList.add(textList.get(0));
        for (int index = 1; index < textList.size(); index++) {
            /*checking some font parameters &
             distance between two lines
             & check appendable or not and creating blocks of similar parts in pdf*/

            boolean appendable = checkDistanceBetween(textList.get(index - 1), textList.get(index));
            boolean isFontequal = check_Font(textList.get(index - 1), textList.get(index));
            if (appendable) {
                if (textList.get(index).getTop() > textList.get(index - 1).getTop()) {
                    if (isFontequal) {
                        HTMLobject object = shrinkedList.get(shrinkedList.size() - 1);
                        object.setValue(object.getValue() + " " + textList.get(index).getValue());
                    } else {
                        shrinkedList.add(textList.get(index));

                    }

                } else {
                    shrinkedList.add(textList.get(index));
                }
            } else {
                int flag = 0;
                if (textList.get(index).getTop() == textList.get(index - 1).getTop()) {
                    if (textList.get(index).getFont_weight().equals("bold")) {
                        if (Math.abs(textList.get(index).getLeft() - textList.get(index - 1).getLeft()) < 10) {
                            HTMLobject object = shrinkedList.get(shrinkedList.size() - 1);
                            object.setValue(object.getValue() + " " + textList.get(index).getValue());
                        } else {
                            shrinkedList.add(textList.get(index));
                            flag = 1;
                        }
                    }
                }
                if (flag == 0) {
                    shrinkedList.add(textList.get(index));
                }
            }
        }
        return shrinkedList;
    }

    //Creation of key value pair
    public static void getKeyValuePairs(List<HTMLobject> shrinkedList) {

        //Initialization of array for keeping track of visited elements

        for (int index = 0; index < shrinkedList.size(); index++) {
            processedElement.add(-1);
        }
        /*
        Logic behind key value pair generation:
         If the element is not visited then process it as follows:
         1. If current element and next element are having same y coordinates & current element is bold then it will result into key value pair.
         2. Else if line spacing between current and next element is between linespace+1 and linespace+2
                 if font is same then -----key value
                 else key will be text-entry
         3. Else if line spacing is greater than linespace+2then key will be text-entry
         4.If line spacing is less than linespace+1 then ------key value
         */
        for (int index = 0; index < shrinkedList.size() - 1; index++) {
            if (processedElement.get(index) == -1) {
                if (shrinkedList.get(index).getTop() == shrinkedList.get(index + 1).getTop()) {

                    if (shrinkedList.get(index).getFont_weight().equals("bold")) {
                        headings.add(shrinkedList.get(index).getValue());
                        textData.add(shrinkedList.get(index + 1).getValue());
                        processedElement.set(index, 1);
                        processedElement.set(index + 1, 1);

                    } else {
                        headings.add("text-entry");
                        textData.add(shrinkedList.get(index).getValue());
                        processedElement.set(index, 1);
                    }
                } else if (Math.abs(shrinkedList.get(index).getTop() - shrinkedList.get(index + 1).getTop()) >= lineSpace + 1
                        && Math.abs(shrinkedList.get(index).getTop() - shrinkedList.get(index + 1).getTop()) < lineSpace + 2) {

                    if (shrinkedList.get(index).getFont_weight().equals(shrinkedList.get(index + 1).getFont_weight())) {
                        headings.add(shrinkedList.get(index).getValue());
                        textData.add(shrinkedList.get(index + 1).getValue());
                        processedElement.set(index, 1);
                        processedElement.set(index + 1, 1);

                    } else {
                        headings.add("text-entry");
                        textData.add(shrinkedList.get(index).getValue());
                        processedElement.set(index, 1);

                    }
                } else if (Math.abs(shrinkedList.get(index).getTop() - shrinkedList.get(index + 1).getTop()) >= lineSpace + 2) {
                    headings.add("text-entry");
                    textData.add(shrinkedList.get(index).getValue());
                    processedElement.set(index, 1);

                } else if (Math.abs(shrinkedList.get(index).getTop() - shrinkedList.get(index + 1).getTop()) < lineSpace + 1) {

                    if (shrinkedList.get(index).getFont_weight().equals("bold")) {
                        headings.add(shrinkedList.get(index).getValue());
                        textData.add(shrinkedList.get(index + 1).getValue());
                        processedElement.set(index, 1);
                        processedElement.set(index + 1, 1);

                    } else {
                        headings.add(shrinkedList.get(index).getValue());
                        textData.add(shrinkedList.get(index + 1).getValue());
                        processedElement.set(index, 1);
                        processedElement.set(index + 1, 1);

                    }
                }
            }

        }
        if (processedElement.get(shrinkedList.size() - 1) == -1) {
            headings.add("text-entry");
            processedElement.set(shrinkedList.size() - 1, 1);
            textData.add(shrinkedList.get(shrinkedList.size() - 1).getValue());
        }

    }

    //Generates XML file & stores it at xmlFilePath location
  /*checks the page index
  If it is non zero then remove xml declaration
  else change > symbol with <page- pageno.>
   */
    public static void XMLGenerator(String xmlFilePath, String tableString, int Index) {
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
        for (int index = 0; index < headings.size(); index++) {
            headings.set(index, headings.get(index).replace(":", "").trim());

            Element toBeProcessed = document.createElement("text");
            //for attribute creation
            if (!headings.get(index).equals("text-entry")) {
                Attr attr = document.createAttribute("key");
                attr.setValue(headings.get(index));
                toBeProcessed.setAttributeNode(attr);
            }
            //gives entry value
            toBeProcessed.appendChild(document.createTextNode(textData.get(index)));
            root.appendChild(toBeProcessed);

        }
        //transform DOM Document to string
        String doc = null;
        if (Index == 0) {
            doc = convertDocumentToString(document).replaceFirst(">", "><page-" + (Index + 1) + ">") + tableString + "</page-" + (Index + 1) + ">";

        } else {
            doc = convertDocumentToString(document);
            int stripIndex = doc.indexOf(">");
            String multiPage = doc.substring(0, stripIndex + 1);
            doc = convertDocumentToString(document).replace(multiPage, "<page-" + (Index + 1) + ">") + tableString + "</page-" + (Index + 1) + ">";

        }
        //merging strings pagewise
        XMLString = XMLString + doc;
        if (Index == totalPages) {
            String XMLDocument = "";
            XMLDocument = XMLString.replaceFirst(">", "><document>");
            XMLString = XMLDocument + "</document>";

            Document mergedDocument = convertStringToDocument(XMLString);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = null;
            try {
                transformer = transformerFactory.newTransformer();
            } catch (TransformerConfigurationException e) {
                e.printStackTrace();
            }
            DOMSource domSource = new DOMSource(mergedDocument);
            StreamResult streamResult = new StreamResult(new File(xmlFilePath));
            try {
                transformer.transform(domSource, streamResult);
            } catch (TransformerException e) {
                e.printStackTrace();
            }
        }
        //check statement
        System.out.println("Done creating XML File");
    }


    //main function which will call all the required functions & will be called in main
    //Iterate through all the pages
    //Create xml pagewise and append together
    public static void XMLGenerationCombined(ArrayList<List<HTMLobject>> htmlObjectList, String XMLPath, List<String> XMLTable) {
        totalPages = htmlObjectList.size() - 1;
        if (htmlObjectList.size() - XMLTable.size() > 0) {
            for (int index = XMLTable.size(); index < htmlObjectList.size(); index++) {
                XMLTable.add("");
            }
        }
        for (int index = 0; index < htmlObjectList.size(); index++) {
            lineSpace = determineLineSpacing(htmlObjectList.get(index));
            List<HTMLobject> shrinkedList = Text2XML.joinHTMLObjectList(htmlObjectList.get(index));
            getKeyValuePairs(shrinkedList);
            XMLGenerator(XMLPath, XMLTable.get(index), index);
            headings.clear();
            textData.clear();
            processedElement.clear();
        }
    }


    //This method checks the color, fontfamily, fontweight of Elements
    public static boolean check_Font(HTMLobject element1, HTMLobject element2) {
        if (element1.getColor().equals(element2.getColor()) & element1.getFont_family().equals(element2.getFont_family())
                & element1.getFont_weight() == element2.getFont_weight()) {
            return true;
        }
        return false;
    }


    //This method checks the distance between two elements: if less distance returns false else true
    public static boolean checkDistanceBetween(HTMLobject element1, HTMLobject element2) {
        if (Math.abs(Math.round(((element1.getTop() - element2.getTop()) * 100.00) / 100.00)) > lineSpace + 9 || element1.getTop() == element2.getTop()) {
            return false;
        }
        return true;
    }


    //This method converts DOM Document to string
    private static String convertDocumentToString(Document doc) {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tf.newTransformer();
            // below code to remove XML declaration
            // transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            String output = writer.getBuffer().toString();
            return output;
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        return null;
    }

    //This method converts string to DOM document
    private static Document convertStringToDocument(String xmlStr) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlStr)));
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}