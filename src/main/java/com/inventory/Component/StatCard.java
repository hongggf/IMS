package com.inventory.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import javax.swing.*;
import java.awt.*;

public class StatCard extends JPanel {
    public StatCard(
            String title,
            String value) {

        setBorder(
          BorderFactory.createEtchedBorder());

        setLayout(
          new BorderLayout());

        JLabel lblTitle =
                new JLabel(title);

        JLabel lblValue =
                new JLabel(value);

        lblValue.setFont(
          new Font(
             "Arial",
             Font.BOLD,
             24));

        add(lblTitle,
            BorderLayout.NORTH);

        add(lblValue,
            BorderLayout.CENTER);
    }
    
}
