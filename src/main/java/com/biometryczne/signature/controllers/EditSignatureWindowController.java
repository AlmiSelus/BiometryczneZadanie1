package com.biometryczne.signature.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by c309044 on 2016-05-10.
 */
public class EditSignatureWindowController implements Initializable {

    private final static Logger log = LoggerFactory.getLogger(EditSignatureWindowController.class);

    @FXML
    public VBox signaturesVBox;

    @FXML
    public Spinner<Integer> signatureFieldsNum;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        signatureFieldsNum.setEditable(true);
        signatureFieldsNum.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 4, 1));
    }

    @FXML
    public void accept() {
        log.info("Accept");
    }

    @FXML
    public void clearAllCanvases() {
        log.info("Clear All Canvases");
    }

    @FXML
    public void showStatisticsForAll() {
        log.info("Show statistics for all");
    }
}
