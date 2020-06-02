# PDF2XML

Project built under Citi, Pune as summer analyst intern project

#  Contents
1. [Problem Statement](https://github.com/adityatumarada/PDF2XML#problem-statement)
2. [Features](https://github.com/adityatumarada/PDF2XML#features)
3. [Approach](https://github.com/adityatumarada/PDF2XML#approach)
4. [Execution](https://github.com/adityatumarada/PDF2XML#execution)

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


## Text Extraction

![enter image description here](https://github.com/adityatumarada/PDF2XML/blob/master/Charts/TextExtraction.png)

#  Execution

The program can be rum in many ways.

##  Executing using `mainClass.java`

 1. Open the terminal 
 2. Change your current working directory (using `cd`) 
    `cd /<your>/<path>/.../PDF2XML/src/main/java/com/example/pdf2xml/mainClass.java`
3. Compile the file
	`javac mainClass.java`
4. Run the file
	`java mainClass`

##  Executing using `.jar` file

If you have obtained the .jar file from the hackerearth portal, follow the below given instructions
 1. Open the terminal 
 2. Change your current working directory (using `cd`):
	  `cd your/path/here/`
 3. Run the file
      `java -jar PDFtoXML-me.jar`
