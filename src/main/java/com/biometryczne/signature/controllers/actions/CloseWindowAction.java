package com.biometryczne.signature.controllers.actions;

import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Almi on 2016-05-09.
 */
public class CloseWindowAction implements IControllerAction {


    private final static Logger log = LoggerFactory.getLogger(CloseWindowAction.class);    @Override
    public Void perform(Pane mainPane) {
//        Stage stage = (Stage)mainPane.getScene().getWindow();
//        stage.close();
        Platform.exit();
        System.exit(0);

        return null;
    }

}
