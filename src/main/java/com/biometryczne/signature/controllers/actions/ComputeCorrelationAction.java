package com.biometryczne.signature.controllers.actions;

import com.biometryczne.signature.beans.SignatureJSONBean;
import com.biometryczne.signature.utils.Signature;
import com.biometryczne.signature.utils.SignatureCharacteristics;
import com.fastdtw.dtw.DTW;
import com.fastdtw.dtw.FastDTW;
import com.fastdtw.timeseries.TimeSeries;
import com.fastdtw.timeseries.TimeSeriesBase;
import com.fastdtw.timeseries.TimeSeriesItem;
import com.fastdtw.timeseries.TimeSeriesPoint;
import com.fastdtw.util.DistanceFunction;
import com.fastdtw.util.Distances;
import com.fastdtw.util.EuclideanDistance;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Almi on 2016-05-30.
 */
public class ComputeCorrelationAction implements IControllerAction<Void> {

    private final static Logger log = LoggerFactory.getLogger(ComputeCorrelationAction.class);

    private final static int ITEMS_TO_EXTRACT = 3;

    private List<SignatureJSONBean> jsonBeans = new ArrayList<>();
    private double[] x;
    private double[] y;
    private double[] p;
    private SessionFactory sessionFactory; //polaczenie z baza danych

    public ComputeCorrelationAction(double[] x, double[] y, double[] p, SessionFactory sessionFactory) {
        this.x = x;
        this.y = y;
        this.p = p;
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Void perform(Pane mainPane) {
//        SignatureJSONBean selectedSignature = null;
//        double prevDTW = Double.MAX_VALUE;

        Map<Double, SignatureJSONBean> competition = new TreeMap<>();

        jsonBeans = getAll();
        //pozniej mozna przepisac calego fora na zapytanie SQLowe :)
        for(SignatureJSONBean bean : jsonBeans) {

            TimeSeries dtwFromDB = TimeSeriesBase.builder().add(0, bean.getX()).add(1, bean.getY()).add(2, bean.getP()).build();
            TimeSeries localDtw  = TimeSeriesBase.builder().add(0, x).add(1, y).add(2, p).build();

            double dtw = FastDTW.compare(dtwFromDB, localDtw, 10, new EuclideanDistance()).getDistance();
            competition.put(dtw, bean);

        }

        int index = 0;
        for(Map.Entry<Double, SignatureJSONBean> entry : competition.entrySet()) {
            if(index < ITEMS_TO_EXTRACT) {
                log.info("DTW = " + entry.getKey() + " " + entry.getValue().toString());
            }
            index++;
        }

        Label selectedUserInfo = ((Label)mainPane.lookup("#selectedUser"));
//        if(selectedSignature != null) {
//            selectedUserInfo.setText(selectedSignature.getName() + " " + " Wynik DTW: " + prevDTW);
//        } else {
//            selectedUserInfo.setText("Uzytkownik nie znaleziony! Dodaj informacje do bazy");
//        }

        return null;
    }

    private TimeSeries getTimeSeries(double[] fromBean) {

        List<TimeSeriesItem> items = new ArrayList<>();
        for(int i = 0; i < fromBean.length; ++i) {
            items.add(new TimeSeriesItem(i, new TimeSeriesPoint(new double[]{fromBean[i]})));
        }
        TimeSeries ts = new TimeSeriesBase(items);
        return ts;

    }

    private List<SignatureJSONBean> getAll() {
        List<SignatureJSONBean> response = new ArrayList<>();
        Session s = sessionFactory.openSession();
        Criteria c = s.createCriteria(SignatureJSONBean.class);
        response = c.list();
        s.close();
        return response;
    }

}
