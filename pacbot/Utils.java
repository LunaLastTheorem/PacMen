public class Utils {
    public static boolean[][] copyGrid(boolean[][] source){
        boolean[][] res = new boolean[source.length][source[0].length];

        for(int i = 0; i < source.length; i++){
            for(int j = 0; j < source[0].length; j++){
                res[i][j] = source[i][j];
            }
        }

        return res;
    }
}
