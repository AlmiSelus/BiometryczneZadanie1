package com.biometryczne.signature.sound.distance;

/**
 * Created by Almi on 2016-06-26.
 */
public class EuclideanDistanceCalculator implements IDistanceCalculator {
    @Override
    public double getDistance(double[] currentFeatures, double[] newFeatures) {
        double distance = currentFeatures == null || newFeatures == null ? Double.POSITIVE_INFINITY : -1.d;
        if (distance < 0) {
            if(currentFeatures.length != newFeatures.length) {
                throw new IllegalArgumentException("Both features should have the same length. Received lengths of [" +
                        + currentFeatures.length + "] and [" + newFeatures.length + "]");
            }
            distance = 0.0;
            for (int i = 0; i < currentFeatures.length; i++) {
                double diff = currentFeatures[i] - newFeatures[i];
                distance += diff * diff;
            }
        }
        return distance;
    }
}
