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
        String p_path = "D:/t10k-images/";

        ReadImage readImage = new ReadImage();

        int[][] image = null;

        try {
            image = readImage.getImagePixel(p_path + "0_1.bmp");
        } catch (Exception e) {
            e.printStackTrace();
        }

        assert image != null;

        for (int[] anImage : image) {
            for (int ananImage : anImage) {
                System.out.print(ananImage + " ");
            }
            System.out.println();
        }
    }
}
