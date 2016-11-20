package team.sudocool.Eye;

/**
 * Created by Heranort on 16/11/15.
 */
import java.awt.image.BufferedImage;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import team.sudocool.ImgWorks.EzGridSquareExtractor;
import team.sudocool.ImgWorks.Recognizer;
import team.sudocool.ImgWorks.nImgProc.Mat2Image;
import team.sudocool.ImgWorks.nImgProc.Utils;

import static team.sudocool.Eye.Eye.WIDTH;

public class VideoCap {
    static{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    VideoCapture cap;
    Recognizer R=new Recognizer();
    public Mat captured;

    VideoCap(){
        captured=new Mat();
        cap = new VideoCapture();
        cap.open(0);
    }

    Mat getMat(){
        getOneFrame();
        return captured;}


    BufferedImage getRectedFrame(){
        Mat mn=new Mat();
        cap.read(mn);
        captured=mn;
        double rate=captured.size().width/captured.size().height;
        Imgproc.resize(captured,captured, new Size(WIDTH, WIDTH/rate));

        return Utils.Mat2BufferedImg(R.Recognize(captured), WIDTH);
    }
    BufferedImage getOneFrame() {
        Mat mn=new Mat();
        cap.read(mn);
        captured=mn;
        double rate=captured.size().width/captured.size().height;
        Imgproc.resize(captured,captured, new Size(WIDTH, WIDTH/rate));
        return Utils.Mat2BufferedImg(captured.clone(), WIDTH); //mat2Img.getImage(mat2Img.mat);
    }
}
