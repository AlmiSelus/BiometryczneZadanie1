package com.biometryczne.signature.utils;

/**
 * Created by Almi on 2016-06-25.
 */
public class SoundUtils {
    public static double[] convertToDouble(final byte[] data) {
        final int bytesRecorded = data.length;
        final int bytesPerSample = 8;
        final double amplification = 100.0; // choose a number as you like
        double[] micBufferData = new double[bytesRecorded - bytesPerSample + 1];
        for (int index = 0, floatIndex = 0; index < bytesRecorded - bytesPerSample + 1; index += bytesPerSample, floatIndex++) {
            double sample = 0;
            for (int b = 0; b < bytesPerSample; b++) {
                int v = data[index + b];
                if (b < bytesPerSample - 1 || bytesPerSample == 1) {
                    v &= 0xFF;
                }
                sample += v << (b * 8);
            }
            double sample32 = amplification * (sample / 32768.0);
            micBufferData[floatIndex] = sample32;

        }
        return micBufferData;
    }
}
