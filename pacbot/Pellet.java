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
