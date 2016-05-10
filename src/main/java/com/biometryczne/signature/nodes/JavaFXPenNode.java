package com.biometryczne.signature.nodes;

import javafx.embed.swing.SwingNode;
import jpen.*;
import jpen.event.PenListener;
import jpen.owner.multiAwt.AwtPenToolkit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * Created by Almi on 2016-05-09.
 */
public class JavaFXPenNode extends SwingNode implements PenListener, ICanvasOperations {

    private final static Logger log = LoggerFactory.getLogger(JavaFXPenNode.class);

    public JavaFXPenNode() {
        super();

        setContent(createCanvas());
        AwtPenToolkit.addPenListener(getContent(), this);
    }

    public void penButtonEvent(PButtonEvent ev) {
        System.out.println(ev);
    }

    public void penKindEvent(PKindEvent ev) {
        System.out.println(ev);
    }

    public void penLevelEvent(PLevelEvent ev) {
        System.out.println(ev);
        System.out.println("Pressure = " + ev.pen.getLevelValue(PLevel.Type.PRESSURE));
    }

    public void penScrollEvent(PScrollEvent ev) {
        System.out.println(ev);
    }

    public void penTock(long availableMillis) {
        System.out.println("TOCK - available period fraction: "+availableMillis);
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
}
