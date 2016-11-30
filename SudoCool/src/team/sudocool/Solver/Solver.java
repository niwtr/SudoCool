package team.sudocool.Solver;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

/**
 * @author L.Laddie
 * @version 1.0
 * @since 2016/11/22
 */
public class Solver {
    private static final int MAXANSWERS = 50;
    private static final int NULLUNIT = -1;

    private int sudoType;
    private boolean finish;
    private int[][] curSudo;
    private ArrayList<int[][]> ansSudo;
    private HashMap<Pair<Integer, Integer>, ArrayList<Integer>> curSudoReadyNum;
    private Stack<Pair<int[][], HashMap<Pair<Integer, Integer>, ArrayList<Integer>>>> sudoBuffer;

    /**
     * Initial
     */
    public Solver(){
        ansSudo = new ArrayList<>();
        sudoBuffer = new Stack<>();
        curSudoReadyNum = new HashMap<>();
    }

    /**
     * This is the function for solving the Sudoku
     */
    public ArrayList<int[][]> solveSudo(int[][] sudo) {
        finish = false;
        sudoType = sudo.length;
        curSudo = copyMatrix(sudo);
        ansSudo.clear();
        sudoBuffer.clear();
        curSudoReadyNum.clear();

        if(checkSudoValid())
        {
            ArrayList<Pair<Integer, Integer>> nullPoint = getNullUnit();
            if(nullPoint.isEmpty())
            {
                ansSudo.add(this.curSudo);
            }
            else
            {
                if(findAvaFill(nullPoint))
                {
                    while(!finish) {
                        fillUnit();
                        updateSudoReadyNum();
                    }
                }
            }
        }

        return ansSudo;
    }

    /**
     * judge the matrix whether have null unit
     * @return  the null unit point list
     */
    private ArrayList<Pair<Integer, Integer>> getNullUnit() {
        if (curSudo == null)
            throw new AssertionError();

        ArrayList<Pair<Integer, Integer>> nullPoint = new ArrayList<>();
        for (int i = 0; i < sudoType; i++) {
            for (int j = 0; j < sudoType; j++) {
                if(curSudo[i][j] == NULLUNIT)
                    nullPoint.add(new Pair<>(i, j));
            }
        }

        return nullPoint;
    }

    /**
     * find every blank unit the possible number
     * @param point_list null unit point list
     * @return false for existing some unit no answer
     */
    private boolean findAvaFill(ArrayList<Pair<Integer, Integer>> point_list) {
        for(Pair<Integer, Integer> point : point_list)
        {
            int i = point.getKey();
            int j = point.getValue();
            ArrayList<Integer> readyNumber = new ArrayList<>();

            for(int value = 1; value <= sudoType; value++)
            {
                if(checkNumValid(i, j, value)) {
                    readyNumber.add(value);
                }
            }

            if(readyNumber.isEmpty())
                return false;
            else
                curSudoReadyNum.put(point, readyNumber);
        }

        return true;
    }

    /**
     * fill in the blank unit
     */
    private void fillUnit() {
        boolean findUnique = false;
        Iterator<HashMap.Entry<Pair<Integer, Integer>, ArrayList<Integer>>> it;

        it = curSudoReadyNum.entrySet().iterator();
        while(it.hasNext()) {
            HashMap.Entry<Pair<Integer, Integer>, ArrayList<Integer>> entry = it.next();
            Pair<Integer, Integer> point = entry.getKey();
            ArrayList<Integer> readyNumber = entry.getValue();

            //find the unique unit
            if(readyNumber.size() == 1)
            {
                findUnique = true;

                curSudo[point.getKey()][point.getValue()] = readyNumber.get(0);
                it.remove();

                break;
            }
        }

        //didn`t find the unique unit
        if(!findUnique) {

            it = curSudoReadyNum.entrySet().iterator();
            if(it.hasNext()) {
                HashMap.Entry<Pair<Integer, Integer>, ArrayList<Integer>> entry = it.next();
                Pair<Integer, Integer> point = entry.getKey();
                int i = point.getKey();
                int j = point.getValue();
                int value;

                ArrayList<Integer> readyNumber = entry.getValue();
                if(!readyNumber.isEmpty())
                {
                    int location = readyNumber.size()-1;
                    value = readyNumber.get(location);
                    readyNumber.remove(location);

                    sudoBuffer.push(new Pair<>(copyMatrix(curSudo), copyHashMap(curSudoReadyNum)));

                    it.remove();
                    curSudo[i][j] = value;
                }
            }
        }
    }

    /**
     * After fill in a blank, check the other ready number whether ready now
     */
    private void updateSudoReadyNum() {

        for (HashMap.Entry<Pair<Integer, Integer>, ArrayList<Integer>> entry : curSudoReadyNum.entrySet()) {
            int i = entry.getKey().getKey();
            int j = entry.getKey().getValue();

            ArrayList<Integer> readyNum = entry.getValue();
            Iterator<Integer> it = readyNum.iterator();
            while(it.hasNext()) {
                int value = it.next();
                if(!checkNumValid(i, j, value))
                    it.remove();
            }

            //existing some unit no answer
            if(entry.getValue().isEmpty()) {
                backState();
                break;
            }
        }

        //check the answer
        if(curSudoReadyNum.isEmpty())
        {
            ansSudo.add(copyMatrix(curSudo));

            if(ansSudo.size() > MAXANSWERS)     //no more than 50 answers
                finish = true;
            backState();
        }
    }

    /**
     * back to the nearest state
     */
    private void backState() {
        if(sudoBuffer.isEmpty()) {
            finish = true;
            return;
        }

        Pair<int[][], HashMap<Pair<Integer, Integer>, ArrayList<Integer>>> bufferPair = sudoBuffer.pop();
        curSudo = bufferPair.getKey();
        curSudoReadyNum = bufferPair.getValue();
    }

    /**
     * check whether the value is allowed
     * @param i horizontal
     * @param j vertical
     * @param value padding value
     * @return true or false
     */
    private boolean checkNumValid(int i, int j, int value) {
        if (curSudo == null)
            throw new AssertionError();

        switch (sudoType)
        {
            case 4:
                return checkNumValid_Four(i, j, value);

            case 5:
                return checkNumValid_Five(i, j, value);

            case 6:
                return checkNumValid_Six(i, j, value);

            case 7:
                return checkNumValid_Seven(i, j, value);

            case 8:
                return checkNumValid_Eight(i, j, value);

            case 9:
                return checkNumValid_Nine(i, j, value);

            default:
                throw new AssertionError();
        }
    }

    private boolean checkHorAndVer(int i, int j, int value) {
        for(int k = 0; k < sudoType; k++)
            if(k != j && curSudo[i][k] == value)
                return false;

        for (int k = 0; k < sudoType; k++)
            if (k != i && curSudo[k][j] == value)
                return false;

        return true;
    }

    /**
     * sudoku rule for 4*4
     * @param i horizontal
     * @param j vertical
     * @param value padding value
     * @return true or false
     */
    private boolean checkNumValid_Four(int i, int j, int value) {
        if(!checkHorAndVer(i, j, value))
            return false;

        for (int k = (i/2)*2; k < (i/2)*2+2; k++)
            for(int w = (j/2)*2; w < (j/2)*2+2; w++)
                if((k != i || w != j) && curSudo[k][w] == value)
                    return false;

        return true;
    }

    /**
     * sudoku rule for 5*5
     * @param i horizontal
     * @param j vertical
     * @param value padding value
     * @return true or false
     */
    private boolean checkNumValid_Five(int i, int j, int value) {
        if(!checkHorAndVer(i, j, value))
            return false;

        HashMap<Integer, ArrayList<Pair<Integer, Integer>>> rulePoint = new HashMap<>();
        ArrayList<Pair<Integer, Integer>> pointArray = new ArrayList<>();
        pointArray.add(new Pair<>(1, 2));
        pointArray.add(new Pair<>(2, 2));
        pointArray.add(new Pair<>(3, 2));
        pointArray.add(new Pair<>(2, 1));
        pointArray.add(new Pair<>(2, 3));
        rulePoint.put(1, pointArray);

        ArrayList<Pair<Integer, Integer>> pointArray2 = new ArrayList<>();
        pointArray2.add(new Pair<>(0, 0));
        pointArray2.add(new Pair<>(0, 1));
        pointArray2.add(new Pair<>(0, 2));
        pointArray2.add(new Pair<>(1, 0));
        pointArray2.add(new Pair<>(1, 1));
        rulePoint.put(2, pointArray2);

        ArrayList<Pair<Integer, Integer>> pointArray3 = new ArrayList<>();
        pointArray3.add(new Pair<>(0, 3));
        pointArray3.add(new Pair<>(0, 4));
        pointArray3.add(new Pair<>(1, 3));
        pointArray3.add(new Pair<>(1, 4));
        pointArray3.add(new Pair<>(2, 4));
        rulePoint.put(3, pointArray3);

        ArrayList<Pair<Integer, Integer>> pointArray4 = new ArrayList<>();
        pointArray4.add(new Pair<>(2, 0));
        pointArray4.add(new Pair<>(3, 0));
        pointArray4.add(new Pair<>(3, 1));
        pointArray4.add(new Pair<>(4, 0));
        pointArray4.add(new Pair<>(4, 1));
        rulePoint.put(4, pointArray4);

        ArrayList<Pair<Integer, Integer>> pointArray5 = new ArrayList<>();
        pointArray5.add(new Pair<>(3, 3));
        pointArray5.add(new Pair<>(3, 4));
        pointArray5.add(new Pair<>(4, 2));
        pointArray5.add(new Pair<>(4, 3));
        pointArray5.add(new Pair<>(4, 4));
        rulePoint.put(5, pointArray5);

        Pair<Integer, Integer> cur_point = new Pair<>(i, j);
        for(int w = 1; w <= 5; w++)
        {
            ArrayList<Pair<Integer, Integer>> pointArray_temp = rulePoint.get(w);
            if(pointArray_temp.indexOf(cur_point) != -1) {
                for(int q = 0; q < 5; q++)
                {
                    Pair<Integer, Integer> point = pointArray_temp.get(q);
                    if(!point.equals(cur_point) && curSudo[point.getKey()][point.getValue()] == value)
                        return false;
                }
            }
        }

        return true;
    }

    /**
     * sudoku rule for 6*6
     * @param i horizontal
     * @param j vertical
     * @param value padding value
     * @return true or false
     */
    private boolean checkNumValid_Six(int i, int j, int value) {
        if(!checkHorAndVer(i, j, value))
            return false;

        for (int k = (i/2)*2; k < (i/2)*2+2; k++)
            for(int w = (j/3)*3; w < (j/3)*3+3; w++)
                if((k != i || w != j) && curSudo[k][w] == value)
                    return false;

        return true;
    }

    /**
     * sudoku rule for 7*7
     * @param i horizontal
     * @param j vertical
     * @param value padding value
     * @return true or false
     */
    private boolean checkNumValid_Seven(int i, int j, int value) {
        if(!checkHorAndVer(i, j, value))
            return false;

        HashMap<Integer, ArrayList<Pair<Integer, Integer>>> rulePoint = new HashMap<>();
        ArrayList<Pair<Integer, Integer>> pointArray = new ArrayList<>();
        pointArray.add(new Pair<>(0, 0));
        pointArray.add(new Pair<>(0, 1));
        pointArray.add(new Pair<>(1, 0));
        pointArray.add(new Pair<>(1, 1));
        pointArray.add(new Pair<>(1, 2));
        pointArray.add(new Pair<>(2, 1));
        pointArray.add(new Pair<>(2, 2));
        rulePoint.put(1, pointArray);

        ArrayList<Pair<Integer, Integer>> pointArray2 = new ArrayList<>();
        pointArray2.add(new Pair<>(0, 2));
        pointArray2.add(new Pair<>(0, 3));
        pointArray2.add(new Pair<>(0, 4));
        pointArray2.add(new Pair<>(0, 5));
        pointArray2.add(new Pair<>(1, 3));
        pointArray2.add(new Pair<>(1, 4));
        pointArray2.add(new Pair<>(1, 5));
        rulePoint.put(2, pointArray2);

        ArrayList<Pair<Integer, Integer>> pointArray3 = new ArrayList<>();
        pointArray3.add(new Pair<>(0, 6));
        pointArray3.add(new Pair<>(1, 6));
        pointArray3.add(new Pair<>(2, 6));
        pointArray3.add(new Pair<>(3, 6));
        pointArray3.add(new Pair<>(4, 6));
        pointArray3.add(new Pair<>(2, 5));
        pointArray3.add(new Pair<>(3, 5));
        rulePoint.put(3, pointArray3);

        ArrayList<Pair<Integer, Integer>> pointArray4 = new ArrayList<>();
        pointArray4.add(new Pair<>(2, 0));
        pointArray4.add(new Pair<>(3, 0));
        pointArray4.add(new Pair<>(4, 0));
        pointArray4.add(new Pair<>(5, 0));
        pointArray4.add(new Pair<>(6, 0));
        pointArray4.add(new Pair<>(3, 1));
        pointArray4.add(new Pair<>(4, 1));
        rulePoint.put(4, pointArray4);

        ArrayList<Pair<Integer, Integer>> pointArray5 = new ArrayList<>();
        pointArray5.add(new Pair<>(2, 3));
        pointArray5.add(new Pair<>(2, 4));
        pointArray5.add(new Pair<>(3, 2));
        pointArray5.add(new Pair<>(3, 3));
        pointArray5.add(new Pair<>(3, 4));
        pointArray5.add(new Pair<>(4, 3));
        pointArray5.add(new Pair<>(4, 2));
        rulePoint.put(5, pointArray5);

        ArrayList<Pair<Integer, Integer>> pointArray6 = new ArrayList<>();
        pointArray6.add(new Pair<>(5, 1));
        pointArray6.add(new Pair<>(5, 2));
        pointArray6.add(new Pair<>(5, 3));
        pointArray6.add(new Pair<>(6, 1));
        pointArray6.add(new Pair<>(6, 2));
        pointArray6.add(new Pair<>(6, 3));
        pointArray6.add(new Pair<>(6, 4));
        rulePoint.put(6, pointArray6);

        ArrayList<Pair<Integer, Integer>> pointArray7 = new ArrayList<>();
        pointArray7.add(new Pair<>(4, 4));
        pointArray7.add(new Pair<>(4, 5));
        pointArray7.add(new Pair<>(5, 4));
        pointArray7.add(new Pair<>(5, 5));
        pointArray7.add(new Pair<>(5, 6));
        pointArray7.add(new Pair<>(6, 5));
        pointArray7.add(new Pair<>(6, 6));
        rulePoint.put(7, pointArray7);

        Pair<Integer, Integer> cur_point = new Pair<>(i, j);
        for(int w = 1; w <= 7; w++)
        {
            ArrayList<Pair<Integer, Integer>> pointArray_temp = rulePoint.get(w);
            if(pointArray_temp.indexOf(cur_point) != -1) {
                for(int q = 0; q < 7; q++)
                {
                    Pair<Integer, Integer> point = pointArray_temp.get(q);
                    if(!point.equals(cur_point) && curSudo[point.getKey()][point.getValue()] == value)
                        return false;
                }
            }
        }

        return true;
    }

    /**
     * sudoku rule for 8*8
     * @param i horizontal
     * @param j vertical
     * @param value padding value
     * @return true or false
     */
    private boolean checkNumValid_Eight(int i, int j, int value) {
        if(!checkHorAndVer(i, j, value))
            return false;

        for (int k = (i/2)*2; k < (i/2)*2+2; k++)
            for(int w = (j/4)*4; w < (j/4)*4+4; w++)
                if((k != i || w != j) && curSudo[k][w] == value)
                    return false;

        return true;
    }

    /**
     * sudoku rule for 9*9
     * @param i horizontal
     * @param j vertical
     * @param value padding value
     * @return true or false
     */
    private boolean checkNumValid_Nine(int i, int j, int value) {
        if(!checkHorAndVer(i, j, value))
            return false;

        for (int k = (i/3)*3; k < (i/3)*3+3; k++)
            for(int w = (j/3)*3; w < (j/3)*3+3; w++)
                if((k != i || w != j) && curSudo[k][w] == value)
                    return false;

        return true;
    }

    /**
     * check whether current sudoku is the valid
     * @return true or false
     */
    private boolean checkSudoValid() {
        for (int i = 0; i < sudoType; i++) {
            for (int j = 0; j < sudoType; j++) {
                if(curSudo[i][j] != NULLUNIT && !checkNumValid(i, j, curSudo[i][j]))
                    return false;
            }
        }

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
     * deep copy of hashmap
     * @param in_hashmap input
     * @return copy hashmap
     */
    private HashMap<Pair<Integer, Integer>, ArrayList<Integer>> copyHashMap(HashMap<Pair<Integer, Integer>, ArrayList<Integer>> in_hashmap) {
        HashMap<Pair<Integer, Integer>, ArrayList<Integer>> newHashMap = new HashMap<>();

        for(HashMap.Entry<Pair<Integer, Integer>, ArrayList<Integer>> entry : in_hashmap.entrySet())
        {
            newHashMap.put(entry.getKey(), (ArrayList<Integer>) entry.getValue().clone());
        }

        return newHashMap;
    }
}
