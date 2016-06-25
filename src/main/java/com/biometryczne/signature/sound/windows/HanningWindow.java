package com.biometryczne.signature.sound.windows;

/**
 * Created by Almi on 2016-06-06.
 */
public class HanningWindow implements IWindow {
    @Override
    public double computeWindow(int frames, int n) {
        return 0.5*(1-Math.cos(2*Math.PI*n/(frames-1)));
    }
}
