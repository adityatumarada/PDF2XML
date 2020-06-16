package com.example.pdf2xml;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Launches GUI
 *
 * @author Jui Pitale
 */
public class App extends Application {

    private static Scene scene;

    /**
     *
     *
     * @throws IOException
     */
    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML(Constant.PRIMARY));
        stage.setScene(scene);
        stage.setTitle(Constant.PDFTOXMLCONVERSION);
        stage.show();
    }



    /**
     * loads fxml file through resources
     * @param fxml fxml file name
     * @return  Parent
     * @throws IOException
     */
    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + Constant.FXMLFILEEXTENSION));
        return fxmlLoader.load();
    }

    /**
     * main function that launches the application
     *
     */
    public static void main(String[] args) {
        launch();
    }

}