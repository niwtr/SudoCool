package team.sudocool.Eye;

/**
 * Created by Heranort on 16/11/15.
 */
import java.awt.image.BufferedImage;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;
import team.sudocool.ImgWorks.nImgProc.Mat2Image;
import team.sudocool.ImgWorks.nImgProc.Utils;

public class VideoCap {
    static{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    VideoCapture cap;
    public Mat captured;

    VideoCap(){
        captured=new Mat();
        cap = new VideoCapture();
        cap.open(0);
    }

    Mat getMat(){return captured;}
    BufferedImage getOneFrame() {
        //Mat mn=new Mat();
        cap.read(captured);

        return Utils.Mat2BufferedImg(captured.clone()); //mat2Img.getImage(mat2Img.mat);
    }
}
