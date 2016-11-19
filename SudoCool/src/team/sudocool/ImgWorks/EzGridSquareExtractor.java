package team.sudocool.ImgWorks;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;
import team.sudocool.ImgWorks.nImgProc.Utils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.opencv.imgproc.Imgproc.*;

/**
 * Created by Heranort on 16/11/16.
 */
public class EzGridSquareExtractor {

    public int SUDOKU_SIZE;
    private int outerBoundSizex;
    private int outerBoundSizey;
    private int scissorWidth;
    private MatOfPoint Bound=new MatOfPoint();
    private List<Mat> ExtractedCells = new ArrayList<>();
    public MatOfPoint getBound(){return this.Bound;}//brings getOuterBoundContour() to passive.
    public List<Mat> getExtractedCells(){return this.ExtractedCells;}


    public EzGridSquareExtractor(int sudokuSize, int sizex, int sizey, int scissor){
        this.SUDOKU_SIZE=sudokuSize;
        this.outerBoundSizex =sizex;
        this.outerBoundSizey =sizey;
        this.scissorWidth =scissor;
    }



    public  MatOfPoint ExtractOuterBoundContour(Mat img){
        Mat img2=new Mat();
        Imgproc.cvtColor(img,img2,Imgproc.COLOR_RGB2GRAY);//morph into gray pic

        //Imgproc.threshold(img2,img2,127,255,THRESH_BINARY_INV+THRESH_OTSU);

        //now using adaptive threshold.
        Imgproc.adaptiveThreshold(img2,img2,255, ADAPTIVE_THRESH_GAUSSIAN_C, THRESH_BINARY, 151,1);


        Imgproc.Laplacian(img2,img2,img2.depth());


        List<MatOfPoint> squares=new ArrayList<>();

        List<MatOfPoint> contours=new ArrayList<>();


        Mat hierarchy=new Mat();
        Imgproc.findContours(img2, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE,new Point(0,0));

        List<MatOfPoint> bound=contours.stream().filter(mp ->
        {
            if(Imgproc.contourArea(mp)<(outerBoundSizex * outerBoundSizey /9))
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

        this.Bound=bound.size()>=1?bound.get(0):null;
        return this.Bound;
    }


    public Mat transform4(Mat img){

        MatOfPoint bound=getBound();
        if(bound==null)return img;

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
        dstl.add(new Point(outerBoundSizex,0));
        dstl.add(new Point(outerBoundSizex, outerBoundSizey));
        dstl.add(new Point(0, outerBoundSizey));



        Mat perspectiveTransform = Imgproc.getPerspectiveTransform(Converters.vector_Point2f_to_Mat(clockwised),
                Converters.vector_Point2f_to_Mat(dstl));
        Mat rst=new Mat();
        Imgproc.warpPerspective(img, rst, perspectiveTransform, new Size(outerBoundSizex, outerBoundSizey));
        return rst;
    }




    public boolean Extract(Mat img){
        List<Mat>rst=new ArrayList<>();
        MatOfPoint bound= getBound();   //GetOuterBoundContour(img);
        if(bound==null)return false;
        Mat tr=transform4(img);
        //Utils.showResult(tr);
        double dx= outerBoundSizex /SUDOKU_SIZE,dy= outerBoundSizey /SUDOKU_SIZE;
        for(int y=0;y<SUDOKU_SIZE;y++){//0 to 8
            for(int x=0;x<SUDOKU_SIZE;x++){
                Rect r=new Rect((int)(x*dx+3), (int)(y*dy+3),(int)dx-2* scissorWidth,(int)dy-2* scissorWidth);
                rst.add(new Mat(tr, r));

            }
        }
        this.ExtractedCells=rst;
        return true;
    }

}
