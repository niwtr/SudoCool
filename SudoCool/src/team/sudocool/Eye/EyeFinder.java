package team.sudocool.Eye;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;
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
 * Created by Heranort on 16/11/16.
 */
public class EyeFinder extends JFrame {

    JFrame fr=new JFrame();

    private Mat currentPhoto;
    Eye e=new Eye();
    public EyeFinder() {


        new EyeFinderThread().start();
    }

    public void Start() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {

                    EyeFinder frame = new EyeFinder();
                    //e=new Eye();
                    e.SnapGUI();

                    e.setVisible(true);

                    frame.setVisible(true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private class EyeFinderThread extends Thread{
        @Override
        public void run() {
            for (;;){
               //repaint();

                Mat em=e.snapAPhoto();

                if(em.size().height>0 && em.channels()>=3) {


                    MatOfByte matOfByte = new MatOfByte();
                    Highgui.imencode(".jpg", GridSquareExtractor.extractOuterBound(em), matOfByte);

                    byte[] byteArray = matOfByte.toArray();
                    BufferedImage bufImage = null;
                    try {
                        InputStream in = new ByteArrayInputStream(byteArray);
                        bufImage = ImageIO.read(in);
                        fr.getContentPane().removeAll();
                        fr.getContentPane().add(new JLabel(new ImageIcon(bufImage)));
                        fr.pack();
                        fr.setVisible(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                try { Thread.sleep(60);
                } catch (InterruptedException e) {    }
            }
        }
    }

}
