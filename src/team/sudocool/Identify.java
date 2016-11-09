package team.sudocool;

import team.sudocool.identify.BP;
import team.sudocool.identify.ReadData;

import java.io.IOException;

/**
 * @author L.Laddie
 * @version 1.0
 * @since 2016/11/9
 */
public class Identify {
    private BP bp_image = null;
    private int size = 7;               //input matrix of data size
    private double allow_error = 0.01;   //when training allow error

    /**
     * Initial the digit_identify network
     */
    public Identify() {
        double rate = 0.001;      //study rate
        double mo_rate = 0.8;   //momentum rate

        //construct bp network
        int[] layer_num = new int[3];
        layer_num[0] = size*size;
        layer_num[2] = 10;
        layer_num[1] = 50;

//        layer_num[1] = (int) (Math.sqrt(0.43*layer_num[0]*layer_num[2] + 0.12*layer_num[2]*layer_num[2]
//                + 2.54*layer_num[0] + 0.77*layer_num[2] + 0.35 + 0.51));

        bp_image = new BP(layer_num, rate, mo_rate);

        try {
            bp_image.loadWeight();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Use bp network to identify digits
     * @param in
     * @return
     */
    public int toDigit(int[][] in) {
        if(isNull(in))      //input data is null
            return -1;

        double[] out = bp_image.forwardProp(convertOne(in));
        return getMax(out);
    }

    /**
     * Incremental learning for data that wrong identify
     * @param in
     * @param num
     */
    public void increLearn(int[][] in, int num) {
        double[] out = new double[10];
        out[num] = 1d;

        bp_image.trainNet(convertOne(in), out, allow_error);

        //save the network weight
        try {
            bp_image.saveWeight();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Let the network to learn
     * @param ppath
     * @param times
     */
    public void Learn(String ppath, int times) {
        ReadData file = new ReadData();

        double[][][] in = file.getData(ppath, size);

        System.out.println("Training data...");

        for(int i = 0; i < times; i++)
            for(int j = 0; j < 979; j++)         //max data is 979
                for(int k = 0; k < 10; k++)
                {
                    double[] out = new double[10];
                    out[k] = 1;

                    bp_image.trainNet(in[k][j], out, allow_error);
                }

        //save the network weight
        try {
            bp_image.saveWeight();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Try to test the bp network performance
     * @param ppath
     * @param num
     * @return
     */
    public double testData(String ppath, int num) {
        ReadData file = new ReadData();

        double[][][] in = file.getData(ppath, size);
        int rtn = 0;

        for(int i = 0; i < 979; i++)
        {
            double[] out = bp_image.forwardProp(in[num][i]);
            int ans = getMax(out);

            if(ans != num)
                rtn++;
        }

        return rtn/979d;
    }

    /**
     * Judge the input data whether null
     * @param in
     * @return
     */
    private boolean isNull(int[][] in) {
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
     * @param in
     * @return
     */
    private double[] convertOne(int[][] in)
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
    private int getMax(double[] in) {
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
