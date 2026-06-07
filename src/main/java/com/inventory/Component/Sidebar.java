package com.inventory.Component;
import javax.swing.*;
import java.awt.*;

import javax.swing.JButton;

public class Sidebar extends JPanel {
     public JButton dashboardBtn;
    public JButton productBtn;

    public Sidebar() {

        setPreferredSize(
            new Dimension(220,0));

        setLayout(
            new GridLayout(10,1,10,10));

        dashboardBtn =
                new JButton("Dashboard");

        productBtn =
                new JButton("Products");

        add(dashboardBtn);
        add(productBtn);
    }
}
