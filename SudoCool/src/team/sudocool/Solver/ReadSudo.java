package team.sudocool.Solver;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by L.Laddie on 2016/11/29.
 */
public class ReadSudo {
    /**
     * Read some matrix from file
     * @param path file_path
     * @param size matrix_size
     * @return matrix(-1 replace 0)
     * @throws IOException file can`t read
     */
    public static int[][][] readMatrixFile(String path, int size) throws IOException {
        int[][] ans_ele = new int[size][size];
        ArrayList<int[][]> ans = new ArrayList<>();

        int j = 0, i = 0;
        FileReader in = null;

        try {
            in = new FileReader(path);
            int temp;

            while ((temp = in.read()) != -1) {
                if (temp >= 48 && temp < 58)
                    ans_ele[i][j++] = temp == 48 ? -1 : (temp-48);  //when read 0, write -1

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
