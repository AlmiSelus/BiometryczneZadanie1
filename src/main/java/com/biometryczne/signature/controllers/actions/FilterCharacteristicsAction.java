package com.biometryczne.signature.controllers.actions;

import com.biometryczne.signature.nodes.JavaFXPenNode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Adi on 2016-05-22.
 */
public class FilterCharacteristicsAction implements IControllerAction {


    private final static Logger log = LoggerFactory.getLogger(FilterCharacteristicsAction.class);

    @Override
    public Void perform(Pane mainPane) {

//        log.info("!!!!!____FILTER");
        JavaFXPenNode penNode = (JavaFXPenNode)mainPane.getScene().lookup("#mainSignatureCanvas");
        penNode.filterSignatureData();

        return null;
    }
}
