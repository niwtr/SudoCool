package team.sudocool;

import team.sudocool.Identifier.Identifier;
import team.sudocool.Identifier.algol.ReadData;
import team.sudocool.Solver.ReadSudo;
import team.sudocool.Solver.Solver;

import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author L.Laddie
 * @version 1.0
 * @since 2016/11/7
 */
public class Main_train {
    /**
     * This is the main function
     */
    public static void main(String args[]) {
        System.out.println("Starting...");

        String path_test = "D:/patterns28/";
        String path_train = "D:/patterns28_60/";
        String path_x = "D:/patterns_x/";

//        Identifier iden = new Identifier(path_test);

//        iden.learnAndTest(0.001);

//        double ave = 0d;
//        for (int i = 1; i < 10; i++) {
//            double ans = iden.testData(i);
//            ave += ans;
//            System.out.println(i + ": " + new DecimalFormat("##.##").format(ans * 100) + "%");
//        }
//        System.out.println("Average: " + new DecimalFormat("##.##").format(ave * 100/9) + "%");


        int[][][] sudoku = null;
        try {
            sudoku = ReadSudo.readMatrixFile("D:/suduku_test.txt", 4);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert sudoku != null;

        Solver solver = new Solver();

        ArrayList<int[][]> ans = solver.solveSudo(sudoku[0]);
        printSuduAns(ans);

//        ArrayList<int[][]> ans2 = solver.solveSudo(sudoku[1]);
//        printSuduAns(ans2);
    }

    private static void printSuduAns(ArrayList<int[][]> ans) {
        if(ans == null || ans.isEmpty())
            System.out.println("Null Answer");
        else
            for(int i = 0; i < ans.size(); i++)
            {
                System.out.println("Answer " + i + ": ");

                for (int[] an : ans.get(i)) {
                    System.out.println(Arrays.toString(an));
                }
            }
        System.out.println();
    }
}
