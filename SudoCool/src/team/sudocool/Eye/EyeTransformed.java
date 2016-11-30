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

public class EyeTransformed extends JFrame {
    private JPanel contentPane;

    public static final int WIDTH=300;
    public Mat captured;


    /**
     * Create the frame.
     */
    public EyeTransformed() {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setBounds(100, 100, WIDTH, WIDTH);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        setTitle("Transformed");
        setVisible(true);
    }

    /**
     * paint the video image
     * @param g default graph
     */
    public void paint(Graphics g){
        if(captured == null)
            return;

        g = contentPane.getGraphics();
        BufferedImage bfi = Utils.Mat2BufferedImg(captured, WIDTH);
        g.drawImage(bfi, 0, 0, this);
    }

    /**
     * set the captured matrix
     * @param captured input
     */
    public void setCaptured(Mat captured) {
        this.captured = captured.clone();
    }
}
