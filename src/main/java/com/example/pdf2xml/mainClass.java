package com.example.pdf2xml;

import com.example.pdf2xml.Models.Details;
import com.example.pdf2xml.Models.HTMLobject;
import org.apache.pdfbox.pdmodel.PDDocument;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.awt.Color.*;

/** Authors: Jui, Aditya **/
public class mainClass extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    private String pdfPath = "";
    private String xmlPath = "";
    private JTextField pdfFilePath;
    private JTextField xmlFilePath;
    private JButton choosePdf = new JButton("CHOOSE PDF");
    private JButton chooseXml = new JButton("CHOOSE XML Path");
    private JButton convert = new JButton("CONVERT");

    //gui creation -
    //3 buttons- 2 for choosing filepaths and 1 for actual conversion
    //2 textfields for showing the paths chosen
    public mainClass() throws IOException {
        //Background colour -gray
        this.getContentPane().setBackground(lightGray);
        this.getContentPane().setLayout(new FlowLayout());

        Box box = Box.createVerticalBox();

        //created Label for project name & changed its some default properties
        JLabel label = new JLabel();
        label.setFont(new Font("Verdana", Font.BOLD, 18));
        label.setBounds(50, 100, 250, 20);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setText("PDF TO XML CONVERTER");
        label.setForeground(blue);
        box.add(label);

        //Created table for Team name & changed the required default properties
        JLabel label1 = new JLabel();
        label1.setFont(new Font("Verdana", Font.BOLD, 14));
        label1.setBounds(50, 100, 250, 20);
        label1.setText("Team VJTI");
        label1.setHorizontalAlignment(JLabel.CENTER);
        label1.setForeground(black);
        box.add(label1);

        //for displaying selected path
        pdfFilePath = new JTextField("");
        pdfFilePath.setForeground(gray);
        pdfFilePath.setBounds(50, 100, 200, 30);
        box.add(pdfFilePath);

        //Button created for choosing the path
        choosePdf.setText("CHOOSE PDF");
        choosePdf.addActionListener(this);
        choosePdf.setBackground(BLACK);
        choosePdf.setForeground(white);
        choosePdf.setBounds(50, 100, 200, 30);
        choosePdf.setFont(new Font("Verdana", Font.BOLD, 14));
        choosePdf.setActionCommand("choosepdf");
        box.add(choosePdf);

        // for displaying xml path
        xmlFilePath = new JTextField("");
        xmlFilePath.setForeground(gray);
        xmlFilePath.setBounds(50, 100, 200, 30);
        box.add(xmlFilePath);

        //for choosing xml filepath
        chooseXml.setText("CHOOSE XML");
        chooseXml.addActionListener(this);
        chooseXml.setBackground(BLACK);
        chooseXml.setForeground(white);
        chooseXml.setFont(new Font("Verdana", Font.BOLD, 14));
        chooseXml.setBounds(50, 100, 200, 30);
        chooseXml.setActionCommand("choosexml");
        box.add(chooseXml);


        //Conversion task
        convert.setBackground(RED);
        convert.setForeground(white);
        convert.setFont(new Font("Verdana", Font.BOLD, 14));
        convert.setBounds(50, 100, 200, 30);
        box.add(convert);
        convert.addActionListener(this);
        convert.setActionCommand("convert");

        //by box keeping it vertically aligned
        add(box);


    }


    @Override
    public void actionPerformed(ActionEvent ae) {
        String action = ae.getActionCommand();
        if (action.equals("convert")) {
            try {

                PDDocument pdf = PDDocument.load(new File(pdfPath));

                //extracting tables from pdf
                Details[] tableDetails = PDFTableStripper.getDetails(pdf);

                //generates HTMLString
                String htmlString = HTMLformatter.generateHTMLFromPDF(pdf);

                //generates List of HTML objects.
                ArrayList<List<HTMLobject>> htmlObjectList = HTMLformatter.parseHTML(htmlString);

                //remove tables
                for(Details table : tableDetails)
                {
                    int pgNo = table.getPageNo();
                    List<HTMLobject> textList = removeTable(htmlObjectList.get(pgNo),table.getTableAllPoints());
                    htmlObjectList.set(pgNo,textList);
                }

                //converts tables to XML
                List<String> XMLtable = Table2XML.convertToXML(tableDetails);
                System.out.println(XMLtable.get(0));
                System.out.println(XMLtable.get(1));

                //use XMLtable and htmlObjectList for text2XML



                //changes color & text of button when done
                convert.setBounds(50, 100, 200, 30);
                convert.setText("Done!!");
                convert.setFont(new Font("Verdana", Font.BOLD, 14));
                convert.setBackground(green);


            } catch (IOException | ParserConfigurationException e) {
                e.printStackTrace();
            }


        }
        if (action.equals("choosepdf")) {
            JFileChooser jFileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

            // allow multiple file selection
            jFileChooser.setMultiSelectionEnabled(true);

            // invoke the showsOpenDialog function to show the path
            int openStatus = jFileChooser.showOpenDialog(null);

            if (openStatus == JFileChooser.APPROVE_OPTION) {
                // get the selected files
                pdfPath = jFileChooser.getSelectedFile().getAbsolutePath();
                pdfPath.replace("\\", "/");
                pdfFilePath.setText(pdfPath);

            }
        }
        if (action.equals("choosexml")) {
            JFileChooser jFilechooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

            // invoke the showsOpenDialog function to show the path
            int openStatus = jFilechooser.showOpenDialog(null);

            if (openStatus == JFileChooser.APPROVE_OPTION) {
                // get the selected filepath
                xmlPath = jFilechooser.getSelectedFile().getAbsolutePath();
                xmlPath.replace("\\", "/");
                xmlFilePath.setText(xmlPath);

            }
        }
    }

    private static void createAndShowGUI() throws IOException {

        //creation of swing frame
        JFrame frame = new mainClass();
        frame.setSize(400, 400);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    public static void main(String[] args) {

        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            public void run() {

                try {
                    createAndShowGUI();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        });

    }

    //Removes table from data to be processed
    //Removes table from data to give text
    private static List<HTMLobject> removeTable(List<HTMLobject> formattedHTMLList, double[] tableCoodinates) {
        List<HTMLobject> textList = new ArrayList<>();

        for (HTMLobject htmLobject : formattedHTMLList) {
            if(!istable(htmLobject,tableCoodinates))
            {
                textList.add(htmLobject);
            }
        }
        return textList;
    }

    //checks if data is present in table
    private static boolean istable(HTMLobject htmLobject, double[] coodinates) {

        double top = htmLobject.getTop();
        double left = htmLobject.getLeft();

        if( top>coodinates[0] && top<coodinates[1] && left>coodinates[2] && left<coodinates[3])
                return true;


        return false;
    }


}