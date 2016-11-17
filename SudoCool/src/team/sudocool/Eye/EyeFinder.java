package team.sudocool.Eye;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
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
 * Created by Heranort on 16/11/16.
 */
public class EyeFinder extends Thread {

    private Eye e;
    private Finder f;
    public EyeFinder() {
        e = new Eye();
        //f = new Finder();
        e.setVisible(true);
       // e.Watch();
        //f.setVisible(true);
        //new EFThread().start();
        this.start();
    }
/*
    public void Watch() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    e.Watch();
                    f.Watch();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
*/
    //private class EFThread extends Thread {
        @Override
        public void run() {
            for (; ; ) {


                Mat em = e.snapAPhoto();
                if (em.size().height > 0 && em.channels() >= 3) {

                    f.setMat(em);
                }

                try {
                    Thread.sleep(3);
                } catch (InterruptedException e) {
                }
            }
        }
   //}
}

