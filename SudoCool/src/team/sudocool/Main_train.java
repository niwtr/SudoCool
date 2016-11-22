package team.sudocool;

import team.sudocool.Identifier.Identifier;
import team.sudocool.Identifier.algol.ReadData;
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

//        Identifier iden = new Identifier(path_test);

//        iden.learnAndTest(0.001);

//        double ave = 0d;
//        for (int i = 0; i < 10; i++) {
//            double ans = iden.testData(i);
//            ave += ans;
//            System.out.println(i + ": " + new DecimalFormat("##.##").format(ans * 100) + "%");
//        }
//        System.out.println("Average: " + new DecimalFormat("##.##").format(ave * 10) + "%");


        int[][][] sudoku = null;
        try {
            sudoku = readMatrixFile("D:/sudoku.txt", 9);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert sudoku != null;

        Solver solver = new Solver();
        ArrayList<int[][]> ans = solver.solveSudo(sudoku[0]);

        if(ans.isEmpty())
            System.out.println("Null Answer");
        else
            for(int i = 0; i < ans.size(); i++)
            {
                System.out.println("Answer " + i + ": ");

                for (int[] an : ans.get(i)) {
                    System.out.println(Arrays.toString(an));
                }
            }
    }

    /**
     * Read some matrix from file
     *
     * @param path file_path
     * @param size matrix_size
     * @return matrix
     * @throws IOException file can`t read
     */
    private static int[][][] readMatrixFile(String path, int size) throws IOException {
        int[][] ans_ele = new int[size][size];
        ArrayList<int[][]> ans = new ArrayList<>();

        int j = 0, i = 0;
        FileReader in = null;

        try {
            in = new FileReader(path);
            int temp;

            while ((temp = in.read()) != -1) {
                if (temp >= 48 && temp < 58)
                    ans_ele[i][j++] = temp - 48;

                if (j == size) {
                    j = 0;
                    i++;
                }

                if (i == size) {
                    i = 0;

                    int[][] matrix_temp = new int[ans_ele.length][];
                    for (int k = 0; k < ans_ele.length; k++)
                        matrix_temp[k] = ans_ele[k].clone();

                    ans.add(matrix_temp);
                }
            }
        } finally {
            if (in != null)
                in.close();
        }

        return ans.toArray(new int[0][0][0]);
    }
}
