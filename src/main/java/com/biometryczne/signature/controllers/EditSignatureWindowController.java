package com.biometryczne.signature.controllers;

import com.biometryczne.signature.beans.SignatureJSONBean;
import com.biometryczne.signature.nodes.JavaFXPenNode;
import com.biometryczne.signature.utils.SignatureCharacteristics;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
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
    public Spinner<Integer> signatureFieldsNum;

    @FXML
    public JavaFXPenNode signatureNode;

    @FXML
    public TextField name;

    private SessionFactory sessionFactory;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        signatureFieldsNum.setEditable(true);
        signatureFieldsNum.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 4, 1));
    }

    @FXML
    public void accept() {
        log.info("Accept");
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        SignatureJSONBean signature = new SignatureJSONBean();
        signature.setName(name.getText().isEmpty() ? "Unknown :<" : name.getText());
        signature.setX(signatureNode.getCanvasValuesAsArray(SignatureCharacteristics.X));
        signature.setY(signatureNode.getCanvasValuesAsArray(SignatureCharacteristics.Y));
        signature.setP(signatureNode.getCanvasValuesAsArray(SignatureCharacteristics.PRESSURE));

        session.save(signature);

        transaction.commit();
        session.close();
    }

    @FXML
    public void clearAllCanvases() {
        log.info("Clear All Canvases");
    }

    @FXML
    public void showStatisticsForAll() {
        log.info("Show statistics for all");
    }

    public void setSessionFactory(final SessionFactory sf) {
        sessionFactory = sf;
    }
}
