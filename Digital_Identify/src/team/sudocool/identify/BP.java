package team.sudocool.identify;

import java.util.Arrays;
import java.util.Random;

/**
 * @author L.Laddie
 * @version 1.0
 * @since 2016/11/7
 */
public class BP {
    private int[] layer_num;
    private double[][] layer_net;
    private double[][] layer_grad;
    private double[][][] layer_weight;
    private double rate;

    /**
     * Initial BP network
     * @param layer_num
     * @param rate
     */
    public BP(int[] layer_num, double rate) {
        int len = layer_num.length;

        this.layer_num = Arrays.copyOf(layer_num, len);
        this.layer_net = new double[len][];
        this.layer_grad = new double[len][];
        this.layer_weight = new double[len][][];
        this.rate = rate;

        Random random = new Random();
        double scope = 2.4/layer_num[0];

        for(int i = 0; i < len; i++)
        {
            this.layer_net[i] = new double[layer_num[i]+1];
            this.layer_grad[i] = new double[layer_num[i]+1];
        }

        for(int i = 0; i < len-1; i++)
        {
            layer_weight[i] = new double[layer_num[i]+1][layer_num[i+1]];
            for(int j = 0; j < layer_num[i]+1; j++)
            {
                for(int k = 0; k < layer_num[i+1]; k++)
                {
                    //random scope:(-2.4/F, 2.4/F)
                    layer_weight[i][j][k] = random.nextDouble()*scope*2 - scope;
                }
            }
        }
    }

    /**
     * Forward propagation: calculate the output
     * @param in
     * @return layer_net[this.layer_num.length-1]
     */
    public double[] forwardProp(double[] in) {
        int len = layer_num.length;

        for(int i = 0; i < len; i++)
        {
            layer_net[i][0] = -1;     //threshold
            if(i == 0)
            {
                for(int j = 0; j < in.length+1; j++)
                    layer_net[i][j + 1] = in[j];
            }
        }

        //forward calculate
        for(int i = 0; i < len-1; i++)
        {
            for(int j = 1; j <= layer_num[i+1]; j++)
            {
                layer_net[i+1][j] = 0;
                for(int k = 0; k < layer_num[i]+1; k++)
                {
                    layer_net[i+1][j] += layer_net[i][k] * layer_weight[i][k][j];
                }
            }
        }

        return layer_net[len-1];
    }

    /**
     * Backward propagation: calculate error and update weight
     * @param sample_out
     */
    public void backProp(double[] sample_out) {
        int len = layer_num.length;

        //calculate the output layer local gradient
        for(int i = 1; i <= layer_num[i]; i++)
        {
            double temp_o = layer_net[len-1][i];
            layer_grad[len-1][i] = temp_o * (1-temp_o) * (sample_out[i-1] - temp_o);
        }

        for(int i = len-2; i >= 0; i--) //backward
        {
            for(int j = 0; j <= layer_num[i]; j++)
            {
                double temp_gradient = 0.0;
                for(int k = 1; k <= layer_num[i+1]; k++)
                {
                    //update the network weight
                    layer_weight[i][j][k] = layer_weight[i][j][k] - rate * layer_net[i][j] * layer_grad[i+1][k];
                    temp_gradient += layer_grad[i+1][k] * layer_weight[i][j][k];
                }

                //calculate the next local gradient
                layer_grad[i][j] = layer_net[i][j] * (1-layer_net[i][j]) * temp_gradient;
            }
        }
    }

    /**
     * Training the network
     * @param in
     * @param out
     */
    public void trainNet(double[][] in, double[][] out) {
        //loop all the training data
        for (int j = 0; j < in.length; j++)
        {
            //update the network
            while(true) {
                double out_error = 0.0;

                double[] ans = this.forwardProp(in[j]);
                this.backProp(out[j]);
                for(int i = 0; i < ans.length; i++)
                    out_error += (out[j][i] - ans[i]) * (out[j][i] - ans[i]) / 2;

                if(out_error < 0.5)
                    break;
            }
        }
    }
}
