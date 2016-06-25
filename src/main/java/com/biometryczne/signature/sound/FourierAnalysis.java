package com.biometryczne.signature.sound;

import com.biometryczne.signature.sound.windows.HanningWindow;
import com.biometryczne.signature.sound.windows.IWindow;
import com.biometryczne.signature.utils.AudioEvent;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Almi on 2016-06-06.
 */
public class FourierAnalysis implements IFrequencyCalculator {

    private final static Logger log = LoggerFactory.getLogger(FourierAnalysis.class);
    private FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD);

    private IWindow windowFunction = new HanningWindow();
    private AudioEvent.AudioListener listener;

    public FourierAnalysis(IWindow windowFunction) {
        this.windowFunction = windowFunction;
    }

    private double log(double val) {
        return val > 0 ?  Math.log(val) : 0;
    }

    private double[] threshold2(double[] arr) {
        List<Double> b = Arrays.asList(ArrayUtils.toObject(arr));
        double t = 0.4*Collections.max(b);
        double[] newarr = new double[arr.length];
        newarr[0] = t;
        for(int i = 1; i < arr.length; ++i) {
            newarr[i] = arr[i] < t ? 0 : arr[i];
        }
        return newarr;
    }

    private double[] threshold(double[] arr) {
        double[] newarr = new double[arr.length];
        for(int i = 0; i < arr.length; ++i) {
            newarr[i] = log(arr[i]);
        }
        return newarr;
    }

    @Override
    public void setAudioListener(AudioEvent.AudioListener listener) {
        this.listener = listener;
    }

    @Override
    public float calculate(AudioEvent event) {

        List<Double> frame = event.getDataForProcessing();
        int frames = frame.size();

        int nexPowerOfTwo = nextPowerOf2(frames);
        log.info("Next power of 2: " + nextPowerOf2(frames));

        double[] array = new double[nexPowerOfTwo];
        for(int i = 0; i < nexPowerOfTwo; ++i) {
            //create and pad with zeros
            if(i < frame.size()) {
                array[i] = frame.get(i);
            } else {
                array[i] = 0;
            }
        }

        for(int i = 0; i < frames; ++i) {
            array[i] = frame.get(i)*windowFunction.computeWindow(frames, i);
        }

        //Step 1: FFT Input
        Complex[] spectrum = transformer.transform(array, TransformType.FORWARD);
        double[] simpleSpectrum = new double[spectrum.length/2];
        for(int i = 0; i < simpleSpectrum.length; ++i) {
            Complex c = spectrum[i];
            //Step 2: Log Magnitude of FFT Input
//            simpleSpectrum[i] = log(Math.sqrt(c.getReal() * c.getReal() + c.getImaginary() * c.getImaginary()));
            simpleSpectrum[i] = c.abs(); // ^to samo
        }

        //Step 3: FFT Inverse
        if(simpleSpectrum.length > 1) {
            Complex[] cepstrumComplex = transformer.transform(simpleSpectrum, TransformType.INVERSE);
            double[] cepstrum = new double[cepstrumComplex.length / 2];
            for (int i = 0; i < cepstrum.length; ++i) {
                Complex c = cepstrumComplex[i];
                cepstrum[i] = c.getReal();
            }

            int pos = 2;
            double start = cepstrum[0];
            for (int i = pos; i < simpleSpectrum.length; ++i) {
                if (start < simpleSpectrum[i]) {
                    start = simpleSpectrum[i];
                    pos = i;
                }
            }

            double spectrummax = simpleSpectrum.length / pos;
            return (float) (frames / spectrummax);
        } else {
            return 0;
        }
    }

    public int nextPowerOf2(final int a) {
        int b = 1;
        while (b < a) {
            b = b << 1;
        }
        return b;
    }

}
