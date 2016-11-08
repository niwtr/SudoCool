package com.company;


import static com.company.ImgProc.Cropper.*;
import static java.lang.StrictMath.abs;
import static org.opencv.core.Core.NORM_MINMAX;
import static org.opencv.core.Core.convertScaleAbs;
import static org.opencv.core.CvType.CV_32FC1;
import static org.opencv.imgproc.Imgproc.*;
import static org.opencv.core.Mat.*;

import com.company.ImgProc.Cropper;
import org.opencv.core.*;

/**
 * Created by 74079_000 on 2016/11/6 0006.
 */

public class extract {//需要一个将原本二值化的图片切成t_size等分的函数

    private static int CalWei(int[] array){
        int i=0,j=array.length;
        for(i=0;i<array.length;i++){
            if(array[i]==1){
                break;
            }
        }
        for(j=array.length-1;j>=0;j--) {
            if (array[j] == 1) {
                break;
            }
        }
        return i-j;
    }

    public static double[][] div_square(int [][]bimg, int t_size, int a_size){
        double[][] rs= new double[a_size][a_size];//8,7
        for(int i=0;i<a_size;i++){
            for(int j=0;j<a_size;j++){
                int count1=0;
                int count2=0;
                for(int k1=i*t_size;(k1<(i+1)*t_size)&&(k1<bimg.length);k1++){
                    for(int k2=j*t_size;(k2<(j+1)*t_size)&&(k2<bimg[0].length);k2++){
                        count1++;
                        if(bimg[k1][k2]==1){
                            count2++;
                        }
                    }
                }
                rs[i][j]=(double)count2/(double)count1;
            }
        }
        return rs;
    }

    public static int[][] div_stroke(int [][]mat, int s_size){
        //s_size是希望有多少条切线
        int cou[][]=new int[2][s_size+1];
        int len1=(mat.length/(s_size+1))*(s_size+1)>=mat.length?mat.length/(s_size+1):mat.length/(s_size+1)+1;
        int len2=(mat[0].length/(s_size+1))*(s_size+1)>=mat[0].length?mat[0].length/(s_size+1):mat[0].length/(s_size+1)+1;
        for(int i=len1;i<mat.length-1;i+=len1){
            int count1=0;
            for(int j=0;j<mat[0].length;j++){
                if(mat[i][j]==1){
                    count1++;
                }
            }
            cou[0][(i/len1)-1]=count1;
        }
        for(int n=len2;n<mat[0].length-1;n+=len2){
            int count2=0;
            for(int m=0;m<mat.length;m++){
                if(mat[m][n]==1){
                    count2++;
                }
            }
            cou[1][(n/len2)-1]=count2;
        }
        return cou;
    }

    public static double[] div_contour(int[][] mat){
        double[] re=new double[3];
        int height=0;
        double count=0;
        height=Cropper.rt_height(mat,1);
        int len=mat.length/2;
        int j=0;
        /*count=CalWei(mat[0]);
        re[0]=abs(count/height);
        count=CalWei(mat[len]);
        re[1]=abs(count/height);
        count=CalWei(mat[mat.length-1]);
        re[2]=abs(count/height);*/
        for(int i=len-1;i<mat.length;i+=len){
            count=CalWei(mat[i]);
            //System.out.println(count);
            re[j]=abs(count/height);
            j++;
        }
        return re;
    }

    public static void PrintDouble(double[][] darr){

        for(int i=0;i<darr.length;i++){
            for(int j=0;j<darr[0].length;j++){
                System.out.print(darr[i][j]);
                System.out.print(" ");
            }
            System.out.println( );
        }
    }

    public static void CheckCorner(int[][] mat){
        int count=0;
        int t1=0,t2=0,nt=0,nb=0,nc=0;
        int v=0,e=0;


        for(int i=1;i<mat.length-1;i++){
            for(int j=1;j<mat[0].length-1;j++){
                if(mat[i][j]==1){
                    t1=abs(mat[i-1][j+1]-mat[i][j+1])+abs(mat[i-1][j]-mat[i-1][j+1])+abs(mat[i-1][j-1]-mat[i-1][j])+abs(mat[i][j-1]-mat[i-1][j-1]);
                    t2=abs(mat[i+1][j-1]-mat[i][j-1])+abs(mat[i+1][j]-mat[i+1][j-1])+abs(mat[i+1][j+1]-mat[i+1][j])+abs(mat[i][j+1]-mat[i+1][j+1]);
                    if(t1+t2==2){
                        nt++;
                    }
                    else if(t1+t2==6){
                        nb++;
                    }
                    else if(t1+t2==8){
                        nc++;
                    }
                }
            }
        }
       // e=(nt+3*nb+4*nc)/2;
       // v=nt+nb+nc;
        System.out.println(nt+" "+nb+" "+nc);
       /* if(e>v){
            count=2;
        }
        else if(e==v&&e!=0&&v!=0){
            count=1;
        }
        else{
            count=0;
        }*/
        //return count;
    }

    public static int CheckCircle(int[][] mat){
        int count=0,count1=0;
        int[][] al = new int[mat.length][mat[0].length];
        int coor1=0,coor2=0;
        int flag=1,flag1=0;
        int max=mat.length>=mat[0].length?mat.length:mat[0].length;
        int inter=(max/10)*10<max?max/10+1:max/10;
        for(int i=0;i<mat.length;i++){
            for(int j=0;j<mat[0].length;j++){
                al[i][j]=0;
                if(mat[i][j]==1&&flag==1&&(j!=mat[0].length-1)) {
                    coor1 = i;
                    coor2 = j;
                    flag=0;
                    al[i][j]=2;
                }
            }
        }
        int temp1=coor1,temp2=coor2;
        flag1=0;
        count1=0;
        while(true){
            if(temp1==coor1&&temp2==coor2&&al[coor1][coor2]==3&&count1>=7){
                count++;
                break;
            }
            if(flag1==1){
                if((abs(coor1-temp1)<=inter)&&(abs(coor2-temp2)<=inter)&&(count1>=7)){
                    count++;
                    break;
                }
                break;
            }
            if((temp2-1>=0)&&(al[temp1][temp2-1]==0)&&(mat[temp1][temp2-1]==1)){
                temp2--;
                al[temp1][temp2]++;
                count1++;
            }
            else if((temp2+1<mat[0].length)&&(al[temp1][temp2+1]==0)&&(mat[temp1][temp2+1]==1)){
                temp2++;
                al[temp1][temp2]++;
                count1++;
            }
            else if((temp1-1>=0)&&(temp2-1>=0)&&(al[temp1-1][temp2-1]==0)&&(mat[temp1-1][temp2-1]==1)){
                temp1--;
                temp2--;
                al[temp1][temp2]++;
                count1++;
            }
            else if((temp1-1>=0)&&(al[temp1-1][temp2]==0)&&(mat[temp1-1][temp2]==1)){
                temp1--;
                al[temp1][temp2]++;
                count1++;
            }
            else if((temp1-1>=0)&&(temp2+1<mat[0].length)&&(al[temp1-1][temp2+1]==0)&&(mat[temp1-1][temp2+1]==1)){
                temp1--;
                temp2++;
                al[temp1][temp2]++;
                count1++;
            }
            else if((temp1+1<mat.length)&&(temp2-1>=0)&&(al[temp1+1][temp2-1]==0)&&(mat[temp1+1][temp2-1]==1)){
                temp1++;
                temp2--;
                al[temp1][temp2]++;
                count1++;
            }
            else if((temp1+1<mat.length)&&(al[temp1+1][temp2]==0)&&(mat[temp1+1][temp2]==1)){
                temp1++;
                al[temp1][temp2]++;
                count1++;
            }
            else if((temp1+1<mat.length)&&(temp2+1<mat[0].length)&&(al[temp1+1][temp2+1]==0)&&(mat[temp1+1][temp2+1]==1)){
                temp1++;
                temp2++;
                al[temp1][temp2]++;
                count1++;
            }
            else {
                flag1=1;
            }
        }
        int T1=0,T2=0;
        T1=coor1;
        T2=coor2;

        flag=1;
        for(int m=mat.length-1;m>=0;m--){
            for(int n=mat[0].length-1;n>=0;n--){
                al[m][n]=0;
                if(mat[m][n]==1&&flag==1&&(n!=0)){
                    flag=0;
                    coor1=m;
                    coor2=n;
                    al[m][n]=2;
                }
            }
        }
        temp1=coor1;
        temp2=coor2;
        flag1=0;
        count1=0;
        while (true){
            if((temp1==coor1)&&(temp2==coor2)&&(al[coor1][coor2]==3)&&(count1>=7)){
                count++;
                break;
            }
            if(flag1==1){
                if((abs(coor1-temp1)<=inter)&&(abs(coor2-temp2)<=inter)&&(count1>=7)){
                    count++;
                    break;
                }
                break;
            }

            if((temp2+1<mat[0].length)&&(al[temp1][temp2+1]==0)&&(mat[temp1][temp2+1]==1)){
                temp2++;
                al[temp1][temp2]++;
                count1++;
            }
            else if((temp1+1<mat.length)&&(temp2-1>=0)&&(al[temp1+1][temp2-1]==0)&&(mat[temp1+1][temp2-1]==1)&&(count>0)){
                temp1++;
                temp2--;
                count1++;
                al[temp1][temp2]++;
            }
            else if((temp1+1<mat.length)&&(al[temp1+1][temp2]==0)&&(mat[temp1+1][temp2]==1)){
                temp1++;
                al[temp1][temp2]++;
                count1++;
            }
            else if((temp1+1<mat.length)&&(temp2+1<mat[0].length)&&(al[temp1+1][temp2+1]==0)&&(mat[temp1+1][temp2+1]==1)){
                temp1++;
                temp2++;
                count1++;
                al[temp1][temp2]++;
            }
            else if((temp1-1>=0)&&(temp2+1<mat[0].length)&&(al[temp1-1][temp2+1]==0)&&(mat[temp1-1][temp2+1]==1)){
                temp1--;
                temp2++;
                al[temp1][temp2]++;
                count1++;
            }
            else if((temp1-1>=0)&&(al[temp1-1][temp2]==0)&&(mat[temp1-1][temp2]==1)){
                temp1--;
                al[temp1][temp2]++;
                count1++;
            }
            else if((temp1-1>=0)&&(temp2-1>=0)&&(al[temp1-1][temp2-1]==0)&&(mat[temp1-1][temp2-1]==1)){
                temp1--;
                temp2--;
                al[temp1][temp2]++;
                count1++;
            }
            else if((temp2-1>=0)&&(al[temp1][temp2-1]==0)&&(mat[temp1][temp2-1]==1)){
                temp2--;
                al[temp1][temp2]++;
                count1++;
            }
            else if((temp1+1<mat.length)&&(temp2-1>=0)&&(al[temp1+1][temp2-1]==0)&&(mat[temp1+1][temp2-1]==1)&&(count==0)){
                temp1++;
                temp2--;
                count1++;
                al[temp1][temp2]++;
            }
            else {
                flag1=1;
            }
        }
        if(al[T1][T2]==1&&count>1){
            count--;
        }
        return count;
    }

    /*public static int CornerHarris_demo(Mat mat){
        int count=0;
        Mat dst=mat;
        Mat dst_norm=dst;
        int Len=mat.rows()>mat.cols()?mat.cols():mat.rows();
        int blockSize = 2;
        int apertureSize = 3;
        double k = 0.04;
        cornerHarris( mat, dst, blockSize, apertureSize, k, BORDER_DEFAULT );
        normalize( dst, dst_norm, 0, 255, NORM_MINMAX, CV_32FC1, dst.rows(),dst.cols() );
        Mat scaledImage=dst_norm;
        convertScaleAbs( dst_norm, scaledImage );
        Mat harrisCorner=dst;
        threshold(dst, harrisCorner, 0.00001, 255, THRESH_BINARY);
        for( int j = 0; j < dst.rows() ; j++ )
        { for( int i = 0; i < dst.cols(); i++ )
        {
            if( (int) dst.get(j,i)[0] >0 )
            {
                count++;
            }
        }
        }
        return count;
    }*/
}

