package com.biometryczne.signature.controllers.actions;

import com.biometryczne.signature.beans.SignatureJSONBean;
import com.biometryczne.signature.utils.Signature;
import com.biometryczne.signature.utils.SignatureCharacteristics;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Almi on 2016-05-30.
 */
public class ComputeCorrelationAction implements IControllerAction<Void> {

    private final static Logger log = LoggerFactory.getLogger(ComputeCorrelationAction.class);

    private List<SignatureJSONBean> jsonBeans = new ArrayList<>();
    private double threshold = 0.5;
    private Signature signature;

    public ComputeCorrelationAction(Signature currentSignature) {
        signature = currentSignature;
    }

    @Override
    public Void perform(Pane mainPane) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            jsonBeans = objectMapper.readValue(getClass().getResourceAsStream("/tmp/database.json"), objectMapper.getTypeFactory().constructCollectionType(List.class, SignatureJSONBean.class));
        } catch (IOException e) {
            log.info(ExceptionUtils.getStackTrace(e));
        }

        PearsonsCorrelation pearson = new PearsonsCorrelation();
        SignatureJSONBean selectedSignature = null;
        double xCorr = 0;
        double yCorr = 0;
        double pCorr = 0;
        for(SignatureJSONBean bean : jsonBeans) {
            xCorr = pearson.correlation(bean.getX(), signature.getAsArray(SignatureCharacteristics.X));
            yCorr = pearson.correlation(bean.getY(), signature.getAsArray(SignatureCharacteristics.Y));
            pCorr = pearson.correlation(bean.getP(), signature.getAsArray(SignatureCharacteristics.PRESSURE));
            if(xCorr >= threshold && yCorr >= threshold && pCorr >= threshold) {
                selectedSignature = bean;
            }
        }

        if(selectedSignature != null) {
            ((Label)mainPane.lookup("selectedUser")).setText(selectedSignature.getName() + " " + " Korelacje: " +
                    "x = " + xCorr + " y = " + yCorr + " p = " + pCorr);
        }
        return null;
    }

}
