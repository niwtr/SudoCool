package team.sudocool.Identifier.algol;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This is for reading the training data
 * @author L.Laddie
 * @version 1.0
 * @since 2016/11/7
 */
public class ReadData {
    /**
     * read from learning file
     * @param size matrix size
     * @return all the one-dimension matrix
     * @throws IOException read error
     */
    private double[][] readFile(String path, int size) throws IOException
    {
        double[] ans_ele = new double[size*size];
        ArrayList<double[]> ans = new ArrayList<double[]>();

        int j = 0;
        FileReader in = null;

        try {
            in = new FileReader(path);
            int temp;

            while((temp = in.read()) != -1) {
                if(temp == 48 || temp == 49)
                {
                    ans_ele[j++] = temp-48;         //'0' or '1'
                }

                if(j == size*size){
                    j = 0;
                    ans.add(ans_ele.clone());
                }
            }
        } finally {
            if (in != null)
                in.close();
        }

        return ans.toArray(new double[0][0]);
    }

    /**
     * get all the ten digit data
     * @param ppath data path
     * @param size matrix size
     * @return all the ten digit data
     */
    public double[][][] getData(String ppath, int size) {
        double[][][] ans = new double[10][][];
        for (int i = 0; i < 10; i++) {
            String path = ppath + String.valueOf(i) + ".pat";
            try{
                ans[i] = readFile(path, size);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return ans;
    }
}
