import java.util.ArrayList;

public class Map {
    private int width;
    private int height;
    private boolean map[][];
    private int pellets[][];

    public Map(int width, int height, ArrayList<String> stringsMap) {
        this.width = width;
        this.height = height;

        map = new boolean[height][width];
        pellets = new int[height][width];

        for (int i = 0; i < stringsMap.size(); i++) {
            String row = stringsMap.get(i);
            for (int j = 0; j < row.length(); j++) {
                map[i][j] = row.charAt(j) == '#';
            }
        }
    }

    public void resetPellets() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                pellets[i][j] = 0;
            }
        }
    }

    public void addPellet(Point pos, int value) {
        pellets[pos.getY()][pos.getX()] = value;
    }

    public Point getNextPellet(Point pos, ArrayList<Point> otherObstacles) {

        boolean[][] mapWithObstacles = Utils.copyGrid(map);
        for (Point point : otherObstacles) {
            mapWithObstacles[point.getY()][point.getX()] = true;
        }

        Point closestPellet = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
        int dist = 999;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (pellets[i][j] == 10) {
                    Point palletPos = new Point(j, i);
                    int currentDistance = palletPos.bfsDistance(pos, map);
                    if (currentDistance < dist) {
                        closestPellet = palletPos;
                        dist = currentDistance;
                    }
                }
            }
        }

        if (dist != 999) {
            return closestPellet;
        }

       for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
            if (pellets[i][j] > 0) {
                Point palletPos = new Point(j, i);
                int currentDistance = palletPos.bfsDistance(pos, map);
                if (currentDistance < dist) {
                    closestPellet = palletPos;
                    dist = currentDistance;
                }
            }
        }
    }
        return closestPellet;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                int p = map[i][j] ? 1: 0;
                res.append(p + " ");
            }
            res.append("\n");
        }
        return res.toString();
    }
}