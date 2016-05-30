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
import com.fastdtw.util.Distances;
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

/**
 * Created by Almi on 2016-05-30.
 */
public class ComputeCorrelationAction implements IControllerAction<Void> {

    private final static Logger log = LoggerFactory.getLogger(ComputeCorrelationAction.class);

    private List<SignatureJSONBean> jsonBeans = new ArrayList<>();
    private TimeSeries x;
    private TimeSeries y;
    private TimeSeries p;
    private SessionFactory sessionFactory; //polaczenie z baza danych

    public ComputeCorrelationAction(double[] x, double[] y, double[] p, SessionFactory sessionFactory) {
        this.x = getTimeSeries(x);
        this.y = getTimeSeries(y);
        this.p = getTimeSeries(p);
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Void perform(Pane mainPane) {
        SignatureJSONBean selectedSignature = null;
        double xDTW = Double.MAX_VALUE;
        double yDTW = Double.MAX_VALUE;
        double pDTW = Double.MAX_VALUE;

        jsonBeans = getAll();
        //pozniej mozna przepisac calego fora na zapytanie SQLowe :)
        for(SignatureJSONBean bean : jsonBeans) {

            TimeSeries xFromDB = getTimeSeries(bean.getX());
            TimeSeries yFromDB = getTimeSeries(bean.getY());
            TimeSeries pFromDB = getTimeSeries(bean.getP());

            double dtwx = FastDTW.compare(xFromDB, x, 10, Distances.EUCLIDEAN_DISTANCE).getDistance();
            double dtwy = FastDTW.compare(yFromDB, y, 10, Distances.EUCLIDEAN_DISTANCE).getDistance();
            double dtwp = FastDTW.compare(pFromDB, p, 10, Distances.EUCLIDEAN_DISTANCE).getDistance();

            if(dtwx < xDTW && yDTW > dtwy && pDTW > dtwp) {
                selectedSignature = bean;
                xDTW = dtwx;
                yDTW = dtwy;
                pDTW = dtwp;
            }

        }
        Label selectedUserInfo = ((Label)mainPane.lookup("#selectedUser"));
        if(selectedSignature != null) {
            selectedUserInfo.setText(selectedSignature.getName() + " " + " Wynik DTW: " +
                    "x = " + xDTW + " y = " + yDTW + " p = " + pDTW);
        } else {
            selectedUserInfo.setText("Uzytkownik nie znaleziony! Dodaj informacje do bazy");
        }

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
