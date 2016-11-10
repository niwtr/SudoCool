package team.sudocool.ImgWorks;

import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * Created by Heranort on 16/11/9.
 */
public class SquareArranger {


   private static class cell<A, B> {
        public  Rect rect;
        public  MatOfPoint mat;
        public int y;
        public int x;
        public int width;
        public int height;
        public cell(Rect a, MatOfPoint b) {
            rect = a;
            mat = b;
            y=a.y;
            x=a.x;
            width=a.width;
            height=a.height;
        }
    }

    public SquareArranger(int sudokuSize, int fillMethod){
        this.sudokuSize=sudokuSize;
        this.fillMethod=fillMethod;
    }

    private int fillMethod;
    private int sudokuSize;


    public static final int STANDARD_SUDOKU_SIZE=9;
    public static final int FILL_EMPTY=0;//arrange the lost squares with empties.
    public static final int FILL_COPY=1;//copy the square next to the lost square.
    private cell copyDown(cell c){
        List<Point> pl;
        if(fillMethod==FILL_COPY)
            pl=c.mat.toList().stream().map((p)->new Point(p.x, p.y+c.height)).collect(Collectors.toList());
        else
            pl=new ArrayList<>();
        c.mat.fromList(pl);
        return new cell(
                new Rect(c.x, c.y+c.height, c.width, c.height),
                c.mat);
    }


    private cell copyUp(cell c){
        List<Point> pl;
        if(fillMethod==FILL_COPY)
            pl=c.mat.toList().stream().map((p)->new Point(p.x, p.y-c.height)).collect(Collectors.toList());
        else
            pl=new ArrayList<>();
        c.mat.fromList(pl);
        return new cell(
                new Rect(c.x, c.y-c.height, c.width, c.height),
                c.mat);
    }
    private cell newCell(cell c){
        return new cell(new Rect(c.x,c.y,0,0), new MatOfPoint());
    }
    private List<List<cell>> _arrange_last(List<List<cell>> matrix, double dist){
        List<cell> lastRow = matrix.get(matrix.size()-1);//last row.
        List<cell>lst=matrix.get(matrix.size()-2);
        matrix.remove(matrix.size()-1);

        for (cell x : lst) {

            if (lastRow.stream().filter((y) ->
                    Math.abs(y.x - x.x) < dist / 2).count() == 0)// found exact position
            {
                int index = 0;

                for (; index < lastRow.size(); index++) {
                    if (lastRow.get(index).x > x.x) {
                        //lastRow.add(index, new cell(new Rect(x.x, x.y, 0, 0), new MatOfPoint()));
                        lastRow.add(index, copyDown(x));

                        break;
                    }
                }

            }
        }
        matrix.add(lastRow);
        return matrix;
    }
    private List<List<cell>> _arrange (List<List<cell>> matrix, double dist){
        List<List<cell>> rmatrix=new ArrayList<>();
        List<cell> lastRow = matrix.get(0);
        matrix.remove(0);
        for (List<cell> lst : matrix) {

            for (cell x : lst) {

                if (lastRow.stream().filter((y) ->
                        Math.abs(y.x - x.x) < dist / 2).count() == 0)// found exact position
                {
                    int index = 0;

                    for (; index < lastRow.size(); index++) {
                        if (lastRow.get(index).x > x.x) {//find next to .
                            lastRow.add(index, copyUp(x));
                            //lastRow.add(index, new cell(new Rect(x.x, x.y, 0, 0), new MatOfPoint()));
                            break;
                        }
                    }

                }
            }
            rmatrix.add(lastRow);
            lastRow=lst;
        }
        rmatrix.add(lastRow);
        return rmatrix;
    }


    public List<List<MatOfPoint>> Arrange(List<MatOfPoint> pl){



        List<cell> rl=pl.stream()
                .map((x)->new cell(Imgproc.boundingRect(x), x))
                .sorted((m1, m2)->(m1.y>m2.y?1:-1))
                .collect(Collectors.toList());
        List<List<cell>> matrix=new ArrayList<>();
        while(rl.size()>0) {

            int y = rl.get(0).y,
                    wid = rl.get(0).width;
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

        //do not handle when exsist one row that
        //the number of elements is larger than SUDOKU_SIZE.


        for(List<cell> lst : matrix)
            if(lst.size()>sudokuSize)return new ArrayList<>();

        int width=matrix.get(0).get(0).width;
        for(int i=0;i<sudokuSize-1;i++)
            matrix=_arrange(matrix,width);//first n-1 rows





        if(matrix.get(matrix.size()-1).size()!=sudokuSize)
            //the last row does not satisfy.
            _arrange_last(matrix, width);

        if(matrix.stream().filter((x)->x.size()!=sudokuSize).count()!=0)
            return new ArrayList<>();
        return matrix.stream().map(
                lst->
                        lst.stream().map(
                                x -> x.mat)
                                .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }
    public static void printM(List<List<cell>> polys){
        polys.forEach((lst)-> {
                    lst.forEach(x ->
                        System.out.printf("%d ",x.x));
                    System.out.println();
                });
    }
}
