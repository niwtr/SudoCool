package team.sudocool.Identifier;

import team.sudocool.Identifier.algol.BP;
import team.sudocool.Identifier.algol.BasisFunc;
import team.sudocool.Identifier.algol.ReadData;

import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * @author L.Laddie
 * @version 1.0
 * @since 2016/11/9
 */
public class Identifier {
    private BP bp_image = null;
    private double[][][] train_data;

    private static final int size = 28;                 //input matrix of data size
    private static final double allow_error = 0.01;     //when training once allow error
    private static final int data_num = 892;            //training data number
    private static final int layer_num_hidden = 120;     //hidden number
    private static final double rate = 0.05;            //study rate
    private static final double mo_rate = 0.8;          //momentum rate


    /**
     * Initial the digit_identify network
     * (Three important parameters)
     */
    public Identifier() {
        int[] layer_num = new int[3];
        layer_num[0] = size*size;
        layer_num[2] = 9;
        layer_num[1] = layer_num_hidden;

//        int test = (int) (Math.sqrt(0.43*layer_num[0]*layer_num[2] + 0.12*layer_num[2]*layer_num[2]
//                + 2.54*layer_num[0] + 0.77*layer_num[2] + 0.35 + 0.51));

        bp_image = new BP(layer_num, rate, mo_rate);

        try {
            bp_image.loadWeight();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Construct function for trainer
     * @param train_path train data path
     */
    public Identifier(String train_path) {
        this();

        ReadData file = new ReadData();
        train_data = file.getData(train_path, size);
    }

    /**
     * Use bp network to identify digits
     * @param in data to identify
     * @return it`s number
     */
    public int toDigit(int[][] in) {
        if(BasisFunc.isNull(in))      //input data is null
            return -1;

        double[] out = bp_image.forwardProp(BasisFunc.convertOne(in));
        return BasisFunc.getMax(out)+1;
    }

    /**
     * Incremental learning for data that wrong identify
     * @param in the wrong data
     * @param num true output number
     */
    public void increLearn(int[][] in, int num) {
        double[] out = new double[9];
        out[num-1] = 1d;

        bp_image.trainNet(BasisFunc.convertOne(in), out, allow_error);

        //save the network weight
        try {
            bp_image.saveWeight();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Let the network to learn
     * @param times learn times
     */
    private void Learn(int times) {
        if (train_data == null)
            throw new AssertionError();

        System.out.println("Training data...");

        for(int i = 0; i < times; i++)
            for(int j = 0; j < data_num; j++)
                for(int k = 0; k < 9; k++)
                {
                    double[] out = new double[9];
                    out[k] = 1;

                    bp_image.trainNet(train_data[k][j], out, allow_error);
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
     * @param num needed number
     * @return test error
     */
    public double testData(int num) {
        if (train_data == null)
            throw new AssertionError();

        int rtn = 0;

        for(int i = 0; i < data_num; i++)
        {
            double[] out = bp_image.forwardProp(train_data[num-1][i]);
            int ans = BasisFunc.getMax(out)+1;

            if(ans != num)
                rtn++;
        }

        return rtn/(double)data_num;
    }

    /**
     * Learn once by once, and output every error times
     * @param error expected error
     */
    public void learnAndTest(double error) {
        double last_error = 0d;
        double out_error = 0d;
        while(true){
            last_error = out_error;
            out_error = 0d;

            Learn(1);

            for (int i = 1; i < 10; i++)
                out_error += testData(i);

            out_error = out_error / 9;

            bp_image.adjustRate(out_error, last_error);

            System.out.println("Error: " + new DecimalFormat("##.##").format(out_error*100) + "%\n");
            if(out_error < error)
                break;
        }
    }
}
