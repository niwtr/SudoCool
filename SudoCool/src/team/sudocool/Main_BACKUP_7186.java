package team.sudocool;

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

import static org.opencv.imgproc.Imgproc.COLOR_RGB2GRAY;
import static org.opencv.imgproc.Imgproc.THRESH_OTSU;


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


<<<<<<< HEAD
        //Mat uu=Highgui.imread("/Users/Heranort/Desktop/fo.jpg");//

        //Mat img=Highgui.imread("./test/sudo.jpg");
        Mat img=Highgui.imread("/Users/Heranort/Downloads/ugi.jpg");



        //这里默许了数独棋盘它是黑色的。


        List<MatOfPoint> rst= SquareExtractor.Extract(img, 500,1500); //20000,26000);//2000,2300);
=======
        Mat img=Highgui.imread("./test/sudo.jpg");

        List<MatOfPoint> rst= SquareExtractor.Extract(img, 20000,26000);
>>>>>>> 15de85e5baf201caecc037aa8cb27d7b8d84c4e8
        //20000,26000 for sudo.jpg

        System.out.println(rst.size());//show the number of blocks scanned in.

        SquareArranger A=new SquareArranger(SquareArranger.STANDARD_SUDOKU_SIZE, SquareArranger.FILL_EMPTY);
        Patternizor P=new Patternizor(7, Patternizor.WHITE_BACKGROUND);
        Patternizor Pb=new Patternizor(7, Patternizor.BLACK_BACKGROUND);
        Identifier I=new Identifier();




        boolean ok=true;
        if(ok) {
                /* get the patterns from a sudoku image. */
                    SquareExtractor.squareCutter(img, A.Arrange(rst))
                        /* arrangeSquare returns an empth Matrix list if
                         *  it fails to arrange. */
                            .stream()
                            .map((lst) ->
                                    lst.stream()
                                            .map(P::Patternize)
                                            .map(I::toDigit)
                                            .collect(Collectors.toList()))
                            .collect(Collectors.toList())

                            .forEach(lst->
                                    {
                                        lst.forEach(x->{
                                            System.out.printf("%d ", x.intValue());
                                            //Utils.printMatrixNonZeros(x);
                                            //System.out.println();
                                        });

                                        System.out.println();
                                    });


        }


    }
}
