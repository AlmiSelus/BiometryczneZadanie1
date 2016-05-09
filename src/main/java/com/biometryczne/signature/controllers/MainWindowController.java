package com.biometryczne.signature.controllers;

import com.biometryczne.signature.controllers.actions.CloseWindowAction;
import com.biometryczne.signature.controllers.actions.ControllerActionManager;
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
public class MainWindowController implements Initializable, PenListener {


    //Referencja do głównego okna programu. Zwykle potrzebna ;)
    @FXML
    private BorderPane mainWindow;

    private ControllerActionManager actionManager = new ControllerActionManager();

    public void initialize(URL location, ResourceBundle resources) {

    }

    public void penButtonEvent(PButtonEvent ev) {
        System.out.println(ev);
    }

    public void penKindEvent(PKindEvent ev) {
        System.out.println(ev);
    }

    public void penLevelEvent(PLevelEvent ev) {
        System.out.println(ev);
        System.out.println("Pressure = " + ev.pen.getLevelValue(PLevel.Type.PRESSURE));
    }

    public void penScrollEvent(PScrollEvent ev) {
        System.out.println(ev);

    }

    public void penTock(long availableMillis) {
        System.out.println("TOCK - available period fraction: "+availableMillis);
    }

    @FXML
    public void closeWindow() {
        actionManager.setPerformer(new CloseWindowAction());
        actionManager.perform(mainWindow);
    }
}
