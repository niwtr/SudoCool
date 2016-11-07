package team.sudocool.identify;

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
        layer_num[0] = 28*28;
        layer_num[2] = 10;
        layer_num[1] = (int) (Math.sqrt(layer_num[0] + layer_num[2]) + 3);
        BP bp_image = new BP(layer_num, 0.8);
        bp_image.loadWeight();

        ReadData file = new ReadData();
        double[][] in = null;
        try{
            in = file.readFile(0, 8);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assert in != null;

        double[] out = new double[10];
        out[0] = 1;

        for(int i = 0; i < 10; i++)         //max data is 979
        {
            System.out.println("Training data " + i);
            bp_image.trainNet(in[i], out, 0.5);
        }

        bp_image.saveWeight();

        double[] temp = bp_image.forwardProp(in[1]);
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
