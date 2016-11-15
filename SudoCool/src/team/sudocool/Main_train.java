package team.sudocool;

import team.sudocool.Identifier.Identifier;

import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
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
        Identifier iden = new Identifier();
//        iden.learnAndTest("D:/patterns7neo/", 0.01);

        for (int i = 0; i < 10; i++)
        {
            double ans = iden.testData("D:/patterns7neo/", i);
            System.out.println(i + ": " + new DecimalFormat("##.##").format(ans*100) + "%");
        }

    }

    private static int[][][] readMatrix(String path, int size) throws IOException
    {
        int[][][] ans = new int[2000][size][size];
        int i = 0, j = 0, k = 0;
        FileReader in = null;

        try {
            in = new FileReader(path);
            int temp;

            while((temp = in.read()) != -1) {
                if(temp == 48 || temp == 49)
                {
                    ans[k][i][j++] = temp-48;
                }

                if(j == size) {
                    j = 0;
                    i++;
                }

                if(i == size) {
                    i = 0;
                    k++;
                }
            }
        } finally {
            if (in != null)
                in.close();
        }

        int[][][] rtn = new int[k][size][size];
        for(int w = 0; w < k; w++)
        {
            for(int q = 0; q < size; q++)
            {
                System.arraycopy(ans[w][q], 0, rtn[w][q], 0, size);
            }
        }

        return rtn;
    }
}
