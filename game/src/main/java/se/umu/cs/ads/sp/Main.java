package se.umu.cs.ads.sp;

import se.umu.cs.ads.sp.controller.GameController;
import se.umu.cs.ads.sp.model.communication.*;
import se.umu.cs.ads.sp.utils.AppSettings;
import se.umu.cs.ads.sp.utils.Utils;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        AppSettings.SetGameConfig();
        AppSettings.PrintSettings();

        Runnable startApp = () -> {
            try {
                new GameController();

                //server.start();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        };

        SwingUtilities.invokeLater(startApp);

    }
}