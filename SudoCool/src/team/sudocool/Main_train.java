package team.sudocool;

import team.sudocool.Identifier.Identifier;

import java.text.DecimalFormat;

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
        Identifier iden = new Identifier(8, 21, 0.005);
//        iden.learnAndTest("D:/patterns/", 0.01);

//        for (int i = 0; i < 10; i++)
//        {
//            double ans = iden.testData("D:/patterns/", i);
//            System.out.println(i + ": " + new DecimalFormat("##.##").format(ans*100) + "%");
//        }

        int[][] test1 = {{0,1,1,1,1,1,1,1},
                        {0,1,0,0,0,0,0,0},
                        {0,1,0,0,0,0,0,0},
                        {1,1,1,1,1,1,1,1},
                        {1,0,0,0,0,0,0,1},
                        {0,0,0,0,0,0,0,1},
                        {0,0,0,0,0,0,0,1},
                        {1,1,1,1,1,1,1,1}};

        int [][] test2 = {{0,0,0,0,0,1,1,0},
                        {0,0,0,1,1,1,1,0},
                        {0,0,1,1,0,0,1,0},
                        {0,1,1,0,0,0,1,0},
                        {1,1,0,0,0,0,0,1},
                        {1,0,0,0,0,0,1,0},
                        {1,1,1,1,1,1,1,1},
                        {0,0,0,0,0,0,1,0}};

        int [][] test3 = {{1,1,1,1,1,1,1,1},
                        {0,0,0,0,0,0,1,1},
                        {0,0,0,0,0,1,1,0},
                        {0,0,0,0,1,1,0,0},
                        {0,0,0,1,1,0,0,0},
                        {0,0,0,1,0,0,0,0},
                        {0,0,0,1,0,0,0,0},
                        {0,0,1,1,0,0,0,0}};

        int[][] test = new int[8][8];      //null test

        System.out.println(iden.toDigit(test1));
        System.out.println(iden.toDigit(test2));
        System.out.println(iden.toDigit(test3));
    }
}
