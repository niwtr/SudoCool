package team.sudocool.Solver;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author L.Laddie
 * @version 1.0
 * @since 2016/11/22
 */
public class Solver {
    private static final int SIZE = 9;

    private int[][] sudo;                   //current sodoku to solve
    private ArrayList<int[][]> ansSudo;     //current ans for sodoku
    private ArrayList<int[][]> sudoBuffer;


    /**
     * This is the function for solving the Sudoku
     */
    public ArrayList<int[][]> solveSudo(int[][] sudo) {
        this.sudo = copyMatrix(sudo);
        ansSudo = new ArrayList<>();
        sudoBuffer = new ArrayList<>();

        ArrayList<Pair<Integer, Integer>> nullPoint = isNullUnit();
        if(nullPoint.isEmpty())
            ansSudo.add(this.sudo);
        else
        {
            HashMap<Pair<Integer, Integer>, ArrayList<Integer>> sudoReadNumber = findAvaFill(nullPoint);
            fillUnit(sudoReadNumber);
        }

        return ansSudo;
    }

    /**
     * judge the matrix whether have null unit
     * @return  the null unit point list
     */
    private ArrayList<Pair<Integer, Integer>> isNullUnit() {
        if (sudo == null)
            throw new AssertionError();

        ArrayList<Pair<Integer, Integer>> nullPoint = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if(sudo[i][j] == 0)
                    nullPoint.add(new Pair<>(i, j));
            }
        }

        return nullPoint;
    }

    /**
     *
     * @param point_list null unit point list
     * @return answer
     */
    private HashMap<Pair<Integer, Integer>, ArrayList<Integer>> findAvaFill(ArrayList<Pair<Integer, Integer>> point_list) {
        HashMap<Pair<Integer, Integer>, ArrayList<Integer>> m = new HashMap<>();

        for(Pair<Integer, Integer> point : point_list)
        {
            int i = point.getKey();
            int j = point.getValue();
            ArrayList<Integer> readyNumber = new ArrayList<>();

            for(int value = 1; value < SIZE+1; value++)
            {
                if(checkHorAndVer(i, j, value)) {
                    readyNumber.add(value);
                }
            }

            m.put(point, readyNumber);
        }
        return m;
    }

    private void fillUnit(HashMap<Pair<Integer, Integer>, ArrayList<Integer>> sudoReadNumber) {
        for (HashMap.Entry<Pair<Integer, Integer>, ArrayList<Integer>> entry : sudoReadNumber.entrySet()) {
            Pair<Integer, Integer> point = entry.getKey();
            ArrayList<Integer> readyNumber = entry.getValue();

            if(readyNumber.size() == 1)
            {
                sudo[point.getKey()][point.getValue()] = readyNumber.get(0);
                sudoReadNumber.remove(point);
            }

        }
    }

    /**
     * check whether the value is allowed
     * @param i horizontal
     * @param j vertical
     * @param value padding value
     * @return true or false
     */
    private boolean checkHorAndVer(int i, int j, int value) {
        if (sudo == null)
            throw new AssertionError();

        for(int k = 0; k < sudo[i].length; k++)
            if(sudo[i][k] == value)
                return false;

        for (int[] aSudo : sudo)
            if (aSudo[j] == value)
                return false;

        return true;
    }

    /**
     * deep copy of matrix
     * @param in_matrix input
     * @return output
     */
    private int[][] copyMatrix(int[][] in_matrix) {
        int[][] out_matrix = new int[in_matrix.length][];

        for(int i = 0; i < in_matrix.length; i++)
            out_matrix[i] = in_matrix[i].clone();

        return out_matrix;
    }

    /**
     * This is for solving sodoku with recursion
     * @param sudo sudoku matrix
     */
    private void solveRecur(int[][] sudo) {
        boolean finish = true;

        for(int i = 0; i < sudo.length; i++)
        {
            for(int j = 0; j < sudo[i].length; j++)
            {
                if(sudo[i][j] == 0) {
                    finish = false;

                    for(int ele = 1; ele < 10; ele++) {
                        if(checkHorAndVer(i, j, ele)) {
                            int[][] tempSudo = copyMatrix(sudo);
                            tempSudo[i][j] = ele;
                            solveRecur(tempSudo);
                        }
                    }
                }
            }
        }

        if(finish)
            this.ansSudo.add(copyMatrix(sudo));
    }
}
