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
    private boolean finish;
    private int[][] curSudo;
    private ArrayList<int[][]> ansSudo;
    private HashMap<Pair<Integer, Integer>, ArrayList<Integer>> curSudoReadyNum;
    private Stack<Pair<int[][], HashMap<Pair<Integer, Integer>, ArrayList<Integer>>>> sudoBuffer;


    /**
     * This is the function for solving the Sudoku
     */
    public ArrayList<int[][]> solveSudo(int[][] sudo) {
        finish = false;
        curSudo = copyMatrix(sudo);
        ansSudo = new ArrayList<>();
        sudoBuffer = new Stack<>();
        curSudoReadyNum = new HashMap<>();

        ArrayList<Pair<Integer, Integer>> nullPoint = isNullUnit();
        if(nullPoint.isEmpty())
        {
            if(checkAnswer())
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

        return ansSudo;
    }

    /**
     * judge the matrix whether have null unit
     * @return  the null unit point list
     */
    private ArrayList<Pair<Integer, Integer>> isNullUnit() {
        if (curSudo == null)
            throw new AssertionError();

        ArrayList<Pair<Integer, Integer>> nullPoint = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if(curSudo[i][j] == 0)
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

            for(int value = 1; value < 10; value++)
            {
                if(checkHorAndVer(i, j, value)) {
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
                if(!checkHorAndVer(i, j, value))
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
//            finish = true;
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
    private boolean checkHorAndVer(int i, int j, int value) {
        if (curSudo == null)
            throw new AssertionError();

        for(int k = 0; k < 9; k++)
            if(k != j && curSudo[i][k] == value)
                return false;

        for (int k = 0; k < 9; k++)
            if (k != i && curSudo[k][j] == value)
                return false;

        for (int k = (i/3)*3; k < (i/3)*3+3; k++)
            for(int w = (j/3)*3; w < (j/3)*3+3; w++)
                if((k != i || w != j) && curSudo[k][w] == value)
                    return false;

        return true;
    }

    /**
     * check whether current sudoku is the answer
     * @return true or false
     */
    private boolean checkAnswer() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if(curSudo[i][j] == 0 || !checkHorAndVer(i, j, curSudo[i][j]))
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
