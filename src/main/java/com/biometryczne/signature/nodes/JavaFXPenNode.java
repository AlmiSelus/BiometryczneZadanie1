package com.biometryczne.signature.nodes;

import com.biometryczne.signature.CanvasPanel;
import com.biometryczne.signature.Signature;
import com.biometryczne.signature.utils.SignatureCharacteristics;
import javafx.embed.swing.SwingNode;
import jpen.*;
import jpen.event.PenListener;
import jpen.owner.multiAwt.AwtPenToolkit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.*;
import java.util.List;


/**
 * Created by Almi on 2016-05-09.
 */
public class JavaFXPenNode extends SwingNode implements PenListener, ICanvasOperations {

    private final static Logger log = LoggerFactory.getLogger(JavaFXPenNode.class);

    private boolean allowWriting;

    private int penPanelWidth = 300, penPanelHeight = 200;

    private Signature signature = new Signature();


    private CanvasPanel canvasPanel;


    public JavaFXPenNode() {
        super();

        allowWriting = false;

/*
//        setContent(createJComponent());
        jp = new JPanel();
        jp.setMinimumSize(new Dimension(penPanelWidth, penPanelHeight));
        jp.setBorder(BorderFactory.createLineBorder(new Color(200,200,200), 5));
*/
        canvasPanel = new CanvasPanel();
        canvasPanel.setMinimumSize(new Dimension(penPanelWidth, penPanelHeight));
        canvasPanel.setBorder(BorderFactory.createLineBorder(new Color(200,200,200), 5));
        setContent(canvasPanel);

//        canvasPanel.setPoint(100,50);

        AwtPenToolkit.addPenListener(getContent(), this);
    }
    /*
    private JLabel createJComponent() {
        JLabel l = new JLabel("");
        l.setMinimumSize(new Dimension(penPanelWidth, penPanelHeight));
        l.setBorder(BorderFactory.createLineBorder(new Color(200,200,200), 5));
        l.setVerticalTextPosition(SwingConstants.CENTER);
        return l;
    }*/



    public void penButtonEvent(PButtonEvent ev) {
        allowWriting = true;
        log.info("\tMozna Pisać");
   }

    public void penKindEvent(PKindEvent ev) {

    }

    public void penLevelEvent(PLevelEvent ev) {
        StringBuilder sb = new StringBuilder();

        if (allowWriting) {
            signature.add((double)ev.pen.getLevelValue(PLevel.Type.X), SignatureCharacteristics.X);
            signature.add((double)ev.pen.getLevelValue(PLevel.Type.Y), SignatureCharacteristics.Y);
            signature.add((double)ev.pen.getLevelValue(PLevel.Type.PRESSURE), SignatureCharacteristics.PRESSURE);

            sb.append("X = ").append(ev.pen.getLevelValue(PLevel.Type.X))
                    .append(" Y = ").append(ev.pen.getLevelValue(PLevel.Type.Y))
                    .append(" Pressure = ").append(ev.pen.getLevelValue(PLevel.Type.PRESSURE));

//            log.info(sb.toString());
            {
                //((JLabel)getContent()).setText(sb.toString());
//                jp.repaint();
                canvasPanel.setSignature(signature);
                canvasPanel.repaint();

            }
        }

        //poza obszarem panelu wylacz mozliwosc pisania (nie pobiera danych)
        if (ev.pen.getLevelValue(PLevel.Type.X) < 5 | ev.pen.getLevelValue(PLevel.Type.X) > penPanelWidth-5
                | ev.pen.getLevelValue(PLevel.Type.Y) < 5 | ev.pen.getLevelValue(PLevel.Type.Y) > penPanelHeight-5) {
            allowWriting = false;
            log.info("\tNIE Mozna Pisać");
        }


    }

    public void penScrollEvent(PScrollEvent ev) {
        log.info(ev.toString());
    }

    public void penTock(long availableMillis) {

    }

//    @Override
    public  void filterSignatureData()
    {
        log.info("Filtering data");
        signature.filterCharacteristics();
    }

    @Override
    public void clearSignatureData() {
        log.info("Clearing canvas");
        signature.clearAll();
        canvasPanel.setSignature(signature);
        canvasPanel.repaint();
    }

    @Override
    public List<Double> getCanvasValues(SignatureCharacteristics characteristics) {
        return signature.get(characteristics);
    }


}
