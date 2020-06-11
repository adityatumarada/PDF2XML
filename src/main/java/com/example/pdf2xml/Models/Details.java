package com.example.pdf2xml.Models;


/** Author: Eshita **/
public class Details {
    private int pageNo;
    private String[][] Tables;
    private double[] TableAllPoints;

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public String[][] getTables() {
        return Tables;
    }

    public void setTables(String[][] tables) {
        Tables = tables;
    }

    public double[] getTableAllPoints() {
        return TableAllPoints;
    }

    public void setTableAllPoints(double[] tableAllPoints) {
        TableAllPoints = tableAllPoints;
    }
}