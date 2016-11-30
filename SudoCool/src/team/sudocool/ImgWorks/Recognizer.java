package team.sudocool.ImgWorks;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import team.sudocool.Identifier.Identifier;
import team.sudocool.ImgWorks.nImgProc.Utils;
import team.sudocool.Solver.Solver;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.opencv.imgproc.Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
import static org.opencv.imgproc.Imgproc.approxPolyDP;

/**
 * Created by Heranort on 16/11/19.
 */
public class Recognizer {

    //background color of sudoku.

    public static final int WHITE_BACKGROUND=Patternizor.WHITE_BACKGROUND;
    public static final int BLACK_BACKGROUND=Patternizor.BLACK_BACKGROUND;
    public static final int STANDARD_OUTERBOUND_SIZEX=450;
    public static final int STANDARD_OUTERBOUND_SIZEY=450;
    public static final int STANDARD_SCISSOR_SIZE=5;//old: three
    public static final int STANDARD_SUDOKU_SIZE=9;

    private Patternizor           P;
    private Identifier            I;
    private Solver                S;
    private EzGridSquareExtractor E;
    private Mat Img;
    private Mat ThresholdImg;
    private MatOfPoint Bound;

    private boolean isSolved=false;
    private List<Integer> RecognizedNumbers;
    private int[][] ArrangedNumbers;
    private int[][][] RecognizedNumbersHistory;
    private int[][] SolvedNumbers;
    private int[][] __empty;//all -1 arr.
    private boolean firstRush=true;//有时候相机刚启动的时候会导致神奇的现象发生——导致一个神奇的正方形被捕捉到。
    //所以我们需要把第一次采样的结果扔掉。





    public Recognizer(){

        P=new Patternizor(7, Patternizor.WHITE_BACKGROUND);//default is white background.
        I=new Identifier();
        E=new EzGridSquareExtractor(STANDARD_SUDOKU_SIZE, STANDARD_OUTERBOUND_SIZEX,
                STANDARD_OUTERBOUND_SIZEY, STANDARD_SCISSOR_SIZE);
        S=new Solver();

        this.RecognizedNumbersHistory=new int[E.SUDOKU_SIZE][E.SUDOKU_SIZE][9];

        this.Bound=new MatOfPoint();

        this.__empty=new int[E.SUDOKU_SIZE][E.SUDOKU_SIZE];
        for(int i=0;i<E.SUDOKU_SIZE;i++){
            for(int j=0;j<E.SUDOKU_SIZE;j++)
                this.__empty[i][j]=-1;
        }

    }

    public void setScissor(int scissorWidth){
        this.E.scissorWidth=scissorWidth;
    }

    private Recognizer getImg(Mat img){
        this.Img=img;return this;
    }

    private Recognizer preProcessImg(){
        Mat img2=this.Img.clone();
        Imgproc.cvtColor(Img,img2,Imgproc.COLOR_RGB2GRAY);//morph into gray pic
        Imgproc.adaptiveThreshold(img2,img2,255, ADAPTIVE_THRESH_GAUSSIAN_C, THRESH_BINARY, 151,1);
        //the background should be in white.
        this.ThresholdImg=img2;

        return this;
    }

    private Recognizer extractOuterBound(){
        E.ExtractOuterBoundContour(this.ThresholdImg);
        return this;
    }
    private Recognizer recognizeNumbers(){
        if(isSolved)return this;
        if(E.Extract(this.ThresholdImg)){
            this.RecognizedNumbers=E
                    .getExtractedCells()
                    .stream()
                    .map(P::Patternize28x28)
                    .map(I::toDigit)
                    .collect(Collectors.toList());
        }
        return this;
    }

    private boolean contains(int[] arr, int j){
        for(int i=0;i<arr.length;i++)
            if(arr[i]==j)return true;
        return false;
    }
    private int[] setAdd(int[] arr, int n){
        for(int i=0;i<arr.length;i++){
            if(arr[i]==n)return arr;
            if(arr[i]==0){//找到第一个空位
                arr[i]=n;return arr;
            }
        }
        return arr;
    }



    private synchronized Recognizer arrangeNumbersMatrix(){
        if(this.RecognizedNumbers==null || isSolved) {

            return this;
        }
        this.ArrangedNumbers=new int[E.SUDOKU_SIZE][E.SUDOKU_SIZE];

        for(int i=0; i<E.SUDOKU_SIZE ; i++){
            for(int j=0; j<E.SUDOKU_SIZE ; j++){
                int num=this.RecognizedNumbers.get(i*E.SUDOKU_SIZE+j);
                this.ArrangedNumbers[i][j]=num;

                int[]anf=this.RecognizedNumbersHistory[i][j];


                if(!contains(anf, -1))//有-1说明该格子其实是空的。
                    this.RecognizedNumbersHistory[i][j]=setAdd(anf, num);

            }
        }



        return this;
    }


    private ArrayList<int[][]> Answers=new ArrayList<>();


    private int[][] copyMatrix(int[][] in_matrix) {
        int[][] out_matrix = new int[in_matrix.length][];

        for(int i = 0; i < in_matrix.length; i++)
            out_matrix[i] = in_matrix[i].clone();

        return out_matrix;
    }
    private boolean sol(int [][] arr, int i, int j){

        if(i>8)return false;

        for(int ind=0;ind<this.RecognizedNumbersHistory[i][j].length;ind++){

            arr[i][j]=this.RecognizedNumbersHistory[i][j][ind];

            ArrayList<int[][]> ans=S.solveSudo(arr);
            if(ans.size()>0){
                this.Answers=ans;

                return true;
            } else {
                return sol(arr, i + (j == 8 ? 1 : 0), (j == 8) ? 0 : (j + 1));
            }
        }


        return false;
    }


    private boolean bruteForce=true;

    private Recognizer solveNumbers(){

        if(this.ArrangedNumbers==null || isSolved)return this;

        ArrayList<int[][]> rst=new ArrayList<>();//=S.solveSudo(this.ArrangedNumbers);

        if(bruteForce) {
//            System.out.println("i am in wring place..");
            if (sol(copyMatrix(this.ArrangedNumbers), 0, 0)) {
                rst = Answers;
            }
        } else {

//            System.out.println("hello");

            rst=S.solveSudo(this.ArrangedNumbers);//alert: potential bug
        }

        if(rst.size()!=0 && !firstRush){
            isSolved=true;
            this.SolvedNumbers=rst.get(0);
            this.RecognizedNumbersHistory=new int[E.SUDOKU_SIZE][E.SUDOKU_SIZE][9];
        }
        return this;
    }


    private Recognizer drawOuterBound (){

        MatOfPoint bound= E.getBound();
        if (bound==null)return this;

        MatOfPoint2f boundf=new MatOfPoint2f(),
                abound=new MatOfPoint2f();
        boundf.fromList(bound.toList());
        double peri= Imgproc.arcLength(boundf, true);
        approxPolyDP(boundf, abound, 0.02*peri, true);
        this.Bound.fromList(abound.toList());
        List<MatOfPoint> mp=new ArrayList<>();
        mp.add(Bound);
        Core.polylines(Img, mp, true, new Scalar(255,255,0), 3);
        return this;
    }

    @FunctionalInterface
    interface Function3 <A, B, C, R> {public R apply (A a, B b, C c);}

    private Recognizer drawRecognizedNumbers() {
        MatOfPoint bound = E.getBound();
        if (bound == null || RecognizedNumbers == null) return this;
        List<Point> clockwised = Utils.clockwise(this.Bound.toList());
        Function<Integer, Point>
                l1=Utils.lineDivPointFunc
                (clockwised.get(Utils.UP_LEFT), clockwised.get(Utils.DOWN_LEFT),
                        E.SUDOKU_SIZE),
                l2=Utils.lineDivPointFunc
                        (clockwised.get(Utils.UP_RIGHT), clockwised.get(Utils.DOWN_RIGHT),
                                E.SUDOKU_SIZE);
        for(int y=0; y<E.SUDOKU_SIZE; y++) {
            for (int x = 0; x < E.SUDOKU_SIZE; x++) {
                Point pl=l1.apply(y+1), pr=l2.apply(y+1);
                BiFunction<Integer, Integer, Point> __=(z, w)->
                {
                    Function<Integer, Point> l3=Utils.lineDivPointFunc(pl,pr, E.SUDOKU_SIZE);
                    return l3.apply(z);
                };
                double width=Math.abs(pl.x-pr.x)/E.SUDOKU_SIZE;
                Point p=__.apply(x,y);
                int num;
                if(!isSolved)
                    num=ArrangedNumbers[y][x];
                else num=SolvedNumbers[y][x];
                Core.putText
                        (Img,
                                (num==-1?"":""+num),
                                p,
                                Core.FONT_HERSHEY_PLAIN,
                                (width/(E.SUDOKU_SIZE))*0.8,
                                isSolved?(new Scalar(0,0,255)):(new Scalar(255,0,0)),
                                2);
            }
        }
        if(firstRush){
            this.firstRush=false;
            this.RecognizedNumbersHistory=new int[E.SUDOKU_SIZE][E.SUDOKU_SIZE][9];
        }
        return this;
    }


    public Mat __getTransformedBound(Mat img){//will be removed in the future.
        this.getImg(img).preProcessImg().extractOuterBound();
        if(E.Extract2(ThresholdImg))
            return E.ExtractedCellsGraph;
        else return img;

    }

    //印刷体识别接口
    public Mat RecognizeAndSolve(Mat img){

        this
                .getImg(img)
                .preProcessImg()
                .extractOuterBound()
                .recognizeNumbers()
                .arrangeNumbersMatrix()
                .solveNumbers()
                .drawOuterBound()
                .drawRecognizedNumbers();

        return this
                .Img;
    }


    public Mat RecognizeOnly(Mat img){
        this.getImg(img)
                .preProcessImg()
                .extractOuterBound()
                .recognizeNumbers()
                .arrangeNumbersMatrix()
                .drawOuterBound()
                .drawRecognizedNumbers();
        return this.Img;
    }

    //solving function that triggered by user.
    public boolean Solve(int[][] input){

        if (input.length != E.SUDOKU_SIZE)
            throw new AssertionError();
        if (input[0].length != E.SUDOKU_SIZE)
            throw new AssertionError();


        this.bruteForce=false;
        this.ArrangedNumbers = input;

        this.solveNumbers();
        return isSolved;
    }


    public int[][] GetCurrentSudoku(){//获取当前识别好的数独矩阵，用-1来表示空
        if(isSolved)
            return this.SolvedNumbers;
        else
            return this.ArrangedNumbers!=null?this.ArrangedNumbers:__empty;
    }

    //User set number to the sudoku matrix.
    public void SetRecognizedNumbers(int y, int x, int num) {//更改对应（X,Y)位置的识别结果
        this.bruteForce = false;
        for (int i = 0; i < 9; i++)
            this.RecognizedNumbersHistory[y][x][i] = 0;
        this.RecognizedNumbersHistory[y][x][0] = num;
    }

    //reset all, prepare for the next scan.
    public  void Reset(){

        this.isSolved=false;
        this.RecognizedNumbers=null;
        this.ArrangedNumbers=new int[E.SUDOKU_SIZE][E.SUDOKU_SIZE];
        this.RecognizedNumbersHistory=new int[E.SUDOKU_SIZE][E.SUDOKU_SIZE][9];
        this.SolvedNumbers=new int[E.SUDOKU_SIZE][E.SUDOKU_SIZE];
        this.firstRush=true;
        this.bruteForce=true;
    }
}
