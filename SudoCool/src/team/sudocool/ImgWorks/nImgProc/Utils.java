package team.sudocool.ImgWorks.nImgProc;

import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.opencv.core.CvType.CV_8U;

/**
 * Created by Heranort on 16/11/5.
 */
public class Utils {


    public static double pointDist(Point p1, Point p2){
        return (p1.x-p2.x)*(p1.x-p2.x)+(p1.y-p2.y)*(p1.y-p2.y);
    }

    public static Function<Integer , Point> lineDivPointFunc(Point p1, Point p2, int divNum){
        double
                x1=p1.x,
                x2=p2.x,
                y1=p1.y,
                y2=p2.y,
                dx=x2-x1,
                dy=y2-y1;

        return (Integer div)-> new Point(x1+div*dx/divNum, y1+div*dy/divNum);

    }

    public static List<Point> clockwise( List<Point> quatrePointList){

        MatOfPoint bbound=new MatOfPoint();
        bbound.fromList(quatrePointList);
        Rect br=Imgproc.boundingRect(bbound);
        List<Point> standardl=new ArrayList<>();
        standardl.add(new Point(br.x, br.y));
        standardl.add(new Point(br.x+br.width, br.y));
        standardl.add(new Point(br.x+br.width, br.y+br.height));
        standardl.add(new Point(br.x, br.y+br.height));

        return standardl.stream().map(p ->
                quatrePointList.stream().min((p1, p2) ->
                {
                    double v1=Utils.pointDist(p,p1),
                            v2=Utils.pointDist(p,p2);
                    if(v1>v2)return 1;
                    else if (v1==v2)return 0;
                    else return -1;
                }).get()).collect(Collectors.toList());
    }



    public static void showResult(Mat img) {
        if(img.empty())return;

        //Imgproc.resize(img,img,new Size(500,500));

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
    public static BufferedImage Mat2BufferedImg(Mat img, double width){
        //if(img==null){System.out.println("hello");return null;}
        if(img.empty())return null;

        Imgproc.resize(img,img,new Size(width,width/(img.size().width/img.size().height)));

        MatOfByte matOfByte = new MatOfByte();
        Highgui.imencode(".jpg", img, matOfByte);
        byte[] byteArray = matOfByte.toArray();
        BufferedImage bufImage = null;
        try {
            InputStream in = new ByteArrayInputStream(byteArray);
            bufImage = ImageIO.read(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bufImage;
    }


    public static int[][]convertMat(Mat bm){
        int xbound=bm.rows();
        int ybound=bm.cols();
        int [][]rst=new int[xbound][ybound];
        byte [] x=new byte[3];
        for(int i=0;i<xbound;i++){
            for(int j=0;j<ybound;j++){

                bm.get(i,j,x);
                //rst[i][j]=(int)(bm.get(i,j)[0]>0?1:0);
                rst[i][j]=(int)x[0]<0?1:0;
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

public static void printMatrixNonZeros(int [][]bimg){
    boolean flag=false;
    for (int i=0;i<bimg.length&&!flag;i++){
        for(int j=0;j<bimg[i].length;j++){
            if(bimg[i][j]==1)flag=true;break;
        }
    }
    if(flag)printMatrix(bimg);
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

    public static void showMatrix(int[][]bimg){
        showResult(convertByteM(bimg));
    }
    public static void writeMatrix(String pathname, int [][]bimg){
        Highgui.imwrite(pathname, convertByteM(bimg));
    }

}
