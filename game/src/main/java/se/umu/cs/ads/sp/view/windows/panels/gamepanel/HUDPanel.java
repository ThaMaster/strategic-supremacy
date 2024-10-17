package se.umu.cs.ads.sp.view.windows.panels.gamepanel;


import se.umu.cs.ads.sp.view.util.UtilView;

import javax.swing.*;
import java.awt.*;

public class HUDPanel extends JPanel {

    public HUDPanel() {
        super();
        this.setOpaque(false);
        this.setPreferredSize(new Dimension(UtilView.screenWidth, UtilView.screenHeight));
        this.setBounds(0, 0, UtilView.screenWidth, UtilView.screenHeight);
    }
}

