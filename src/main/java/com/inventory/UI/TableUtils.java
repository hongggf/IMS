package com.inventory.UI;

import javax.swing.*;
import java.awt.*;

public class TableUtils {
    public static void applyTheme(JTable table) {
        table.setBackground(UITheme.TABLE_BG);
        table.setForeground(UITheme.TEXT_COLOR);
        table.setRowHeight(26);
        table.setGridColor(UITheme.GRID_COLOR);
        table.getTableHeader().setBackground(UITheme.HEADER_BG);
        table.getTableHeader().setForeground(UITheme.TITLE_COLOR);
    }
}