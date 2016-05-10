package com.biometryczne.signature.controllers.actions;

import com.biometryczne.signature.nodes.JavaFXPenNode;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by c309044 on 2016-05-10.
 */
public class ClearCanvasAction implements IControllerAction {

    private final static Logger log = LoggerFactory.getLogger(ClearCanvasAction.class);

    @Override
    public Void perform(Pane mainPane) {

        log.info("hehehe");

        JavaFXPenNode penNode = (JavaFXPenNode)mainPane.getScene().lookup("#mainSignatureCanvas");
        penNode.clearCanvas();

        return null;
    }
}
