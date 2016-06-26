package com.biometryczne.signature.sound.features;

/**
 * Created by Almi on 2016-06-26.
 */
public abstract class WindowExtractor<T> implements IFeaturesExtractor<T> {

    private static final int DEFAULT_TARGET_WINDOW_LENGTH_IN_MILLIS = 24;
    private static final float MIN_SAMPLE_RATE = 8000.0F;

    protected final int windowSize;
    protected final float sampleRate;

    /**
     * Base constructor required by this abstract class
     * @param sampleRate the sample rate of the voice samples, minimum 8000.0
     */
    public WindowExtractor(float sampleRate) {
        if(sampleRate < MIN_SAMPLE_RATE) {
            throw new IllegalArgumentException("Sample rate should be at least 8000 Hz");
        }
        this.sampleRate = sampleRate;
        this.windowSize = getWindowSize(sampleRate);
    }

    protected int getWindowSize(float sampleRate) {
        return getClosestPowerOfTwoWindowSize(sampleRate, DEFAULT_TARGET_WINDOW_LENGTH_IN_MILLIS);
    }

    protected final int getClosestPowerOfTwoWindowSize(float sampleRate, int targetSizeInMillis) {
        boolean done = false;
        int pow = 8; // 8 bytes == 1ms at 8000 Hz
        float previousMillis = 0.0f;

        while(!done) {
            float millis = 1000 / sampleRate * pow;
            if(millis < targetSizeInMillis) {
                previousMillis = millis;
                pow *= 2;
            } else {
                // closest value to target wins
                if(Math.abs(targetSizeInMillis - millis) > targetSizeInMillis - previousMillis) {
                    pow /= 2; // previousMillis was closer
                }
                done = true;
            }
        }
        return pow;
    }
}
