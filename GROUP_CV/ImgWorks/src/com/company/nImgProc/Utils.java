package com.company.nImgProc;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.opencv.core.CvType.CV_8U;

/**
 * Created by Heranort on 16/11/5.
 */
public class Utils {


    public static void showResult(Mat img) {
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


    public static int[][]convertMat(Mat bm){
        int xbound=bm.rows();
        int ybound=bm.cols();
        int [][]rst=new int[xbound][ybound];

        for(int i=0;i<xbound;i++){
            for(int j=0;j<ybound;j++){
                rst[i][j]=(int)(bm.get(i,j)[0]>0?1:0);
            }
        }
        return rst;
    }



    public static Mat convertByteM(int [][] intm){
        int xbound=intm.length;
        int ybound=intm[0].length;
        Mat bm=new Mat(xbound,ybound,CV_8U);
        for(int i=0;i<xbound;i++){

            byte[] u=new byte[ybound];
            for(int j=0;j<ybound;j++)
                u[j]=(byte)(intm[i][j]>0?255:0);
            bm.put(i,0, u);
        }

        return bm;
    }


    public static void printMatrix(int [][]bimg){
        for(int i=0;i<bimg.length;i++){
            int count=0;
            for(int j=0;j<bimg[i].length;j++){
                if(bimg[i][j]>0) {
                    System.out.print(1);
                    System.out.print(" ");
                }
                else{
                    System.out.print(0);
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }
    public static String dumpMatrix(int [][]bimg){
        String rst=new String();
        for(int i=0;i<bimg.length;i++){
            int count=0;
            for(int j=0;j<bimg[i].length;j++){
                if(bimg[i][j]>0) {
                    //System.out.print(1);
                    rst=rst+String.valueOf(1)+" ";
                }
                else{
                    rst=rst+String.valueOf(0)+" ";
                }
            }
            rst=rst+"\n";
        }
        return rst;
    }

    public static void writeMatrix(String pathname, int [][]bimg){
        Highgui.imwrite(pathname, convertByteM(bimg));
    }

}
