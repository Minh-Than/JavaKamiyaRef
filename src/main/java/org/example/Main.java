package org.example;

import jakarta.enterprise.context.ApplicationScoped;

import javax.swing.*;
import java.awt.*;

@ApplicationScoped
public class Main{
    ApplicationModel applicationModel;
    Main(){
        applicationModel = new ApplicationModel();
        JFrame frame = new MainFrame(applicationModel);
        frame.setPreferredSize(new Dimension(775, 650));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new GridLayout());
        frame.setTitle("Kamiya Reference Finder");
        frame.setResizable(false);
        frame.setVisible(true);
        frame.pack();
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(Main::new);
    }
}

