package team.sudocool.identify;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;

/**
 * @author L.Laddie
 * @version 1.0
 * @since 2016-11-7
 */
public class ReadImage {

    /**
     * Read a image to matrix
     * @return int[][]
     */
    public int[][] getImagePixel(String image_path) throws Exception{
        File file = new File(image_path);
        if(!file.exists()) {
            return null;
        }

        BufferedImage bufImg = null;

        bufImg = ImageIO.read(file);

        int height = bufImg.getHeight();
        int width = bufImg.getWidth();
        int minx = bufImg.getMinX();
        int miny = bufImg.getMinY();
        int[][] ans = new int[height][width];
        int[] rgb = new int[3];

        assert bufImg.getType() == BufferedImage.TYPE_BYTE_GRAY;       //gray picture

        WritableRaster raster = bufImg.getRaster();

        for (int i = minx; i < width; i++)
        {
            for (int j = miny; j < height; j++) {

//                int pixel = bufImg.getRGB(i, j);
//                rgb[0] = (pixel & 0xff0000) >> 16;
//                rgb[1] = (pixel & 0xff00) >> 8;
//                rgb[2] = (pixel & 0xff);
//                ans[j][i] = (int)(rgb[0]*0.299 + rgb[1]*0.587 + rgb[2]*0.114);

                Object pixel = raster.getDataElements(i, j, null);
                ans[j][i] = ((byte[]) pixel)[0] & 0xff;
            }
        }

        return ans;
    }
}