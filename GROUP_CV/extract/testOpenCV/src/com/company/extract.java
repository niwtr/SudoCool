package com.company;


import static com.company.ImgProc.Cropper.*;
import static java.lang.StrictMath.abs;

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

    public static double[][] div_square(int [][]bimg, int t_size){
        double[][] rs= new double[t_size][t_size];
        for(int i=0;i<t_size;i++){
            for(int j=0;j<t_size;j++){
                int count1=0;
                int count2=0;
                for(int k1=i*(8/t_size);k1<(i+1)*(8/t_size);k1++){
                    for(int k2=j*(8/t_size);k2<(j+1)*(8/t_size);k2++){
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
        int cou[][]=new int[2][s_size];
        int len=mat[0].length/(s_size+1)+1;
        for(int i=len;i<mat.length-1;i+=len){
            int count1=0;
            for(int j=0;j<mat[0].length;j++){
                if(mat[i][j]==1){
                    count1++;
                }
            }
            cou[0][(i/len)-1]=count1;
        }
        for(int n=len;n<mat[0].length-1;n+=len){
            int count2=0;
            for(int m=0;m<mat.length;m++){
                if(mat[m][n]==1){
                    count2++;
                }
            }
            cou[1][(n/len)-1]=count2;
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
        count=CalWei(mat[0]);
        re[0]=abs(count/height);
        count=CalWei(mat[len]);
        re[1]=abs(count/height);
        count=CalWei(mat[mat.length-1]);
        re[2]=abs(count/height);
        /*for(int i=len-1;i<mat.length;i+=len){
            count=CalWei(mat[i]);
            //System.out.println(count);
            re[j]=abs(count/height);
            j++;
        }*/
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

    public static int CheckCircle(int[][] mat){
        int count=0;
        int[][] al = new int[mat.length][mat[0].length];
        int coor1=0,coor2=0;
        int flag=1,flag1=0;
        for(int i=0;i<mat.length;i++){
            for(int j=0;j<mat[0].length;j++){
                al[i][j]=0;
                if(mat[i][j]==1&&flag==1) {
                    coor1 = i;
                    coor2 = j;
                    flag=0;
                    al[i][j]=2;
                }
            }
        }
        int temp1=coor1,temp2=coor2;
        flag1=0;
        while(true){
            if(temp1==coor1&&temp2==coor2&&al[coor1][coor2]==3){//找到圈
                count++;
                break;
            }System.out.print(flag1);
            if(flag1==1){
                if((abs(coor1-temp1)<=1)&&(abs(coor2-temp2)<=1)){
                    count++;
                   // System.out.println(count);
                    break;
                }
                break;
            }
            if((temp1-1>=0)&&(temp2+1<mat[0].length)&&(al[temp1-1][temp2+1]!=1)&&(mat[temp1-1][temp2+1]==1)){
                temp1--;
                temp2++;
                al[temp1][temp2]++;
            }
            else if((temp1-1>=0)&&(al[temp1-1][temp2]!=1)&&(mat[temp1-1][temp2]==1)){
                temp1--;
                al[temp1][temp2]++;
            }
            else if((temp1-1>=0)&&(temp2-1>=0)&&(al[temp1-1][temp2-1]!=1)&&(mat[temp1-1][temp2-1]==1)){
                temp1--;
                temp2--;
                al[temp1][temp2]++;
            }
            else if((temp2-1>=0)&&(al[temp1][temp2-1]!=1)&&(mat[temp1][temp2-1]==1)){
                temp2--;
                al[temp1][temp2]++;
            }
            else if((temp1+1<mat.length)&&(temp2-1>=0)&&(al[temp1+1][temp2-1]!=1)&&(mat[temp1+1][temp2-1]==1)){
                temp1++;
                temp2--;
                al[temp1][temp2]++;
            }
            else if((temp1+1<mat.length)&&(al[temp1+1][temp2]!=1)&&(mat[temp1+1][temp2]==1)){
                temp1++;
                al[temp1][temp2]++;
            }
            else if((temp1+1<mat.length)&&(temp2+1<mat[0].length)&&(al[temp1+1][temp2+1]!=1)&&(mat[temp1+1][temp2+1]==1)){
                temp1++;
                temp2++;
                al[temp1][temp2]++;
            }
            else {
                flag1=1;
            }
        }

      /*  flag=1;
        flag1=0;
        for(int m=mat.length-1;m>=0;m--){
            for(int n=mat[0].length-1;n>=0;n--){
                al[m][n]=0;
                if(mat[m][n]==1&&flag==1){
                    flag=0;
                    coor1=m;
                    coor2=n;
                    al[m][n]=2;
                }
            }
        }System.out.println();
        //System.out.println(count);
        temp1=coor1;
        temp2=coor2;
        while (true){
            if((temp1==coor1)&&(temp2==coor2)&&(al[coor1][coor2]==3)){
                count++;
                break;
            }System.out.print(flag1);
            if(flag1==1){
                if((abs(coor1-temp1)<=1)&&(abs(coor2-temp2)<=1)){
                    count++;
                    break;
                }
                break;
            }
            if((temp2-1>=0)&&(al[temp1][temp2-1]!=1)&&(mat[temp1][temp2-1]==1)){
                temp2--;
                al[temp1][temp2]++;
            }
            else if((temp1+1<mat.length)&&(temp2-1>=0)&&(al[temp1+1][temp2-1]!=1)&&(mat[temp1+1][temp2-1]==1)){
                temp1++;
                temp2--;
                al[temp1][temp2]++;
            }
            else if((temp1+1<mat.length)&&(al[temp1+1][temp2]!=1)&&(mat[temp1+1][temp2]==1)){
                temp1++;
                al[temp1][temp2]++;
            }
            else if((temp1+1<mat.length)&&(temp2+1<mat[0].length)&&(al[temp1+1][temp2+1]!=1)&&(mat[temp1+1][temp2+1]==1)){
                temp1++;
                temp2++;
                al[temp1][temp2]++;
            }
            else if((temp1-1>=0)&&(temp2-1>=0)&&(al[temp1-1][temp2-1]!=1)&&(mat[temp1-1][temp2-1]==1)){
                temp1--;
                temp2--;
                al[temp1][temp2]++;
            }
            else if((temp1-1>=0)&&(al[temp1-1][temp2]!=1)&&(mat[temp1-1][temp2]==1)){
                temp1--;
                al[temp1][temp2]++;
            }
            else if((temp1-1>=0)&&(temp2+1<mat[0].length)&&(al[temp1-1][temp2+1]!=1)&&(mat[temp1-1][temp2+1]==1)){
                temp1--;
                temp2++;
                al[temp1][temp2]++;
            }
            else {
                flag1=1;
            }
        }
        System.out.println(count);*/
        return count;
    }
}
