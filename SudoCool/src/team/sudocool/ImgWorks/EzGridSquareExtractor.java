package team.sudocool.ImgWorks;

import com.sun.tools.corba.se.idl.toJavaPortable.Util;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;
import team.sudocool.Identifier.Identifier;
import team.sudocool.ImgWorks.nImgProc.Utils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Exchanger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.opencv.imgproc.Imgproc.*;

/**
 * Created by Heranort on 16/11/16.
 */
public class EzGridSquareExtractor {

    private static final int SUDOKU_SIZE=9;
    private int sizex;
    private int sizey;
    private int scissor;
    public EzGridSquareExtractor(int sizex, int sizey, int scissor){
        this.sizex=sizex;
        this.sizey=sizey;
        this.scissor=scissor;
    }


    Patternizor P=new Patternizor(7, Patternizor.WHITE_BACKGROUND);//cowsay
    Identifier I=new Identifier();
    public  MatOfPoint getOuterBoundContour(Mat img){
        Mat img2=new Mat();
        Imgproc.cvtColor(img,img2,Imgproc.COLOR_RGB2GRAY);//morph into gray pic

        //Imgproc.threshold(img2,img2,127,255,THRESH_BINARY_INV+THRESH_OTSU);

        Imgproc.adaptiveThreshold(img2,img2,255, ADAPTIVE_THRESH_GAUSSIAN_C, THRESH_BINARY, 151,1);


        Imgproc.Laplacian(img2,img2,img2.depth());


        List<MatOfPoint> squares=new ArrayList<>();

        List<MatOfPoint> contours=new ArrayList<>();


        Mat hierarchy=new Mat();
        Imgproc.findContours(img2, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE,new Point(0,0));

        List<MatOfPoint> bound=contours.stream().filter(mp ->
        {
            if(Imgproc.contourArea(mp)<(sizex*sizey/9))
                return false;

            MatOfPoint2f m2f=new MatOfPoint2f(),
                    apr=new MatOfPoint2f();
            m2f.fromList(mp.toList());
            double peri=Imgproc.arcLength(m2f, true);

            Imgproc.approxPolyDP(m2f, apr, 0.02*peri,true);
            return apr.size().height==4;
        }).sorted((mp1,mp2)->
        {
            double amp1=Imgproc.contourArea(mp1),amp2=Imgproc.contourArea(mp2);
            if(amp1>amp2)return -1;
            else if(amp1==amp2)return 0;
            else return 1;
        }).collect(Collectors.toList());
        return bound.size()>=1?bound.get(0):null;
    }


    public  Mat DrawOuterBound(Mat img){
        Mat iimg=img.clone();
        MatOfPoint bound=getOuterBoundContour(img);
        if (bound==null)return img;
        List<MatOfPoint> mp=new ArrayList<>();
        mp.add(bound);
        Core.polylines(img, mp, true, new Scalar(0,255,0), 5);
        return img;
    }

    //3 -> 0~3

    @FunctionalInterface
    interface Function3 <A, B, C, R> {
        //R is like Return, but doesn't have to be last in the list nor named R.
        public R apply (A a, B b, C c);
    }


    public Mat DrawRecognizedNumbers(Mat img){

        Mat iimg=img.clone();
        MatOfPoint bound=getOuterBoundContour(img);
        if (bound==null)return img;
        List<MatOfPoint> mp=new ArrayList<>();
        mp.add(bound);
        Core.polylines(img, mp, true, new Scalar(0,0,255), 3);

        List<Mat> outseq2=Extract(iimg);
        List<Integer> numList=outseq2.stream().map(P::Patternize28x28).map(I::toDigit).collect(Collectors.toList());

        MatOfPoint2f boundf=new MatOfPoint2f(),
                abound=new MatOfPoint2f();
        boundf.fromList(bound.toList());
        double peri=Imgproc.arcLength(boundf, true);
        approxPolyDP(boundf, abound, 0.02*peri, true);
        List<Point> clockwised=Utils.clockwise(abound.toList());


        Function <Integer, Point>
                l1=Utils.lineDivPointFunc(clockwised.get(0), clockwised.get(1), SUDOKU_SIZE),
                l2=Utils.lineDivPointFunc(clockwised.get(0), clockwised.get(3), SUDOKU_SIZE); //really 9?

        Function3<Integer, Integer, Integer, Point> __=(zero, x, y)->
        {
            Point pO=l1.apply(0), pE=l1.apply(x), pW=l2.apply(y);
            double
                    deltay1=pW.y-pO.y,
                    deltay2=pE.y-pO.y,
                    deltax1=pW.x-pO.x,
                    deltax2=pE.x-pO.x,
                    px=pO.x+deltax1+deltax2,
                    py=pO.y+deltay1+deltay2;
            return new Point(px, py);
        };


        Point p=new Point();
        for(int y=0; y<9; y++){
            for(int x=0; x<9; x++){

                Point
                        p0=__.apply(0, x, y),
                        p1=__.apply(0, x+1, y),
                        p3=__.apply(0, x, y+1);

                double
                        p0x=p0.x,
                        p1x=p1.x,
                        p0y=p0.y,
                        p3y=p3.y,
                        width=Math.abs(p1x-p0x);

                p.x=p0x;
                p.y=p3y;
                Core.putText
                        (img,
                        ""+numList.get(y*SUDOKU_SIZE+x),
                        p,
                        Core.FONT_HERSHEY_PLAIN,
                        width/(SUDOKU_SIZE+4),
                        new Scalar(0,255,0),
                        2);
            }
        }
        return img;
    }






    public Mat transform4(Mat img, MatOfPoint bound){


        MatOfPoint2f boundf=new MatOfPoint2f(), abound=new MatOfPoint2f();
        boundf.fromList(bound.toList());
        double peri=Imgproc.arcLength(boundf, true);
        approxPolyDP(boundf, abound, 0.02*peri, true);



        List<Point> lp=abound.toList();

        Comparator<Point>
                _x=( _1, _2)->
        {
            if (_1.x > _2.x) return 1;
            else if (_1.x == _2.x) return 0;
            else return -1;
        },
                _y=(_1, _2)->
                {
                    if(_1.y>_2.y)return 1;
                    else if (_1.y == _2.y)return 0;
                    else return -1;
                };


        List<Point> clockwised=Utils.clockwise(lp);


        //add points clockwisely.
        List<Point> dstl=new ArrayList<>();
        dstl.add(new Point(0,0));
        dstl.add(new Point(sizex,0));
        dstl.add(new Point(sizex,sizey));//cowsay, move to 4
        dstl.add(new Point(0,sizey));



        Mat perspectiveTransform = Imgproc.getPerspectiveTransform(Converters.vector_Point2f_to_Mat(clockwised),
                Converters.vector_Point2f_to_Mat(dstl));
        Mat rst=new Mat();
        Imgproc.warpPerspective(img, rst, perspectiveTransform, new Size(sizex,sizey));
        return rst;
    }




    public List<Mat> Extract(Mat img){
        List<Mat>rst=new ArrayList<>();
        MatOfPoint bound=getOuterBoundContour(img);
        if(bound==null)return null;
        Mat tr=transform4(img, bound);
        //Utils.showResult(tr);
        double dx=sizex/SUDOKU_SIZE,dy=sizey/SUDOKU_SIZE;
        for(int y=0;y<SUDOKU_SIZE;y++){//0 to 8
            for(int x=0;x<SUDOKU_SIZE;x++){
                Rect r=new Rect((int)(x*dx+3), (int)(y*dy+3),(int)dx-2*scissor,(int)dy-2*scissor);
                rst.add(new Mat(tr, r));

            }
        }
        return rst;
    }

}
