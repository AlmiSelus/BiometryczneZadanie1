package com.biometryczne.signature.nodes;

import com.biometryczne.signature.utils.CanvasPanel;
import com.biometryczne.signature.utils.Signature;
import com.biometryczne.signature.utils.SignatureCharacteristics;
import javafx.embed.swing.SwingNode;
import jpen.*;
import jpen.event.PenListener;
import jpen.owner.multiAwt.AwtPenToolkit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;


/**
 * Created by Almi on 2016-05-09.
 */
public class JavaFXPenNode extends SwingNode implements PenListener, ICanvasOperations {

    private final static Logger log = LoggerFactory.getLogger(JavaFXPenNode.class);
    private boolean allowWriting;
    private int penPanelWidth = 300, penPanelHeight = 200;
    private Signature signature = new Signature();

    public JavaFXPenNode() {
        super();
        allowWriting = false;
        setContent(createJComponent());
        AwtPenToolkit.addPenListener(getContent(), this);
    }

    private JComponent createJComponent() {
        CanvasPanel canvasPanel = new CanvasPanel();
        canvasPanel.setMinimumSize(new Dimension(penPanelWidth, penPanelHeight));
        canvasPanel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 5));
        return canvasPanel;
    }

    public void penButtonEvent(PButtonEvent ev) {
        allowWriting = true;
//        log.info("\tMozna Pisać");
    }

    public void penKindEvent(PKindEvent ev) {

    }

    public void penLevelEvent(PLevelEvent ev) {
        StringBuilder sb = new StringBuilder();

        if (allowWriting) {
            signature.add((double) ev.pen.getLevelValue(PLevel.Type.X), SignatureCharacteristics.X);
            signature.add((double) ev.pen.getLevelValue(PLevel.Type.Y), SignatureCharacteristics.Y);
            signature.add((double) ev.pen.getLevelValue(PLevel.Type.PRESSURE), SignatureCharacteristics.PRESSURE);

            sb.append("X = ").append(ev.pen.getLevelValue(PLevel.Type.X))
                    .append(" Y = ").append(ev.pen.getLevelValue(PLevel.Type.Y))
                    .append(" Pressure = ").append(ev.pen.getLevelValue(PLevel.Type.PRESSURE));

            ((CanvasPanel) getContent()).setSignature(signature);
            getContent().repaint();

        }

        //poza obszarem panelu wylacz mozliwosc pisania (nie pobiera danych)
        if (ev.pen.getLevelValue(PLevel.Type.X) < 5 | ev.pen.getLevelValue(PLevel.Type.X) > penPanelWidth - 5
                | ev.pen.getLevelValue(PLevel.Type.Y) < 5 | ev.pen.getLevelValue(PLevel.Type.Y) > penPanelHeight - 5) {
            allowWriting = false;
//            log.info("\tNIE Mozna Pisać");
        }
    }

    public void penScrollEvent(PScrollEvent ev) {
        log.info(ev.toString());
    }

    public void penTock(long availableMillis) {

    }

    //    @Override
    public void filterSignatureData() {
        log.info("Filtering data");
        signature.filterCharacteristics();

        ((CanvasPanel) getContent()).setSignature(signature);
        getContent().repaint();
    }

    @Override
    public void clearSignatureData() {
        log.info("Clearing canvas");
        signature.clearAll();
        ((CanvasPanel) getContent()).setSignature(signature);
        getContent().repaint();
    }

    @Override
    public List<Double> getCanvasValues(SignatureCharacteristics characteristics) {
        return signature.get(characteristics);
    }

    public double[] getCanvasValuesAsArray(SignatureCharacteristics characteristics) {
        return signature.getAsArray(characteristics);
    }


    public void setSignature(Signature signature) {
        this.signature = signature;
        log.info("\n" + signature.getAsArray(SignatureCharacteristics.X).length);
        ((CanvasPanel) getContent()).setSignature(signature);
        getContent().repaint();
    }

    public Signature getSignature() {
        return this.signature;
    }
}
