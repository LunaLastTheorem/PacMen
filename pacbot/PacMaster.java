import java.util.ArrayList;

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