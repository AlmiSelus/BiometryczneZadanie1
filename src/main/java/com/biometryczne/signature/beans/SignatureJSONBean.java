package com.biometryczne.signature.beans;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

/**
 * Created by c309044 on 2016-05-10.
 */
@JsonSerialize
@JsonDeserialize
public class SignatureJSONBean {
    private String name;
    private String surname;
    private List<PointJSON> coordinates;
    private double pressure;
    private double time;

    public SignatureJSONBean() {

    }

    public SignatureJSONBean(String name, String surname, List<PointJSON> coordinates, double pressure, double time) {
        setName(name);
        setSurname(surname);
        setCoordinates(coordinates);
        setPressure(pressure);
        setTime(time);
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setCoordinates(List<PointJSON> coordinates) {
        this.coordinates = coordinates;
    }

    public void addCoordinate(PointJSON coordinate) {
        coordinates.add(coordinate);
    }

    public void removeCoordinate(PointJSON coordinate) {
        coordinates.add(coordinate);
    }

    public void setTime(double time) {
        this.time = time;
    }

    public double getPressure() {
        return pressure;
    }

    public double getTime() {
        return time;
    }

    public List<PointJSON> getCoordinates() {
        return coordinates;
    }

    public String getSurname() {
        return surname;
    }

    public String getName() {
        return name;
    }
}
