package com.biometryczne.signature.sound.features;

/**
 * Created by Almi on 2016-06-26.
 */
public interface IFeaturesExtractor<T> {
    T extractFeatures(T voiceSample);
}
