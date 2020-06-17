package com.example.pdf2xml;

import com.example.pdf2xml.models.HTMLobject;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.fit.pdfdom.PDFDomTree;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to extract and format text from pdf
 *
 * @author Aditya
 **/
public class HTMLformatter {

    /**
     * formats htmlObject list by joining words present in same line
     *
     * @param htmlObjectList
     * @return formatted list of htmlObject
     */
    static List<HTMLobject> formatHTMLList(List<HTMLobject> htmlObjectList) {
        List<HTMLobject> formattedHTMLList = new ArrayList<>();
        if (htmlObjectList.size() == 0) {
            return formattedHTMLList;
        }
        formattedHTMLList.add(htmlObjectList.get(0));
        for (int index = 1; index < htmlObjectList.size(); index++) {
            double prevTop = formattedHTMLList.get(formattedHTMLList.size() - 1).getTop();
            double curTop = htmlObjectList.get(index).getTop();
            String prevFont = formattedHTMLList.get(formattedHTMLList.size() - 1).getFontFamily();
            String curFont = htmlObjectList.get(index).getFontFamily();
            double prevFontSize = formattedHTMLList.get(formattedHTMLList.size() - 1).getFontSize();
            double curFontSize = htmlObjectList.get(index).getFontSize();
            String prevFontWeight = formattedHTMLList.get(formattedHTMLList.size() - 1).getFontWeight();
            String curFontWeight = htmlObjectList.get(index).getFontWeight();
            if (prevTop == curTop && curFont.equals(prevFont) && prevFontSize == curFontSize && prevFontWeight.equals(curFontWeight)) {
                HTMLobject object = formattedHTMLList.get(formattedHTMLList.size() - 1);
                object.setWidth(object.getWidth() + htmlObjectList.get(index).getWidth());
                object.setValue(object.getValue() + " " + htmlObjectList.get(index).getValue());
            } else
                formattedHTMLList.add(htmlObjectList.get(index));

        }
        return formattedHTMLList;
    }

    /**
     * Converts pdf document to HTML string
     *
     * @param pdf
     * @return HTML string of input PDF
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public static String generateHTMLFromPDF(PDDocument pdf) throws IOException, ParserConfigurationException {
        Writer output = new StringWriter();
        new PDFDomTree().writeText(pdf, output);
        output.close();
        return output.toString();

    }

    /**
     * Extracts text from htmlString and converts to list of htmlobject
     *
     * @param htmlString
     * @return page wise list of HTMLobjects
     */
    public static ArrayList<List<HTMLobject>> parseHTML(String htmlString) {
        Document doc = Jsoup.parse(htmlString);
        Elements pages = doc.getElementsByClass("page");

        ArrayList<List<HTMLobject>> pageElement = new ArrayList<>();
        for (Element page : pages) {
            Elements divs = page.getElementsByClass("p");
            List<HTMLobject> htmlObjectList = new ArrayList<>();
            for (Element div : divs) {
                if (div.hasText())
                    htmlObjectList.add(htmlObjectCoverter(div));
            }
            htmlObjectList = formatHTMLList(htmlObjectList);
            pageElement.add(htmlObjectList);
        }

        return pageElement;
    }

    /**
     * Converts html element to html object by extracting style attributes and text
     *
     * @param element
     * @return htmlobject
     */
    public static HTMLobject htmlObjectCoverter(Element element) {
        HTMLobject object = new HTMLobject(0, 0, " ", 0, " ", " ", " ", 0);
        object.setValue(element.text());

        String style = element.attr("style");
        String[] styleAttrs = style.split(";");
        for (String styleAttr : styleAttrs) {
            String[] arr = styleAttr.split(":");
            if (arr[0].equals("top")) {
                object.setTop(Double.parseDouble(arr[1].substring(0, arr[1].length() - 2)));
            } else if (arr[0].equals("left")) {
                object.setLeft(Double.parseDouble(arr[1].substring(0, arr[1].length() - 2)));
            } else if (arr[0].equals("font-family")) {
                object.setFontFamily(arr[1]);
            } else if (arr[0].equals("font-size")) {
                object.setFontSize(Double.parseDouble(arr[1].substring(0, arr[1].length() - 2)));
            } else if (arr[0].equals("font-weight")) {
                object.setFontWeight(arr[1]);
            } else if (arr[0].equals("color")) {
                object.setColor(arr[1]);
            } else if (arr[0].equals("width")) {
                object.setWidth(Double.parseDouble(arr[1].substring(0, arr[1].length() - 2)));
            }
        }
        return object;
    }
}
