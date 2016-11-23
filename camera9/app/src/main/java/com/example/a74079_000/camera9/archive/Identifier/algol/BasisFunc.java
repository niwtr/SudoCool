package com.example.a74079_000.camera9.archive.Identifier.algol;

import java.io.FileReader;
import java.io.IOException;

/**
 * @author L.Laddie
 * @version 1.0
 * @since 2016/11/11
 */
public class BasisFunc {

    /**
     * Judge the input data whether null
     * @param in matrix
     * @return whether null
     */
    public static boolean isNull(int[][] in) {
        for(int[] i : in)
            for(int ele : i)
                if(ele != 0) {
                    return false;
                }
        return true;
    }

    /**
     * Convert Two-dimensional to One-dimensional
     * and also int to double
     * @param in two-dimension matrix
     * @return one-dimension matrix
     */
    public static double[] convertOne(int[][] in)
    {
        int n = in.length;
        double[] ans = new double[n*n];
        for(int i = 0; i < n; i++)
        {
            for(int j = 0; j < n; j++)
            {
                ans[i*n+j] = in[i][j];
            }
        }

        return ans;
    }

    /**
     * Calculate the max
     */
    public static int getMax(double[] in) {
        double max_val = in[0];
        int ans = 0;

        for(int i = 1; i < in.length; i++)
        {
            if(in[i] > max_val){
                ans = i;
                max_val = in[i];
            }
        }

        return ans;
    }
}