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
import static org.opencv.core.Mat.zeros;
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

          System.out.println("hello");
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

    /* well this one is not as good as the version1 . */
    private static boolean isSquare2 (MatOfPoint mp, double lb, double ub){
        Rect rt=boundingRect(mp);
        return (rt.area()<=ub && rt.area()>=lb);

    }


    private static Mat prepare_findSquares(Mat oimg){
        Mat img2=new Mat();
        Imgproc.cvtColor(oimg,img2,Imgproc.COLOR_RGB2GRAY);//morph into gray pic

        //Imgproc.GaussianBlur(img2, img2, new Size(3,3), 0);

        Highgui.imwrite("/Users/Heranort/Desktop/blured.jpg", img2);

        Imgproc.Canny(img2, img2, 60,200); //canny contour

        Highgui.imwrite("/Users/Heranort/Desktop/am.jpg", img2);

        Mat elem10=new Mat(5,5, CV_8U, new Scalar(1));
        Imgproc.morphologyEx(img2, img2, MORPH_CLOSE, elem10);//morph close


        Highgui.imwrite("/Users/Heranort/Desktop/an.jpg", img2);

        return img2;

    }
    private static List<MatOfPoint> findSquares1(Mat oimg, double lb, double ub){ //,int minarea, int maxarea, int maxangl){

        Mat img2=prepare_findSquares(oimg);

        List<MatOfPoint> squares=new ArrayList<>();

        List<MatOfPoint> contours=new ArrayList<>();

        Mat hierarchy=new Mat();
        Imgproc.findContours(img2, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE,new Point(0,0));

        List<MatOfPoint> tmp= new ArrayList<>();
        for(MatOfPoint mp : contours){
            if (isSquare(mp, lb, ub)) {
                tmp.add(mp);
            }
        }
        return tmp;

    }
    private static Mat cut_squares1 (Mat img,  List<MatOfPoint> polys){
        Mat res=new Mat(img.size(), CV_8U, new Scalar(0));
        Core.fillPoly(res, polys, new Scalar(255));
        Mat rest=new Mat(img.size(), CV_8UC3, new Scalar(0, 255, 0));
        img.copyTo(rest, res);
        return rest;
    }
    private static List<Rect> findSquares2(Mat oimg, double lb, double ub){

            Mat img2=prepare_findSquares(oimg);

            List<MatOfPoint> contours=new ArrayList<>();

            Mat hierarchy=new Mat();
            Imgproc.findContours(img2, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE,new Point(0,0));

            List<Rect> rct=new ArrayList<>();
            for(MatOfPoint mp : contours){
                if (isSquare(mp, lb, ub)) {
                    rct.add(boundingRect(mp));
                }
            }
            return rct;
    }

    private static Mat cut_squares2 (Mat img, List<Rect> rct){
        Mat rected_img=new Mat(img.rows(), img.cols(), CV_8UC1, new Scalar(0));
        for(Rect r : rct) {
            Core.rectangle(rected_img, new Point(r.x, r.y),new Point(r.x+r.width, r.y+r.height), new Scalar(255,0,0), Core.FILLED);
        }
        Mat rest2=new Mat(img.size(), CV_8UC3, new Scalar(0, 255, 0));
        img.copyTo(rest2, rected_img);
        return rest2;
    }


    private static void arrangeSquares(List<Rect> rct){
        List<Rect>rects=new ArrayList<Rect>(rct);

        rects.sort((Rect a, Rect b)->(a.y>b.y)?1:-1);

        List <Rect>r =new ArrayList<>();


        int i=0;
        double start = rects.get(0).y;
        double end = rects.get(rects.size() - 1).y;
        double delta = (end - start) / 9;
        while(rects.size()>0) {

            start = rects.get(0).y;
            while(rects.size()>0) {
                if ((rects.get(0).y - start) < delta) {
                    r.add(rects.get(0));
                    rects.remove(0);
                } else break;
            }
            System.out.println(r.size());
        }
    }

    public static void main(String[] args) {

        Mat imgi= Highgui.imread("/Users/Heranort/Desktop/sudo5.jpg");//low

                List<MatOfPoint> squares= findSquares1(imgi, 6000, 13000);
        List<Rect> rects=findSquares2(imgi, 6000, 13000);
        //Core.polylines(imgi, squares, true, new Scalar(255,255,0));

        Mat rst1=cut_squares1(imgi, squares);
        Mat rst2=cut_squares2(imgi, rects);


        System.out.println(rects.size());

        Highgui.imwrite("/Users/Heranort/Desktop/aaa.jpg", rst1);
    }
}
