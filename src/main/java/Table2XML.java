import java.util.List;

public class Table2XML {

    public static String convertToXML(List<String[][]> tables) {
        StringBuilder XMLstring = new StringBuilder("<tabular-data>");
        int table_id = 1;
        for (String[][] table : tables) {
            XMLstring.append("<table id=\"").append(table_id).append("\">");
            for (int i = 0; i < table.length; i++) {
                XMLstring.append("<tr>");
                for (int j = 0; j < table[0].length; j++) {

                    String string = table[i][j].trim().replace("\n", " ");
                    if (i == 0) {
                        if (!string.isEmpty())
                            XMLstring.append("<th>").append(string).append("</th>");
                        else
                            XMLstring.append("<th>-</th>");
                    } else {
                        if (string.length() != 0)
                            XMLstring.append("<td>").append(string).append("</td>");
                        else
                            XMLstring.append("<td>-</td>");
                    }
                }
                XMLstring.append("</tr>");
            }
            XMLstring.append("</table>");
        }
        XMLstring.append("</tabular-data>");

        return XMLstring.toString();
    }

}
