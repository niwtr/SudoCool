package team.sudocool.identify;

import java.io.*;

/**
 * This is for reading the training data
 * @author L.Laddie
 * @version 1.0
 * @since 2016/11/7
 */
public class ReadData {
    public double[][] readFile(int n, int size) throws Exception
    {
        String path = "D:/patterns/" + String.valueOf(n) + ".pat";

        double[][] ans = new double[2000][size*size];
        int i = 0, j = 0;

        FileReader in = new FileReader(path);
        int temp;

        while((temp = in.read()) != -1) {
            if(temp == 48 || temp == 49)
            {
                ans[i][j++] = temp-48;
            }

            if(j == size*size){
                j = 0;
                i++;
            }
        }

        in.close();

        return ans;
    }
}
