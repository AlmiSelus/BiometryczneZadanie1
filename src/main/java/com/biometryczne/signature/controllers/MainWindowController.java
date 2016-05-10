package com.biometryczne.signature.controllers;

import com.biometryczne.signature.controllers.actions.ClearCanvasAction;
import com.biometryczne.signature.controllers.actions.CloseWindowAction;
import com.biometryczne.signature.controllers.actions.ControllerActionManager;
import com.biometryczne.signature.controllers.actions.EditSignatureAction;
import com.biometryczne.signature.nodes.JavaFXPenNode;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Almi on 2016-05-09.
 */
public class MainWindowController implements Initializable {


    @FXML
    private VBox jPenPane;
    @FXML
    private Pane xValuesChart;
    @FXML
    private Pane yValuesChart;
    @FXML
    private Pane pValuesChart;
    @FXML
    VBox vBoxCharts;

    private final static Logger log = LoggerFactory.getLogger(MainWindowController.class);

    @FXML
    public JavaFXPenNode mainSignatureCanvas;

    //Referencja do głównego okna programu. Zwykle potrzebna ;)
    @FXML
    private BorderPane mainWindow;

    private ControllerActionManager actionManager = new ControllerActionManager();

    public void initialize(URL location, ResourceBundle resources) {
        mainSignatureCanvas = new JavaFXPenNode();
        jPenPane.getChildren().add(mainSignatureCanvas);
    }

    @FXML
    public void closeWindow() {
        actionManager.setPerformer(new CloseWindowAction());
        actionManager.perform(mainWindow);
    }

    //          -- tutaj jeszcze posprzatam
    @FXML
    public void showCurrentSignature() {
        LineChart<Number, Number> lineChart = new LineChart<Number, Number>(new NumberAxis(), new NumberAxis());

        lineChart.setTitle("pozycja na osi X");
        lineChart.setPrefSize(450, 200);

        // X VALUES
        XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
        series.setNode(new Rectangle(0, 0));

        for (int i = 0; i < mainSignatureCanvas.x.size(); i++) {
            series.getData().add(new XYChart.Data(i, mainSignatureCanvas.x.get(i)));
        }

        lineChart.getData().add(series);
//        vBoxCharts.getChildren().add(lineChart);

        // Y VALUES
        LineChart<Number, Number> lineChart2 = new LineChart<Number, Number>(new NumberAxis(), new NumberAxis());

        lineChart2.setTitle("pozycja na osi X");
        lineChart2.setPrefSize(450, 200);

        series = new XYChart.Series<Number, Number>();
        series.setNode(new Rectangle(0, 0));

        for (int i = 0; i < mainSignatureCanvas.x.size(); i++) {
            series.getData().add(new XYChart.Data(i, mainSignatureCanvas.y.get(i)));
        }

        lineChart2.getData().add(series);
//        vBoxCharts.getChildren().add(lineChart2);

        // Pressure VALUES
        LineChart<Number, Number> lineChart3 = new LineChart<Number, Number>(new NumberAxis(), new NumberAxis());

        lineChart3.setTitle("Nacisk");
        lineChart3.setPrefSize(450, 200);

        series = new XYChart.Series<Number, Number>();
        series.setNode(new Rectangle(0, 0));

        for (int i = 0; i < mainSignatureCanvas.x.size(); i++) {
            series.getData().add(new XYChart.Data(i, mainSignatureCanvas.p.get(i)));
        }

        lineChart3.getData().add(series);
        vBoxCharts.getChildren().clear();
        vBoxCharts.getChildren().addAll(lineChart, lineChart2, lineChart3);

    }

    @FXML
    public void editSignature(ActionEvent actionEvent) {
        log.info("Edit signature");
        actionManager.setPerformer(new EditSignatureAction());
        actionManager.perform(mainWindow);
    }

    @FXML
    public void clearSignatureCanvas(ActionEvent actionEvent) {
        log.info("Clear Signature canvas");
//        actionManager.setPerformer(new ClearCanvasAction());
//        actionManager.perform(mainWindow);


        System.out.println("Czyszcze!");
        mainSignatureCanvas.clearArrays();
    }

}
