package com.biometryczne.signature.controllers.actions;

import javax.sound.sampled.AudioFormat;
import java.io.ByteArrayOutputStream;

/**
 * Created by Almi on 2016-06-25.
 */
public abstract class AudioAction {

    protected AudioFormat getFormat() {
        float sampleRate = 44100;
        int sampleSizeInBits = 8;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = true;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    public interface OnAudioListener {
        void onCaptured(ByteArrayOutputStream outputStream);
    }
}
