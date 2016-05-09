package com.biometryczne.signature;

import jpen.*;
import jpen.demo.StatusReport;
import jpen.event.PenListener;
import jpen.owner.multiAwt.AwtPenToolkit;

import javax.swing.*;

/**
 * Created by Almi on 2016-04-24.
 */
public class Test1 implements PenListener {

    public static void main(String... args) throws Throwable{
        new Test1();
    }

    Test1(){
        JLabel l=new JLabel("Move the pen or mouse over me!");
        AwtPenToolkit.addPenListener(l, this);

        JFrame f=new JFrame("JPen Example");
        f.getContentPane().add(l);
        f.setSize(300, 300);
        f.setVisible(true);

        System.out.println(new StatusReport(AwtPenToolkit.getPenManager()));
    }

    //@Override
    public void penButtonEvent(PButtonEvent ev) {
        System.out.println(ev);
    }
    //@Override
    public void penKindEvent(PKindEvent ev) {
        System.out.println(ev);
    }
    //@Override
    public void penLevelEvent(PLevelEvent ev) {
        System.out.println(ev);
        System.out.println("Pressure = " + ev.pen.getLevelValue(PLevel.Type.PRESSURE));
    }
    //@Override
    public void penScrollEvent(PScrollEvent ev) {
        System.out.println(ev);

    }
    //@Override
    public void penTock(long availableMillis) {
        System.out.println("TOCK - available period fraction: "+availableMillis);
    }

}
