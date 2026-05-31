package fr.gamity.launcher.thomas260913;

import javafx.application.Application;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<String> arguments = Arrays.asList(args);
        String version;
        if(arguments.contains("--installerVersion")){
            version = arguments.get(arguments.indexOf("--installerVersion")+1);
        }
        try {
            Class.forName("javafx.application.Application");
            Application.launch(Launcher.class, args);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Erreur:\n" + e.getMessage() + " not found",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}