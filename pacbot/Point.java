public class Point {
    private int x;
    private int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int distance(Point target) {
        return Math.abs(this.x - target.getX()) + Math.abs(this.y - target.getY());
    }

    public int bfsDistance(Point target, boolean[][] map) {
        BFS.Node node = BFS.getPathBFS(map, this, target);
        if(node != null){
            return node.getPathLength();
        }
        return 999999;
    }

    public static Point getPointInDirection(Point point, Direction dir, int dist){
        return new Point(point.getX() + (dir.Dx  * dist), point.getY() + (dir.Dy * dist));
    }

    @Override
    public String toString() {
        return "x: " + x + " y: " + y;
    }

    @Override
    public boolean equals(Object obj) {
        Point point = (Point) obj;
        return x == point.getX() && y == point.getY();
    }
}