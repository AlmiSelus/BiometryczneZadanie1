package com.biometryczne.signature.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by Adi on 2016-05-10.
 */
public class Signature {
    private final static Logger log = LoggerFactory.getLogger(Signature.class);
    private String name;
    private Map<SignatureCharacteristics, List<Double>> characteristics = new HashMap<>();

    public Signature() {
        this("new");
    }

    public Signature(String name) {
        this.name = name;
        characteristics.put(SignatureCharacteristics.X, new ArrayList<>());
        characteristics.put(SignatureCharacteristics.Y, new ArrayList<>());
        characteristics.put(SignatureCharacteristics.PRESSURE, new ArrayList<>());
    }

    public Signature(Signature template, String name)
    {
        this.name = name;
        characteristics.put(SignatureCharacteristics.X, template.get(SignatureCharacteristics.X));
        characteristics.put(SignatureCharacteristics.Y, template.get(SignatureCharacteristics.Y));
        characteristics.put(SignatureCharacteristics.PRESSURE, template.get(SignatureCharacteristics.PRESSURE));

    }
    public Signature(Signature template)
    {
        this.name = template.getName();
        characteristics.put(SignatureCharacteristics.X, template.get(SignatureCharacteristics.X));
        characteristics.put(SignatureCharacteristics.Y, template.get(SignatureCharacteristics.Y));
        characteristics.put(SignatureCharacteristics.PRESSURE, template.get(SignatureCharacteristics.PRESSURE));
    }


    public void add(double value, SignatureCharacteristics type) {
        characteristics.get(type).add(value);
    }

    public void addAll(double[] values, SignatureCharacteristics type) {
        for(double value : values) {
            add(value, type);
        }
    }

    public List<Double> get(SignatureCharacteristics type) {
        return characteristics.get(type);
    }

    public double[] getAsArray(SignatureCharacteristics type) {
        List<Double> response = get(type);
        double[] asArray = new double[response.size()];
        int i = 0;
        for(Double d : response) {
            asArray[i] = d;
            i++;
        }
        return asArray;
    }

    public void clearAll() {
        for (Map.Entry<SignatureCharacteristics, List<Double>> entry : characteristics.entrySet()) {
            entry.getValue().clear();
        }
    }

    public void filterCharacteristics()   // nie wiem jak to podpiac......
    {

        //===================== count last items with 0 value
        int nullItems = 1;
        for (int i = 1; i < characteristics.get(SignatureCharacteristics.PRESSURE).size(); i++) {
            if (characteristics.get(SignatureCharacteristics.PRESSURE).get(i) == 0 && characteristics.get(SignatureCharacteristics.PRESSURE).get(i - 1) == 0) {
                nullItems++;
            } else {
                nullItems = 1;
            }
        }
        log.info("Zero PRESSURE count: " + nullItems);

        //===================== delete zeroes in characteristics
        for (Map.Entry<SignatureCharacteristics, List<Double>> entry : characteristics.entrySet()) {

//            log.info("Charcteristic length: "+entry.getValue().size());

            for (int i = 0; i < nullItems; i++)
                entry.getValue().remove(entry.getValue().size() - 1);

//            log.info("Charcteristic length: "+entry.getValue().size());
        }

        //===================== translateCharacteristics (X,Y)

//        log.info("translation: X:");
        translateCharacteristic(characteristics.get(SignatureCharacteristics.X));
//        log.info("translation: Y:");
        translateCharacteristic(characteristics.get(SignatureCharacteristics.Y));

    }

    private void translateCharacteristic(List<Double> characteristic) {
//        log.info(Arrays.toString(characteristic.stream().toArray()));
        double minimumValue = Collections.min(characteristic);
//        Collections.min(characteristics.get(SignatureCharacteristics.Y));
//        log.info("\tminimym Value: "+ minimumValue);
        for (int i = 0; i < characteristic.size(); i++) {
            characteristic.set(i, characteristic.get(i) - minimumValue);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

