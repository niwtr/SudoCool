package com.company;

import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import java.util.stream.Collectors;


import static org.opencv.core.CvType.CV_8U;
import static org.opencv.core.CvType.CV_8UC3;
import static org.opencv.imgproc.Imgproc.MORPH_CLOSE;
import static org.opencv.imgproc.Imgproc.boundingRect;


class cell<A, B> {
    public  Rect rect;
    public  MatOfPoint mat;
    public int y;
    public int x;
    public int width;
    public cell(Rect a, MatOfPoint b) {
        rect = a;
        mat = b;
        y=a.y;
        x=a.x;
        width=a.width;
    }



}



public class SquareExtractor {

    private static final int SUDOKU_SIZE=9;
    private static List<List<cell>> _arrange (List<List<cell>> matrix, double dist){
        List<List<cell>> rmatrix=new ArrayList<>();
        List<cell> lastRow = matrix.get(0);
        matrix.remove(0);
        for (List<cell> lst : matrix) {

            for (cell x : lst) {

                if (lastRow.stream().filter((y) ->
                        Math.abs(y.x - x.x) < dist / 2).count() == 0)// found exact position
                {
                    int index = 0;

                    for (; index < lastRow.size(); index++) {
                        if (lastRow.get(index).x > x.x) {
                            lastRow.add(index, new cell(new Rect(x.x, x.y, 0, 0), new MatOfPoint()));
                            break;
                        }
                    }

                }
            }
            rmatrix.add(lastRow);
            lastRow=lst;
        }
        rmatrix.add(lastRow);
        return rmatrix;
    }


    public static List<List<MatOfPoint>> arrangeSquare(List<MatOfPoint> pl){
        List<cell> rl=pl.stream()
                .map((x)->new cell(Imgproc.boundingRect(x), x))
                .sorted((m1, m2)->(m1.y>m2.y?1:-1))
                .collect(Collectors.toList());
        List<List<cell>> matrix=new ArrayList<>();
        while(rl.size()>0) {

            int y = rl.get(0).y,
                    wid = rl.get(0).width;
            matrix.add(
                    rl.stream().filter((x) ->
                            (Math.abs(x.y - y) < wid / 2))
                            .sorted((a,b)->
                                    (a.x>b.x?1:-1))
                            .collect(Collectors.toList())
            );
            rl = rl.stream()
                    .filter((x) ->
                            (Math.abs(x.y - y) > wid / 2))
                    .collect(Collectors.toList());
        }

        int width=matrix.get(0).get(0).width;
        for(int i=0;i<SUDOKU_SIZE-1;i++)
            matrix=_arrange(matrix,width);

        if(matrix.stream().filter((x)->x.size()!=SUDOKU_SIZE).count()!=0)
            return new ArrayList<>();
        return matrix.stream().map(
                (lst)->
                        lst.stream().map(
                                (x) -> x.mat)
                                .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }


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
    public static List<List<Mat>> squareCutter (Mat img, List<List<MatOfPoint>> polys){
        return polys.stream().map(
                (lst)->
                        lst.stream().map(
                                (x)->{
                                    if(x.empty())
                                        return new Mat();
                                    /* warning: could be show! */
                                    Mat res=new Mat(img.size(), CV_8U, new Scalar(0));
                                    Core.fillConvexPoly(res, x, new Scalar(255));
                                    Mat rest=new Mat(img.size(), CV_8UC3, new Scalar(0, 0, 0));
                                    //black background will be removed by the cropper.
                                    //really?
                                    img.copyTo(rest, res);
                                    return new Mat(rest, Imgproc.boundingRect(x));
                                })
                                .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }


}
