package com.inventory.model;

import javax.swing.*;
import java.awt.*;

public class ActionBar extends JPanel {

    public ActionBar(JComponent left, JComponent right) {

        setLayout(new BorderLayout());
        setOpaque(false);

        add(left, BorderLayout.WEST);
        add(right, BorderLayout.EAST);
    }

    public static JPanel rightButtons(JButton... buttons) {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setOpaque(false);

        for (JButton b : buttons) {
            panel.add(b);
        }

        return panel;
    }
}