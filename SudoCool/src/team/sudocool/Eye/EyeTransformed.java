package team.sudocool.Eye;

/**
 * Created by Heranort on 16/11/15.
 */

import org.opencv.core.Mat;
import team.sudocool.ImgWorks.nImgProc.Utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

public class EyeTransformed extends JPanel {
    private static final int WIDTH=293;
    private Mat captured;
    private ImageIcon backgroundImage;

    /**
     * Create the frame.
     */
    public EyeTransformed() {
        backgroundImage = new ImageIcon(Eye.backgroundImage);

        this.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.setLayout(null);
    }

    /**
     * paint the video image
     * @param g default graph
     */
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        if(captured == null) {
            return;
        }
        g.drawImage(backgroundImage.getImage(), 0, 0, this);

        BufferedImage bfi = Utils.Mat2BufferedImg(captured, WIDTH);
        g.drawImage(bfi, 77, 0, this);
    }

    /**
     * set the captured matrix and repaint
     * @param captured input
     */
    public void repaint(Mat captured) {
        this.captured = captured.clone();
        this.repaint();
    }
}
