package team.sudocool;

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
        Identify iden = new Identify();
//        iden.Learn("D:/patterns/", 5000);

        int[][] test = {{0,0,0,0,0,0,0,1},      //number 6
                        {0,0,0,0,0,0,1,1},
                        {0,0,0,0,0,1,1,0},
                        {0,0,0,1,1,1,0,0},
                        {0,1,1,0,0,1,0,0},
                        {0,1,0,0,0,1,0,0},
                        {1,0,0,0,0,1,0,0},
                        {1,1,1,1,1,1,0,0}};

        int [][] test1 = {{0,0,1,0,0,0,0,0},    //number 0
                        {0,0,1,0,1,0,0,0},
                        {0,1,0,0,0,0,1,0},
                        {0,1,0,0,0,0,0,1},
                        {0,1,0,0,0,0,0,1},
                        {0,1,0,0,0,1,0,0},
                        {1,0,0,0,0,1,0,0},
                        {1,1,1,1,1,1,0,0}};

        int[][] test2 = new int[8][8];      //null test

        System.out.println(iden.toDigit(test));
    }
}
