import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

enum PacTypes {
    ROCK,
    PAPER,
    SCISSORS;

    PacTypes getOpposite(){
        if(this == ROCK){
            return PAPER;
        } else if(this == PAPER){
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
        if(typeId.equals("ROCK")){
            type = PacTypes.ROCK;
        } else if(typeId.equals("PAPER")){
            type = PacTypes.PAPER;
        } else if(typeId.equals("SCISSORS")){
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

    public PacTypes getType(){
        return type;
    }

    public void play() {
        System.err.println("Plays pac: " + id);

        computeEnemyDistances();
        
        tryAttacking();
        trySpeed();

        move();
    }

    private boolean trySwitch(PacTypes toType){
        if(abilityCD != 0 || played){
            return false;
        }
        doSwitch(toType);
        return true;
    }

    private void doSwitch(PacTypes type){
        System.out.println(" | SWITCH " + id + " " + type.toString());
    }

    private void tryAttacking() {

        if(played){
            return;
        }

        Map.Entry<Pac, Integer> enemyPac = getClosestEnemy();

        if(enemyPac != null){
            System.err.println("Closest enmey " + enemyPac.getKey().getId() + " at dist " + enemyPac.getValue());
        }

        if(enemyPac == null || enemyPac.getValue() > 7){
            return;
        }

        if(this.beats(enemyPac.getKey())){
            System.err.println(id + "Attacking " + enemyPac.getKey().getId());
            moveTo(enemyPac.getKey().getPos(), null);
        }else{
            System.err.println(id + " Swtiching");
            trySwitch(enemyPac.getKey().getType().getOpposite());
        }
    }

    public boolean beats(Pac pac){
        if(type == PacTypes.ROCK){
            return pac.type == PacTypes.SCISSORS;
        }
        if(type == PacTypes.PAPER){
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

        if (played) {
            return;
        }

        ArrayList<Point> otherPacs = new ArrayList<>();

        for (Pac pac : pacMaster.getPacs()) {
            if (pac.getId() == id) {
                continue;
            }

            Point nextPos = pac.getNextPos();
            if (nextPos != null) {
                // System.err.println("my tm next pos is: " + nextPos);
                otherPacs.add(nextPos);
            } else {
                otherPacs.add(pac.getPos());
            }
        }

        for (Pac pac : pacMaster.getEnemy().getPacs()) {
            otherPacs.add(pac.getPos());
        }

        if (nextPos != null) {
            System.err.println("going for super at: " + nextPos);
            BFS.Node path = BFS.getPathBFS(maze.getObstacles(otherPacs), pos, nextPos);
            moveTo(nextPos, path);
            return;
        }

        Point target = maze.getNextPellet(pos, otherPacs);
        if (target != null) {
            System.err.println("Target: " + target.toString());
            BFS.Node path = BFS.getPathBFS(maze.getObstacles(otherPacs), pos, target);

            moveTo(target, path);
        } else {
            System.out.println("something went wrong");
            moveTo(new Point(0, 0), null);
        }
    }

    private void trySpeed() {
        // System.err.println("trying speed");

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
        System.out.println(" | MOVE " + id + " " + nextDest.getX() + " " + nextDest.getY() + " Hamilton " + id + " ");
    }

    private void speed() {
        System.out.println(" | SPEED " + id);
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