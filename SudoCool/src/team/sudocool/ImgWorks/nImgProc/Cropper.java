package team.sudocool.ImgWorks.nImgProc;

/**
 * Created by Heranort on 16/11/5.
 */
public class Cropper {
    private static int collectOnes(int [] arr){
        int count=0;
        for(int i=0;i<arr.length;i++){
            if(arr[i]==1)count++;
        }return count;
    }
    private static int scan_down(int [][] mat, int threshold){
        int rst=0;
        for(int i=0;i<mat.length-2;i++){
            int
                    count1=collectOnes(mat[i]),
                    count2=collectOnes(mat[i+1]),
                    count3=collectOnes(mat[i+2]);
            if(count1>0 && count2>0 && count3>0){
                rst=i;
                break;
            }
        }
        return rst;
    }
    private static int scan_up(int [][] mat, int threshold){
        int rst=0;
        for(int i=mat.length-1;i>2;i--){
            int
                    count1=collectOnes(mat[i-2]),
                    count2=collectOnes(mat[i-1]),
                    count3=collectOnes(mat[i]);
            if(count1>0 && count2>0 && count3>0){
                rst=i+1;
                break;
            }
        }
        return rst;
    }

    private static int [][] transposeMat(int [][] mat){
        int [][]tmat=new int[mat[0].length][mat.length];
        for(int i=0;i<tmat.length;i++) {
            for (int j = 0; j < tmat[i].length; j++) {
                tmat[i][j] = mat[j][i];
            }
        }return tmat;
    }
    private static int scan_right(int [][] mat, int threshold){
        return scan_down(transposeMat(mat),threshold);
    }

    private static int scan_left(int [][]mat, int threshold){
        return scan_up(transposeMat(mat), threshold);
    }

    //erase the outer contour of the matrix. (_->0)
    private static void pre_crop(int [][]mat){
        for(int j=0;j<mat[0].length;j++){
            mat[0][j]=0;
            mat[mat.length-1][j]=0;
        }
        for(int i=0;i<mat.length;i++){
            mat[i][0]=0;
            mat[i][mat[0].length-1]=0;
        }
    }

    public static int[][] crop(int [][] mat, int threshold){
        pre_crop(mat);
        int
                ub=scan_down(mat, threshold),
                db=scan_up(mat, threshold),
                rb=scan_left(mat, threshold),
                lb=scan_right(mat, threshold);
        int [][] o = new int[db-ub][rb-lb];
        for(int i=0;i<o.length;i++){
            for(int j=0;j<o[i].length;j++){
                o[i][j]=mat[i+ub][j+lb];
            }
        }
        return o;
    }

    public static int [][]nomalize(int [][]mat, int xmin, int ymin){

        int rows=mat.length,cols=mat[0].length;
        int [][]rst=new int [ymin>rows?ymin:rows][xmin>cols?xmin:cols];
        int dx=((int)((xmin-cols)/2)>0)?((int)((xmin-cols)/2)):0,
                dy=((int)((ymin-rows)/2))>0?((int)((ymin-rows)/2)):0;
        for(int y=0;y<rows;y++){
            //平移
            for(int x=0;x<cols;x++){
                rst[y+dy][x+dx]=mat[y][x];
            }
        }
        return rst;
    }

}
