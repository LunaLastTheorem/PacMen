public class Utils {
    public static boolean[][] copyGrid(boolean[][] source) {
        boolean[][] res = new boolean[source.length][source[0].length];

        for (int i = 0; i < source.length; i++) {
            for (int j = 0; j < source[0].length; j++) {
                res[i][j] = source[i][j];
            }
        }

        return res;
    }

    public String printBoard(boolean[][] map) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                res.append(map[i][j] + " ");
            }
            res.append("\n");
        }
        return res.toString();
    }
}