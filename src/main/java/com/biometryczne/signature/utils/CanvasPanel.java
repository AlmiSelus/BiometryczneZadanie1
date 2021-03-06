package com.biometryczne.signature.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Adi on 2016-05-18.
 */


public class CanvasPanel extends JPanel{

    private final static Logger log = LoggerFactory.getLogger(CanvasPanel.class);

    private Signature signature;
    public CanvasPanel()
    {
        signature = new Signature();
    }
    private void doDrawing(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        g2d.setStroke(new BasicStroke(2));
        int characteristicLength = signature.get(SignatureCharacteristics.X).size();
//        log.info(characteristicLength + "");
        if (characteristicLength >1) {
            for (int i = 1; i < characteristicLength; i++) {

                float tmpP = (signature.get(SignatureCharacteristics.PRESSURE).get(i).floatValue());

                g2d.setColor(new Color(tmpP, 0, 0));

                int offset = 0;
                if (tmpP > 0) {
                    g2d.drawLine(
                            signature.get(SignatureCharacteristics.X).get(i).intValue()+offset,
                            signature.get(SignatureCharacteristics.Y).get(i).intValue()+offset,
                            signature.get(SignatureCharacteristics.X).get(i - 1).intValue()+offset,
                            signature.get(SignatureCharacteristics.Y).get(i - 1).intValue()+offset);
                }
            }
        }
    }

    public void setSignature(Signature SIGNATURE)
    {
        this.signature = SIGNATURE;
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        doDrawing(g);
    }
}
