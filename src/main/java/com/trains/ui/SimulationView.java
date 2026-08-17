package com.trains.ui;

import javax.swing.BorderFactory;
import javax.swing.Timer;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        this.panel = new JPanel() {

            /**
             * Enables JPanel to draw elements
             * 
             * @param Graphics
             */
            @Override
            protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics); // Clears background
                Graphics2D graphics2d = (Graphics2D) graphics;

                // Code to draw trains and other objects... (in process)
            }

            // Method creates a visible element and draws it (in process)
            // public void draw(Graphics2D graphics2d);
        };

        // Panel settings for the background and size of the window
        panel.setBorder(BorderFactory.createEmptyBorder(screenSize, screenSize, screenSize, screenSize));
        panel.setLayout(new GridLayout());
        panel.setBackground(Color.BLACK); // BG colour (temporarily black)

        // Frame settings
        frame.add(panel, BorderLayout.CENTER); // Adds a panel onto the frame taking all the remaining space
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Stops the program on close
        frame.setTitle("Train System Simulation");
        frame.pack(); // Applies the selected size to fit all UI elements
        frame.setVisible(true);
    }

    /**
     * Launches an infinite screen update sequence using Timer
     */
    public void startScreenUpdateTimer() {
        ActionListener action = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                frame.repaint();
            }
        };

        screenUpdate = new Timer(screenUpdateDelay, action);
        screenUpdate.setInitialDelay(0);
        screenUpdate.start();
    }
}
