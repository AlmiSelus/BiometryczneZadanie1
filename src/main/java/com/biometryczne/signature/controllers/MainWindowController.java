package com.biometryczne.signature.controllers;

import com.biometryczne.signature.beans.SignatureJSONBean;
import com.biometryczne.signature.controllers.actions.*;
import com.biometryczne.signature.nodes.JavaFXPenNode;
import com.biometryczne.signature.utils.Signature;
import com.biometryczne.signature.utils.SignatureCharacteristics;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.StageStyle;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Almi on 2016-05-09.
 */
public class MainWindowController implements Initializable {

    private final static Logger log = LoggerFactory.getLogger(MainWindowController.class);

    @FXML
    private VBox vBoxCharts;

//    @FXML
//    private VBox vBoxTableView;

    @FXML
    public JavaFXPenNode mainSignatureCanvas;

    @FXML
    private BorderPane mainWindow;

    private ControllerActionManager actionManager = new ControllerActionManager();

    private final TableView<Signature> tableView = new TableView<Signature>();

    private SessionFactory sessionFactory;

    public void initialize(URL location, ResourceBundle resources) {


        showCurrentSignature();
        addTableView();
        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        configuration.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:h2:./signatures");
        configuration.setProperty("hibernate.hbm2ddl.auto", "create");

        configuration.addAnnotatedClass(SignatureJSONBean.class);
        sessionFactory = configuration.buildSessionFactory();
        Session s = sessionFactory.openSession();
        Criteria c = s.createCriteria(SignatureJSONBean.class);
        List<SignatureJSONBean> list = c.list();
        s.close();

        for (SignatureJSONBean bean : list) {
            log.info(bean.toString());
        }
    }

    //----------------------------------------------------------------------------------------------------- table view
    private ObservableList<Signature> data = FXCollections.observableArrayList(
//            new Signature("a1")
    );

    @FXML
    TableView fxTable;

    private void addTableView() {

        fxTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
                if (fxTable.getSelectionModel().getSelectedItem() != null) {
                    int index = fxTable.getSelectionModel().getSelectedIndex();
                    log.info("\n\tSelected Value\t\t" + index);
                }
            }
        });

        TableColumn firstNameCol = new TableColumn("Name");
        firstNameCol.setCellValueFactory(
                new PropertyValueFactory<>("name"));

        fxTable.setItems(data);
        fxTable.getColumns().addAll(firstNameCol);
    }

    @FXML
    private void addTableItem() {
//        Signature tmp = mainSignatureCanvas.getSignature();
        TextInputDialog dialog = new TextInputDialog("nowy...");
        dialog.setTitle("Dodaj podpis");
        dialog.setHeaderText("Wpisz nazwę poniżej");
        dialog.setContentText("Nazwa:");//        tmp.setName(dialog.showAndWait().get());


        data.add(new Signature(mainSignatureCanvas.getSignature(), dialog.showAndWait().get()));

        for (int i = 0; i<data.size(); i++)
        {
            log.info("\n"+data.get(i).getName());
        }
    }

    @FXML
    private void renameTableItem() {
        int index = -1;


        index = fxTable.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            TextInputDialog dialog = new TextInputDialog(data.get(index).getName());
            dialog.setTitle("Zmień nazwę");
            dialog.setHeaderText("Wpisz nazwę poniżej");
            dialog.setContentText("Nowa nazwa:");
            data.get(index).setName(dialog.showAndWait().get());
            fxTable.refresh();
        }
    }

    @FXML
    private void removeTableItem() {
        int index = -1;

        index = fxTable.getSelectionModel().getSelectedIndex();
        if (index >= 0) data.remove(index);
    }

    @FXML
    private void showSelectedTableItem() {
        log.info("Showing selected item");
        int index = -1;

        index = fxTable.getSelectionModel().getSelectedIndex();
        if (index >= 0)
        {
            mainSignatureCanvas.setSignature(new Signature(data.get(index)));
            log.info("\n\tName: "+data.get(index).getName());
            log.info("\n\tLength: "+data.get(index).get(SignatureCharacteristics.Y).size());
        }

//        showCurrentSignature();
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
    public void filterCurrentSignature() {
        log.info("Filter Signature Data");
        actionManager.setPerformer(new FilterCharacteristicsAction());

        actionManager.perform(mainWindow);
    }


    @FXML
    public void editSignature() {
        log.info("Edit signature");
        actionManager.setPerformer(new EditSignatureAction(sessionFactory));
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

        if (axisValues == null) {
            throw new IllegalArgumentException("Nieprawidlowy typ danych");
        }

        for (int i = 0; i < penNode.getCanvasValues(SignatureCharacteristics.X).size(); i++) {
            series.getData().add(new XYChart.Data<>(i, axisValues.get(i)));
        }

        lineChart.getData().add(series);

        lineChart.setCreateSymbols(false);

        return lineChart;
    }

    @FXML
    public void computeCorrelation() {
        double[] x = mainSignatureCanvas.getCanvasValuesAsArray(SignatureCharacteristics.X);
        double[] y = mainSignatureCanvas.getCanvasValuesAsArray(SignatureCharacteristics.Y);
        double[] p = mainSignatureCanvas.getCanvasValuesAsArray(SignatureCharacteristics.PRESSURE);
        actionManager.setPerformer(new ComputeCorrelationAction(x, y, p, sessionFactory));
        actionManager.perform(mainWindow);
    }
}
