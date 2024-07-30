package fr.gamity.bootstrap;

import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Splash extends Frame implements ActionListener {
    public static AtomicBoolean isDownloading = new AtomicBoolean(true);
    public Splash()
    {
        super("Gamity Bootstrap");

        this.setSize(218, 234);
        this.setLayout(new BorderLayout());
        this.setAlwaysOnTop(false);
        this.setResizable(false);
        this.setUndecorated(true);

        final SplashScreen splash = SplashScreen.getSplashScreen();
        splash.createGraphics();
        while (isDownloading.get()) {
            splash.update();
        }
        splash.close();
    }

    @Override
    public void actionPerformed(ActionEvent ae)
    {
        System.exit(0);
    }
}
