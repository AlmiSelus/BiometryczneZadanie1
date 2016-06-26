package com.biometryczne.signature.sound.windows;

/**
 * Created by Almi on 2016-06-06.
 */
public class HanningWindow implements IWindow {

    private final int windowSize;
    private final double[] factors;

    public HanningWindow(int windowSize) {
        this.windowSize = windowSize;
        this.factors = getPrecomputedFactors(windowSize);
    }

    private double[] getPrecomputedFactors(int windowSize) {
        double[] factors;
        factors = new double[windowSize];
        int sizeMinusOne = windowSize - 1;
        for(int i = 0; i < windowSize; i++) {
            factors[i] = 0.5d * (1 - Math.cos((2 * Math.PI * i) / sizeMinusOne));
        }
        return factors;
    }

    @Override
    public double[] computeWindow(double[] window) {
        double[] resultWindow = new double[window.length];
        if (window.length == this.windowSize) {
            for (int i = 0; i < window.length; i++) {
                resultWindow[i] = window[i] * factors[i];
            }
        }else {
            throw new IllegalArgumentException();
        }

        return resultWindow;
    }
}
