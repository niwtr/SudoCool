package com.example.a74079_000.camera9.archive;

import com.example.a74079_000.camera9.archive.Identifier.Identifier;

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
        System.out.println("Starting...");

        String path_test = "D:/patterns28/";
        String path_train = "D:/patterns28_60/";

        Identifier iden = new Identifier(path_test);

        //手写体
//        iden_train.learnAndTest(0.001);

        for (int i = 0; i < 10; i++)
        {
            double ans = iden.testData(i);
            System.out.println(i + ": " + new DecimalFormat("##.##").format(ans*100) + "%");
        }

        //印刷体
//        int[][][] test = null;
//        try {
//            test = readMatrixFile("D:/test2.pat", 7);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        while(true) {
//            for (int i = 0; i < test.length; i++) {
//                iden.increLearn(test[i], i);
//                System.out.print(i + ":" + iden.toDigit(test[i]) + " ");
//            }
//            System.out.println();
//        }

//        while(true) {
//            iden.increLearn(test[0], 6);
//            iden.increLearn(test[1], 9);
//
//            System.out.print(6 + ":" + iden.toDigit(test[0]) + " ");
//            System.out.print(9 + ":" + iden.toDigit(test[1]) + " ");
//
//            System.out.println();
//        }
    }

    /**
     * Read some matrix from file
     * @param path file_path
     * @param size matrix_size
     * @return  matrix
     * @throws IOException  file can`t read
     */
    public static int[][][] readMatrixFile(String path, int size) throws IOException
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