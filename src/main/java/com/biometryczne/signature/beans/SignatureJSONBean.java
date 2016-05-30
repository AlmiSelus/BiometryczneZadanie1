package com.biometryczne.signature.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

/**
 * Created by c309044 on 2016-05-10.
 */
@JsonSerialize
@JsonDeserialize
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignatureJSONBean {
    private String name;
    private double[] x;
    private double[] y;
    private double[] p;
    private double time;

    public SignatureJSONBean() {

    }

    public SignatureJSONBean(String name, String surname, double[] x, double[] y, double[] p, double pressure, double time) {
        setName(name);
        setX(x);
        setY(y);
        setP(p);
        setTime(time);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double[] getX() {
        return x;
    }

    public void setX(double[] x) {
        this.x = x;
    }

    public double[] getY() {
        return y;
    }

    public void setY(double[] y) {
        this.y = y;
    }

    public double[] getP() {
        return p;
    }

    public void setP(double[] p) {
        this.p = p;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }
}
