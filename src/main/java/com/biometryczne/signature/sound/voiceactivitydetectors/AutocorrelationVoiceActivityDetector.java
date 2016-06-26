package com.biometryczne.signature.sound.voiceactivitydetectors;

import java.util.Arrays;

/**
 * Created by Almi on 2016-06-26.
 */
public class AutocorrelationVoiceActivityDetector {

    private static final int WINDOW_MILLIS = 1;
    private static final int FADE_MILLIS = 2;
    private static final int MIN_SILENCE_MILLIS = 4;
    private static final int MIN_VOICE_MILLIS = 200;

    private double threshold = 0.0001d;

    private double[] fadeInFactors;
    private double[] fadeOutFactors;

    /**
     * Returns the noise threshold used to determine if a given section is silence or not
     * @return the threshold
     */
    public double getAutocorrellationThreshold() {
        return threshold;
    }

    /**
     * Sets the noise threshold used to determine if a given section is silence or not
     * @param threshold the threshold
     */
    public void setAutocorrellationThreshold(double threshold) {
        this.threshold = threshold;
    }

    /**
     * Removes silence out of the given voice sample
     * @param voiceSample the voice sample
     * @param sampleRate the sample rate
     * @return a new voice sample with silence removed
     */

    public double[] removeSilence(double[] voiceSample, float sampleRate) {
        int oneMilliInSamples = (int)sampleRate / 1000;

        int length = voiceSample.length;
        int minSilenceLength = MIN_SILENCE_MILLIS * oneMilliInSamples;
        int minActivityLength = getMinimumVoiceActivityLength(sampleRate);
        boolean[] result = new boolean[length];

        if(length < minActivityLength) {
            return voiceSample;
        }

        int windowSize = WINDOW_MILLIS * oneMilliInSamples;
        double[] correllation = new double[windowSize];
        double[] window = new double[windowSize];


        for(int position = 0; position + windowSize < length; position += windowSize) {
            System.arraycopy(voiceSample, position, window, 0, windowSize);
            double mean = bruteForceAutocorrelation(window, correllation);
            Arrays.fill(result, position, position + windowSize, mean > threshold);
        }


        mergeSmallSilentAreas(result, minSilenceLength);

        int silenceCounter = mergeSmallActiveAreas(result, minActivityLength);

//        System.out.println((int)((double)silenceCounter / result.length * 100.0d) + "% removed");

        if (silenceCounter > 0) {

            int fadeLength = FADE_MILLIS * oneMilliInSamples;
            initFadeFactors(fadeLength);
            double[] shortenedVoiceSample = new double[voiceSample.length - silenceCounter];
            int copyCounter = 0;
            for (int i = 0; i < result.length; i++) {
                if (result[i]) {
                    // detect lenght of active frame
                    int startIndex = i;
                    int counter = 0;
                    while (i < result.length && result[i++]) {
                        counter++;
                    }
                    int endIndex = startIndex + counter;

                    applyFadeInFadeOut(voiceSample, fadeLength, startIndex, endIndex);
                    System.arraycopy(voiceSample, startIndex, shortenedVoiceSample, copyCounter, counter);
                    copyCounter += counter;
                }
            }
            return shortenedVoiceSample;

        } else {
            return voiceSample;
        }
    }

    /**
     * Gets the minimum voice activity length that will be considered by the remove silence method
     * @param sampleRate the sample rate
     * @return the length
     */
    public int getMinimumVoiceActivityLength(float sampleRate) {
        return MIN_VOICE_MILLIS * (int) sampleRate / 1000;
    }

    /**
     * Applies a linear fade in / out to the given portion of audio (removes unwanted cracks)
     * @param voiceSample the voice sample
     * @param fadeLength the fade length
     * @param startIndex fade in start point
     * @param endIndex fade out end point
     */
    private void applyFadeInFadeOut(double[] voiceSample, int fadeLength, int startIndex, int endIndex) {
        int fadeOutStart = endIndex -  fadeLength;
        for(int j = 0; j < fadeLength; j++) {
            voiceSample[startIndex + j] *= fadeInFactors[j];
            voiceSample[fadeOutStart + j] *= fadeOutFactors[j];
        }
    }

    /**
     * Merges small active areas
     * @param result the voice activity result
     * @param minActivityLength the minimum length to apply
     * @return a count of silent elements
     */
    private int mergeSmallActiveAreas(boolean[] result, int minActivityLength) {
        boolean active;
        int increment = 0;
        int silenceCounter = 0;
        for(int i = 0; i < result.length; i += increment) {
            active = result[i];
            increment = 1;
            while((i + increment < result.length) && result[i + increment] == active) {
                increment++;
            }
            if(active && increment < minActivityLength) {
                // convert short activity to opposite
                Arrays.fill(result, i, i + increment, !active);
                silenceCounter += increment;
            }
            if(!active) {
                silenceCounter += increment;
            }
        }
        return silenceCounter;
    }

    /**
     * Merges small silent areas
     * @param result the voice activity result
     * @param minSilenceLength the minimum silence length to apply
     */
    private void mergeSmallSilentAreas(boolean[] result, int minSilenceLength) {
        boolean active;
        int increment = 0;
        for(int i = 0; i < result.length; i += increment) {
            active = result[i];
            increment = 1;
            while((i + increment < result.length) && result[i + increment] == active) {
                increment++;
            }
            if(!active && increment < minSilenceLength) {
                // convert short silence to opposite
                Arrays.fill(result, i, i + increment, !active);
            }
        }
    }

    /**
     * Initialize the fade in/ fade out factors properties
     * @param fadeLength
     */
    private void initFadeFactors(int fadeLength) {
        fadeInFactors = new double[fadeLength];
        fadeOutFactors = new double[fadeLength];
        for(int i = 0; i < fadeLength; i ++) {
            fadeInFactors[i] = (1.0d / fadeLength) * i;
        }
        for(int i = 0; i < fadeLength; i ++) {
            fadeOutFactors[i] = 1.0d - fadeInFactors[i];
        }
    }

    /**
     * Applies autocorrelation in O² operations. Keep arrays very short !
     * @param voiceSample the voice sample buffer
     * @param correllation the correlation buffer
     * @return the mean correlation value
     */
    private double bruteForceAutocorrelation(double[] voiceSample, double[] correllation) {
        Arrays.fill(correllation, 0);
        int n = voiceSample.length;
        for (int j = 0; j < n; j++) {
            for (int i = 0; i < n; i++) {
                correllation[j] += voiceSample[i] * voiceSample[(n + i - j) % n];
            }
        }
        double mean = 0.0d;
        for(int i = 0; i < voiceSample.length; i++) {
            mean += correllation[i];
        }
        return mean / correllation.length;
    }

}
