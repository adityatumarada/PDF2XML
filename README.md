# PDF2XML

Project built under Citi, Pune as summer analyst intern project

> Note:  The other repository is at [https://github.com/EshitaShukla/PDFToXML](https://github.com/EshitaShukla/PDFToXML), where only some parts of the Program ares available. Project structure is different in both reposetories

#  Contents
1. [Problem Statement](https://github.com/adityatumarada/PDF2XML#problem-statement)
2. [Features](https://github.com/adityatumarada/PDF2XML#features)
3. [Approach](https://github.com/adityatumarada/PDF2XML#approach)
4. [Libraries Used](https://github.com/adityatumarada/PDF2XML/blob/master/README.md#softwares--platforms-used)
5. [Execution](https://github.com/adityatumarada/PDF2XML#execution) 

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

More types of tables need to yet be accomodated, in the extraction algorithm.

## Text Extraction

![enter image description here](https://github.com/adityatumarada/PDF2XML/blob/master/Charts/TextExtraction.png)

Text extraction needs to be generalised for a wider range of "line-spacings"


#  Softwares & platforms used

- **JDK Platform version 8**
- **Apache PDFBox version 2.0.19**



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
