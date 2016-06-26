package com.biometryczne.signature.sound;

/**
 * Created by Almi on 2016-06-26.
 */
public class VoiceMatch {
    private String key;
    private int probability;
    private double distance;

    VoiceMatch(String key, int probability, double distance) {
        this.key = key;
        this.probability = probability;
        this.distance = distance;
    }

    public String getKey() {
        return key;
    }

    public int getProbability() {
        return probability;
    }

    public double getDistance() {
        return distance;
    }
}
