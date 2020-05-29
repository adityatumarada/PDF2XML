package Models;

import java.util.List;

public class Details{

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public List<String[][]> getTables() {
        return Tables;
    }

    public void setTables(List<String[][]> tables) {
        Tables = tables;
    }

    public String getXcoordinates() {
        return Xcoordinates;
    }

    public void setXcoordinates(String xcoordinates) {
        Xcoordinates = xcoordinates;
    }

    public String getRowPartitions() {
        return RowPartitions;
    }

    public void setRowPartitions(String rowPartitions) {
        RowPartitions = rowPartitions;
    }

    String Text;
    List<String[][]> Tables;
    String Xcoordinates;
    String RowPartitions;

}