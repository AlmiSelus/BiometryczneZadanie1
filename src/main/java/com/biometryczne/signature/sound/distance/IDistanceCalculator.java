package com.biometryczne.signature.sound.distance;

/**
 * Created by Almi on 2016-06-26.
 */
public interface IDistanceCalculator {
    double getDistance(double[] currentFeatures, double[] newFeatures);
}
