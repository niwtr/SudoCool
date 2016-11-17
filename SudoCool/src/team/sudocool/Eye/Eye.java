package team.sudocool.Eye;

/**
 * Created by Heranort on 16/11/15.
 */
import org.opencv.core.Mat;
import team.sudocool.ImgWorks.GridSquareExtractor;
import team.sudocool.ImgWorks.nImgProc.Utils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.EmptyBorder;





public class Eye extends JFrame {
    private JPanel contentPane;

    public  Mat captured;
    private VideoCap videoCap;// = new VideoCap();

  /*
     * Launch the application.
     */
    public void Watch() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Eye frame = new Eye();

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
    public Eye() {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 650, 490);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        videoCap=new VideoCap();
        new eyeThread().start();
    }




    public void paint(Graphics g){
        g = contentPane.getGraphics();
        BufferedImage bfi=videoCap.getRectedFrame();
        g.drawImage(bfi, 0, 0, this);
    }

    public Mat snapAPhoto(){

        this.captured=videoCap.getMat();//videoCap.captured;
        return this.captured;
    }


    private class eyeThread extends Thread{
        @Override
        public void run() {
            for (;;){
                 repaint();

                try { Thread.sleep(3);
                } catch (InterruptedException e) {    }
            }
        }
    }
}
