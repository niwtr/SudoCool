package com.company;

import org.omg.CORBA.IMP_LIMIT;
import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.core.CvType.CV_8UC3;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;

public class Main {
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

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

    private static void findSquares(Mat img, List<MatOfPoint> squares){ //,int minarea, int maxarea, int maxangl){

        List<MatOfPoint> contours=new ArrayList<>();

        int N=6;
        Size sz=new Size(img.width()& -2, img.height()&-2);
        Mat timg=img.clone();
/*
        Mat gray=new Mat(sz, CV_8UC1);
        Mat pyr=new Mat(new Size(sz.width/2, sz.height/2), CV_8UC3);
        Mat tgray = new Mat(sz, CV_8UC1);


        //CvSeq* result;disknow type.
        double s, t;

        //CvSeq* squares = cvCreateSeq( 0, sizeof(CvSeq), sizeof(CvPoint), storage ); disknow size.

        timg.adjustROI(0,0,(int)sz.width,(int)sz.height);
        Imgproc.pyrDown(timg, pyr );// cvPyrDown( timg, pyr, 7 );
        Imgproc.pyrUp(pyr,timg);

*/
        Highgui.imwrite("/Users/Heranort/Desktop/aa.jpg", timg);


        Mat hierarchy=new Mat();
        //for(int l=0;l<N;l++){
            Imgproc.findContours(timg, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE,new Point(0,0));


        List<MatOfPoint> tmp= new ArrayList<>();
        for(MatOfPoint mp : contours){
            if (isContourSquare(mp))
                tmp.add(mp);
        }
        int sqq=0;
        for(MatOfPoint mp : tmp){
            sqq+=Imgproc.contourArea(mp);
            System.out.println(Imgproc.contourArea(mp));
        }
        sqq/=tmp.size();
        System.out.println(sqq);

            contours.clear();
    }


    public static void main(String[] args) {
        Imgproc img=new Imgproc();

        Mat imgi= Highgui.imread("/Users/Heranort/Desktop/sudo.jpg");



        Mat img2=new Mat();
        Mat img3=new Mat();
        Mat img4=new Mat();
        Imgproc.cvtColor(imgi,img2,Imgproc.COLOR_RGB2GRAY);
        Imgproc.Canny(img2, img2, 60,200);
        //Imgproc.threshold(img2, img2, 100, 255, THRESH_BINARY);



        List<MatOfPoint> squares=new ArrayList<>();
        List<MatOfPoint> sq=new ArrayList<>();
        findSquares(img2, squares);



        //Highgui.imwrite("/Users/Heranort/Desktop/aa.jpg", img2);


        Core.polylines(imgi, squares, true, new Scalar(255,255,0));
        Highgui.imwrite("/Users/Heranort/Desktop/ab.jpg", imgi);






        //Imgproc.Sobel(img2,img3,img2.depth(),1,0);
        //Imgproc.Sobel(img2,img4,img2.depth(),0,1);


        /*
        Imgproc.threshold(img2,img3, 50, 255, 1);
        Highgui.imwrite("/Users/Heranort/Desktop/aa.jpg", img3);
        */

    }
}
