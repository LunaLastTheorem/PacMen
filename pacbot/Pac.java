import java.util.ArrayList;

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
        moveTo(target);
    }

    private void moveTo(Point point) {
        System.out.print("| MOVE " + id + " " + point.getX() + " " + point.getY() + " ");
    }

    public int getId() {
        return this.id;
    }
}