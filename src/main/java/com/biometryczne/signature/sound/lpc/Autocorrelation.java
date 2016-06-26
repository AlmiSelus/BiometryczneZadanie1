package com.biometryczne.signature.sound.lpc;

/**
 * Created by Almi on 2016-06-26.
 */
public class Autocorrelation {
    public double autocorrelate(double[] buffer, int lag) {
        if(lag > -1 && lag < buffer.length) {
            double result = 0.0;
            for (int i = lag; i < buffer.length; i++) {
                result += buffer[i] * buffer[i - lag];
            }
            return result;
        } else {
            throw new IndexOutOfBoundsException("Lag parameter range is : -1 < lag < buffer size. Received ["
                    + lag + "] for buffer size of [" + buffer.length + "]");
        }
    }
}
