package com.inventory.UI;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.*;
import java.awt.*;
public class LoginFrame extends JFrame {

    public LoginFrame() {

        setTitle("Inventory Login");
        setSize(500,350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5,1,10,10));

        JTextField txtUser =
                new JTextField();

        JPasswordField txtPass =
                new JPasswordField();

        JButton btnLogin =
                new JButton("Login");

        panel.add(new JLabel("Username"));
        panel.add(txtUser);

        panel.add(new JLabel("Password"));
        panel.add(txtPass);

        panel.add(btnLogin);

        add(panel);

        btnLogin.addActionListener(e -> {

            new DashboardFrame();

            dispose();
        });
    }
    
}
