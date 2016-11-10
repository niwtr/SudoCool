package team.sudocool.ImgWorks;

import com.sun.tools.corba.se.idl.toJavaPortable.Skeleton;
import org.opencv.core.*;
import team.sudocool.ImgWorks.nImgProc.Cropper;
import team.sudocool.ImgWorks.nImgProc.Utils;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

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
    public static int [][] Patternize(Mat img, int size, int color){

        Mat oimg=img.clone();
        if(img.empty())return new int[size][size];
        Imgproc.cvtColor(img, img, COLOR_RGB2GRAY);
        //这里默许了数独棋盘它是黑色的。
        Imgproc.threshold(img, img, 127,255, color+THRESH_OTSU);


        return pattern(oimg, Skeletonizor.Skeletonize(img), size);

    }

    public int [][] Patternize(Mat img){

        Mat iimg=img.clone();

        if(img.empty())return new int[size][size];
        Imgproc.cvtColor(img, img, COLOR_RGB2GRAY);
        //这里默许了数独棋盘它是黑色的。
        Imgproc.threshold(img, img, 127,255, COLOR+THRESH_OTSU);

        return pattern(iimg, Skeletonizor.Skeletonize(img), size);


    }

    private static int[][] pattern (Mat oimg, Mat img, int size) {


        List<MatOfPoint> contours=new ArrayList<>();


        Mat workingImg=img.clone();

        Mat hierarchy=new Mat();
        Imgproc.findContours(workingImg, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE,new Point(0,0));

        Function<MatOfPoint ,Double> __=(pl->
                pl.toList().stream()
                        .map(p->p.x)
                        .reduce(0.0,
                                (x1,x2)->x1+x2)/pl.toList().size());
        Function<MatOfPoint ,Double> __2=(pl->
                pl.toList().stream()
                        .map(p->p.y)
                        .reduce(0.0,
                                (y1,y2)->y1+y2)/pl.toList().size());

        MatOfPoint mp;
        if(contours.size()>1){
            mp=contours.stream().sorted((c1,c2)-> __2.apply(c1)>__2.apply(c2)?1:-1)
                    .max(
                    (con1, con2)->
                    {

                        return __.apply(con1)>__.apply(con2)?1:-1;
                    }).get();
        }
        else if(contours.size()==1) {
            mp=contours.get(0);

        }
        else
            mp=new MatOfPoint();




        if(mp.toList().size()>0){
            Rect r=Imgproc.boundingRect(mp);

            Mat rected_img=new Mat(img.rows(), img.cols(), CV_8UC1, new Scalar(0));

            Core.rectangle(rected_img, new Point(r.x, r.y),new Point(r.x+r.width, r.y+r.height), new Scalar(255,0,0), Core.FILLED);

            Mat rest2=new Mat(img.size(), CV_8UC3, new Scalar(0, 255, 0));
            img.copyTo(rest2, rected_img);
            Utils.showResult(rest2);
        }


        int[][] bimg = Cropper.crop(Utils.convertMat(img), 1);


        if(bimg.length==0)return new int[size][size];//all empty matrix.



/*
        Utils.showMatrix(
        divPattern(
                Cropper.nomalize(bimg,(bimg[0].length%size)>(size/2)?(bimg[0].length/size+1)*size:size
                        ,(bimg.length%size)>(size/2)?(bimg.length/size+1)*size:size), size));
*/
/*
        return divPattern(
                Cropper.nomalize(bimg,(bimg[0].length%size)>(size/2)?(bimg[0].length/size+1)*size:size
                        ,(bimg.length%size)>(size/2)?(bimg.length/size+1)*size:size), size);
*/



/*
        return divPattern(
                Cropper.nomalize(bimg,(bimg[0].length%size)>(size/2)?(bimg[0].length/size+1)*size:size
                        ,size), size);
*/

    //Utils.showMatrix(Cropper.nomalize(bimg,size,size));
        Mat _1=Utils.convertByteM(Cropper.nomalize(bimg,size,size));
        Imgproc.resize(_1,_1,new Size(140,140));
        bimg=Utils.convertMat(Skeletonizor.Skeletonize(_1));

        return divPattern(bimg, size);

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
        return rst;
    }
}
