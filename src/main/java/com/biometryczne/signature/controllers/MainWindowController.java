package com.biometryczne.signature.controllers;

import com.biometryczne.signature.beans.SignatureJSONBean;
import com.biometryczne.signature.controllers.actions.*;
import com.biometryczne.signature.controllers.actions.AudioAction.OnAudioListener;
import com.biometryczne.signature.dao.SignatureDAO;
import com.biometryczne.signature.nodes.JavaFXPenNode;
import com.biometryczne.signature.sound.SoundRecognitionSystem;
import com.biometryczne.signature.sound.VoiceEntry;
import com.biometryczne.signature.sound.dao.SoundDao;
import com.biometryczne.signature.sound.windows.HanningWindow;
import com.biometryczne.signature.utils.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.DynamicTimeSeriesCollection;
import org.jfree.data.time.Millisecond;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Almi on 2016-05-09.
 */
public class MainWindowController implements Initializable, OnAudioListener, AudioEvent.AudioListener {

    private final static Logger log = LoggerFactory.getLogger(MainWindowController.class);

    @FXML
    private VBox vBoxCharts;

    @FXML
    public JavaFXPenNode mainSignatureCanvas;

    @FXML
    private BorderPane mainWindow;

    @FXML
    private TableView fxTable;

    @FXML
    private Button recordButton;

    @FXML
    private Button playButton;

    private ObservableList<SignatureEntry> data = FXCollections.observableArrayList();

    private ControllerActionManager actionManager = new ControllerActionManager();

    private SessionFactory sessionFactory;

    private SignatureDAO signatureDAO;

    private SoundDao soundDao;
    private SoundRecognitionSystem srs;

    private ByteArrayOutputStream out;

    private boolean isRecording = false;

    public void initialize(URL location, ResourceBundle resources) {


        showCurrentSignature();
        addTableView();
        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        configuration.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:h2:./signatures");

        configuration.addAnnotatedClass(SignatureJSONBean.class);
        configuration.addAnnotatedClass(VoiceEntry.class);
        sessionFactory = configuration.buildSessionFactory();
        signatureDAO = new SignatureDAO(sessionFactory);
        soundDao = new SoundDao(sessionFactory);

        srs = new SoundRecognitionSystem(44100, soundDao);

        List<SignatureJSONBean> list = signatureDAO.getAll();
        for(SignatureJSONBean bean : list) {
            data.add(new SignatureEntry(bean.getId(), bean.getName()));
        }

        playButton.setDisable(true);

    }

    //----------------------------------------------------------------------------------------------------- table view


    private void addTableView() {
/*
        fxTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
                if (fxTable.getSelectionModel().getSelectedItem() != null) {
                    int index = fxTable.getSelectionModel().getSelectedIndex();
                    log.info("\n\tSelected Value\t\t" + index);
                }
            }
        });
*/
        TableColumn firstNameCol = new TableColumn<>("Name");
        firstNameCol.setCellValueFactory(new PropertyValueFactory("name"));

        fxTable.setItems(data);
        fxTable.getColumns().addAll(firstNameCol);
    }

    @FXML
    private void addTableItem() {
        TextInputDialog dialog = new TextInputDialog("nowy...");
        dialog.setTitle("Dodaj podpis");
        dialog.setHeaderText("Wpisz nazwę poniżej");
        dialog.setContentText("Nazwa:");//        tmp.setName(dialog.showAndWait().get());

        String name = dialog.showAndWait().get();

        Signature sig = mainSignatureCanvas.getSignature();
        int id = data.size()+1;
        data.add(new SignatureEntry(id, name));


        SignatureJSONBean bean = new SignatureJSONBean();
        bean.setId(id);
        bean.setName(name);
        bean.setX(sig.getAsArray(SignatureCharacteristics.X));
        bean.setY(sig.getAsArray(SignatureCharacteristics.Y));
        bean.setP(sig.getAsArray(SignatureCharacteristics.PRESSURE));
        signatureDAO.create(bean);

//        for (int i = 0; i<data.size(); i++)
//        {
//            log.info("\n"+data.get(i).getName());
//        }
    }

    @FXML
    private void renameTableItem() {
        int index = fxTable.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            int indexForDB = data.get(index).getId();
            TextInputDialog dialog = new TextInputDialog(data.get(index).getName());
            dialog.setTitle("Zmień nazwę");
            dialog.setHeaderText("Wpisz nazwę poniżej");
            dialog.setContentText("Nowa nazwa:");
            String name = dialog.showAndWait().get();
            data.get(index).setName(name);

            SignatureJSONBean bean = signatureDAO.getById(indexForDB);
            bean.setName(name);
            signatureDAO.update(bean);

            fxTable.refresh();
        }
    }

    @FXML
    private void removeTableItem() {
        int index = fxTable.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            int indexForDB = data.get(index).getId();
            data.remove(index);
            signatureDAO.remove(indexForDB);
        }
    }



    @FXML
    private void showSelectedTableItem() {
        log.info("Showing selected item");
        int index = fxTable.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            int indexForDB = data.get(index).getId();
            SignatureJSONBean bean = signatureDAO.getById(indexForDB);
            Signature signature = new Signature();
            signature.setName(bean.getName());
            signature.addAll(bean.getX(), SignatureCharacteristics.X);
            signature.addAll(bean.getY(), SignatureCharacteristics.Y);
            signature.addAll(bean.getP(), SignatureCharacteristics.PRESSURE);

            mainSignatureCanvas.setSignature(signature);
            log.info("\n\tName: "+bean.getName());
            log.info("\n\tLength: "+bean.getY().length);
        }

        showCurrentSignature();
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

    private CaptureAudioAction captureAudioAction;

    @FXML
    public void recordAudio() {
        if(captureAudioAction == null) {
            captureAudioAction = new CaptureAudioAction(true, this);
            recordButton.setText("Zakończ nagrywanie dźwięku");
            playButton.setDisable(true);
        } else {
            captureAudioAction.stopRecording();
            recordButton.setText("Rozpocznij nagrywanie dźwięku");
//            playButton.setDisable(false);
        }
        actionManager.setPerformer(captureAudioAction);
        actionManager.perform(mainWindow);
    }

    @FXML
    public void playAudio() {
        actionManager.setPerformer(new PlayAudioAction(out));
        actionManager.perform(mainWindow);
    }

    @Override
    public void onCaptured(ByteArrayOutputStream outputStream) throws IOException {
        this.out = outputStream;
        playButton.setDisable(false);

        double[] data2 = SoundUtils.convertToDouble(out.toByteArray());

        int lastIndex = Integer.MAX_VALUE;
        for(int i = data2.length -1; i >= 0; --i) {
            if(data2[i] == 0.0) {
                lastIndex = i;
            } else {
                break;
            }
        }

        double max = Double.MIN_VALUE;
        double[] data = new double[lastIndex+1];
        for(int i = 0; i < data.length ;++i) {
            data[i] = data2[i];
            if(data[i] > max) {
                max = data[i];
            }
        }

        for(int i = 0; i < data.length; ++i) {
            data[i] = data[i]/max;
        }

        log.info(Arrays.toString(data));

        AudioEvent event = new AudioEvent(data);
        event.setDataForProcessing(0, 0, data.length -1 );

        createChart(data, "Wykres dzwieku w dziedzinie czasu");
    }

    private void createChart(double[] data, String datasetName) {
        float[] fdata = new float[data.length];
        int i = 0;
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for(Double f : data) {
            fdata[i] = f.floatValue();
            if(f < min) {
                min = f;
            }

            if(f > max ) {
                max = f;
            }
            i++;
        }

        final DynamicTimeSeriesCollection dataset =
                new DynamicTimeSeriesCollection(1, data.length, new Millisecond());
        dataset.setTimeBase(new Millisecond());
        dataset.addSeries(fdata, 0, "Gaussian data");
        final JFreeChart result = ChartFactory.createTimeSeriesChart(datasetName, "time", "frequency", dataset, false, true, false);
        final XYPlot plot = result.getXYPlot();
        ValueAxis domain = plot.getDomainAxis();
        domain.setAutoRange(true);
        ValueAxis range = plot.getRangeAxis();
        range.setRange(min, max);

        class FramedChart extends ApplicationFrame {

            public FramedChart(String title) {
                super(title);
                add(new ChartPanel(result), BorderLayout.CENTER);
            }
        }
        FramedChart demo = new FramedChart("Test");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }

    @Override
    public void onProcessed(AudioEvent event, double[] data, String datasetName) {
        createChart(data, datasetName);
    }


}
