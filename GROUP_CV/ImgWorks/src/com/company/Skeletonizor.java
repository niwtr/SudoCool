package com.company;

import com.company.nImgProc.nPoint;
import com.company.nImgProc.Utils;
import org.opencv.core.*;


import static org.opencv.core.Mat.zeros;

import java.util.LinkedList;
import java.util.List;

public class Skeletonizor {



    public static Mat Skeletonize(final Mat givenImage) {
        int[][] binaryImage=Utils.convertMat(givenImage);

        int a, b;
        List<nPoint> pointsToChange = new LinkedList();
        boolean hasChange;
        do {
            hasChange = false;
            for (int y = 1; y + 1 < binaryImage.length; y++) {
                for (int x = 1; x + 1 < binaryImage[y].length; x++) {
                    a = getA(binaryImage, y, x);
                    b = getB(binaryImage, y, x);
                    if (binaryImage[y][x] == 1 && 2 <= b && b <= 6 && a == 1
                            && (binaryImage[y - 1][x] * binaryImage[y][x + 1] * binaryImage[y + 1][x] == 0)
                            && (binaryImage[y][x + 1] * binaryImage[y + 1][x] * binaryImage[y][x - 1] == 0)) {
                        pointsToChange.add(new nPoint(x, y));
//binaryImage[y][x] = 0;
                        hasChange = true;
                    }
                }
            }
            for (nPoint point : pointsToChange) {
                binaryImage[point.getY()][point.getX()] = 0;
            }
            pointsToChange.clear();
            for (int y = 1; y + 1 < binaryImage.length; y++) {
                for (int x = 1; x + 1 < binaryImage[y].length; x++) {
                    a = getA(binaryImage, y, x);
                    b = getB(binaryImage, y, x);
                    if (binaryImage[y][x] == 1 && 2 <= b && b <= 6 && a == 1
                            && (binaryImage[y - 1][x] * binaryImage[y][x + 1] * binaryImage[y][x - 1] == 0)
                            && (binaryImage[y - 1][x] * binaryImage[y + 1][x] * binaryImage[y][x - 1] == 0)) {
                        pointsToChange.add(new nPoint(x, y));
                        hasChange = true;
                    }
                }
            }
            for (nPoint point : pointsToChange) {
                binaryImage[point.getY()][point.getX()] = 0;
            }
            pointsToChange.clear();
        } while (hasChange);

        return Utils.convertByteM(binaryImage);
    }

    private static int getA(int[][] binaryImage, int y, int x) {
        int count = 0;
//p2 p3
        if (y - 1 >= 0 && x + 1 < binaryImage[y].length && binaryImage[y - 1][x] == 0 && binaryImage[y - 1][x + 1] == 1) {
            count++;
        }
//p3 p4
        if (y - 1 >= 0 && x + 1 < binaryImage[y].length && binaryImage[y - 1][x + 1] == 0 && binaryImage[y][x + 1] == 1) {
            count++;
        }
//p4 p5
        if (y + 1 < binaryImage.length && x + 1 < binaryImage[y].length && binaryImage[y][x + 1] == 0 && binaryImage[y + 1][x + 1] == 1) {
            count++;
        }
//p5 p6
        if (y + 1 < binaryImage.length && x + 1 < binaryImage[y].length && binaryImage[y + 1][x + 1] == 0 && binaryImage[y + 1][x] == 1) {
            count++;
        }
//p6 p7
        if (y + 1 < binaryImage.length && x - 1 >= 0 && binaryImage[y + 1][x] == 0 && binaryImage[y + 1][x - 1] == 1) {
            count++;
        }
//p7 p8
        if (y + 1 < binaryImage.length && x - 1 >= 0 && binaryImage[y + 1][x - 1] == 0 && binaryImage[y][x - 1] == 1) {
            count++;
        }
//p8 p9
        if (y - 1 >= 0 && x - 1 >= 0 && binaryImage[y][x - 1] == 0 && binaryImage[y - 1][x - 1] == 1) {
            count++;
        }
//p9 p2
        if (y - 1 >= 0 && x - 1 >= 0 && binaryImage[y - 1][x - 1] == 0 && binaryImage[y - 1][x] == 1) {
            count++;
        }
        return count;
    }

    private static  int getB(int[][] binaryImage, int y, int x) {
        return binaryImage[y - 1][x] + binaryImage[y - 1][x + 1] + binaryImage[y][x + 1]
                + binaryImage[y + 1][x + 1] + binaryImage[y + 1][x] + binaryImage[y + 1][x - 1]
                + binaryImage[y][x - 1] + binaryImage[y - 1][x - 1];
    }



    //opencv-ized version
    /**
     * @param img
     */
    public Mat skeletonize (Mat img){
        Mat binaryImage=new Mat();
        binaryImage=img;
        int a, b;
        List<nPoint> pointsToChange=new LinkedList<>();
        boolean hasChange;
        do{
            hasChange=false;
            for(int y=1; y + 1<binaryImage.rows();y++){
                for(int x=1;x+1<binaryImage.cols();x++){
                    a=getA(binaryImage, y,x);
                    b=getB(binaryImage, y,x);
                    if (binaryImage.get(y,x)[0] >10 && 2 <= b && b <= 6 && a == 1
                            && (binaryImage.get(y-1,x)[0] * binaryImage.get(y,x+1)[0] * binaryImage.get(y+1,x)[0] == 0)
                            && (binaryImage.get(y,x+1)[0] * binaryImage.get(y+1,x)[0] * binaryImage.get(y,x-1)[0] == 0)) {
                        pointsToChange.add(new nPoint(x, y));
                        hasChange = true;
                    }

                }
            }
            byte[] buf=new byte[1];buf[0]=0;
            for (nPoint point : pointsToChange) {
                binaryImage.put(point.getY(),point.getX(), buf);
            }
            pointsToChange.clear();
            for (int y = 1; y + 1 < binaryImage.rows(); y++) {
                for (int x = 1; x + 1 < binaryImage.cols(); x++) {
                    a = getA(binaryImage, y, x);
                    b = getB(binaryImage, y, x);
                    if (binaryImage.get(y,x)[0] >10 && 2 <= b && b <= 6 && a == 1
                            && (binaryImage.get(y-1,x)[0] * binaryImage.get(y,x+1)[0] * binaryImage.get(y,x-1)[0] == 0)
                            && (binaryImage.get(y-1,x)[0] * binaryImage.get(y+1,x)[0] * binaryImage.get(y,x-1)[0] == 0)) {
                        pointsToChange.add(new nPoint(x, y));
                        hasChange = true;
                    }

                }
            }
            for (nPoint point : pointsToChange) {

                binaryImage.put(point.getY(),point.getX(), buf);
            }
            pointsToChange.clear();
        }while (hasChange);
        return binaryImage;
    }

    private int getA(Mat binaryImage, int y, int x) {
        int count = 0;
//p2 p3
        int cols=binaryImage.cols();
        int rols=binaryImage.rows();
        if (y - 1 >= 0 && x + 1 < cols && binaryImage.get(y-1,x)[0] == 0 && binaryImage.get(y-1,x+1)[0] >10) {
            count++;
        }
//p3 p4
        if (y - 1 >= 0 && x + 1 < cols && binaryImage.get(y-1,x+1)[0] == 0 && binaryImage.get(y,x+1)[0] > 0) {
            count++;
        }
//p4 p5
        if (y + 1 < rols && x + 1 < cols && binaryImage.get(y,x+1)[0] == 0 && binaryImage.get(y+1,x+1)[0]>10) {
            count++;
        }
//p5 p6
        if (y + 1 < rols && x + 1 < cols && binaryImage.get(y+1,x+1)[0] == 0 && binaryImage.get(y+1,x)[0]>10) {
            count++;
        }
//p6 p7
        if (y + 1 < rols && x - 1 >= 0 && binaryImage.get(y+1,x)[0] == 0 && binaryImage.get(y+1,x-1)[0]>10) {
            count++;
        }
//p7 p8
        if (y + 1 < rols && x - 1 >= 0 && binaryImage.get(y+1,x-1)[0] == 0 && binaryImage.get(y,x-1)[0]>10) {
            count++;
        }
//p8 p9
        if (y - 1 >= 0 && x - 1 >= 0 && binaryImage.get(y,x-1)[0]== 0 && binaryImage.get(y-1,x-1)[0]>10) {
            count++;
        }
//p9 p2
        if (y - 1 >= 0 && x - 1 >= 0 && binaryImage.get(y-1,x-1)[0]== 0 && binaryImage.get(y-1,x)[0]>10) {
            count++;
        }
        return count;
    }



    private int getB(Mat binaryImage, int y, int x) {
        int xx=(int)(binaryImage.get(y-1,x)[0] + binaryImage.get(y-1,x+1)[0] + binaryImage.get(y,x+1)[0]
                + binaryImage.get(y+1,x+1)[0] + binaryImage.get(y+1,x)[0] + binaryImage.get(y+1,x-1)[0]
                + binaryImage.get(y,x-1)[0] + binaryImage.get(y-1,x-1)[0])/255;

        return xx;
    }




}


