package com.company;

import com.sun.javafx.geom.Vec2f;
import org.omg.CORBA.IMP_LIMIT;
import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.CvType.*;
import static org.opencv.imgproc.Imgproc.*;

import java.io.*;
import javax.imageio.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class Main {
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }


    private static void findLines(Mat img, Mat origin_img){
        double PI=3.14150265;
        Mat lines=new Mat();

        Imgproc.HoughLinesP(img, lines, 1, PI/180, 200);



        for (int x = 0; x < lines.cols(); x++)
        {
            double[] vec = lines.get(0, x);
            double x1 = vec[0],
                    y1 = vec[1],
                    x2 = vec[2],
                    y2 = vec[3];
            Point start = new Point(x1, y1);
            Point end = new Point(x2, y2);

            System.out.println(Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1)));
            Core.line(origin_img, start, end, new Scalar(255,0,0), 3);

        }
        System.out.println(lines.get(0,1).length);
        showResult(origin_img);
    }

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



    private static  boolean isSquare (MatOfPoint mp, double lb, double ub){
        double area=Imgproc.contourArea(mp);
                return (area<=ub && area>=lb);
    }


    private static List<MatOfPoint> findSquares(Mat oimg, double lb, double ub){ //,int minarea, int maxarea, int maxangl){


        Mat img2=new Mat();
        Imgproc.cvtColor(oimg,img2,Imgproc.COLOR_RGB2GRAY);//morph into gray pic

        Imgproc.Canny(img2, img2, 60,200); //canny contour

        Highgui.imwrite("/Users/Heranort/Desktop/am.jpg", img2);

        Mat elem10=new Mat(10,10, CV_8U, new Scalar(1));
        Imgproc.morphologyEx(img2, img2, MORPH_CLOSE, elem10);//morph close

        Highgui.imwrite("/Users/Heranort/Desktop/an.jpg", img2);

        List<MatOfPoint> squares=new ArrayList<>();


        List<MatOfPoint> contours=new ArrayList<>();

        Mat timg=img2.clone();

        Mat hierarchy=new Mat();
        Imgproc.findContours(timg, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE,new Point(0,0));


        List<MatOfPoint> tmp= new ArrayList<>();
        for(MatOfPoint mp : contours){
            if (isSquare(mp, lb, ub))
                tmp.add(mp);
            else{
             //   System.out.println(Imgproc.contourArea(mp));
            }
        }

        //cut the image.
        Mat res=new Mat(img2.size(), CV_8U, new Scalar(0));
        Core.fillPoly(res, tmp, new Scalar(255));
        Mat rest=new Mat(img2.size(), CV_8UC3, new Scalar(0, 255, 0));
        oimg.copyTo(rest, res);
        Highgui.imwrite("/Users/Heranort/Desktop/ax.jpg", rest);

        squares.addAll(tmp);
            contours.clear();

        return squares;

    }


    public static void main(String[] args) {

        Mat imgi= Highgui.imread("/Users/Heranort/Desktop/sudo2.jpg");//low
        List<MatOfPoint> squares= findSquares(imgi, 2000, 3000);
        Core.polylines(imgi, squares, true, new Scalar(255,255,0));

    }
}
