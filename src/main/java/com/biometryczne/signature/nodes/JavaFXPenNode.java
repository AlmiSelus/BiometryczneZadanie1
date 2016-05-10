package com.biometryczne.signature.nodes;

import javafx.embed.swing.SwingNode;
import jpen.*;
import jpen.event.PenListener;
import jpen.owner.multiAwt.AwtPenToolkit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Almi on 2016-05-09.
 */
public class JavaFXPenNode extends SwingNode implements PenListener, ICanvasOperations {

    private final static Logger log = LoggerFactory.getLogger(JavaFXPenNode.class);

    JLabel l;

    private boolean allowWriting;

    public ArrayList<Float> x; //pozycja x
    public ArrayList<Float> y; //pozycja y
    public ArrayList<Float> p; //nacisk

    private int penPanelWidth = 300, penPanelHeight = 200;

    public JavaFXPenNode() {
        super();

        allowWriting = false;
        l = new JLabel("");
        l.setMinimumSize(new Dimension(penPanelWidth, penPanelHeight));
        l.setBorder(BorderFactory.createLineBorder(new Color(200,200,200), 5));
        l.setVerticalTextPosition(SwingConstants.CENTER);

        x = new ArrayList<Float>();
        y = new ArrayList<Float>();
        p = new ArrayList<Float>();

        setContent(l);
//        setContent(createCanvas());
        AwtPenToolkit.addPenListener(getContent(), this);
    }

    public void penButtonEvent(PButtonEvent ev) {
//        System.out.println(ev);

        allowWriting = true;
        System.out.println("\tMozna Pisać");
   }

    public void penKindEvent(PKindEvent ev) {
//        System.out.println(ev);
    }

    public void penLevelEvent(PLevelEvent ev) {
//        System.out.println(ev);
//        System.out.println("Pressure = " + ev.pen.getLevelValue(PLevel.Type.PRESSURE));

        String tmpStr = "";
//        System.out.println(ev);
//        if (ev.pen.getButtonValue(PButton.Type.LEFT))
        if (allowWriting) {
            x.add(ev.pen.getLevelValue(PLevel.Type.X));
            y.add(ev.pen.getLevelValue(PLevel.Type.Y));
            p.add(ev.pen.getLevelValue(PLevel.Type.PRESSURE));


            tmpStr = (""
                    + "X = " + ev.pen.getLevelValue(PLevel.Type.X)
                    + "Y = " + ev.pen.getLevelValue(PLevel.Type.Y)
                    + " Pressure = " + ev.pen.getLevelValue(PLevel.Type.PRESSURE)
//                    + " LeftButton = " + ev.pen.getButtonValue(PButton.Type.LEFT)
//                    + " RightButton = " + ev.pen.getButtonValue(PButton.Type.RIGHT)
                    + "");


            System.out.println(tmpStr);
            l.setText(tmpStr);
        }
        //poza obszarem panelu wylacz mozliwosc pisania (nie pobiera danych)
        if (ev.pen.getLevelValue(PLevel.Type.X) < 5 | ev.pen.getLevelValue(PLevel.Type.X) > penPanelWidth-5
                | ev.pen.getLevelValue(PLevel.Type.Y) < 5 | ev.pen.getLevelValue(PLevel.Type.Y) > penPanelHeight-5) {
            allowWriting = false;
            System.out.println("\tNIE Mozna Pisać");
        }


    }

    public void penScrollEvent(PScrollEvent ev) {
        System.out.println(ev);
    }

    public void penTock(long availableMillis) {
//        System.out.println("TOCK - available period fraction: "+availableMillis);
    }

    private JComponent createCanvas() {
        final JComponent mainComponent = new JLabel("W tym miejscu się podpisz");

        return mainComponent;
    }


    @Override
    public void clearCanvas() {

        log.info("Clearing canvas");

        if(getContent() != null) {
            AwtPenToolkit.removePenListener(getContent(), this);
        }

        setContent(createCanvas());
        AwtPenToolkit.addPenListener(getContent(), this);

    }

    public void clearArrays() {
        x.clear();
        y.clear();
        p.clear();
    }
}
