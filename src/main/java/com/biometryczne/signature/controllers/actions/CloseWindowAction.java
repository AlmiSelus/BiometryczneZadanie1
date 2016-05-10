package com.biometryczne.signature.controllers.actions;

import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Created by Almi on 2016-05-09.
 */
public class CloseWindowAction implements IControllerAction {

    @Override
    public Void perform(Pane mainPane) {
        Stage stage = (Stage)mainPane.getScene().getWindow();
        stage.close();

        return null;
    }

}
