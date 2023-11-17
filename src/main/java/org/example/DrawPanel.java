package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class DrawPanel extends JPanel {
    private final ApplicationModel applicationModel;

    public DrawPanel(ApplicationModel applicationModel) {
        this.applicationModel = applicationModel;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        int square = 450;
        int margin = 30;

        // background
        g2.setColor(new Color(255, 255, 255));
        g2.fillRect(0, 0, this.getWidth(), this.getHeight());

        if(!applicationModel.getResultData().isEmpty()){
            ResultData result = applicationModel.getResultData().get(applicationModel.getResultIndex());
            int xLine = (int) Math.round(square * result.getx());
            int yLine = (int) Math.round(square * result.gety());

            double endPointCoor1 = result.getx() / result.gety();
            double endPointCoor2 = (1 - result.getx()) / result.gety();

            double endPointCoor1_x;
            double endPointCoor1_y;
            double endPointCoor2_x;
            double endPointCoor2_y;

            if (endPointCoor1 > 1) {
                endPointCoor1_x = square + margin;
                endPointCoor1_y = square + margin - (square / endPointCoor1);
            } else {
                endPointCoor1_x = (square * endPointCoor1) + margin;
                endPointCoor1_y = margin;
            }

            if (endPointCoor2 > 1) {
                endPointCoor2_x = margin;
                endPointCoor2_y = square + margin - (square / endPointCoor2);
            } else {
                endPointCoor2_x = square * (1 - endPointCoor2) + margin;
                endPointCoor2_y = margin;
            }

            // green rect
            g2.setColor(new Color(0, 255, 0, 64));
            g2.fillRect(margin, square + margin - yLine, xLine, yLine);

            // blue rect
            g2.setColor(new Color(0, 0, 255, 64));
            g2.fillRect(margin + xLine, square + margin - yLine, square - xLine, yLine);

            // green, blue, and 2 red lines
            g2.setColor(new Color(20, 140, 20));
            g2.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
            g2.drawLine(margin, square + margin, (int)endPointCoor1_x, (int)endPointCoor1_y);
            g2.setColor(new Color(0, 0, 255));
            g2.drawLine(square + margin, square + margin, (int)endPointCoor2_x, (int)endPointCoor2_y);
            g2.setColor(new Color(255, 0, 0));
            g2.drawLine(margin, square + margin - yLine, square + margin, square + margin - yLine);
            g2.drawLine(margin + xLine, margin, margin + xLine, square + margin);

            // borders
            g2.setColor(new Color(0, 0, 0));
            g2.setStroke(new BasicStroke(3));
            g2.drawLine(margin, margin, margin, square + margin);
            g2.drawLine(margin, square + margin, square + margin, square + margin);
            g2.drawLine(margin, margin, square + margin, margin);
            g2.drawLine(square + margin, margin, square + margin, square + margin);

            // Draw the image onto the panel
            try {
                BufferedImage refImg = ImageIO.read(Objects.requireNonNull(Main.class.getResourceAsStream("/" + result.getPng1())));
                g2.drawImage(refImg, square + margin * 2, 0,  250, 250,null);
                refImg = ImageIO.read(Objects.requireNonNull(Main.class.getResourceAsStream("/" + result.getPng2())));
                g2.drawImage(refImg, square + margin * 2, 251,  250, 250,null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // text
            int textXOffset = -25;
            g2.setFont(new Font("Arial", Font.PLAIN, 24));
            g2.drawString(result.getx1(), margin + (xLine / 2) + textXOffset, square + margin - yLine - 15);
            g2.drawString(result.getx2(), square + margin - ((square - xLine) / 2) + textXOffset, square + margin - yLine - 15);
            g2.drawString(result.getystr(), margin + xLine + 10 + textXOffset, margin + (square - (yLine / 2)));
            g2.drawString(result.getRatio1(), margin + (xLine / 2) + textXOffset, square + margin - (yLine / 2) + 20);
            g2.drawString(result.getRatio2(), square + margin - ((square - xLine) / 2) + textXOffset, square + margin - (yLine / 2) + 20);
        }

    }
}
