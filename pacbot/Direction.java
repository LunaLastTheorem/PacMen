public class Direction {
    public int Dx, Dy;
    public String name;

    public Direction(int dx, int dy, String name){
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

    public static Direction getDir(String dir){
        for(Direction d : directions){
            if(d.name.equals(dir)){
                return d;
            }
        }
        return null;
    }
}
