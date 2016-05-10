package com.biometryczne.signature;

import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;

import java.util.ArrayList;

/**
 * Created by Adi on 2016-05-10.
 */
public class Signature {

    String name;
    public ArrayList<Float> x; //pozycja x
    public ArrayList<Float> y; //pozycja y
    public ArrayList<Float> p; //nacisk
    public ArrayList<Float> t; //dziko troche, ale to numer probki


    public Signature ()
    {
        String name= "new";
        x = new ArrayList<Float>();
        y = new ArrayList<Float>();
        p = new ArrayList<Float>();
        t = new ArrayList<Float>();
    }




}

