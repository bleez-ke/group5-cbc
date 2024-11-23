package com.mycompany.cbcsystemassessmentsystem;

import javax.swing.*;
import java.awt.*;

public class SplashScreen extends JFrame {
    private JLabel countdownLabel;

    public SplashScreen() {
        // Set up the splash screen properties
        setTitle("Splash Screen");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Add content to the splash screen
        JLabel titleLabel = new JLabel("Welcome to Group 5 CBC System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.CENTER);

        countdownLabel = new JLabel("Loading...", SwingConstants.CENTER);
        countdownLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        add(countdownLabel, BorderLayout.SOUTH);
    }

    public void updateCountdown(int seconds) {
        countdownLabel.setText("Starting in " + seconds + " seconds...");
    }
}
