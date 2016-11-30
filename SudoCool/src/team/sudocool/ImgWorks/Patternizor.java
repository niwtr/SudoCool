package team.sudocool.ImgWorks;

import org.opencv.core.*;
import team.sudocool.ImgWorks.nImgProc.Cropper;
import team.sudocool.ImgWorks.nImgProc.Utils;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.opencv.core.Core.NORM_MINMAX;
import static org.opencv.core.CvType.CV_8U;
import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.core.CvType.CV_8UC3;
import static org.opencv.imgproc.Imgproc.*;

/**
 * Created by Heranort on 16/11/7.
 */


public class Patternizor {


    public static final int BLACK_BACKGROUND=THRESH_BINARY;
    public static final int WHITE_BACKGROUND=THRESH_BINARY_INV;



    private int COLOR;
    private int size;

    public Patternizor(int size, int COLOR){
        this.COLOR=COLOR;
        this.size=size;
    }
    /*@obselete
    public static int [][] Patternize(Mat img, int size, int color){

        Mat oimg=img.clone();
        if(img.empty())return new int[size][size];
        Imgproc.cvtColor(img, img, COLOR_RGB2GRAY);
        //这里默许了数独棋盘它是黑色的。
//        Imgproc.threshold(img, img, 127,255, color+THRESH_OTSU);

        Imgproc.adaptiveThreshold(img,img,255, ADAPTIVE_THRESH_GAUSSIAN_C, THRESH_BINARY, 151,1);
        //why we should do this again?


        return pattern(oimg, Skeletonizor.Skeletonize(img), size);

    }


    public int [][] Patternize(Mat img){


        Mat iimg=img.clone();


        if(img.empty())return new int[size][size];

        //Core.normalize(img,img,0,255,NORM_MINMAX);

        Imgproc.cvtColor(img, img, COLOR_RGB2GRAY);

        Imgproc.threshold(img, img, 127,255, COLOR);//+THRESH_OTSU);
        //Utils.printMatrix(pattern(iimg, Skeletonizor.Skeletonize(img), size));
        //System.out.println();

        return pattern(iimg, Skeletonizor.Skeletonize(img), size);


    }
    */
    public int[][] Patternize28x28(Mat img){
        Mat iimg=img.clone();
        if(img.empty())return new int[size][size];
        Imgproc.threshold(img, img, 127,255, COLOR);//+THRESH_OTSU);
        return ezPattern28x28(iimg, img, size); //old: pattern28x28.
    }

    private static int[][] pattern28x28(Mat oimg, Mat img, int size){

        img=Cropper.ccrop(img);
        int[][] bimg = Cropper.crop(Utils.convertMat(img), 1);


        if(bimg.length==0)return new int[size][size];//all empty matrix.

        Mat _1=Utils.convertByteM(Cropper.nomalize(bimg,size,size));
        Imgproc.resize(_1,_1,new Size(28,28));

        //Utils.showResult(_1);
        return Utils.convertMat(_1);
    }


    private static int[][] ezPattern28x28(Mat oimg, Mat img, int size){
        int[][] bimg=Cropper.ezCrop(img);
        if(bimg.length==0)return new int[size][size];
        Mat _1=Utils.convertByteM(Cropper.nomalize(bimg,size,size));
        Imgproc.resize(_1,_1,new Size(28,28));
        return Utils.convertMat(_1);

    }


    private static double calcVal(int [][]img, int x1,int x2, int y1, int y2){
        int sum=0, wcount=0;
        for(int i=x1;i<x2;i++){
            for(int j=y1;j<y2;j++){
                sum++;
                if(img[j][i]==1)wcount++;
            }
        }
        return (double)wcount/sum;
    }
    private static int[][] divPattern(int[][]img, int iterate){
        int [][]rst=new int[iterate][iterate];
        int cols=img[0].length,rows=img.length;
        if(rows>=iterate && cols>=iterate){
            int hstep=cols/iterate;
            int vstep=rows/iterate;

            for(int i=0;i<iterate;i++){
                for(int j=0;j<iterate;j++){
                    int x=i*hstep,y=j*vstep;
                    double val=
                            calcVal(img,
                                    x,
                                    x+(i<iterate-1?hstep:(cols-x)),
                                    y,
                                    y+(j<iterate-1?vstep:(rows-y)));
                    rst[j][i]=val>0?1:0;
                }
            }

        }
        else{
            //error: img is not normalized.
        }
        //Utils.printMatrix(rst);
        //System.out.println();
        return rst;
    }
}
