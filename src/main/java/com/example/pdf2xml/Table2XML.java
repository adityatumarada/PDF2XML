package com.example.pdf2xml;

import com.example.pdf2xml.models.Details;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to convert table to XML
 * @author Aditya
 */
public class Table2XML {

    /**
     * Converts tables to xml string
     * @param details
     * @return list of xml string for each table
     */
    public static List<String> convertToXML(Details[] details) {

        if (details.length == 0) {
            return new ArrayList<>();
        }

        int totalPages = details[details.length - 1].getPageNo();
        List<String> tableXML = new ArrayList<>();
        for (int index = 0; index <= totalPages; index++) {
            tableXML.add("");
        }
        int tableID = 1;

        for (Details detail : details) {
            String temp = tableXML.get(detail.getPageNo());
            if (temp.isEmpty())
                tableXML.set(detail.getPageNo(), table2XML(detail.getTables(), tableID));
            else
                tableXML.set(detail.getPageNo(), temp + table2XML(detail.getTables(), tableID));
            tableID++;
        }
        return tableXML;
    }

    /**
     * converts 2d array to XML
     * @param table
     * @param table_id
     * @return  xml string of input array
     */
    public static String table2XML(String[][] table, int table_id) {
        StringBuilder XMLstring = new StringBuilder("");
        XMLstring.append("<table id=\"").append(table_id).append("\">");
        for (int row = 1; row < table.length; row++) {
            XMLstring.append("<row>");
            for (int col = 0; col < table[0].length; col++) {
                String starTag = "<row-entry>";
                String endTag = "</row-entry>";
                if (table[0][col] != null) {
                    String header = table[0][col].trim().replaceAll("[^a-zA-Z0-9]", "");
                    starTag = "<" + header + ">";
                    endTag = "</" + header + ">";
                }
                String string = "";
                if (table[row][col] != null)
                    string = table[row][col].trim().replaceAll("\n", " ");
                if (string.length() == 0)
                    string = "";
                XMLstring.append(starTag).append(string).append(endTag);
            }
            XMLstring.append("</row>");
        }
        XMLstring.append("</table>");

        return XMLstring.toString();
    }

}