package com.biometryczne.signature.controllers;

import com.biometryczne.signature.controllers.actions.ClearCanvasAction;
import com.biometryczne.signature.controllers.actions.CloseWindowAction;
import com.biometryczne.signature.controllers.actions.ControllerActionManager;
import com.biometryczne.signature.nodes.JavaFXPenNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Almi on 2016-05-09.
 */
public class MainWindowController implements Initializable {

    private final static Logger log = LoggerFactory.getLogger(MainWindowController.class);

    @FXML
    public JavaFXPenNode mainSignatureCanvas;

    //Referencja do głównego okna programu. Zwykle potrzebna ;)
    @FXML
    private BorderPane mainWindow;

    private ControllerActionManager actionManager = new ControllerActionManager();

    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void closeWindow() {
        actionManager.setPerformer(new CloseWindowAction());
        actionManager.perform(mainWindow);
    }

    @FXML
    public void editSignature(ActionEvent actionEvent) {
        log.info("Edit signature");
    }

    @FXML
    public void clearSignatureCanvas(ActionEvent actionEvent) {
        log.info("Clear Signature canvas");
        actionManager.setPerformer(new ClearCanvasAction());
        actionManager.perform(mainWindow);
    }
}
