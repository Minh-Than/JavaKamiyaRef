package org.example;

import javax.swing.*;

public class Main{
    Main(){ new MainFrame(); }
    public static void main(String[] args){
        SwingUtilities.invokeLater(Main::new);
    }
}

