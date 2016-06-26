package com.biometryczne.signature.sound;

import com.biometryczne.signature.sound.distance.IDistanceCalculator;
import com.sun.istack.internal.NotNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by Almi on 2016-06-26.
 */
@Table(name = "signatures")
@Entity
public class VoiceEntry implements Serializable { //cały obiekt w bazie bedziemy trzymac

    @Id
    @Column(unique = true)
    private Integer id;

    @Column(length = Integer.MAX_VALUE) //marnowanie pamieci - pomyslec co z tym zrobic
    @NotNull
    private double[] features;

    @Column
    private int meanCount;

    @Column
    private String name;

    public VoiceEntry() {

    }

    public VoiceEntry(double[] features, String name) {
        this.features = features;
        meanCount = 1;
        this.name = name;
    }

    public VoiceEntry(VoiceEntry entry) {
        this(Arrays.copyOf(entry.features, entry.features.length), entry.getName());
    }

    double getDistance(IDistanceCalculator calculator, VoiceEntry voicePrint) {
        return calculator.getDistance(this.features, voicePrint.features);
    }

    void merge(double[] features) {
        if(this.features.length != features.length) {
            throw new IllegalArgumentException("Domyslne i dodatkowe VoiceEntry maja rozne rozmiary: Dodatkowe[" +
                    features.length + "] Domyslne [" + this.features.length + "]");
        }
        merge(this.features, features);
        meanCount++;

    }

    private void merge(double[] inner, double[] outer) {
        for (int i = 0; i < inner.length; i++) {
            inner[i] = (inner[i] * meanCount + outer[i]) / (meanCount + 1);
        }
    }

    public String getName() {
        return name;
    }
}
