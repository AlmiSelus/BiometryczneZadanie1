package com.biometryczne.signature.sound;

import com.biometryczne.signature.utils.AudioEvent;

/**
 * Created by Almi on 2016-06-05.
 */
public interface IFrequencyCalculator {
    float calculate(AudioEvent event);
    void setAudioListener(AudioEvent.AudioListener listener);
}
