package com.biometryczne.signature.controllers.actions;

import com.biometryczne.signature.beans.SignatureJSONBean;
import com.fastdtw.dtw.FastDTW;
import com.fastdtw.timeseries.TimeSeries;
import com.fastdtw.timeseries.TimeSeriesBase;
import com.fastdtw.timeseries.TimeSeriesItem;
import com.fastdtw.timeseries.TimeSeriesPoint;
import com.fastdtw.util.EuclideanDistance;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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


        Map<Double, SignatureJSONBean> competition = new TreeMap<>();

        jsonBeans = getAll();
        int localLenght = x.length;
        int dbLength = 0;


        int index = 0;
        for (SignatureJSONBean bean : jsonBeans)        // for each in dataBase
        {
            dbLength = bean.getX().length;

            //macierz kosztów
            double[][] costMatrix = new double[localLenght][dbLength];

            for (int i = 0; i < localLenght; i++) {
                for (int j = 0; j < dbLength; j++) {

                    double[] localVector = {x[i], y[i], p[i]};
                    double[] dbVector = {bean.getX()[j], bean.getY()[j], bean.getP()[j]};

                    if (i == 0 && j == 0) {
                        costMatrix[i][j] = distance3d(localVector, dbVector);
                    } else if (i == 0 && j != 0) {
                        costMatrix[i][j] = distance3d(localVector, dbVector) + costMatrix[i][j - 1];
                    } else if (i != 0 && j == 0) {
                        costMatrix[i][j] = distance3d(localVector, dbVector) + costMatrix[i - 1][j];
                    } else {
                        ArrayList<Double> tmp = new ArrayList<>();
                        tmp.add(costMatrix[i][j - 1]);
                        tmp.add(costMatrix[i - 1][j - 1]);
                        tmp.add(costMatrix[i - 1][j]);

                        costMatrix[i][j] =
                                distance3d(localVector, dbVector) + Collections.min(tmp);
                    }
                }
            }

            log.info("db " + index + ": " + dbLength + ", DTW: " + costMatrix[localLenght - 1][dbLength - 1]);
            competition.put(costMatrix[localLenght - 1][dbLength - 1], bean);
            index++;
        }

       int indx = 0;
        for (Map.Entry<Double, SignatureJSONBean> entry : competition.entrySet()) {
            if (indx < ITEMS_TO_EXTRACT) {
                if (entry.getKey() < 5000)
                {

                    log.info(entry.getValue().getName() + ", DTW = " + entry.getKey());

                }
                else log.info("Uzytkownik nieznany, DTW = " + entry.getKey());
            }
            indx++;
        }

        Label selectedUserInfo = ((Label) mainPane.lookup("#selectedUser"));
        return null;
    }

    private double distance3d(double x[], double y[]) {
        return Math.sqrt(
                Math.pow((x[0] - y[0]), 2) +           //x
                        Math.pow((x[1] - y[1]), 2) +   //y
                        Math.pow((x[2] - y[2]), 2)     //p
        );
    }

   private TimeSeries getTimeSeries(double[] fromBean) {

        List<TimeSeriesItem> items = new ArrayList<>();
        for (int i = 0; i < fromBean.length; ++i) {
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
