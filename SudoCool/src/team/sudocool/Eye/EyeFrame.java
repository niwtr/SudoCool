package team.sudocool.Eye;

/**
 * Created by Heranort on 16/11/15.
 */
import org.opencv.core.Mat;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import team.sudocool.ImgWorks.nImgProc.Utils;

public class EyeFrame extends JFrame {
    private JPanel contentPane;

    public static final int WIDTH=800;
    public Mat captured;

    /**
     * Launch the application.
     */
    public void Watch() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    EyeFrame frame = new EyeFrame();

                    frame.setVisible(true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public EyeFrame() {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, WIDTH, (int)(WIDTH*0.5625));
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
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
