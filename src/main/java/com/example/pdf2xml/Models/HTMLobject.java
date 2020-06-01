//aditya

package com.example.pdf2xml.Models;

/** Author: Aditya **/
public class HTMLobject {
    private double top;
    private double left;
    private String font_family;
    private double font_size;
    private String font_weight;
    private String color;
    private String value;
    private Double width;

    public HTMLobject(double top, double left, String font_family, double font_size, String font_weight, String color, String value,double width) {
        this.top = top;
        this.left = left;
        this.font_family = font_family;
        this.font_size = font_size;
        this.font_weight = font_weight;
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

    public String getFont_family() {
        return font_family;
    }

    public void setFont_family(String font_family) {
        this.font_family = font_family;
    }

    public double getFont_size() {
        return font_size;
    }

    public void setFont_size(double font_size) {
        this.font_size = font_size;
    }

    public String getFont_weight() {
        return font_weight;
    }

    public void setFont_weight(String font_weight) {
        this.font_weight = font_weight;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }


}
