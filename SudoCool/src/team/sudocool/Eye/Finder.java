package team.sudocool.Eye;

/**
 * Created by Heranort on 16/11/17.
 */

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;
import team.sudocool.ImgWorks.EzGridSquareExtractor;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;
import team.sudocool.ImgWorks.EzGridSquareExtractor;
import team.sudocool.ImgWorks.GridSquareExtractor;
import team.sudocool.ImgWorks.nImgProc.Utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;


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





public class Finder extends JFrame {
    private JPanel contentPane;

    public Mat captured=new Mat();

    private EzGridSquareExtractor ege=new EzGridSquareExtractor(450,450,3);
    public void setMat(Mat s){


        this.captured=s;

    }

    public Mat getMat(){


        return captured;
    }
    /*
       * Launch the application.
       */
    /*
    public void Watch() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Finder frame = new Finder();

                    frame.setVisible(true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    */
    /**
     * Create the frame.
     */
    public Finder() {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 450);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        new finderThread().start();
    }




    public void paint(Graphics g){
        g = contentPane.getGraphics();



        BufferedImage bfi=Utils.Mat2BufferedImg(ege.transform4(getMat()));


        g.drawImage(bfi, 0, 0, this);
    }



    private class finderThread extends Thread{
        @Override
        public void run() {
            for (;;){

                repaint();


                try { Thread.sleep(30);
                } catch (InterruptedException e) {    }
            }
        }
    }
}

