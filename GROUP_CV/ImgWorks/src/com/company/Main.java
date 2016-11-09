package com.company;

import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import com.company.nImgProc.Utils;
import org.opencv.imgproc.Imgproc;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


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

    public static void main(String[] args) {

        Mat img=Highgui.imread("/Users/Heranort/Desktop/sudo.jpg");

        List<MatOfPoint> rst=SquareExtractor.Extract(img, 23000,30000);

        Highgui.imwrite("/Users/Heranort/Desktop/sq.jpg",SquareExtractor.cutSquares(img, rst));

        List<List<int[][]>> result=
                /* get the patterns from a sudoku image. */
                SquareExtractor.squareCutter(img, SquareExtractor.arrangeSquare(rst))
                        /* arrangeSquare may return an empth Matrix list if
                         *  it fails to arrange. */
                        .stream()
                        .map((lst)->
                                lst.stream()
                                        .map((x)->Patternizor
                                                .Patternize(x, 7, Patternizor.WHITE_BACKGROUND))
                                        .collect(Collectors.toList()))
                        .collect(Collectors.toList());

        result.forEach((lst)->lst.forEach((x)->{Utils.printMatrix(x);System.out.println();}));

    }
}
