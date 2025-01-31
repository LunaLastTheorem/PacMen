import java.util.ArrayList;

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