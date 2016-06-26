package com.biometryczne.signature.sound.features;

import com.biometryczne.signature.sound.lpc.LinearPredictiveCoding;
import com.biometryczne.signature.sound.windows.HanningWindow;
import com.biometryczne.signature.sound.windows.IWindow;

/**
 * Created by Almi on 2016-06-26.
 */
public class LpcFeaturesExtractor extends WindowExtractor<double[]> {

    private final int poles;
    private final IWindow windowFunction;
    private final LinearPredictiveCoding lpc;

    public LpcFeaturesExtractor(float sampleRate, int poles) {
        super(sampleRate);
        this.poles = poles;
        this.windowFunction = new HanningWindow(windowSize);
        this.lpc = new LinearPredictiveCoding(windowSize, poles);
    }

    @Override
    public double[] extractFeatures(double[] voiceSample) {
        double[] voiceFeatures = new double[poles];
        double[] audioWindow = new double[windowSize];

        int counter = 0;
        int halfWindowLength = windowSize / 2;

        for (int i = 0; (i + windowSize) <= voiceSample.length; i += halfWindowLength) {

            System.arraycopy(voiceSample, i, audioWindow, 0, windowSize);

            double[] windowedData = windowFunction.computeWindow(audioWindow);
            double[] lpcCoeffs = lpc.applyLinearPredictiveCoding(windowedData)[0];

            for (int j = 0; j < poles; j++) {
                voiceFeatures[j] += lpcCoeffs[j];
            }
            counter++;
        }

        if (counter > 1) {
            for (int i = 0; i < poles; i++) {
                voiceFeatures[i] /= counter;
            }
        }
        return voiceFeatures;
    }
}
