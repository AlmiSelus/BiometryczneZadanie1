package com.biometryczne.signature.nodes;

import javafx.embed.swing.SwingNode;
import org.jfree.chart.ChartPanel;

/**
 * Created by Almi on 2016-06-26.
 */
public class JavaFXFreeChart extends SwingNode {
    public JavaFXFreeChart(ChartPanel panel) {
        setContent(panel);
    }
}
