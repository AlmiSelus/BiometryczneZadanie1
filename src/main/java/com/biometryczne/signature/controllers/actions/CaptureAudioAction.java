package com.biometryczne.signature.controllers.actions;

import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Almi on 2016-06-25.
 */
public class CaptureAudioAction extends AudioAction implements IControllerAction {

    private ByteArrayOutputStream out;
    private OnAudioListener audioListener;
    private AtomicBoolean isRunning = new AtomicBoolean();
    private final static Logger log = LoggerFactory.getLogger(CaptureAudioAction.class);

    public CaptureAudioAction(final boolean isCapturing, OnAudioListener listener) {
        isRunning.set(isCapturing);
        audioListener = listener;
    }

    @Override
    public Object perform(Pane mainPane) {
        if(isRunning.get()) {
            captureAudio();
        }
        return null;
    }

    private void captureAudio() {
        try {
            final AudioFormat format = getFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();
            Runnable runner = new Runnable() {

                int bufferSize = (int)format.getSampleRate() * format.getFrameSize();
                byte buffer[] = new byte[bufferSize];

                public void run() {
                    out = new ByteArrayOutputStream();
                    try {
                        while (isRunning.get()) {
                            log.info(isRunning.get() + "");
                            int count = line.read(buffer, 0, buffer.length);
                            if (count > 0) {
                                out.write(buffer, 0, count);
                            }
                        }
                        out.close();
                        audioListener.onCaptured(out);
                        line.close();
                    } catch (IOException e) {
                        System.err.println("I/O problems: " + e);
                    }
                }
            };
            Thread captureThread = new Thread(runner);
            captureThread.start();
        } catch (LineUnavailableException e) {
            System.err.println("Line unavailable: " + e);
        }
    }

    public void stopRecording() {
        isRunning.set(!isRunning.get());
    }
}
