package com.biometryczne.signature.controllers;

import com.biometryczne.signature.controllers.actions.CloseWindowAction;
import com.biometryczne.signature.controllers.actions.ControllerActionManager;
import com.biometryczne.signature.nodes.JavaFXPenNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import jpen.*;
import jpen.event.PenListener;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Almi on 2016-05-09.
 */
public class MainWindowController implements Initializable {


    //Referencja do głównego okna programu. Zwykle potrzebna ;)
    @FXML
    private BorderPane mainWindow;

    private ControllerActionManager actionManager = new ControllerActionManager();

    public void initialize(URL location, ResourceBundle resources) {
        //dodaj FX Pen'a
        mainWindow.setCenter(new JavaFXPenNode());
    }

    @FXML
    public void closeWindow() {
        actionManager.setPerformer(new CloseWindowAction());
        actionManager.perform(mainWindow);
    }
}
