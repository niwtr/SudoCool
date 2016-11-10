package team.sudocool.Identifier.algol;

import java.io.*;

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
        double[][] ans = new double[2000][size*size];
        int i = 0, j = 0;
        FileReader in = null;

        try {
            in = new FileReader(path);
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
        } finally {
            if (in != null)
                in.close();
        }

        double[][] rtn = new double[i][size*size];
        for(int w = 0; w < i; w++)
            System.arraycopy(ans[w], 0, rtn[w], 0, size*size);

        return rtn;
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
