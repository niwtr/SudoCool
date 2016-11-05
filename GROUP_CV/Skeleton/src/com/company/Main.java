package com.company;


import com.sun.tools.corba.se.idl.toJavaPortable.Skeleton;
import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import static com.company.ImgProc.Cropper.crop;
import static com.company.Skeletonizor.Skeletonize;
import static org.opencv.core.CvType.CV_8U;
import static org.opencv.core.Mat.zeros;
import static org.opencv.imgproc.Imgproc.*;
import com.company.ImgProc.Utils;
import java.io.*;
import javax.imageio.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class Main {
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }


    private static void showResult(Mat img) {
        double ratio=img.cols()/img.rows();
        Imgproc.resize(img, img, new Size(640, 640*ratio));
        MatOfByte matOfByte = new MatOfByte();
        Highgui.imencode(".jpg", img, matOfByte);
        byte[] byteArray = matOfByte.toArray();
        BufferedImage bufImage = null;
        try {
            InputStream in = new ByteArrayInputStream(byteArray);
            bufImage = ImageIO.read(in);
            JFrame frame = new JFrame();
            frame.getContentPane().add(new JLabel(new ImageIcon(bufImage)));
            frame.pack();
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private static void pattern (Mat img){
        int [][] bimg=crop(Utils.convertMat(img),1);
        for(int i=0;i<bimg.length;i++){
            int count=0;
            for(int j=0;j<bimg[i].length;j++){
                if(bimg[i][j]>0) {
                    System.out.print(1);
                    System.out.print(" ");
                }
                else{
                    System.out.print(0);
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
        Highgui.imwrite("/Users/Heranort/Desktop/fivx.jpg", Utils.convertByteM(bimg));
    }




    public static void main(String[] args) {

        Mat imgi= Highgui.imread("/Users/Heranort/Desktop/fiv.jpg");//low
        Imgproc.resize(imgi, imgi,new Size(50,50));
        Imgproc.cvtColor(imgi, imgi, COLOR_RGB2GRAY);

        //这里默许了数独棋盘它是白色的。

        Imgproc.threshold(imgi, imgi, 127,255, THRESH_BINARY_INV+THRESH_OTSU);


        //Highgui.imwrite("/Users/Heranort/Desktop/skel.png", Skeletonize(imgi));

        Mat imgu=Skeletonize(imgi);

        pattern(imgu);

    }
}
