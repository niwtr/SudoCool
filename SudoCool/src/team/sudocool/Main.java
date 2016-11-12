package team.sudocool;

import com.sun.tools.corba.se.idl.toJavaPortable.Util;
import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import team.sudocool.Identifier.Identifier;
import team.sudocool.ImgWorks.Patternizor;
import team.sudocool.ImgWorks.Skeletonizor;
import team.sudocool.ImgWorks.SquareArranger;
import team.sudocool.ImgWorks.SquareExtractor;
import team.sudocool.ImgWorks.nImgProc.*;

import java.io.*;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.opencv.imgproc.Imgproc.*;


public class Main {
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }




    static ArrayList<String> filelist = new ArrayList<>();


    static void getFiles(String filePath, int [] types){
        File root = new File(filePath);
        File[] files = root.listFiles();
        int count=0;
        for(File file:files){
            if(file.isDirectory()){
                ;
            }else{
                int type=file.getName().charAt(0)-48;
                filelist.add(file.getAbsolutePath());
                types[count]=type;
                count++;
            }
        }
    }

    static void doPatternize(String p, int type){

        Mat imgi= Highgui.imread(p);//low
        String path = "/Users/Heranort/Desktop/patterns/" + String.valueOf(type) + ".pat";
        File f = new File(path);
        FileOutputStream of;
        if(f.exists()){
            try {
                of = new FileOutputStream(f, true);
                try {
                    of.write(Utils.dumpMatrix(
                            Patternizor.Patternize(
                                    imgi,
                                    7 ,
                                    Patternizor.BLACK_BACKGROUND))
                            .getBytes()
                    );
                    of.write("\n".getBytes());
                    of.close();
                } catch (IOException e){
                    System.out.printf("IO Exception: %s", f.getName());
                }
            }
            catch (FileNotFoundException e){
                System.out.printf("File not found: %s", f.getName());
            }
        }
    }

    private static void do_run(int __x){
                /* 有史以来写得最烂的一堆代码，我自己都嫌弃。  */
        String filePath="/Users/Heranort/Desktop/t10k-images/";

        List<String>patternFiles=new ArrayList<>();

        int[] types=new int[200000];

        getFiles(filePath,types);

        for(int i=0;i<filelist.size();i++){
            doPatternize(filelist.get(i), types[i]);
        }
    }



    /* please ignore the sick code above. */

    private static void doAll(String[] args){

    }
    public static void main(String[] args) {


        //Mat img=Highgui.imread("/Users/Heranort/Desktop/nine.jpg");//


        //Mat img=Highgui.imread("/Users/Heranort/Desktop/sudo.jpg");
        Mat img=Highgui.imread("./test/sdk.jpg");



        SquareExtractor.Extract(img);










    }
}
