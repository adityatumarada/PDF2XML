import java.util.List;

public class Table2XML {

    public static String convertToXML(List<String[][]> tables) {
        String XMLstring = "<tabular-data>";
        int table_id = 1;
        for (String[][] table : tables) {
            XMLstring = XMLstring + "<table id=\"" + table_id + "\">\n";
            for (int i = 0; i < table.length; i++) {
                XMLstring = XMLstring + "<tr>\n";
                for (int j = 0; j < table[0].length; j++) {
                    String string="";
                    if (table[i][j].equals(null)) {
                        string = " ";
                    } else {
                        string = table[i][j].trim().replace("\n", " ");
                    }
                    if (i == 0) {
                        XMLstring = XMLstring + "<th>" + string + "</th>\n";
                    } else {
                        XMLstring = XMLstring + "<td>" + string + "</td>\n";
                    }
                }
                XMLstring = XMLstring + "</tr>\n";
            }
            XMLstring = XMLstring + "</table>\n";
        }
        XMLstring = XMLstring + "</tabular-data>\n";
        return XMLstring;
    }

}
