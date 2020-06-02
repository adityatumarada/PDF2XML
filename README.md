# Problem Statement

Write a PDF to XML utility (tool) by leveraging the pdfbox library so that we can
use this tool to compare pdf files to DB tables.

## Description

Citi has a framework, which can compare the XML data with database tables and reports the data differences.

However, the system does not support the data comparison between PDF and table data due to which data is compared manually, and this makes it error-prone.

So that once we convert the PDF file to the XML file, it can be injected into the existing Framework to compare the data and find the root cause of the data discrepancy in the PDF files.

# Features

A pdf contains:
- **Table Extraction**
- **Text Extraction**
- **Image Extraction**

Our approach is unique because we handle tables separately and text separately. Then, we extract images.

# Approach

## Table Extraction

![enter image description here](https://github.com/adityatumarada/PDF2XML/blob/master/Charts/TableExtraction.png)

## Image Extraction

![enter image description here](https://github.com/adityatumarada/PDF2XML/blob/master/Charts/TextExtraction.png)

