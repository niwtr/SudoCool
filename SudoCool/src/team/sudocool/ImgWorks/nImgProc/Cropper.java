package team.sudocool.ImgWorks.nImgProc;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.CvType.CV_8UC1;

/**
 * Created by Heranort on 16/11/5.
 */
public class Cropper {
    private static int collectOnes(int [] arr){
        int count=0;
        for(int i=0;i<arr.length;i++){
            if(arr[i]==1)count++;
        }return count;
    }
    private static int scan_down(int [][] mat, int threshold){
        int rst=0;
        for(int i=0;i<mat.length-2;i++){
            int
                    count1=collectOnes(mat[i]),
                    count2=collectOnes(mat[i+1]),
                    count3=collectOnes(mat[i+2]);
            if(count1>0 && count2>0 && count3>0){
                rst=i;
                break;
            }
        }
        return rst;
    }



    private static int scan_up(int [][] mat, int threshold){
        int rst=0;
        for(int i=mat.length-1;i>2;i--){
            int
                    count1=collectOnes(mat[i-2]),
                    count2=collectOnes(mat[i-1]),
                    count3=collectOnes(mat[i]);
            if(count1>0 && count2>0 && count3>0){
                rst=i+1;
                break;
            }
        }
        return rst;
    }

    private static int [][] transposeMat(int [][] mat){
        int [][]tmat=new int[mat[0].length][mat.length];
        for(int i=0;i<tmat.length;i++) {
            for (int j = 0; j < tmat[i].length; j++) {
                tmat[i][j] = mat[j][i];
            }
        }return tmat;
    }
    private static int scan_right(int [][] mat, int threshold){
        return scan_down(transposeMat(mat),threshold);
    }

    private static int scan_left(int [][]mat, int threshold){
        return scan_up(transposeMat(mat), threshold);
    }

    //erase the outer contour of the matrix. (_->0)
    private static int[][] pre_crop(int [][]mat, int thickness){
        for(int j=0;j<mat[0].length;j++){

            for(int i=0;i<thickness;i++) {
                mat[i][j] = 0;
                mat[mat.length - 1 -i][j] = 0;
            }
        }
        for(int i=0;i<mat.length;i++){
            for(int j=0;j<thickness;j++) {
                mat[i][j] = 0;
                mat[i][mat[0].length - 1 - j] = 0;
            }
        }
        return mat;
    }

    public static int[][] crop(int [][] mat, int threshold){

//        System.out.printf("%d %d \n", mat.length, mat[0].length);
        mat=pre_crop(mat, 3); //we need not pre_crop since we have ccrop

        int
                ub=scan_down(mat, threshold),
                db=scan_up(mat, threshold),
                rb=scan_left(mat, threshold),
                lb=scan_right(mat, threshold);
        int [][] o = new int[db-ub][rb-lb];
        for(int i=0;i<o.length;i++){
            for(int j=0;j<o[i].length;j++){
                o[i][j]=mat[i+ub][j+lb];
            }
        }
        //Utils.showMatrix(o);

        return o;
    }


    private static int ezScanDown(int [][] mat){
        int rst=0;
        for(int i=0;i<mat.length-1;i++){
            int
                    count1=collectOnes(mat[i]);

            if(count1>0){
                rst=i;
                break;
            }
        }
        return rst;
    }
    private static int ezScanUp(int [][] mat){
        int rst=0;
        for(int i=mat.length-1;i>0;i--){
            int
                    count=collectOnes(mat[i]);
            if(count>0){
                rst=i;//cowsay: hmm..
                break;
            }
        }
        return rst;
    }
    private static int ezScanRight(int [][] mat){
        return ezScanDown(transposeMat(mat));
    }

    private static int ezScanLeft(int [][]mat){ return ezScanUp(transposeMat(mat)); }


    public static int[][] ezCrop(Mat m){

        m=ccrop(m);

        int[][]mtr=Utils.convertMat(m);
        mtr=pre_crop(mtr, (int)(m.cols()*0.1-1));
        int
                ub=ezScanDown(mtr),
                db=ezScanUp(mtr),
                rb=ezScanLeft(mtr),
                lb=ezScanRight(mtr);

        int [][] o = new int[db-ub][rb-lb];
        for(int i=0;i<o.length;i++){
            for(int j=0;j<o[i].length;j++){
                o[i][j]=mtr[i+ub][j+lb];
            }
        }
        return o;
    }
    public static Mat ccrop(Mat m){
        int xx=m.cols(),yy=m.rows();
        Mat mm=m.clone();
        List<MatOfPoint> lmp=new ArrayList<>();
        Mat hierarchy=new Mat();
        Imgproc.findContours(mm, lmp, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE,new Point(0,0));
        for (MatOfPoint mp : lmp) {
            Moments mom= Imgproc.moments(mp);
            int x=(int)(mom.get_m10()/mom.get_m00()),
                    y=(int)(mom.get_m01()/mom.get_m00());

            double th=0.1;//old:0.1

            if(x<xx*th || x>xx*(1-th) || y<yy*th || y>yy* (1-th)) {

                Core.fillConvexPoly(m, mp, new Scalar(0));

            }
        }

        return m;
    }

    public static int [][]nomalize(int [][]mat, int xmin, int ymin){

        int rows=mat.length,cols=mat[0].length;
        int [][]rst=new int [ymin>rows?ymin:rows][xmin>cols?xmin:cols];
        int dx=((int)((xmin-cols)/2)>0)?((int)((xmin-cols)/2)):0,
                dy=((int)((ymin-rows)/2))>0?((int)((ymin-rows)/2)):0;
        for(int y=0;y<rows;y++){
            //平移
            for(int x=0;x<cols;x++){
                rst[y+dy][x+dx]=mat[y][x];
            }
        }
        return rst;
    }

}
