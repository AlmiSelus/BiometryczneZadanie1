package com.biometryczne.signature.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by c309044 on 2016-05-10.
 */
@JsonSerialize
@JsonDeserialize
public class PointJSON {

    @JsonProperty
    public double x;

    @JsonProperty
    public double y;

    public PointJSON(double x, double y) {
        setX(x);
        setY(y);
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getX() {
        return x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getY() {
        return y;
    }
}

