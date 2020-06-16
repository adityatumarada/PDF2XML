package com.example.pdf2xml;

import com.example.pdf2xml.models.HTMLobject;
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
 * <h2>XML Conversion</h2>
 * This class is used for processing of text which we got
 * by HTML Conversion and then a complete XML transformation
 * <p>
 * low:</b>
 * <p>
 * <ul>
 *     <li>Initially we will check the vertical distance between consecutive elements and we will find out maximum occuring element as linespace.
 *     <li>Using linespacing and text styles we will make blocks of text elements so that we can make the document more structured for each page while iterating through each page.
 *     <li>Using the linespacing and text styles we will be classifying the elements as key and value pairs for each page.
 *     <li>The key value pairs will be given to DOM parser and document will be formed.
 *     <li>Convert the Document to string and append table XMLstring to it.This will be done for each page and each page's xml string will be appended.
 *     <li>Convert the complete string to Document and then transform to XML
 * </ul><p>
 *
 * @author Jui
 */

public class Text2XML {
    //variables
    private static List<String> headings;
    private static List<String> textData;
    private static Double lineSpace;
    private static Map<Double, Integer> linespacing;
    private static List<Integer> processedElement;
    private static String XMLString;
    private static Integer totalPages;

    /**
     * Constructor
     */
    public Text2XML() {
        headings = new ArrayList<>();
        textData = new ArrayList<>();
        lineSpace = 0.00;
        linespacing = new HashMap<Double, Integer>();
        processedElement = new ArrayList<>();
        XMLString = "";
        totalPages = 0;

    }

    /**
     * Determines line spacing
     * <p>
     * <b>Algorithm:</b>Get absolute difference between consecutive elements.Store count of these differences.
     * Ignore 0.0 distance and find maximum from remaining differences and return it as linespacing.
     *
     * @param textList htmlobject list containing text pagewise
     * @return Double
     */
    public Double determineLineSpacing(List<HTMLobject> textList) {
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


    /**
     * Join the respective blocks using font style and linespacing
     * <p>
     * <b>Algorithm:</b>Check whether appendable or not,if appendable check fonts if same add to one block(curr emlements top should be greater than
     * previous elements top for this.
     * If same vertical coordinate then if curr element is normal weighted and previous is bold then consider as different elements else append
     *
     * @param textList htmlobject listcontaining text pagewise
     * @return List of HTMLobjects
     */
    public List<HTMLobject> joinHTMLObjectList(List<HTMLobject> textList) {

        List<HTMLobject> shrinkedList = new ArrayList<>();
        shrinkedList.add(textList.get(0));
        for (int index = 1; index < textList.size(); index++) {

            boolean appendable = checkDistanceBetween(textList.get(index - 1), textList.get(index));
            boolean isFontequal = checkFont(textList.get(index - 1), textList.get(index));
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
                    if (textList.get(index).getFontWeight().equals(Constant.BOLD)) {
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


    /**
     * Creates key value pair
     * <p>
     * <b>Logic behind key value pair generation:</b> If the element is not visited then process it as follows:
     * If current element and next element are having same y coordinates and current element is bold then it will result into key value pair.
     * Else if line spacing between current and next element is between linespace+1 and linespace+2
     * if font is same then -----key value
     * else key will be text-entry.
     * Else if line spacing is greater than linespace+2then key will be text-entry.
     * If line spacing is less than linespace+1 then ------key value.
     * If last element is not visited then append it to textData and append text-entry to heading.
     *
     * @param shrinkedList htmlobject list that contains element blocks in document( Related elements together)
     */
    public void getKeyValuePairs(List<HTMLobject> shrinkedList) {

        //Initialization of array for keeping track of visited elements

        for (int index = 0; index < shrinkedList.size(); index++) {
            processedElement.add(-1);
        }

        for (int index = 0; index < shrinkedList.size() - 1; index++) {
            if (processedElement.get(index) == -1) {
                if (shrinkedList.get(index).getTop() == shrinkedList.get(index + 1).getTop()) {

                    if (shrinkedList.get(index).getFontWeight().equals(Constant.BOLD)) {
                        headings.add(shrinkedList.get(index).getValue());
                        textData.add(shrinkedList.get(index + 1).getValue());
                        processedElement.set(index, 1);
                        processedElement.set(index + 1, 1);

                    } else {
                        headings.add(Constant.TEXT_ENTRY);
                        textData.add(shrinkedList.get(index).getValue());
                        processedElement.set(index, 1);
                    }
                } else if (Math.abs(shrinkedList.get(index).getTop() - shrinkedList.get(index + 1).getTop()) >= lineSpace + 1
                        && Math.abs(shrinkedList.get(index).getTop() - shrinkedList.get(index + 1).getTop()) < lineSpace + 2) {

                    if (shrinkedList.get(index).getFontWeight().equals(shrinkedList.get(index + 1).getFontWeight())) {
                        headings.add(shrinkedList.get(index).getValue());
                        textData.add(shrinkedList.get(index + 1).getValue());
                        processedElement.set(index, 1);
                        processedElement.set(index + 1, 1);

                    } else {
                        headings.add(Constant.TEXT_ENTRY);
                        textData.add(shrinkedList.get(index).getValue());
                        processedElement.set(index, 1);

                    }
                } else if (Math.abs(shrinkedList.get(index).getTop() - shrinkedList.get(index + 1).getTop()) >= lineSpace + 2) {
                    headings.add(Constant.TEXT_ENTRY);
                    textData.add(shrinkedList.get(index).getValue());
                    processedElement.set(index, 1);

                } else if (Math.abs(shrinkedList.get(index).getTop() - shrinkedList.get(index + 1).getTop()) < lineSpace + 1) {

                    if (shrinkedList.get(index).getFontWeight().equals(Constant.BOLD)) {
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
            headings.add(Constant.TEXT_ENTRY);
            processedElement.set(shrinkedList.size() - 1, 1);
            textData.add(shrinkedList.get(shrinkedList.size() - 1).getValue());
        }

    }


    /**
     * Generates XML file and stores it at xmlFilePath location
     *
     * <p>
     * <b>Algorithm:</b>Using headings and textdata we will be inserting elements in dom Document using dom parser.
     * We will be checking for index, if it is zero then xml declaration will be retained or else removed.
     * Added some required tags and made a string of it and added tables to it .
     * So for every page xml +table string will be there and that will be appended to final xmlstring pagewise.If the page is last page then it
     * will be transformed to xml.
     *
     * @param xmlFilePath filepath for xml
     * @param tableString xml string of table on the page
     * @param pageIndex   index of current page which is being processed
     */
    public void XMLGenerator(String xmlFilePath, String tableString, int pageIndex) {
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
        Element root = document.createElement(Constant.NON_TABULAR_DATA);
        document.appendChild(root);
        //Creates temp node with required values and attributes and appends them as a child to root
        for (int index = 0; index < headings.size(); index++) {
            headings.set(index, headings.get(index).replace(":", "").trim());

            Element toBeProcessed = document.createElement(Constant.TEXT);
            //for attribute creation
            if (!headings.get(index).equals(Constant.TEXT_ENTRY)) {
                Attr attr = document.createAttribute(Constant.KEY);
                attr.setValue(headings.get(index));
                toBeProcessed.setAttributeNode(attr);
            }
            //gives entry value
            toBeProcessed.appendChild(document.createTextNode(textData.get(index)));
            root.appendChild(toBeProcessed);

        }
        //transform DOM Document to string
        String doc = null;
        if (pageIndex == 0) {
            doc = convertDocumentToString(document).replaceFirst(">", "><page-" + (pageIndex + 1) + ">") + tableString + "</page-" + (pageIndex + 1) + ">";

        } else {
            doc = convertDocumentToString(document);
            int stripIndex = doc.indexOf(">");
            String multiPage = doc.substring(0, stripIndex + 1);
            doc = convertDocumentToString(document).replace(multiPage, "<page-" + (pageIndex + 1) + ">") + tableString + "</page-" + (pageIndex + 1) + ">";

        }
        //merging strings pagewise
        XMLString = XMLString + doc;
        if (pageIndex == totalPages) {
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
        System.out.println(Constant.DONE);
    }


    /**
     * main function which will call all the required functions and will be called in main
     *
     * <p>
     * <b>Algorithm:</b>If pdf is nonempty and If no of table strings is less no of pages we will add empty tablestrings.
     * We will iterate through each page. We will determine linespacing, join the blocks and create key value pairs and
     * will be using XMLGenerator function for XML Creation
     * <p>
     * <b> Note:</b>clearing headings,textData and processedElement in each iteration is very important step otherwise the pages will be having duplications.
     *
     * @param htmlObjectList pagewise list of text elements
     * @param XMLPath        path of xml file
     * @param XMLTable       pagewise xml string list of tables
     */
    public void XMLGenerationCombined(ArrayList<List<HTMLobject>> htmlObjectList, String XMLPath, List<String> XMLTable) {
        if (htmlObjectList.isEmpty()) {
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
            Element root = document.createElement(Constant.DOCUMENT);
            document.appendChild(root);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = null;
            try {
                transformer = transformerFactory.newTransformer();
            } catch (TransformerConfigurationException e) {
                e.printStackTrace();
            }
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(XMLPath));
            try {
                transformer.transform(domSource, streamResult);
            } catch (TransformerException e) {
                e.printStackTrace();
            }
        } else {
            totalPages = htmlObjectList.size() - 1;
            if (htmlObjectList.size() - XMLTable.size() > 0) {
                for (int index = XMLTable.size(); index < htmlObjectList.size(); index++) {
                    XMLTable.add("");
                }
            }
            for (int index = 0; index < htmlObjectList.size(); index++) {
                lineSpace = determineLineSpacing(htmlObjectList.get(index));
                List<HTMLobject> shrinkedList = joinHTMLObjectList(htmlObjectList.get(index));
                getKeyValuePairs(shrinkedList);
                XMLGenerator(XMLPath, XMLTable.get(index), index);
                headings.clear();
                textData.clear();
                processedElement.clear();
            }
        }
    }


    /**
     * This method checks the color, fontfamily, fontweight of Elements. If same return true (equal font) else false (unequal font)
     *
     * @param element1 HTMLobject
     * @param element2 HTMLobject
     * @return boolean
     */
    public boolean checkFont(HTMLobject element1, HTMLobject element2) {
        if (element1.getColor().equals(element2.getColor()) & element1.getFontFamily().equals(element2.getFontFamily())
                & element1.getFontWeight() == element2.getFontWeight()) {
            return true;
        }
        return false;
    }


    /**
     * This method checks the distance between two elements: if less distance returns false else true
     * <p>
     * <b>Algorithm:</b> find the vertical distance between two elements and check whether it is greater than linespace +9 or
     * having same vertical coordinates. If yes, then return false(not appendable) else return true(appendable)
     *
     * @param element1 HTMLobject
     * @param element2 HTMLobject
     * @return boolean
     */
    public boolean checkDistanceBetween(HTMLobject element1, HTMLobject element2) {
        if (Math.abs(Math.round(((element1.getTop() - element2.getTop()) * 100.00) / 100.00)) > lineSpace + 9 || element1.getTop() == element2.getTop()) {
            return false;
        }
        return true;
    }


    /**
     * This method converts DOM Document to string
     *
     * @param doc DOM document
     * @return String
     */
    private String convertDocumentToString(Document doc) {
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


    /**
     * This method converts string to DOM document
     *
     * @param xmlStr
     * @return Document
     */
    private Document convertStringToDocument(String xmlStr) {
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