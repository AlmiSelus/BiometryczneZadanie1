package com.biometryczne.signature.controllers.actions;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by c309044 on 2016-05-10.
 */
public class EditSignatureAction implements IControllerAction {

    private final static Logger log = LoggerFactory.getLogger(EditSignatureAction.class);

    @Override
    public Void perform(Pane mainPane) {
        try {
            final Parent parent = new FXMLLoader().load(getClass().getResourceAsStream("/views/SignatureEdit.fxml"));
            final Scene scene = new Scene(parent, 400, 300);
            final Stage stage = new Stage(StageStyle.DECORATED);
            stage.setScene(scene);
            stage.show();
        } catch(Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

}
