package com.biometryczne.signature;

import com.biometryczne.signature.utils.SignatureCharacteristics;
import jpen.PLevel;
import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.geom.Arc2D;
import java.util.*;

/**
 * Created by Adi on 2016-05-10.
 */
public class Signature {
    private final static Logger log = LoggerFactory.getLogger(Signature.class);


    private String name;
    private Map<SignatureCharacteristics, List<Double>> characteristics = new HashMap<>();

    public Signature() {
        name = "new";
        characteristics.put(SignatureCharacteristics.X, new ArrayList<>());
        characteristics.put(SignatureCharacteristics.Y, new ArrayList<>());
        characteristics.put(SignatureCharacteristics.PRESSURE, new ArrayList<>());
    }

    public void add(double value, SignatureCharacteristics type) {
        characteristics.get(type).add(value);
    }

    public List<Double> get(SignatureCharacteristics type) {
        return characteristics.get(type);
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
        double minimumValue = Collections.min(characteristic);
//        Collections.min(characteristics.get(SignatureCharacteristics.Y));
//        log.info("\tminimym Value: "+ minimumValue);
        for (int i = 0; i < characteristic.size(); i++) {
            characteristic.set(i, characteristic.get(i) - minimumValue);
        }
    }


}

