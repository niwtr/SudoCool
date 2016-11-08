package team.sudocool.identify;

import java.util.Arrays;

/**
 * @author L.Laddie
 * @version 1.0
 * @since 2016/11/7
 */
public class MainActivity {
    /**
     * This is the main function
     */
    public static void main(String args[]) {
        //construct bp network
        int[] layer_num = new int[3];
        layer_num[0] = 8*8;
        layer_num[2] = 10;
        layer_num[1] = (int) (Math.sqrt(layer_num[0] + layer_num[2]) + 5);
        BP bp_image = new BP(layer_num, 0.5, 0.8);
        bp_image.loadWeight();

        int num = 5;    //the number to identify

        ReadData file = new ReadData();
        double[][] in = null;
        try{
            in = file.readFile(num, 8);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assert in != null;

        double[] out = new double[10];
        out[num] = 0.99;

        for(int j = 0; j < 20; j++)
        {
            for(int i = 0; i < 979; i++)         //max data is 979
            {
                System.out.println("Training data " + i);
                bp_image.trainNet(in[i], out, 10);
            }
        }

        bp_image.saveWeight();

        double[] temp = bp_image.forwardProp(in[100]);
        System.out.print(getMax(temp));
    }

    /**
     * calculate the max
     */
    private static int getMax(double[] in) {
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
