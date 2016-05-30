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
    private List<Double> x;
    private List<Double> y;
    private List<Double> p;
    private double time;

    public SignatureJSONBean() {

    }

    public SignatureJSONBean(String name, String surname, List<Double> x, List<Double> y, List<Double> p, double pressure, double time) {
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

    public List<Double> getX() {
        return x;
    }

    public void setX(List<Double> x) {
        this.x = x;
    }

    public List<Double> getY() {
        return y;
    }

    public void setY(List<Double> y) {
        this.y = y;
    }

    public List<Double> getP() {
        return p;
    }

    public void setP(List<Double> p) {
        this.p = p;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }
}
