package com.biometryczne.signature.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Almi on 2016-06-05.
 */
public class AudioEvent {

    private final static Logger log = LoggerFactory.getLogger(AudioEvent.class);

    private Map<Integer, List<Double>> data;

    private float sampleRate;

    private float audioLengthMilliseconds;
    private int channelsCount;
    private long framesNum;

    private List<Double> dataForProcessing;

    public AudioEvent() {

    }

    public AudioEvent(double[] data) throws IOException {
        Map<Integer, List<Double>> fullData = new HashMap<>();
        for (int i = 0; i < 1; ++i) {
            fullData.put(i, new ArrayList<>());
        }

        for(double sample : data) {
            fullData.get(0).add(sample);
        }

        dataForProcessing = new ArrayList<>();

        setData(fullData);
        setSampleRate(44100);
        setChannelsCount(1);
        setFramesNum(data.length);
    }

    public Map<Integer, List<Double>> getData() {
        return data;
    }

    public void setData(Map<Integer, List<Double>> data) {
        this.data = data;
    }

    public float getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(float sampleRate) {
        this.sampleRate = sampleRate;
    }

    public float getAudioLengthMilliseconds() {
        return audioLengthMilliseconds;
    }

    public void setAudioLengthMilliseconds(float audioLengthMilliseconds) {
        this.audioLengthMilliseconds = audioLengthMilliseconds;
    }

    public int getChannelsCount() {
        return channelsCount;
    }

    public void setChannelsCount(int channelsCount) {
        this.channelsCount = channelsCount;
    }

    public void setFramesNum(long framesNum) {
        this.framesNum = framesNum;
    }

    public long getFramesNum() {
        return framesNum;
    }

    public List<Double> getDataForProcessing() {
        return dataForProcessing;
    }

    public void setDataForProcessing(int channel, int begin, int duration) {
        this.dataForProcessing = new ArrayList<>();
        List<Double> ds = data.get(channel);
        if(begin + duration < ds.size()) {
            for (int i = begin; i < begin + duration; ++i) {
                dataForProcessing.add(ds.get(i));
            }
        }
    }


    public interface AudioListener {
        void onProcessed(AudioEvent event, double[] data, String datasetName);
    }
}
