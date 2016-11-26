package team.sudocool;

import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import team.sudocool.Eye.Eye;
import team.sudocool.ImgWorks.Patternizor;
import team.sudocool.ImgWorks.Recognizer;
import team.sudocool.ImgWorks.nImgProc.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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
        Patternizor P=new Patternizor(7, Patternizor.WHITE_BACKGROUND);

        //Imgproc.cvtColor(imgi, imgi, COLOR_RGB2GRAY);
        //Imgproc.adaptiveThreshold(imgi,imgi,255, ADAPTIVE_THRESH_GAUSSIAN_C, THRESH_BINARY, 151,1);
        FileOutputStream of;
        if(f.exists()){
            try {
                of = new FileOutputStream(f, true);
                try {
                    of.write(Utils.dumpMatrix(
                            P.Patternize28x28(imgi))
                            //Patternizor.Patternize28x28(
//                                    imgi,
//                                   7 ,
//                                    Patternizor.BLACK_BACKGROUND))
                            .getBytes()
                    );
                    of.write("\n".getBytes());
                    of.close();
                }catch (IOException e){
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
        String filePath="/Users/Heranort/Desktop/train_imc/"; //"t10k-images/";

        List<String>patternFiles=new ArrayList<>();

        int[] types=new int[600000];

        getFiles(filePath,types);

        for(int i=0;i<filelist.size();i++){
            doPatternize(filelist.get(i), types[i]);
        }
    }
    public static void main(String[] args) {



        Mat img=Highgui.imread("./test/sdk3.jpg");

        Recognizer r=new Recognizer();
        img=r.Recognize(img);
        Highgui.imwrite("/Users/Heranort/Desktop/a.jpg", img);

        Eye e=new Eye();

        //e.Watch();


        //List<Mat> outseq=GridSquareExtractor.GridExtract(img);


/*
        EzGridSquareExtractor ege=new EzGridSquareExtractor(450,450,3);

        List<Mat> outseq2=ege.Extract(img);

        Patternizor P=new Patternizor(7, Patternizor.WHITE_BACKGROUND);
        Identifier I=new Identifier();

        List<Integer> x=outseq2.stream().map(P::Patternize28x28).map(I::toDigit).collect(Collectors.toList());

        for(int u=0;u<x.size();u++)
        {
            System.out.printf("%d ", x.get(u));
            if((u+1)%9==0)System.out.println();
        }
*/

    }
}
