package com.biometryczne.signature.sound;

import com.biometryczne.signature.sound.dao.SoundDao;
import com.biometryczne.signature.sound.distance.EuclideanDistanceCalculator;
import com.biometryczne.signature.sound.distance.IDistanceCalculator;
import com.biometryczne.signature.sound.features.IFeaturesExtractor;
import com.biometryczne.signature.sound.features.LpcFeaturesExtractor;
import com.biometryczne.signature.sound.utils.AudioNormalizer;
import com.biometryczne.signature.sound.voiceactivitydetectors.AutocorrelationVoiceActivityDetector;

import java.util.*;

/**
 * Created by Almi on 2016-06-26.
 */
public class SoundRecognitionSystem {
    private final static float MIN_SAMPLE_RATE = 8000.f;
    private final SoundDao soundDao;
    private float sampleRate;
    private boolean universalModelSetByUser = true;
    private VoiceEntry universalModel;


    public SoundRecognitionSystem(float sampleRate, SoundDao soundDao) {
        if(sampleRate < MIN_SAMPLE_RATE) {
            throw new IllegalArgumentException("SampleRate powinno byc > 8000 Hz");
        }
        this.sampleRate = sampleRate;

        this.soundDao = soundDao;
    }

    public VoiceEntry createVoicePrint(String userKey, double[] voiceSample) {
        if(userKey == null) {
            throw new NullPointerException("The userKey is null");
        }
        if(soundDao.getByName(userKey) != null) {
            throw new IllegalArgumentException("The userKey already exists: [" + userKey + "]");
        }

        double[] features = extractFeatures(voiceSample, sampleRate);
        VoiceEntry voicePrint = new VoiceEntry(features, userKey);

        if (!universalModelSetByUser) {
            if (universalModel == null) {
                universalModel = new VoiceEntry(voicePrint);
            } else {
                universalModel.merge(features);
            }
        }
//        database.put(userKey, voicePrint);
        soundDao.create(voicePrint);

        return voicePrint;
    }

    public VoiceEntry mergeVoiceSample(String userKey, double[] voiceSample) {

        if(userKey == null) {
            throw new NullPointerException("The userKey is null");
        }

        VoiceEntry original = soundDao.getByName(userKey);
        if(original == null) {
            throw new IllegalArgumentException("No voice print linked to this user key [" + userKey + "]");
        }

        double[] features = extractFeatures(voiceSample, sampleRate);

        if(!universalModelSetByUser) {
            universalModel.merge(features);
        }

        original.merge(features);

        return original;
    }

    public List<VoiceMatch> identify(double[] voiceSample) {

        if(soundDao.getAll().size() == 0) {
            throw new IllegalStateException("There is no voice print enrolled in the system yet");
        }

        VoiceEntry voicePrint = new VoiceEntry(extractFeatures(voiceSample, sampleRate), "SampleEntry");

        IDistanceCalculator calculator = new EuclideanDistanceCalculator();
        List<VoiceMatch> matches = new ArrayList<>(soundDao.getAll().size());

        double distanceFromUniversalModel = voicePrint.getDistance(calculator, universalModel);
        for (VoiceEntry entry : soundDao.getAll()) {
            double distance = entry.getDistance(calculator, voicePrint);

            int likelihood = 100 - (int) (distance / (distance + distanceFromUniversalModel) * 100);
            matches.add(new VoiceMatch(entry.getName(), likelihood, distance));
        }

        Collections.sort(matches, (m1, m2) -> Double.compare(m1.getDistance(), m2.getDistance()));

        return matches;
    }

    public void setUniversalModel(VoiceEntry universalModel) {
        if(universalModel == null) {
            throw new IllegalArgumentException("UniversalModel == null");
        }
        this.universalModelSetByUser = false;
        this.universalModel = universalModel;
    }

    public VoiceEntry getUniversalModel() {
        return new VoiceEntry(universalModel);
    }

    private double[] extractFeatures(double[] voiceSample, float sampleRate) {

        AutocorrelationVoiceActivityDetector voiceDetector = new AutocorrelationVoiceActivityDetector();
        AudioNormalizer normalizer = new AudioNormalizer();
        IFeaturesExtractor<double[]> lpcExtractor = new LpcFeaturesExtractor(sampleRate, 20);

        voiceDetector.removeSilence(voiceSample, sampleRate);
        normalizer.normalize(voiceSample, sampleRate);
        double[] lpcFeatures = lpcExtractor.extractFeatures(voiceSample);

        return lpcFeatures;
    }
}
