package team.sudocool.ImgWorks;

import com.sun.tools.corba.se.idl.toJavaPortable.Util;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;
import team.sudocool.ImgWorks.nImgProc.Utils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Exchanger;
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


    private static MatOfPoint getOuterBoundContour(Mat img){
        Mat img2=new Mat();
        Imgproc.cvtColor(img,img2,Imgproc.COLOR_RGB2GRAY);//morph into gray pic

        //Imgproc.threshold(img2,img2,127,255,THRESH_BINARY_INV+THRESH_OTSU);

        Imgproc.adaptiveThreshold(img2,img2,255, ADAPTIVE_THRESH_GAUSSIAN_C, THRESH_BINARY, 151,1);


        Imgproc.Laplacian(img2,img2,img2.depth());


        List<MatOfPoint> squares=new ArrayList<>();

        List<MatOfPoint> contours=new ArrayList<>();


        Mat hierarchy=new Mat();
        Imgproc.findContours(img2, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE,new Point(0,0));

        MatOfPoint bound=contours.stream().filter(mp ->
        {
            MatOfPoint2f m2f=new MatOfPoint2f(),
                    apr=new MatOfPoint2f();
            m2f.fromList(mp.toList());
            double peri=Imgproc.arcLength(m2f, true);

            Imgproc.approxPolyDP(m2f, apr, 0.02*peri,true);
            return apr.size().height==4;
        }).min((mp1,mp2)->
        {
            double amp1=Imgproc.contourArea(mp1),amp2=Imgproc.contourArea(mp2);
            if(amp1>amp2)return -1;
            else if(amp1==amp2)return 0;
            else return 1;
        }).get();
        return bound;
    }


    public static Mat DrawOuterBound(Mat img){
        MatOfPoint bound=getOuterBoundContour(img);
        List<MatOfPoint> mp=new ArrayList<>();
        mp.add(bound);
        Core.polylines(img, mp, true, new Scalar(0,255,0), 5);
        return img;
    }
    public Mat transform4(Mat img){

        MatOfPoint bound=getOuterBoundContour(img);
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

        lp.sort(_x);
        lp.subList(0,1).sort(_y.reversed());
        lp.subList(2,3).sort(_y);

        /*
        p1=lp.get(1);
        p3=lp.get(0);
        p2=lp.get(2);
        p4=lp.get(3);
        */
        /* 1 2
           3 4 */

        List<Point> dstl=new ArrayList<>();
        dstl.add(new Point(0,sizey));
        dstl.add(new Point(0,0));
        dstl.add(new Point(sizex,sizey));//cowsay, move to 4
        dstl.add(new Point(sizex,0));



        Mat perspectiveTransform = Imgproc.getPerspectiveTransform(Converters.vector_Point2f_to_Mat(lp),
                Converters.vector_Point2f_to_Mat(dstl));
        Mat rst=new Mat();
        Imgproc.warpPerspective(img, rst, perspectiveTransform, new Size(sizex,sizey));
        return rst;
    }


    public List<Mat> Extract(Mat img){
        List<Mat>rst=new ArrayList<>();
        Mat tr=transform4(img);
        Utils.showResult(tr);
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
