package com.biometryczne.signature.controllers;

import com.biometryczne.signature.controllers.actions.AudioAction;
import com.biometryczne.signature.controllers.actions.CaptureAudioAction;
import com.biometryczne.signature.controllers.actions.ControllerActionManager;
import com.biometryczne.signature.controllers.actions.PlayAudioAction;
import com.biometryczne.signature.nodes.JavaFXFreeChart;
import com.biometryczne.signature.sound.SoundRecognitionSystem;
import com.biometryczne.signature.sound.VoiceEntry;
import com.biometryczne.signature.sound.VoiceMatch;
import com.biometryczne.signature.sound.dao.SoundDao;
import com.biometryczne.signature.utils.SoundUtils;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.DynamicTimeSeriesCollection;
import org.jfree.data.time.Millisecond;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public Button identifyButton;

    @FXML
    private Button recordButton;

    @FXML
    private Button playButton;

    @FXML
    private BorderPane soundWindow;

    @FXML
    private VBox soundChartsVBox;

    @FXML
    private Button loadButton;

    private ObservableList<VoiceEntry> voiceData = FXCollections.observableArrayList();

    private SoundDao soundDao;
    private SoundRecognitionSystem srs;

    private ByteArrayOutputStream out;
    private ControllerActionManager actionManager = new ControllerActionManager();
    private SessionFactory sessionFactory;

    private final static int THRESHOLDING = 50;

    ImageView[] images;

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
//        configuration.setProperty("hibernate.hbm2ddl.auto", "update");
        configuration.addAnnotatedClass(VoiceEntry.class);
        sessionFactory = configuration.buildSessionFactory();
        populateTable();

        if(soundDao.getAll().size() > 0) {
            VoiceEntry entry = soundDao.getAll().get(0);
            srs.setUniversalModel(entry);
        }

        playButton.setDisable(true);
        identifyButton.setDisable(true);
        images = new ImageView[] {
                 new ImageView(new Image(getClass().getResourceAsStream("/images/record.png"))),
                 new ImageView(new Image(getClass().getResourceAsStream("/images/stop.png"))),
                 new ImageView(new Image(getClass().getResourceAsStream("/images/play.png"))),
                 new ImageView(new Image(getClass().getResourceAsStream("/images/fingerprint.png")))
        };

        recordButton.setGraphic(images[0]);
        playButton.setGraphic(images[2]);
        identifyButton.setGraphic(images[3]);
    }

    private CaptureAudioAction captureAudioAction;

    @FXML
    public void recordAudio() {
        if(captureAudioAction == null) {
            captureAudioAction = new CaptureAudioAction(true, this);
//            recordButton.setText("Zakończ nagrywanie dźwięku");
            playButton.setDisable(true);
            identifyButton.setDisable(true);
            recordButton.setGraphic(images[1]);
        } else {
            captureAudioAction.stopRecording();
//            recordButton.setText("Rozpocznij nagrywanie dźwięku");
            recordButton.setGraphic(images[0]);
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
        List<VoiceMatch> matches = srs.identify(normalizeInput(out));
        VoiceMatch foundMatch = null;
        for(VoiceMatch match : matches) {
            if(foundMatch != null) {
                if (match.getProbability() >= THRESHOLDING && foundMatch.getProbability() < match.getProbability()) {
                    foundMatch = match;
                }
            } else {
                if(match.getProbability() >= THRESHOLDING ) {
                    foundMatch = match;
                }
            }
        }
        String title = "Identyfikacja użytkownika";
        if(foundMatch != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(title);
            alert.setContentText("Użytkownik zidentyfikowany jako " + foundMatch.getKey() + "! (Prawdopodobieństwo: "+foundMatch.getProbability()+"%)");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(title);
            alert.setContentText("Niestety, nie znaleziono użytkownika!");

            alert.showAndWait();
        }
    }


    private JavaFXFreeChart createChart(double[] data, String datasetName) {
        float[] fdata = new float[data.length];
        int i = 0;
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        XYSeries xySeries = new XYSeries("Input data");
        for(Double f : data) {
            fdata[i] = f.floatValue();
            if(f < min) {
                min = f;
            }

            if(f > max ) {
                max = f;
            }

            xySeries.add(i, f);

            i++;
        }

        final XYDataset dataset = new XYSeriesCollection(xySeries);
        JFreeChart result = ChartFactory.createXYAreaChart(datasetName, "sample count", "frequency", dataset, PlotOrientation.VERTICAL, false, true, false);
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
        identifyButton.setDisable(false);


        double[] data = normalizeInput(out);
        JavaFXFreeChart chart = createChart(data, "Wykres dzwieku w dziedzinie czasu");
        Platform.runLater(()-> {
            soundChartsVBox.getChildren().clear();
            soundChartsVBox.getChildren().add(chart);
        });
    }

    public void populateTable() {
        soundDao = new SoundDao(sessionFactory);
        srs = new SoundRecognitionSystem(44100, soundDao);
        List<VoiceEntry> list = soundDao.getAll();
        for(VoiceEntry bean : list) {
            voiceData.add(bean);
        }
    }

    @FXML
    public void addToDatabase() {
        double[] data = normalizeInput(out);
        TextInputDialog nameDialog = new TextInputDialog("Nowy użytkownik");
        nameDialog.setTitle("Dodaj podpis");
        nameDialog.setHeaderText("Podaj nazwę użytkownika");
        nameDialog.setContentText("Nazwa:");
        String name = nameDialog.showAndWait().get();
        VoiceEntry voice = srs.createVoiceEntry(name, data); //dodaje do bazy
        if(voice != null) {
            voiceData.add(voice);
        }
    }

    @FXML
    public void loadFromDatabase() {
        VoiceEntry entry = soundDatabaseTable.getSelectionModel().getSelectedItem();
        if(entry != null) {
            JavaFXFreeChart chart = createChart(entry.getData(), "Wykres dzwieku dla użytkownika " + entry.getName());
            Platform.runLater(() -> {
                soundChartsVBox.getChildren().clear();
                soundChartsVBox.getChildren().add(chart);
            });

        }
    }

    private double[] normalizeInput(ByteArrayOutputStream stream) {
        double[] data2 = SoundUtils.convertToDouble(stream.toByteArray());

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
        double[] data = new double[lastIndex];
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
            if(data[i] < 0.0f) {
                data[i] = data[i]/min;
            } else if(data[i] > 0.0f) {
                data[i] = data[i]/max;
            }
        }

        log.info(Arrays.toString(data));
        return data;
    }
}
