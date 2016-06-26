package com.biometryczne.signature.sound;

import com.biometryczne.signature.sound.distance.IDistanceCalculator;
import com.sun.istack.internal.NotNull;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by Almi on 2016-06-26.
 */
@Table(name = "voices")
@Entity
public class VoiceEntry implements Serializable { //cały obiekt w bazie bedziemy trzymac

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name="id", nullable = false, unique = true)
    private Integer id;

    @Column(length = Integer.MAX_VALUE) //marnowanie pamieci - pomyslec co z tym zrobic
    @NotNull
    private double[] features;

    @Column(length = Integer.MAX_VALUE)
    @NotNull
    private double[] data;

    @Column
    private int meanCount;

    @Column
    private String name;

    public VoiceEntry() {

    }

    public VoiceEntry(double[] features, String name, double[] data) {
        this.features = features;
        meanCount = 1;
        this.name = name;
        this.data = data;
    }

    public VoiceEntry(VoiceEntry entry) {
        this(Arrays.copyOf(entry.features, entry.features.length), entry.getName(), entry.getData());
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

    public double[] getData() {
        return data;
    }
}
