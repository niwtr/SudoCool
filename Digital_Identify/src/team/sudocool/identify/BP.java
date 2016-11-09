package team.sudocool.identify;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Random;

/**
 * @author L.Laddie
 * @version 1.0
 * @since 2016/11/7
 */
public class BP {
    private int[] layer_num;
    private double[][] layer_out;
    private double[][] layer_grad;
    private double[][][] layer_weight;
    private double[][][] layer_weight_delta;    //momentum adjustment value
    private double rate;                        //study rate
    private double mo_rate;                     //Momentum coefficient

    /**
     * Initial BP network
     * @param layer_num
     * @param rate
     */
    public BP(int[] layer_num, double rate, double mo_rate) {
        int len = layer_num.length;

        this.layer_num = Arrays.copyOf(layer_num, len);
        this.layer_out = new double[len][];
        this.layer_grad = new double[len][];
        this.layer_weight = new double[len-1][][];
        this.layer_weight_delta = new double[len][][];
        this.rate = rate;
        this.mo_rate = mo_rate;

        Random random = new Random();
        double scope = 2.4/layer_num[0];

        for(int i = 0; i < len; i++)
        {
            this.layer_out[i] = new double[layer_num[i]+1];
            this.layer_grad[i] = new double[layer_num[i]+1];
        }

        for(int i = 0; i < len-1; i++)
        {
            layer_weight[i] = new double[layer_num[i]+1][layer_num[i+1]+1];
            layer_weight_delta[i] = new double[layer_num[i]+1][layer_num[i+1]+1];

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
     * Try to load weight from file "net_weight.txt"
     */
    public void loadWeight() throws Exception {

    }

    /**
     * Try to conserve the weight to file "net_weight.txt"
     */
    public void saveWeight() throws Exception {
        FileWriter file = null;
        try {
            file = new FileWriter("D:/bp_net.data");

            file.write(Arrays.deepToString(layer_weight));

            file.flush();
            file.close();
        } finally {
            if(file != null)
                file.close();
        }
    }

    /**
     * Forward propagation: calculate the output
     * @param in
     * @return layer_out[this.layer_num.length-1]
     */
    public double[] forwardProp(double[] in) {
        int len = layer_num.length;

        //forward calculate
        for(int i = 0; i < len-1; i++)
        {
            layer_out[i][0] = -1;     //threshold

            //initial the input data
            if(i == 0)
            {
                for(int j = 0; j < in.length; j++)
                    layer_out[0][j + 1] = in[j];       //whether to use sigmod
            }

            for(int j = 1; j <= layer_num[i+1]; j++)
            {
                double temp = 0d;
                for(int k = 0; k <= layer_num[i]; k++)
                {
                    temp += layer_out[i][k] * layer_weight[i][k][j];
                }
                layer_out[i+1][j] = sigMoid(temp);
            }
        }

        double[] ans = new double[layer_num[len-1]];
        System.arraycopy(layer_out[len-1], 1, ans, 0, layer_num[len-1]);

        return ans;
    }

    /**
     * Backward propagation: calculate error and update weight
     * @param sample_out
     */
    public void backProp(double[] sample_out) {
        int len = layer_num.length;

        //calculate the output layer local gradient
        for(int i = 1; i <= layer_num[len-1]; i++)
        {
            double temp_o = layer_out[len-1][i];
            layer_grad[len-1][i] = temp_o * (1-temp_o) * (sample_out[i-1] - temp_o);
        }

        for(int i = len-2; i >= 0; i--) //backward
        {
            for(int j = 0; j <= layer_num[i]; j++)      //when j=0, adjust the threshold
            {
                double temp_gradient = 0d;
                for(int k = 1; k <= layer_num[i+1]; k++)
                {
                    //update the network weight
                    layer_weight_delta[i][j][k] = mo_rate*layer_weight_delta[i][j][k]       //notice that it`s '+' but not '-'
                            + rate * layer_out[i][j] * layer_grad[i+1][k];
                    layer_weight[i][j][k] = layer_weight[i][j][k] + layer_weight_delta[i][j][k];

                    temp_gradient += layer_grad[i+1][k] * layer_weight[i][j][k];
                }

                //calculate the next local gradient
                layer_grad[i][j] = layer_out[i][j] * (1-layer_out[i][j]) * temp_gradient;
            }
        }
    }

    /**
     * Training the network
     * @param in
     * @param out
     */
    public void trainNet(double[] in, double[] out, double allow_err) {
        double last_error = 0d;
        double out_error = 0d;

        while(true) {
            last_error = out_error;
            out_error = 0d;

            double[] ans = this.forwardProp(in);
            this.backProp(out);
            for(int i = 0; i < ans.length; i++)
                out_error += (out[i] - ans[i]) * (out[i] - ans[i]) / 2;

//            System.out.println("Error: " + out_error);

            if(Math.abs(last_error-out_error) < allow_err)
                break;

//            if(out_error < allow_err)
//                break;
        }
    }

    /**
     * Sigmoid function
     * @param val
     */
    private double sigMoid(double val) {
        return 1d / (1d + Math.exp(-val));
    }
}
