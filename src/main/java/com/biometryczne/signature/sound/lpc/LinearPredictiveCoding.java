package com.biometryczne.signature.sound.lpc;

import java.util.Arrays;

/**
 * Created by Almi on 2016-06-26.
 */
public class LinearPredictiveCoding {
    private final int windowSize;
    private final int poles;
    private final double[] output;
    private final double[] error;
    private final double[] k;
    private final double[][] matrix;

    public LinearPredictiveCoding(int windowSize, int poles) {
        this.windowSize = windowSize;
        this.poles = poles;
        this.output = new double[poles];
        this.error = new double[poles];
        this.k = new double[poles];
        this.matrix = new double[poles][poles];
    }

    public double[][] applyLinearPredictiveCoding(double[] window) {

        if(windowSize != window.length) {
            throw new IllegalArgumentException("Given window length was not equal to the one provided in constructor : ["
                    + window.length +"] != [" + windowSize + "]");
        }

        Arrays.fill(k,  0.0d);
        Arrays.fill(output, 0.0d);
        Arrays.fill(error, 0.0d);
        for(double[] d : matrix) {
            Arrays.fill(d, 0.0d);
        }

        Autocorrelation dalj = new Autocorrelation();
        double[] autocorrelations = new double[poles];
        for(int i = 0; i < poles; i++) {
            autocorrelations[i] = dalj.autocorrelate(window, i);
        }

        error[0] = autocorrelations[0];

        for (int m = 1; m < poles; m++) {
            double tmp = autocorrelations[m];
            for (int i = 1; i < m; i++) {
                tmp -= matrix[m - 1][i] * autocorrelations[m - i];
            }
            k[m] = tmp / error[m - 1];

            for (int i = 0; i < m; i++) {
                matrix[m][i] = matrix[m - 1][i] - k[m] * matrix[m - 1][m - i];
            }
            matrix[m][m] = k[m];
            error[m] = (1 - (k[m] * k[m])) * error[m - 1];
        }

        for (int i = 0; i < poles; i++) {
            if (Double.isNaN(matrix[poles - 1][i])) {
                output[i] = 0.0;
            } else {
                output[i] = matrix[poles - 1][i];
            }
        }

        return new double[][] { output, error };
    }
}
