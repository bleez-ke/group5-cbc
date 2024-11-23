package com.mycompany.cbcsystemassessmentsystem;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CBCSystemAssessmentsystem {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SplashScreen splashScreen = new SplashScreen();
            splashScreen.setVisible(true);

            // Timer for splash screen countdown
            Timer timer = new Timer(1000, new ActionListener() {
                int countdown = 5;
                @Override
                public void actionPerformed(ActionEvent e) {
                    splashScreen.updateCountdown(countdown);
                    countdown--;

                    if (countdown < 0) {
                        ((Timer) e.getSource()).stop();
                        splashScreen.dispose(); 
                        LoginFrame loginFrame = new LoginFrame();
                        loginFrame.setVisible(true); 
                    }
                }
            });
            timer.start();
        });
    }
}
