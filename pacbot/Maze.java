import java.util.ArrayList;

public class Maze {
    private int width;
    private int height;
    private boolean map[][];
    private int pellets[][];

    public Maze(int width, int height, ArrayList<String> stringsMap) {
        this.width = width;
        this.height = height;

        map = new boolean[height][width];
        pellets = new int[height][width];

        for (int i = 0; i < stringsMap.size(); i++) {
            String row = stringsMap.get(i);
            for (int j = 0; j < row.length(); j++) {
                map[i][j] = row.charAt(j) == '#';
                pellets[i][j] = map[i][j] ? 0 : 1;
            }
        }
    }

    public ArrayList<Point> getSuperPelletsPos(){
        ArrayList<Point> points = new ArrayList<>();
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                if(pellets[i][j] == 10){
                    points.add(new Point(j, i));
                }
            }
        }
        return points;
    }

    public void updatePellets(ArrayList<Pellet> visiblePellets, ArrayList<Pac> pacs) {
        updateSuperPellets(visiblePellets);

        ArrayList<Point> visibleCells = new ArrayList<>();
        for (Pac pac : pacs) {
            visibleCells.addAll(getVisibleCells(pac.getPos()));
        }

        for (Point point : visibleCells) {
            boolean hasPelletOnPos = false;
            for (Pellet pellet : visiblePellets) {
                if (!point.equals(pellet.getPos())) {
                    hasPelletOnPos = true;
                    break;
                }
            }

            if (!hasPelletOnPos) {
                pellets[point.getY()][point.getX()] = 0;
            }
        }
    }

    public ArrayList<Point> getVisibleCells(Point from) {
        ArrayList<Point> visibleCells = new ArrayList<>();
        visibleCells.add(from);

        for (Direction dir : Direction.directions) {
            for (int dist = 1; true; dist++) {
                Point target = Point.getPointInDirection(from, dir, dist);
                if (!isCellFree(target)) {
                    break;
                }
                visibleCells.add(target);
            }
        }

        return visibleCells;
    }

    public boolean isCellFree(Point pos) {
        if (pos.getX() < 0 || pos.getY() < 0 || pos.getX() >= width || pos.getY() >= height) {
            return false;
        }
        return !map[pos.getY()][pos.getX()];
    }

    public void updateSuperPellets(ArrayList<Pellet> visiblePellets) {
        for (Pellet pellet : visiblePellets) {
            if (pellet.getValue() == 10) {
                pellets[pellet.getPos().getY()][pellet.getPos().getX()] = 10;
            }
        }

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (pellets[i][j] == 10) {
                    boolean stillExists = false;
                    for (Pellet pellet : visiblePellets) {
                        if (pellet.getValue() == 10 && pellet.getPos().equals(new Point(j, i))) {
                            stillExists = true;
                        }
                    }

                    if (!stillExists) {
                        pellets[i][j] = 0;
                    }
                }
            }
        }
    }

    public boolean[][] getObstacles(ArrayList<Point> otherObstacles) {
        boolean[][] mapWithObstacles = Utils.copyGrid(map);
        // System.err.println("Otherobstacles: ");
        for (Point point : otherObstacles) {
            // System.err.println(point);
            mapWithObstacles[point.getY()][point.getX()] = true;
        }

        return mapWithObstacles;
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

        boolean[][] mapWithObstacles = getObstacles(otherObstacles);

        Point closestPellet = new Point(999, 999);
        int dist = 999;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (pellets[i][j] == 1) {
                    Point palletPos = new Point(j, i);
                    int currentDistance = pos.bfsDistance(palletPos, mapWithObstacles);
                    if (currentDistance < dist) {
                        closestPellet = palletPos;
                        dist = currentDistance;
                    }
                }
            }
        }

        if(dist != 999){
            return closestPellet;
        }

        return null;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int p = map[i][j] ? 1 : 0;
                res.append(p + " ");
            }
            res.append("\n");
        }
        return res.toString();
    }
}