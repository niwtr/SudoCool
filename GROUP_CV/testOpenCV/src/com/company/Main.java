package com.company;

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


    private static void showResult(Mat img) {
        Imgproc.resize(img, img, new Size(640, 480));
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

    private static boolean isContourSquare(MatOfPoint thisContour) {

        Rect ret = null;

        MatOfPoint2f thisContour2f = new MatOfPoint2f();
        MatOfPoint approxContour = new MatOfPoint();
        MatOfPoint2f approxContour2f = new MatOfPoint2f();

        thisContour.convertTo(thisContour2f, CvType.CV_32FC2);

        Imgproc.approxPolyDP(thisContour2f, approxContour2f, 2, true);

        approxContour2f.convertTo(approxContour, CvType.CV_32S);


        if (approxContour.size().height == 4) {
            ret = Imgproc.boundingRect(approxContour);
        }

        return (ret != null);
    }

    private static  boolean isSquare (MatOfPoint mp){
        double area=Imgproc.contourArea(mp);
        return (area<=26000 && area>=21000);
    }


    private static void findSquares(Mat img, List<MatOfPoint> squares, Mat oimg){ //,int minarea, int maxarea, int maxangl){

        List<MatOfPoint> contours=new ArrayList<>();

        int N=6;
        Size sz=new Size(img.width()& -2, img.height()&-2);
        Mat timg=img.clone();



        Mat hierarchy=new Mat();
        //for(int l=0;l<N;l++){
            Imgproc.findContours(timg, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE,new Point(0,0));


        List<MatOfPoint> tmp= new ArrayList<>();
        for(MatOfPoint mp : contours){
            if (isSquare(mp))
                tmp.add(mp);
            else{
             //   System.out.println(Imgproc.contourArea(mp));
            }
        }





        Mat res=new Mat(img.size(), CV_8U, new Scalar(0));
        Core.fillPoly(res, tmp, new Scalar(255));
        Mat rest=new Mat(img.size(), CV_8UC3, new Scalar(0, 255, 0));
        oimg.copyTo(rest, res);


        Rect r=Imgproc.boundingRect(tmp.get(0));
        System.out.println(r.area());




        Highgui.imwrite("/Users/Heranort/Desktop/ax.jpg", rest);




        squares.addAll(tmp);
            contours.clear();

    }


    public static void main(String[] args) {
        Imgproc img=new Imgproc();

        Mat imgi= Highgui.imread("/Users/Heranort/Desktop/sudo.jpg");



        Mat img2=new Mat();
        Mat img3=new Mat();
        Mat img4=new Mat();
        Imgproc.cvtColor(imgi,img2,Imgproc.COLOR_RGB2GRAY);
        /*erode and dilate*/

        Imgproc.Canny(img2, img2, 60,200);


        Mat elem10=new Mat(10,10, CV_8U, new Scalar(1));
        Imgproc.morphologyEx(img2, img2, MORPH_CLOSE, elem10);


        Highgui.imwrite("/Users/Heranort/Desktop/an.jpg", img2);



        List<MatOfPoint> squares=new ArrayList<>();


        findSquares(img2, squares, imgi);



        Core.polylines(imgi, squares, true, new Scalar(255,255,0));

        Highgui.imwrite("/Users/Heranort/Desktop/ab.jpg", imgi);



    }
}
