package com.trains.ui;

import java.util.Timer;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class SimulationView extends javax.swing.JFrame {
    // GUI visual params
    JFrame frame;
    JPanel panel;
    Timer screenUpdate; // Screen update timer
    int screenUpdateDelay; // Initial delay (default 1 ms)
    int screenSize; // Screen size in pixels in 4 directions e.g., 800x800

    public SimulationView(int screenSize) {
        this.screenSize = screenSize;
        this.screenUpdateDelay = 1; // 1ms
        this.frame = new JFrame();
        this.panel = new JPanel();
    }
}
