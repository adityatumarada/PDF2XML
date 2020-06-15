package com.example.pdf2xml;

import com.example.pdf2xml.models.Details;

import java.util.ArrayList;
import java.util.List;

/* Author: Aditya */
public class Table2XML {

    public static List<String> convertToXML(Details[] details) {

        if (details.length == 0) {
            return new ArrayList<>();
        }

        int totalPages = details[details.length - 1].getPageNo();
        List<String> tableXML = new ArrayList<>();
        for (int i = 0; i <= totalPages; i++) {
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

    public static String table2XML(String[][] table, int table_id) {
        StringBuilder XMLstring = new StringBuilder("");
        XMLstring.append("<table id=\"").append(table_id).append("\">");
        for (int i = 1; i < table.length; i++) {
            XMLstring.append("<row>");
            for (int j = 0; j < table[0].length; j++) {
                String starTag = "<row-entry>";
                String endTag = "</row-entry>";
                if (table[0][j] != null) {
                    String header = table[0][j].trim().replaceAll("[^a-zA-Z0-9]", "");
                    starTag = "<" + header + ">";
                    endTag = "</" + header + ">";
                }
                String string = "";
                if (table[i][j] != null)
                    string = table[i][j].trim().replaceAll("\n", " ");
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