package team.sudocool.ImgWorks;

import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.utils.Converters;
import team.sudocool.Identifier.Identifier;
import team.sudocool.ImgWorks.nImgProc.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.opencv.core.Core.FILLED;
import static org.opencv.core.Core.NORM_MINMAX;
import static org.opencv.core.CvType.*;
import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.imgproc.Imgproc.*;
import static org.opencv.imgproc.Imgproc.MORPH_CLOSE;
import static org.opencv.imgproc.Imgproc.boundingRect;

/**
 * Created by Heranort on 16/11/16.
 */
public class GridSquareExtractor {


    private static final int SUDOKU_SIZE=9;

    public static List<Mat> GridExtract(Mat img){

        double rate=img.size().width/img.size().height;
        Imgproc.resize(img,img, new Size(800, 800/rate));

        Mat extracted=extractOuterBound(img.clone());

        //Utils.showResult(extracted);
        Mat exx=verticalLines(extracted.clone());
        Mat exy=horizonalLines(extracted.clone());
        Mat grids=gridPoints(exx,exy);
        //Utils.showResult(grids);
        try {
            return arrange(img, grids);
        }catch (IndexOutOfBoundsException ie){
            System.out.println("Cannot extract all of the squares.");
            return new ArrayList<>();
        }

    }



    public static Mat extractOuterBound(Mat img){

        Mat img2=new Mat();
        Imgproc.cvtColor(img,img2,Imgproc.COLOR_RGB2GRAY);//morph into gray pic

        //Imgproc.threshold(img2,img2,127,255,THRESH_BINARY_INV+THRESH_OTSU);

        Imgproc.adaptiveThreshold(img2,img2,255, ADAPTIVE_THRESH_GAUSSIAN_C, THRESH_BINARY, 151,1);

        //Utils.showResult(img2);
        Imgproc.Laplacian(img2,img2,img2.depth());


        List<MatOfPoint> squares=new ArrayList<>();

        List<MatOfPoint> contours=new ArrayList<>();


        Mat hierarchy=new Mat();
        Imgproc.findContours(img2, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE,new Point(0,0));

        contours.sort((mp1,mp2)->
        {
            double amp1=Imgproc.contourArea(mp1),amp2=Imgproc.contourArea(mp2);
            if(amp1>amp2)return -1;
            else if(amp1==amp2)return 0;
            else return 1;
        });

        MatOfPoint bound=contours.get(0);

        //System.out.println(Imgproc.contourArea(bound));
        Mat res=new Mat(img.size(), CV_8U, new Scalar(0));
        Core.fillConvexPoly(res, bound, new Scalar(255));
        Mat rest=new Mat(img.size(), CV_8UC3, new Scalar(0, 0, 0));
        Imgproc.cvtColor(img,img,Imgproc.COLOR_RGB2GRAY);//morph into gray pic
        //equalizeHist(img,img);//cowsay
        img.copyTo(rest, res);


        return rest;

    }

    private static Mat verticalLines(Mat extracted){


        Mat kernelx = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(2,10));


        Imgproc.Sobel(extracted,extracted, CV_16S,1,0);
        Core.convertScaleAbs(extracted,extracted);
        Core.normalize(extracted,extracted,0,255,NORM_MINMAX);

        Imgproc.threshold(extracted, extracted ,0,255,Imgproc.THRESH_BINARY+Imgproc.THRESH_OTSU);


        Imgproc.morphologyEx(extracted,extracted,MORPH_DILATE,kernelx);

        //Utils.showResult(extracted);
        List<MatOfPoint> contour=new ArrayList<>();
        Mat H=new Mat();
        Imgproc.findContours(extracted,contour,H,RETR_EXTERNAL,CHAIN_APPROX_SIMPLE);

        for(MatOfPoint cnt : contour) {
            Rect r = boundingRect(cnt);
            int h = r.height, w = r.width, x = r.x, y = r.y;
            List<MatOfPoint> cl = new ArrayList<>();
            cl.add(cnt);
            if (h / w > 5)
                Imgproc.drawContours(extracted, cl, 0, new Scalar(255), -1);
            else {
                Imgproc.drawContours(extracted,cl, 0, new Scalar(0), -1);
            }
        }
        Imgproc.morphologyEx(extracted,extracted,MORPH_CLOSE,new Mat());


        return extracted;
    }

    private static Mat horizonalLines(Mat extracted){
        Mat kernely = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(10,2));
        Imgproc.Sobel(extracted,extracted, CV_16S,0,2);
        Core.convertScaleAbs(extracted,extracted);
        Core.normalize(extracted,extracted,0,255,NORM_MINMAX);

        Imgproc.threshold(extracted, extracted ,0,255,Imgproc.THRESH_BINARY+Imgproc.THRESH_OTSU);
        Imgproc.morphologyEx(extracted,extracted,MORPH_DILATE,kernely);

        List<MatOfPoint> contour=new ArrayList<>();
        Mat H=new Mat();
        Imgproc.findContours(extracted,contour,H,RETR_EXTERNAL,CHAIN_APPROX_SIMPLE);
        for(MatOfPoint cnt : contour) {
            Rect r = boundingRect(cnt);
            int h = r.height, w = r.width, x = r.x, y = r.y;
            List<MatOfPoint> cl = new ArrayList<>();
            cl.add(cnt);
            if (w / h > 5)
                Imgproc.drawContours(extracted, cl, 0, new Scalar(255), -1);
            else {
                Imgproc.drawContours(extracted,cl, 0, new Scalar(0), -1);
            }
        }


        Imgproc.morphologyEx(extracted,extracted,MORPH_CLOSE,new Mat());


        return extracted;
    }



    private static Mat gridPoints(Mat exx, Mat exy){
        Mat rst=new Mat();
        Core.bitwise_and(exx,exy,rst);
        return rst;
    }


    private static List<Mat> arrange(Mat oimg, Mat grids){

        List<MatOfPoint> contour=new ArrayList<>();
        findContours(grids, contour,new Mat(),RETR_LIST,CHAIN_APPROX_SIMPLE);

        List<Point> centroids=new ArrayList<>();
        for(MatOfPoint cnt : contour) {
            Moments mom= Imgproc.moments(cnt);
            int x=(int)(mom.get_m10()/mom.get_m00()),
                    y=(int)(mom.get_m01()/mom.get_m00());

            Core.circle(grids, new Point(x, y), 4, new Scalar(255, 255, 255),-1);
            centroids.add(new Point(x,y));
        }

        List<Point> rl=centroids
                .stream()
                .sorted((p3,p4)->
                {
                    if(p3.x>p4.x)return 1;
                    else if (p3.x==p4.x)return 0;
                    else return -1;
                })
                .sorted((p1,p2)->
                {
                    if(p1.y>p2.y)return 1;
                    else if (p1.y==p2.y)return 0;
                    else return -1;
                })
                .collect(Collectors.toList());


        //rl.forEach(x->System.out.printf("%f %f\n",x.x, x.y));
        List<List<Point>> matrix=new ArrayList<>();
        double wid = Math.abs(rl.get(0).y-rl.get(rl.size()-1).y)/SUDOKU_SIZE;
        while(rl.size()>0) {
            double y = rl.get(0).y;
            matrix.add(
                    rl.stream().filter((x) ->
                            (Math.abs(x.y - y) < wid / 2))
                            .sorted((a,b)->
                                    (a.x>b.x?1:-1))
                            .collect(Collectors.toList())
            );
            rl = rl.stream()
                    .filter((x) ->
                            (Math.abs(x.y - y) > wid / 2))
                    .collect(Collectors.toList());
        }

        for(int i=0;i<matrix.size();i++){
            if(matrix.get(i).size()<2)
                matrix.remove(i);
        }


        for (List<Point> pl : matrix) {
            double xx=pl.get(0).x;
            double _wid=(pl.get(pl.size()-1).x-pl.get(0).x)/SUDOKU_SIZE;
            for(int i=1;i<pl.size();i++){

                if(pl.get(i).x-xx<0.8*_wid)
                {
                    pl.remove(i);
                    i--;
                    //break;
                }
                xx=pl.get(i).x;
            }
        }

//        matrix.forEach(x->System.out.println(x.size()));

        List<List<Point>> matrixr=new ArrayList<>();
        for(int j=0;j<10;j++){
            List<Point> ls=new ArrayList<>();
            for(int i=0;i<10;i++)
                ls.add(new Point(i*50,j*50));
            matrixr.add(ls);
        }


        Mat outall=Mat.zeros(450,450, CV_8UC1);

        List<Mat> outseq=new ArrayList<>();


        for(int i=0;i<9;i++){
            for(int j=0;j<9;j++){//1324,1243
                Mat Out = Mat.zeros(450,450, CV_8UC1);


                Point p1=matrix.get(i).get(j),p1r=matrixr.get(i).get(j),
                        p2=matrix.get(i).get(j+1),p2r=matrixr.get(i).get(j+1),
                        p3=matrix.get(i+1).get(j),p4r=matrixr.get(i+1).get(j),
                        p4=matrix.get(i+1).get(j+1),p3r=matrixr.get(i+1).get(j+1);

                List<Point> source = new ArrayList<Point>();
                source.add(p1);
                source.add(p2);
                source.add(p3);
                source.add(p4);
                Mat src = Converters.vector_Point2f_to_Mat(source);
                List<Point> dest = new ArrayList<Point>();
                dest.add(p1r);
                dest.add(p2r);

                dest.add(p4r);
                dest.add(p3r);
                Mat dst = Converters.vector_Point2f_to_Mat(dest);
                Mat perspectiveTransform = Imgproc.getPerspectiveTransform(src, dst);
                Mat _empty=new Mat(Out.rows(),Out.cols(), CV_8UC1,new Scalar(0));
                Mat rst=new Mat();
                Imgproc.warpPerspective(oimg, rst, perspectiveTransform, new Size(450,450));

                Core.rectangle(_empty, new Point(p2r.x-2, p2r.y+2),
                        new Point(p4r.x+2, p4r.y-2), new Scalar(255),FILLED);
                rst.copyTo(Out, _empty);

                rst.copyTo(outall,_empty);
                Out=new Mat(Out, new Rect( new Point(p2r.x-3, p2r.y+3),
                        new Point(p4r.x+3, p4r.y-3)));

                //Core.rectangle(outall, p2r,p4r, new Scalar(0,0,0), 2);

                outseq.add(Out);
            }

        }

        //outseq.forEach(Utils::showResult);
        //Utils.showResult(outall);
        Utils.showResult(outall);



/*
        matrix.forEach(y->
        {
            y.forEach(x->System.out.printf("%f,%f ", x.x, x.y));
            System.out.println();
        });
*/


        return outseq;

    }




}
