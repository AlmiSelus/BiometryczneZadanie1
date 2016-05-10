package com.biometryczne.signature.nodes;

import com.biometryczne.signature.utils.SignatureCharacteristics;

import java.util.List;

/**
 * Created by c309044 on 2016-05-10.
 */
public interface ICanvasOperations {
    void clearSignatureData();
    List<Double> getCanvasValues(SignatureCharacteristics characteristics);
}
