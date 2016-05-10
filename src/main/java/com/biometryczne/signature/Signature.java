package com.biometryczne.signature;

import com.biometryczne.signature.utils.SignatureCharacteristics;
import jpen.PLevel;
import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;

import java.awt.geom.Arc2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Adi on 2016-05-10.
 */
public class Signature {

    private String name;
    private Map<SignatureCharacteristics, List<Double>> characteristics = new HashMap<>();

    public Signature () {
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
        for(Map.Entry<SignatureCharacteristics, List<Double>> entry : characteristics.entrySet()) {
            entry.getValue().clear();
        }
    }


}

