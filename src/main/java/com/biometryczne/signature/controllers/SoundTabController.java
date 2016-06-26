package com.biometryczne.signature.controllers;

import com.biometryczne.signature.beans.SignatureJSONBean;
import com.biometryczne.signature.controllers.actions.AudioAction;
import com.biometryczne.signature.controllers.actions.CaptureAudioAction;
import com.biometryczne.signature.controllers.actions.ControllerActionManager;
import com.biometryczne.signature.controllers.actions.PlayAudioAction;
import com.biometryczne.signature.nodes.JavaFXFreeChart;
import com.biometryczne.signature.sound.SoundRecognitionSystem;
import com.biometryczne.signature.sound.VoiceEntry;
import com.biometryczne.signature.sound.dao.SoundDao;
import com.biometryczne.signature.utils.AudioEvent;
import com.biometryczne.signature.utils.SignatureEntry;
import com.biometryczne.signature.utils.SoundUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
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
 * Created by Almi on 2016-06-26.
 */
public class SoundTabController extends BorderPane implements Initializable, AudioAction.OnAudioListener {

    private final static Logger log = LoggerFactory.getLogger(SoundTabController.class);

    @FXML
    public TableView<VoiceEntry> soundDatabaseTable;

    @FXML
    private Button recordButton;

    @FXML
    private Button playButton;

    private ObservableList<VoiceEntry> voiceData = FXCollections.observableArrayList();

    private SoundDao soundDao;
    private SoundRecognitionSystem srs;

    private ByteArrayOutputStream out;
    private ControllerActionManager actionManager = new ControllerActionManager();
    private SessionFactory sessionFactory;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TableColumn<VoiceEntry, String> firstNameCol = new TableColumn<>("Name");
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        soundDatabaseTable.setItems(voiceData);
        soundDatabaseTable.getColumns().addAll(firstNameCol);

        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        configuration.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:h2:./voices");
        configuration.setProperty("hibernate.hbm2ddl.auto", "create");
        configuration.addAnnotatedClass(VoiceEntry.class);
        sessionFactory = configuration.buildSessionFactory();
        populateTable();

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
        actionManager.perform(this);
    }

    @FXML
    public void playAudio() {
        actionManager.setPerformer(new PlayAudioAction(out));
        actionManager.perform(this);
    }

    @FXML
    public void identify() {
//        srs.identify() //tutaj trzeba dodac rozpoznawanie nowej probki dzwieku.
    }


    private JavaFXFreeChart createChart(double[] data, String datasetName) {
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
        dataset.addSeries(fdata, 0, "Input data");
        final JFreeChart result = ChartFactory.createTimeSeriesChart(datasetName, "time", "frequency", dataset, false, true, false);
        final XYPlot plot = result.getXYPlot();
        ValueAxis domain = plot.getDomainAxis();
        domain.setAutoRange(true);
        ValueAxis range = plot.getRangeAxis();
        range.setRange(min, max);
        return new JavaFXFreeChart(new ChartPanel(result));
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
        double min = Double.MAX_VALUE;
        double[] data = new double[lastIndex+1];
        for(int i = 0; i < data.length ;++i) {
            data[i] = data2[i];
            if(data[i] > max) {
                max = data[i];
            }

            if(data[i] < min) {
                min = data[i];
            }
        }

        log.info("Max = " + max);
        log.info("Min = " + min);

        for(int i = 0; i < data.length; ++i) {
            data[i] = data[i] > 0 ? data[i]/max : data[i]/min;
        }

        log.info(Arrays.toString(data));

        AudioEvent event = new AudioEvent(data);
        event.setDataForProcessing(0, 0, data.length -1 );

        JavaFXFreeChart chart = createChart(data, "Wykres dzwieku w dziedzinie czasu");
        setCenter(chart);


        srs.createVoicePrint("User", data);
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void populateTable() {
        soundDao = new SoundDao(sessionFactory);
        srs = new SoundRecognitionSystem(44100, soundDao);
        List<VoiceEntry> list = soundDao.getAll();
        for(VoiceEntry bean : list) {
            voiceData.add(bean);
        }
    }
}
