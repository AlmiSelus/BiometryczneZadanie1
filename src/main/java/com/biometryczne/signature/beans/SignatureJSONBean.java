package com.biometryczne.signature.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sun.istack.internal.NotNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

/**
 * Created by c309044 on 2016-05-10.
 */
@Table(name = "signatures")
@Entity
public class SignatureJSONBean {

    @Id
    @Column(unique=true)
    private String name;

    @Column(length = Integer.MAX_VALUE) //marnowanie pamieci - pomyslec co z tym zrobic
    @NotNull
    private double[] x;

    @Column(length = Integer.MAX_VALUE)
    @NotNull
    private double[] y;

    @Column(length = Integer.MAX_VALUE)
    @NotNull
    private double[] p;

    @Column
    @NotNull
    private double time;

    public SignatureJSONBean() {

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

    @Override
    public String toString() {
        return "Uzytkownik w bazie : " + name;
    }
}
