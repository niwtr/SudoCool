package com.company.ImgProc;

import org.opencv.core.Mat;

import static org.opencv.core.CvType.CV_8U;

/**
 * Created by Heranort on 16/11/5.
 */
public class Utils {
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

}
