package team.sudocool.ImgWorks;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import java.util.stream.Collectors;

import static org.opencv.core.Core.*;
import static org.opencv.core.CvType.*;
import static org.opencv.imgproc.Imgproc.*;


public class SquareExtractor {


    private static final double SUDOKU_SIZE=9;


    private static Mat preProcessing(Mat img){

        GaussianBlur(img,img,new Size(5,5),0);
        cvtColor(img,img, COLOR_BGR2GRAY);
        Mat mask=Mat.zeros(img.size(), CV_8UC1);

        Mat kernel1=getStructuringElement(MORPH_ELLIPSE,new Size(11,11));

        Mat close=new Mat();
        morphologyEx(img,close,MORPH_CLOSE,kernel1);
        Mat div=new Mat();
        Core.divide(img,close,div);

        Core.normalize(div,div,0,255,NORM_MINMAX);

        cvtColor(div,div,COLOR_GRAY2BGR);
        return div;
    }





/* unusable */
    private static Mat whiteBalance(Mat img){
        List<Mat> g_vChannels=new ArrayList<>();
        Core.split(img, g_vChannels);
        Mat imageBlueChannel = g_vChannels.get(0);
        Mat imageGreenChannel = g_vChannels.get(1);
        Mat imageRedChannel = g_vChannels.get(2);
        double imageBlueChannelAvg=0;
        double imageGreenChannelAvg=0;
        double imageRedChannelAvg=0;
        imageBlueChannelAvg = Core.mean(imageBlueChannel).val[0];
        imageGreenChannelAvg = Core.mean(imageGreenChannel).val[0];
        imageRedChannelAvg = Core.mean(imageRedChannel).val[0];
        double K = (imageRedChannelAvg+imageGreenChannelAvg+imageRedChannelAvg)/3;
        double Kb = K/imageBlueChannelAvg;
        double Kg = K/imageGreenChannelAvg;
        double Kr = K/imageRedChannelAvg;

        addWeighted(imageBlueChannel,0,new Mat(imageBlueChannel.size(), imageBlueChannel.type(), new Scalar(Kb))
                ,0,0,imageBlueChannel);
        addWeighted(imageGreenChannel,0, new Mat(imageGreenChannel.size(),imageGreenChannel.type(),new Scalar(Kg))
                ,0,0,imageGreenChannel);
        addWeighted(imageRedChannel,0, new Mat(imageRedChannel.size(),imageRedChannel.type(), new Scalar(Kr))
                ,0,0,imageRedChannel);
        List<Mat> newL=new ArrayList<>();newL.add(imageBlueChannel);
        newL.add(imageGreenChannel);newL.add(imageRedChannel);
        Mat dst=new Mat();
        merge(newL,dst);

        return dst;
    }

    private static  boolean isSquare (MatOfPoint mp, double lb, double ub){
        double area= Imgproc.contourArea(mp);
        return isSquare2(mp)&&(area<=ub && area>=lb);
    }

    private static boolean isSquare2(MatOfPoint mp){
        Rect r=Imgproc.boundingRect(mp);
        return Math.abs(r.height-r.width)<((r.height+r.width)/2)*0.3;
    }

    private static Mat prepare_findSquares(Mat oimg){
        Mat img2=new Mat();

        Imgproc.cvtColor(oimg,img2,Imgproc.COLOR_RGB2GRAY);//morph into gray pic


        Imgproc.threshold(img2,img2,127,155,THRESH_BINARY_INV+THRESH_OTSU);


        Imgproc.Laplacian(img2,img2,img2.depth());

        //Imgproc.Canny(img2, img2, 60,200); //canny contour


        Mat elem10=new Mat(27,27, CV_8U, new Scalar(255));
        Imgproc.morphologyEx(img2, img2, MORPH_CLOSE, elem10);//morph close


        return img2;
    }


    public static List<MatOfPoint> Extract(Mat img, double lb, double ub){
        return findSquares(prepare_findSquares(img), lb, ub);
    }

    private static List<MatOfPoint> findSquares(Mat oimg, double lb, double ub){

        List<MatOfPoint> squares=new ArrayList<>();

        List<MatOfPoint> contours=new ArrayList<>();

        Mat hierarchy=new Mat();
        Imgproc.findContours(oimg, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE,new Point(0,0));

        List<MatOfPoint> tmp= new ArrayList<>();
        for(MatOfPoint mp : contours){
            if (isSquare(mp, lb, ub)) {
                tmp.add(mp);
            }
        }
        return tmp;

    }
    public static Mat cutSquares (Mat img,  List<MatOfPoint> polys){
        Mat res=new Mat(img.size(), CV_8U, new Scalar(0));
        Core.fillPoly(res, polys, new Scalar(255));
        Mat rest=new Mat(img.size(), CV_8UC3, new Scalar(0, 255, 0));
        img.copyTo(rest, res);
        return rest;
    }

    public static Mat drawSquares(Mat img, List<MatOfPoint> polys){
        Mat newM=img.clone();
        for(MatOfPoint mp : polys){
            Rect rt=Imgproc.boundingRect(mp);
            Core.rectangle(newM, new Point(rt.x, rt.y), new Point (rt.x+ rt.width, rt.y+rt.height), new Scalar(0,255,0),3);
        }
        return newM;
    }



    public static List<List<Mat>> squareCutter (Mat img, List<List<MatOfPoint>> polys){

        return polys.stream().map(
                (lst)->
                        lst.stream().map(
                                (x)->{
                                    if(x.empty())
                                        return new Mat();
                                    /* warning: could be show! */
                                    Mat res=new Mat(img.size(), CV_8U, new Scalar(0));
                                    Core.fillConvexPoly(res, x, new Scalar(255));
                                    Mat rest=new Mat(img.size(), CV_8UC3, new Scalar(0, 0, 0));
                                    //black background will be removed by the cropper.
                                    //really?
                                    img.copyTo(rest, res);
                                    return new Mat(rest, Imgproc.boundingRect(x));
                                })
                                .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }


    public static List<Mat> squareCutter_Linear(Mat img, List<MatOfPoint> polys){
        return polys.stream().map(
                (x)->{
                    if(x.empty())
                        return new Mat();
                                    /* warning: could be show! */
                    Mat res=new Mat(img.size(), CV_8U, new Scalar(0));
                    Core.fillConvexPoly(res, x, new Scalar(255));
                    Mat rest=new Mat(img.size(), CV_8UC3, new Scalar(0, 0, 0));
                    //black background will be removed by the cropper.
                    //really?
                    img.copyTo(rest, res);
                    return new Mat(rest, Imgproc.boundingRect(x));
                }
        ).collect(Collectors.toList());
    }

}
