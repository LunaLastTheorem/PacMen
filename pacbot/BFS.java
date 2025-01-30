import java.util.LinkedList;
import java.util.Queue;

public class BFS {
    static int[][] dir = {{0,1},{1,0},{-1,0},{0,-1}};

    public static class Node{
        int x;
        int y;
        Node parent;

        public Node(int x, int y, Node parent){
            this.x = x;
            this.y = y;
            this.parent = parent;
        }

        public Node getParent(){return this.parent; }

        public Point getNextPoint(){
            Point p = new Point(x, y);

            Node parent = this;
            while(parent.getParent() != null){
                p = new Point(parent.x, parent.y);
                parent = parent.getParent();
            }

            return p;
        }

        public String toString(){return "x: " + x + " y: " + y;}

        public int getPathLength(){
            Node p = this;

            int result = 0;
            while(p.getParent() != null){
                p = p.getParent();
                result++;
            }

            return result;
        }
    }

    public static Node getPathBFS(boolean[][] maze, Point src, Point dst){
        if(!isFree(maze, dst.getX(), dst.getY())){
            return null;
        }

        Queue<Node> q = new LinkedList<>();
        maze = Utils.copyGrid(maze);
        maze[src.getY()][src.getX()] = true;
        q.add(new Node(src.getX(), src.getY(), null));

        while(!q.isEmpty()){
            Node p = q.poll();

            if(p.x == dst.getX() && p.y == dst.getY()){
                return p;
            }

            for(int i = 0; i < dir.length; i++){
                int newX = p.x + dir[i][0];
                int newY = p.y + dir[i][1];
                if(isFree(maze, newX, newY)){
                    maze[newY][newX] = true;
                    Node nextP = new Node(newX, newY, p);
                    q.add(nextP);
                }
            }
        }

        return null;
    }

    private static boolean isFree(boolean[][] maze, int x, int y){
        return (y >= 0 && y < maze.length) && (x >= 0 && x < maze[0].length) && !maze[y][x];
    }
}
