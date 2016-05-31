package com.biometryczne.signature.utils;

/**
 * Created by Almi on 2016-05-31.
 */
public class SignatureEntry {
    private String name;
    private Integer id;

    public SignatureEntry(int id, String name) {
        setId(id);
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}