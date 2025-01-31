import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Grab the pellets as fast as you can!
 **/
class Player {
    Maze map;
    PacMaster me = new PacMaster();
    PacMaster enemy = new PacMaster();

    public static void main(String args[]) {
        new Player().run();
    }

    private void run() {
        Scanner in = new Scanner(System.in);
        int width = in.nextInt(); // size of the grid
        int height = in.nextInt(); // top left corner is (x=0, y=0)
        if (in.hasNextLine()) {
            in.nextLine();
        }
        ArrayList<String> stringMap = new ArrayList<>();

        for (int i = 0; i < height; i++) {
            String row = in.nextLine();
            stringMap.add(row);
        }

        map = new Maze(width, height, stringMap);
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

                Pac pac = new Pac(pacId, new Point(x, y), abilityCooldown, mine, typeId);

                if (mine) {
                    me.addPac(pac);
                } else {
                    enemy.addPac(pac);
                }
            }

            ArrayList<Pellet> pellets = new ArrayList<>();
            int visiblePelletCount = in.nextInt();
            for (int i = 0; i < visiblePelletCount; i++) {
                int x = in.nextInt();
                int y = in.nextInt();
                int value = in.nextInt();

                pellets.add(new Pellet(new Point(x, y), value));
            }

            map.updatePellets(pellets, me.getPacs());

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

            public Point getNextPoint() {
                Point p = new Point(x, y);

                Node parent = this;
                while (parent.getParent() != null) {
                    p = new Point(parent.x, parent.y);
                    parent = parent.getParent();
                }

                return p;
            }

            public Point get2ndNextPoint() {
                Point p = new Point(x, y);

                Node parent = this;
                while (parent.getParent() != null && parent.getParent().getParent() != null) {
                    p = new Point(parent.x, parent.y);
                    parent = parent.getParent();
                }

                return p;
            }

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

            int width = maze[0].length;

            if (!isFree(maze, dst.getX(), dst.getY())) {
                return null;
            }

            Queue<Node> q = new LinkedList<>();
            maze = Utils.copyGrid(maze);
            maze[src.getY()][src.getX()] = true;
            q.add(new Node(src.getX(), src.getY(), null));

            while (!q.isEmpty()) {
                Node p = q.remove();

                if (p.x == dst.getX() && p.y == dst.getY()) {
                    return p;
                }

                for (int i = 0; i < dir.length; i++) {
                    int newX = (p.x + dir[i][0] + width) % width;
                    int newY = p.y + dir[i][1];
                    if (isFree(maze, newX, newY)) {
                        maze[newY][newX] = true;
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

    public static class Direction {
        public int Dx, Dy;
        public String name;

        public Direction(int dx, int dy, String name) {
            Dx = dx;
            Dy = dy;
            this.name = name;
        }

        public static Direction[] directions = {
                new Direction(-1, 0, "w"),
                new Direction(1, 0, "e"),
                new Direction(0, -1, "n"),
                new Direction(0, 1, "s"),
        };

        public static Direction getDir(String dir) {
            for (Direction d : directions) {
                if (d.name.equals(dir)) {
                    return d;
                }
            }
            return null;
        }
    }

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

        public ArrayList<Point> getSuperPelletsPos() {
            ArrayList<Point> points = new ArrayList<>();
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (pellets[i][j] == 10) {
                        points.add(new Point(j, i));
                    }
                }
            }
            return points;
        }

        public void updatePellets(ArrayList<Pellet> visiblePellets, ArrayList<Pac> pacs) {
            updateSuperPellets(visiblePellets);

            Set<Point> visibleCells = new HashSet<>();
            for (Pac pac : pacs) {
                visibleCells.addAll(getVisibleCells(pac.getPos()));
            }

            for (Point point : visibleCells) {
                boolean hasPellet = false;
                for (Pellet pellet : visiblePellets) {
                    if (!point.equals(pellet.getPos())) {
                        hasPellet = true;
                        break;
                    }
                }

                if (!hasPellet) {
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

            if (dist != 999) {
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

    enum PacTypes {
        ROCK,
        PAPER,
        SCISSORS;

        PacTypes getOpposite() {
            if (this == ROCK) {
                return PAPER;
            } else if (this == PAPER) {
                return SCISSORS;
            }
            return ROCK;
        }
    }

    public class Pac {
        private int id;
        private Point pos;
        private Maze maze;
        private PacMaster pacMaster;
        private int abilityCD;
        private boolean played = false;
        private Point nextPos = null;
        private boolean isMine;
        private PacTypes type;
        private HashMap<Pac, Integer> enemyPacs = new HashMap<>();

        public Pac(int id, Point pos, int abilityCD, boolean isMine, String typeId) {
            this.id = id;
            this.pos = pos;
            this.abilityCD = abilityCD;
            this.isMine = isMine;
            if (typeId.equals("ROCK")) {
                type = PacTypes.ROCK;
            } else if (typeId.equals("PAPER")) {
                type = PacTypes.PAPER;
            } else if (typeId.equals("SCISSORS")) {
                type = PacTypes.SCISSORS;
            }
        }

        public Point getPos() {
            return this.pos;
        }

        public void setMaze(Maze maze) {
            this.maze = maze;
        }

        public void setPacMaster(PacMaster pacMaster) {
            this.pacMaster = pacMaster;
        }

        public Point getNextPos() {
            return nextPos;
        }

        public void setNextPosition(Point nextPos) {
            this.nextPos = nextPos;
        }

        public boolean isMine() {
            return isMine;
        }

        public PacTypes getType() {
            return type;
        }

        public void play() {
            System.err.println("Plays pac: " + id);

            computeEnemyDistances();

            tryAttacking();
            trySpeed();

            move();
        }

        private boolean trySwitch(PacTypes toType) {
            if (abilityCD != 0 || played) {
                return false;
            }
            doSwitch(toType);
            return true;
        }

        private void doSwitch(PacTypes type) {
            System.out.print("SWITCH " + id + " " + type.toString() + "|");
            played = true;
        }

        private void tryAttacking() {

            if (played) {
                return;
            }

            Map.Entry<Pac, Integer> enemyPac = getClosestEnemy();

            if (enemyPac != null) {
                System.err.println("Closest enmey " + enemyPac.getKey().getId() + " at dist " + enemyPac.getValue());
            }

            if (enemyPac == null || enemyPac.getValue() > 7) {
                return;
            }

            if (this.beats(enemyPac.getKey())) {
                System.err.println(id + "Attacking " + enemyPac.getKey().getId());
                moveTo(enemyPac.getKey().getPos(), null);
            } else {
                System.err.println(id + " Swtiching");
                trySwitch(enemyPac.getKey().getType().getOpposite());
            }
        }

        public boolean beats(Pac pac) {
            if (type == PacTypes.ROCK) {
                return pac.type == PacTypes.SCISSORS;
            }
            if (type == PacTypes.PAPER) {
                return pac.type == PacTypes.ROCK;
            }

            return pac.type == PacTypes.PAPER;
        }

        private void computeEnemyDistances() {
            for (Pac pac : pacMaster.getEnemy().getPacs()) {
                enemyPacs.put(pac, pos.bfsDistance(pac.getPos(), maze.getObstacles(new ArrayList<>())));
            }
        }

        private Map.Entry<Pac, Integer> getClosestEnemy() {
            Map.Entry<Pac, Integer> closestPac = null;
            int minDist = 9999;
            for (Map.Entry<Pac, Integer> pac : enemyPacs.entrySet()) {
                if (closestPac == null || pac.getValue() < minDist) {
                    closestPac = pac;
                    minDist = pac.getValue();
                }
            }
            return closestPac;
        }

        private void move() {
            System.err.println("check 1");
            if (played) {
                return;
            }
            System.err.println("check 2");

            ArrayList<Point> otherPacs = new ArrayList<>();

            System.err.println("check 3");
            for (Pac pac : pacMaster.getPacs()) {
                if (pac.getId() == id)
                    continue;

                System.err.println("check 4");
                Point nextPos = pac.getNextPos();
                if (nextPos != null) {
                    otherPacs.add(nextPos);
                } else {
                    otherPacs.add(pac.getPos());
                }
            }

            System.err.println("check 5");
            for (Pac pac : pacMaster.getEnemy().getPacs()) {
                otherPacs.add(pac.getPos());
            }

            System.err.println("check 6");
            Point nextPos = this.nextPos; // Ensure nextPos is properly assigned

            if (nextPos != null) {
                System.err.println("going for super at: " + nextPos);
                played = true;
                moveTo(nextPos, null);
                return;
            }

            System.err.println("check 7");
            Point target = maze.getNextPellet(pos, otherPacs);

            if (target != null) {
                System.err.println("check 8");
                System.err.println("Target: " + target);

                moveTo(target, null);
            } else {
                System.err.println("something went wrong");
                moveTo(new Point(0, 0), null);
            }
        }

        private void trySpeed() {
            if (abilityCD != 0 || played) {
                return;
            }
            speed();
        }

        private void moveTo(Point point, BFS.Node path) {
            Point nextDest = point;
            if (path != null) {
                System.err.println("Goint from: " + pos + " to target " + point + " through: " + path.getNextPoint());
                nextPos = path.getNextPoint();
                nextDest = path.get2ndNextPoint();
            }

            played = true;
            System.out.print("MOVE " + id + " " + nextDest.getX() + " " + nextDest.getY() + "|");
        }

        private void speed() {
            System.out.print("SPEED " + id + "|");
            played = true;
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
        static int[][] dir = { { 0, 1 }, { 1, 0 }, { -1, 0 }, { 0, -1 } };

        Maze map;
        private PacMaster enemy;

        public void setMap(Maze map) {
            this.map = map;
        }

        public void resetPacs() {
            pacs.clear();
        }

        public ArrayList<Pac> getPacs() {
            return pacs;
        }

        public void addPac(Pac pac) {
            pac.setMaze(map);
            pac.setPacMaster(this);
            this.pacs.add(pac);
        }

        public void play() {
            findClosestPacsToSuperPellets();

            for (Pac pac : pacs) {
                pac.play();
            }
            System.out.println();
        }

        private void findClosestPacsToSuperPellets() {
            ArrayList<Point> pelletsPos = map.getSuperPelletsPos();
            ArrayList<Pac> allPacs = new ArrayList<>();
            allPacs.addAll(pacs);
            allPacs.addAll(enemy.getPacs());

            for (Point pelletPos : pelletsPos) {

                Pac closesPac = null;
                int minDist = 9999;
                for (Pac pac : allPacs) {

                    if (pac.getNextPos() != null) {
                        continue;
                    }

                    int currDist = pac.getPos().bfsDistance(pelletPos, map.getObstacles(new ArrayList<>()));
                    if (closesPac == null || currDist < minDist) {
                        closesPac = pac;
                        minDist = currDist;
                    }
                }

                if (closesPac != null && closesPac.isMine()) {
                    closesPac.setNextPosition(pelletPos);
                }
            }
        }

        public void setEnemy(PacMaster enemy) {
            this.enemy = enemy;
        }

        public PacMaster getEnemy() {
            return this.enemy;
        }
    }

    public class Pellet {
        private Point pos;
        private int value;

        public Pellet(Point pos, int value) {
            this.pos = pos;
        }

        public Point getPos() {
            return pos;
        }

        public void setPos(Point pos) {
            this.pos = pos;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

    }

    public static class Point {
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
            if (node != null) {
                return node.getPathLength();
            }
            return 999999;
        }

        public static Point getPointInDirection(Point point, Direction dir, int dist) {
            return new Point(point.getX() + (dir.Dx * dist), point.getY() + (dir.Dy * dist));
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
}