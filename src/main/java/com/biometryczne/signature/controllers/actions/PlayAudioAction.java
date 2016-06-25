package com.biometryczne.signature.controllers.actions;

import javafx.scene.layout.Pane;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Almi on 2016-06-25.
 */
public class PlayAudioAction extends AudioAction implements IControllerAction {

    public ByteArrayOutputStream out ;

    public PlayAudioAction(final ByteArrayOutputStream out) {
        this.out = out;
    }

    @Override
    public Object perform(Pane mainPane) {

        playAudio();

        return null;
    }

    private void playAudio() {
        try {
            byte audio[] = out.toByteArray();
            InputStream input = new ByteArrayInputStream(audio);
            final AudioFormat format = getFormat();
            final AudioInputStream ais = new AudioInputStream(input, format, audio.length / format.getFrameSize());
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            final SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            Runnable runner = new Runnable() {
                int bufferSize = (int) format.getSampleRate() * format.getFrameSize();
                byte buffer[] = new byte[bufferSize];

                public void run() {
                    try {
                        int count;
                        while ((count = ais.read(buffer, 0, buffer.length)) != -1) {
                            if (count > 0) {
                                line.write(buffer, 0, count);
                            }
                        }
                        line.drain();
                        line.close();
                    } catch (IOException e) {
                        System.err.println("I/O problems: " + e);
                    }
                }
            };
            Thread playThread = new Thread(runner);
            playThread.start();
        } catch (LineUnavailableException e) {
            System.err.println("Line unavailable: " + e);
        }
    }
}
