package com.biometryczne.signature;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by Almi on 2016-04-24.
 */
public class SignatureRecognizer extends Application {

    public static void main(String... args) throws Throwable{
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        final Parent parent = new FXMLLoader().load(getClass().getResourceAsStream("/views/MainView.fxml"));
        final Scene scene = new Scene(parent, 1200, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Biometryczne - Zadanie 1");
        primaryStage.show();
    }

}
