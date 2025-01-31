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

    private void run(){
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

                if(mine){
                    me.addPac(pac);
                }else{
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

            // System.err.println("all intputes read");

            map.updatePellets(pellets, me.getPacs());

            me.play();
        }
    }
}