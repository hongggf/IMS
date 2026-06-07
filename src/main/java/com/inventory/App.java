package com.inventory;
import javax.swing.SwingUtilities;

import com.formdev.flatlaf.FlatDarkLaf;
import com.inventory.UI.LoginFrame;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {   
     FlatDarkLaf.setup();
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
       
    }
}
