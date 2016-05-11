package com.biometryczne.signature.controllers;

import com.biometryczne.signature.Signature;
import com.biometryczne.signature.controllers.actions.ClearCanvasAction;
import com.biometryczne.signature.controllers.actions.CloseWindowAction;
import com.biometryczne.signature.controllers.actions.ControllerActionManager;
import com.biometryczne.signature.controllers.actions.EditSignatureAction;
import com.biometryczne.signature.nodes.JavaFXPenNode;
import com.biometryczne.signature.utils.SignatureCharacteristics;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import jpen.PenEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Almi on 2016-05-09.
 */
public class MainWindowController implements Initializable {

    private final static Logger log = LoggerFactory.getLogger(MainWindowController.class);

    @FXML
    private VBox vBoxCharts;

    @FXML
    public JavaFXPenNode mainSignatureCanvas;

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
    public void showCurrentSignature() {

        LineChart<Number, Number> XPosition = createLineChart(mainSignatureCanvas, "Pozycja na osi X", SignatureCharacteristics.X);
        LineChart<Number, Number> YPosition = createLineChart(mainSignatureCanvas, "Pozycja na osi Y", SignatureCharacteristics.Y);
        LineChart<Number, Number> PPosition = createLineChart(mainSignatureCanvas, "Nacisk", SignatureCharacteristics.PRESSURE);

        vBoxCharts.getChildren().clear();
        vBoxCharts.getChildren().addAll(XPosition, YPosition, PPosition);

    }

    @FXML
    public void editSignature() {
        log.info("Edit signature");
        actionManager.setPerformer(new EditSignatureAction());
        actionManager.perform(mainWindow);
    }

    @FXML
    public void clearSignatureCanvas() {
        log.info("Clear Signature canvas");
        actionManager.setPerformer(new ClearCanvasAction());
        actionManager.perform(mainWindow);
        showCurrentSignature();
    }

    private LineChart<Number, Number> createLineChart(JavaFXPenNode penNode, String title, SignatureCharacteristics characteristics) {
        LineChart<Number, Number> lineChart = new LineChart<>(new NumberAxis(), new NumberAxis());

        lineChart.setTitle(title);
        lineChart.setPrefSize(450, 200);

        XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
        series.setNode(new Rectangle(0, 0));

        List<Double> axisValues = penNode.getCanvasValues(characteristics);

        if(axisValues == null) {
            throw new IllegalArgumentException("Nieprawidlowy typ danych");
        }

        for (int i = 0; i < penNode.getCanvasValues(SignatureCharacteristics.X).size(); i++) {
            series.getData().add(new XYChart.Data<>(i, axisValues.get(i)));
        }

        lineChart.getData().add(series);

        lineChart.setCreateSymbols(false);

        return lineChart;
    }

}
