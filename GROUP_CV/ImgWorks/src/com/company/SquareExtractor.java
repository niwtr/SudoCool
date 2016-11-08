package com.company;

import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.CvType.CV_8U;
import static org.opencv.core.CvType.CV_8UC3;
import static org.opencv.imgproc.Imgproc.MORPH_CLOSE;

/**
 * Created by Heranort on 16/11/8.
 */
public class SquareExtractor {


    private static  boolean isSquare (MatOfPoint mp, double lb, double ub){
        double area= Imgproc.contourArea(mp);
        return (area<=ub && area>=lb);
    }


    private static Mat prepare_findSquares(Mat oimg){
        Mat img2=new Mat();
        Imgproc.cvtColor(oimg,img2,Imgproc.COLOR_RGB2GRAY);//morph into gray pic

        Imgproc.Canny(img2, img2, 60,200); //canny contour

        Mat elem10=new Mat(5,5, CV_8U, new Scalar(1));
        Imgproc.morphologyEx(img2, img2, MORPH_CLOSE, elem10);//morph close

        Highgui.imwrite("/Users/Heranort/Desktop/an.jpg", img2);
        return img2;
    }


    public static List<MatOfPoint> Extract(Mat img, double lb, double ub){
        return findSquares(prepare_findSquares(img), lb, ub);
    }

    private static List<MatOfPoint> findSquares(Mat oimg, double lb, double ub){

        List<MatOfPoint> squares=new ArrayList<>();

        List<MatOfPoint> contours=new ArrayList<>();

        Mat hierarchy=new Mat();
        Imgproc.findContours(oimg, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE,new Point(0,0));

        List<MatOfPoint> tmp= new ArrayList<>();
        for(MatOfPoint mp : contours){
            if (isSquare(mp, lb, ub)) {
                tmp.add(mp);
            }
        }
        return tmp;

    }
    public static Mat cutSquares (Mat img,  List<MatOfPoint> polys){
        Mat res=new Mat(img.size(), CV_8U, new Scalar(0));
        Core.fillPoly(res, polys, new Scalar(255));
        Mat rest=new Mat(img.size(), CV_8UC3, new Scalar(0, 255, 0));
        img.copyTo(rest, res);
        return rest;
    }

}
