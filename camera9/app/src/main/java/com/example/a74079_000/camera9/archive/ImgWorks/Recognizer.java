package com.example.a74079_000.camera9.archive.ImgWorks;

import org.omg.PortableInterceptor.SUCCESSFUL;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import com.example.a74079_000.camera9.archive.Identifier.Identifier;
import com.example.a74079_000.camera9.archive.ImgWorks.nImgProc.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.opencv.imgproc.Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
import static org.opencv.imgproc.Imgproc.approxPolyDP;

/**
 * Created by Heranort on 16/11/19.
 */
public class Recognizer {

    //background color of sudoku.

    public static final int WHITE_BACKGROUND=Patternizor.WHITE_BACKGROUND;
    public static final int BLACK_BACKGROUND=Patternizor.BLACK_BACKGROUND;
    public static final int STANDARD_OUTERBOUND_SIZEX=450;
    public static final int STANDARD_OUTERBOUND_SIZEY=450;
    public static final int STANDARD_SCISSOR_SIZE=5;//old: three
    public static final int STANDARD_SUDOKU_SIZE=9;

    private Patternizor P;
    private Identifier I;
    private EzGridSquareExtractor E;
    private Mat Img;
    private Mat ThresholdImg;
    private MatOfPoint Bound;

    private List<Integer> RecognizedNumbers;
    private int[][] ArrangedNumbers;


    public Recognizer(){

        P=new Patternizor(7, Patternizor.WHITE_BACKGROUND);//default is white background.
        I=new Identifier();
        E=new EzGridSquareExtractor(STANDARD_SUDOKU_SIZE, STANDARD_OUTERBOUND_SIZEX,
                STANDARD_OUTERBOUND_SIZEY, STANDARD_SCISSOR_SIZE);

        this.Bound=new MatOfPoint();
    }

    public void setScissor(int scissorWidth){
        this.E.scissorWidth=scissorWidth;
    }

    private Recognizer getImg(Mat img){
        this.Img=img;return this;
    }

    private Recognizer preProcessImg(){
        Mat img2=this.Img.clone();
        Imgproc.cvtColor(Img,img2,Imgproc.COLOR_RGB2GRAY);//morph into gray pic
        Imgproc.adaptiveThreshold(img2,img2,255, ADAPTIVE_THRESH_GAUSSIAN_C, THRESH_BINARY, 151,1);
        //the background should be in white.
        this.ThresholdImg=img2;

        return this;
    }

    private Recognizer extractOuterBound(){
        E.ExtractOuterBoundContour(this.ThresholdImg);
        return this;
    }
    private Recognizer recognizeNumbers(){
        if(E.Extract(this.ThresholdImg)){
            this.RecognizedNumbers=E
                    .getExtractedCells()
                    .stream()
                    .map(P::Patternize28x28)
                    .map(I::toDigit)
                    .collect(Collectors.toList());
        }
        return this;
    }


    private Recognizer arrangeNumbersMatrix(){
        if(this.RecognizedNumbers==null)return this;
        this.ArrangedNumbers=new int[E.SUDOKU_SIZE][E.SUDOKU_SIZE];
        for(int i=0; i<E.SUDOKU_SIZE ; i++){
            for(int j=0; j<E.SUDOKU_SIZE ; j++){
                this.ArrangedNumbers[i][j]=this.RecognizedNumbers.get(i*E.SUDOKU_SIZE+j);
            }
        }
        return this;
    }

    private Recognizer drawOuterBound (){
        MatOfPoint bound= E.getBound();
        if (bound==null)return this;

        MatOfPoint2f boundf=new MatOfPoint2f(),
                abound=new MatOfPoint2f();
        boundf.fromList(bound.toList());
        double peri= Imgproc.arcLength(boundf, true);
        approxPolyDP(boundf, abound, 0.02*peri, true);
        this.Bound.fromList(abound.toList());
        List<MatOfPoint> mp=new ArrayList<>();
        mp.add(Bound);

        //mp.add(bound);
        Core.polylines(Img, mp, true, new Scalar(255,255,0), 3);
        return this;
    }

    @FunctionalInterface
    interface Function3 <A, B, C, R> {public R apply (A a, B b, C c);}


    private Recognizer drawRecognizedNumbers(){

        MatOfPoint bound= E.getBound();
        List<Integer> numList=this.RecognizedNumbers;
        if(bound==null || numList==null)return this;


        List<Point> clockwised= Utils.clockwise(this.Bound.toList());


        Function<Integer, Point>
                l1=Utils.lineDivPointFunc
                (clockwised.get(0), clockwised.get(1), E.SUDOKU_SIZE),
                l2=Utils.lineDivPointFunc
                        (clockwised.get(0), clockwised.get(3), E.SUDOKU_SIZE);

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
        for(int y=0; y<E.SUDOKU_SIZE; y++){
            for(int x=0; x<E.SUDOKU_SIZE; x++){

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


                int num=numList.get(y*E.SUDOKU_SIZE+x);
                Core.putText
                        (Img,
                                (num==-1?"":""+num),
                                p,
                                Core.FONT_HERSHEY_PLAIN,
                                width/(E.SUDOKU_SIZE+4),
                                new Scalar(0,0,255),
                                2);
            }
        }
        return this;
    }




    public Mat __getTransformedBound(Mat img){//will be removed in the future.
        this.getImg(img).preProcessImg().extractOuterBound();
        E.Extract2(ThresholdImg);
        return E.ExtractedCellsGraph;

    }

    public Mat Recognize(Mat img){
        this
                .getImg(img)
                .preProcessImg()
                .extractOuterBound()
                .recognizeNumbers()
                .arrangeNumbersMatrix()
                .drawOuterBound()
                .drawRecognizedNumbers();

        return this
                .Img;
    }



}