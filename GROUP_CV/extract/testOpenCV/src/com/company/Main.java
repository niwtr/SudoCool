package com.company;


import com.company.ImgProc.Cropper;
import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import  static com.company.extract.*;
import static com.company.ImgProc.Cropper.*;
import static com.company.Skeletonizor.Skeletonize;
import static org.opencv.core.Mat.zeros;
import static org.opencv.imgproc.Imgproc.*;
import com.company.ImgProc.Utils;
import java.io.*;
import javax.imageio.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class Main {
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }


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



    private static int[][] pattern (Mat img, int size) {
        int[][] bimg = Cropper.crop(Utils.convertMat(img), 1);
        return divPattern(Cropper.nomalize(bimg, size, size), size);
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


    public static void main(String[] args) {

        Mat imgi= Highgui.imread("F:/number/7_4.bmp");//low
        //Imgproc.resize(imgi, imgi,new Size(50,50));
        Imgproc.cvtColor(imgi, imgi, COLOR_RGB2GRAY);//灰度图

        //这里默许了数独棋盘它是白色的。

        Imgproc.threshold(imgi, imgi, 127,255, THRESH_BINARY+THRESH_OTSU);//二值化


        Highgui.imwrite("F:/2.jpg", Skeletonize(imgi));//骨架

        Mat imgu=Skeletonize(imgi);
        Mat imgu1=Skeletonize(imgu);


        int[][] arr=pattern(imgu,7);
        int[][] arr1 = Cropper.crop(Utils.convertMat(imgu), 1);
        int[][] arr2 = Cropper.crop(Utils.convertMat(imgu1), 1);

        Utils.printMatrix(pattern(imgu, 7));


       // System.out.println(CornerHarris_demo(imgi));
        int num=(arr1.length/7)*7==arr1.length?arr1.length/7:arr1.length/7+1;
       // PrintDouble(div_square(arr1,num,7));
        System.out.println("---------------");
        double[] darr=div_contour(arr1);
        for(int i=0;i<darr.length;i++){
            System.out.print(darr[i]);
            System.out.print(" ");
        }
        System.out.println();
        System.out.println("---------------");
        int[][] iarr=div_stroke(arr1,4);
        for(int j=0;j<iarr.length;j++){
            for(int k=0;k<iarr[0].length;k++){
                System.out.print(iarr[j][k]);
                System.out.print(" ");
            }
            System.out.println(" ");
        }
        System.out.println("---------------");
        System.out.println(CheckCircle(arr));
        System.out.println("---------------");
        //CornerHarris_demo(imgi);
        CheckCorner(arr2);
    }
}
