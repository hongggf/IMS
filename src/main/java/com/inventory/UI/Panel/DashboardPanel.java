package com.inventory.UI.Panel;

import com.inventory.Component.StatCard;

import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {
     public DashboardPanel() {

        setLayout(
           new GridLayout(
             1,4,20,20));

        add(
          new StatCard(
            "Products",
            "100"));

        add(
          new StatCard(
            "Low Stock",
            "8"));

        add(
          new StatCard(
            "Suppliers",
            "15"));

        add(
          new StatCard(
            "Orders",
            "55"));
    }
}
