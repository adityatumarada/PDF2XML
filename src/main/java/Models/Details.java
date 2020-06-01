package Models;

import java.util.List;

public class Details {

    private String Text;
    private List<String[][]> Tables;
    private String Xcoordinates;
    private String RowPartitions;
    private double[][] TableVerticalCoord; // Horizontal is same for even page, see : PART 1
    private double[][] TableHorizontCoord;
    private List<double[]> TableAllPoints;

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

    public double[][] getTableVerticalCoord() {
        return TableVerticalCoord;
    }

    public void setTableVerticalCoord(double[][] tableVerticalCoord) {
        TableVerticalCoord = tableVerticalCoord;
    }

    public double[][] getTableHorizontCoord() {
        return TableHorizontCoord;
    }

    public void setTableHorizontCoord(double[][] tableHorizontCoord) {
        TableHorizontCoord = tableHorizontCoord;
    }


    public List<double[]> getTableAllPoints() {
        return TableAllPoints;
    }

    public void setTableAllPoints(List<double[]> tableAllPoints) {
        TableAllPoints = tableAllPoints;
    }
}