import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Grab the pellets as fast as you can!
 **/
class Player {
    Map map;
    PacMaster me = new PacMaster();
    PacMaster enemy = new PacMaster();

    public static void main(String args[]) {
        new Player().run();
    }

    private void run() {
        Scanner in = new Scanner(System.in);
        int width = in.nextInt();
        int height = in.nextInt();
        if (in.hasNextLine()) {
            in.nextLine();
        }
        ArrayList<String> stringMap = new ArrayList<>();

        for (int i = 0; i < height; i++) {
            String row = in.nextLine();
            stringMap.add(row);
        }

        map = new Map(width, height, stringMap);
        me.setMap(map);
        enemy.setMap(map);

        me.setEnemy(enemy);
        enemy.setEnemy(me);

        // game loop
        while (true) {
            me.resetPacs();
            enemy.resetPacs();
            int myScore = in.nextInt();
            int opponentScore = in.nextInt();
            int visiblePacCount = in.nextInt();
            for (int i = 0; i < visiblePacCount; i++) {
                int pacId = in.nextInt();
                boolean mine = in.nextInt() != 0;
                int x = in.nextInt();
                int y = in.nextInt();
                String typeId = in.next();
                int speedTurnsLeft = in.nextInt();
                int abilityCooldown = in.nextInt();

                Pac pac = new Pac(pacId, new Point(x, y));

                if (mine) {
                    me.addPac(pac);
                } else {
                    enemy.addPac(pac);
                }
            }
            map.resetPellets();
            int visiblePelletCount = in.nextInt();
            for (int i = 0; i < visiblePelletCount; i++) {
                int x = in.nextInt();
                int y = in.nextInt();
                int value = in.nextInt();

                map.addPellet(new Point(x, y), value);
            }

            me.play();
        }
    }

    public class BFS {
        static int[][] dir = { { 0, 1 }, { 1, 0 }, { -1, 0 }, { 0, -1 } };

        static class Node {
            int x;
            int y;
            Node parent;

            public Node(int x, int y, Node parent) {
                this.x = x;
                this.y = y;
                this.parent = parent;
            }

            public Node getParent() {
                return this.parent;
            }

            // public Point getNextPoint(){
            // Point p = new Point(x, y);

            // Node parent = this;
            // while(parent.getParent() != null){
            // p = new Point(parent.x, parent.y);
            // parent = parent.getParent();
            // }

            // return p;
            // }

            public String toString() {
                return "x: " + x + " y: " + y;
            }

            public int getPathLength() {
                Node p = this;

                int result = 0;
                while (p.getParent() != null) {
                    p = p.getParent();
                    result++;
                }

                return result;
            }
        }

        public static Node getPathBFS(boolean[][] maze, Point src, Point dst) {
            System.err.println("Searching for path from " + src + " to " + dst);

            if (!isFree(maze, dst.getX(), dst.getY())) {
                return new Node(dst.getX(), dst.getY(), null);
            }

            Queue<Node> q = new LinkedList<>();
            boolean[][] visited = Utils.copyGrid(maze);
            visited[src.getY()][src.getX()] = true;
            q.add(new Node(src.getX(), src.getY(), null));

            while (!q.isEmpty()) {
                Node p = q.poll();

                if (p.x == dst.getX() && p.y == dst.getY()) {
                    return p;
                }

                for (int i = 0; i < dir.length; i++) {
                    int newX = p.x + dir[i][0];
                    int newY = p.y + dir[i][1];
                    if (isFree(maze, newX, newY) && !visited[newY][newX]) {
                        visited[newY][newX] = true;
                        Node nextP = new Node(newX, newY, p);
                        q.add(nextP);
                    }
                }
            }

            return null;
        }

        private static boolean isFree(boolean[][] maze, int x, int y) {
            return (y >= 0 && y < maze.length) && (x >= 0 && x < maze[0].length) && !maze[y][x];
        }
    }

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

            Point closestPellet = new Point(0, 0);
            int dist = 999;

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (pellets[i][j] == 10) {
                        Point palletPos = new Point(j, i);
                        int currentDistance = palletPos.bfsDistance(pos, mapWithObstacles);
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
                        int currentDistance = palletPos.bfsDistance(pos, mapWithObstacles);
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

    public class Pac {
        private int id;
        private Point pos;
        private Map map;
        private PacMaster pacMaster;

        public Pac(int id, Point pos) {
            this.id = id;
            this.pos = pos;
        }

        public Point getPosition() {
            return this.pos;
        }

        public void setMap(Map map) {
            this.map = map;
        }

        public void setPacMaster(PacMaster pacMaster) {
            this.pacMaster = pacMaster;
        }

        public void play() {
            move();
        }

        private void move() {
            ArrayList<Point> otherPacs = new ArrayList<>();

            for (Pac pac : pacMaster.getPacs()) {
                if (pac.getId() != id) {
                    continue;
                }
                otherPacs.add(pac.getPosition());
            }

            for (Pac pac : pacMaster.getEnemy().getPacs()) {
                otherPacs.add(pac.getPosition());
            }

            Point target = map.getNextPellet(pos, otherPacs);
            System.err.println(map.toString());
            moveTo(target);
        }

        private void moveTo(Point point) {
            System.out.print("| MOVE " + id + " " + point.getX() + " " + point.getY() + " ");
        }

        public int getId() {
            return this.id;
        }

        @Override
        public String toString() {
            return "id: " + id + ", " + pos.toString();
        }
    }

    public class PacMaster {
        ArrayList<Pac> pacs = new ArrayList<>();
        Map map;
        private PacMaster enemy;

        public void setMap(Map map) {
            this.map = map;
        }

        public void resetPacs() {
            pacs.clear();
        }

        public ArrayList<Pac> getPacs() {
            return this.pacs;
        }

        public void addPac(Pac pac) {
            pac.setMap(map);
            pac.setPacMaster(this);
            this.pacs.add(pac);
        }

        public void play() {
            for (Pac pac : pacs) {
                pac.play();
            }
            System.out.println();
        }

        public void setEnemy(PacMaster enemy) {
            this.enemy = enemy;
        }

        public PacMaster getEnemy() {
            return this.enemy;
        }
    }

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

        public int bfsDistance(Point target, boolean[][] newMap) {
            return BFS.getPathBFS(newMap, target, this).getPathLength();
        }

        @Override
        public String toString() {
            return "x: " + x + " y: " + y;
        }
    }

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

        public static String printBoard(boolean[][] map) {
            StringBuilder res = new StringBuilder();
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[0].length; j++) {
                    int p = map[i][j] ? 1 : 0;
                    res.append(p + " ");
                }
                res.append("\n");
            }
            return res.toString();
        }
    }

}