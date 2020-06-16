//aditya

package com.example.pdf2xml.models;

/** Author: Aditya **/
public class HTMLobject {
    private double top;
    private double left;
    private String fontFamily;
    private double fontSize;
    private String fontWeight;
    private String color;
    private String value;
    private Double width;

    public HTMLobject(double top, double left, String fontFamily, double fontSize, String fontWeight, String color, String value, double width) {
        this.top = top;
        this.left = left;
        this.fontFamily = fontFamily;
        this.fontSize = fontSize;
        this.fontWeight = fontWeight;
        this.color = color;
        this.value = value;
        this.width = width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getWidth() {
        return width;
    }

    public String getValue() { return value; }

    public void setValue(String value) { this.value = value; }

    public double getTop() {
        return top;
    }

    public void setTop(double top) {
        this.top = top;
    }

    public double getLeft() {
        return left;
    }

    public void setLeft(double left) {
        this.left = left;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public double getFontSize() {
        return fontSize;
    }

    public void setFontSize(double fontSize) {
        this.fontSize = fontSize;
    }

    public String getFontWeight() {
        return fontWeight;
    }

    public void setFontWeight(String fontWeight) {
        this.fontWeight = fontWeight;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }


}
