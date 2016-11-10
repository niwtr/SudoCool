package team.sudocool.ImgWorks;

import team.sudocool.ImgWorks.nImgProc.Cropper;
import team.sudocool.ImgWorks.nImgProc.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

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

        if(img.empty())return new int[size][size];
        Imgproc.cvtColor(img, img, COLOR_RGB2GRAY);
        //这里默许了数独棋盘它是黑色的。
        Imgproc.threshold(img, img, 127,255, color+THRESH_OTSU);

        return pattern(Skeletonizor.Skeletonize(img), size);

    }

    public int [][] Patternize(Mat img){

        //Utils.showResult(img);
        if(img.empty())return new int[size][size];
        Imgproc.cvtColor(img, img, COLOR_RGB2GRAY);

        //这里默许了数独棋盘它是黑色的。
        Imgproc.threshold(img, img, 127,255, COLOR+THRESH_OTSU);
        return pattern(Skeletonizor.Skeletonize(img), size);

    }

    private static int[][] pattern (Mat img, int size) {
        int[][] bimg = Cropper.crop(Utils.convertMat(img), 1);
        if(bimg.length==0)return new int[size][size];//all empty matrix.

        return divPattern(
                Cropper.nomalize(bimg,(bimg[0].length%size)>(size/2)?(bimg[0].length/size+1)*size:size
                        ,(bimg.length%size)>(size/2)?(bimg.length/size+1)*size:size), size);

/*
        return divPattern(
                Cropper.nomalize(bimg,(bimg[0].length%size)>(size/2)?(bimg[0].length/size+1)*size:size
                        ,size), size);
*/
        //return divPattern(Cropper.nomalize(bimg, size, size), size);
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
