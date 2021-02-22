import java.util.ArrayList;
import java.util.HashMap;

public class GameState {
    Level l;
    ArrayList<Player> players = new ArrayList<>();
    ArrayList<Adversary> adversaries = new ArrayList<>();
    ArrayList<Integer> adversaryIDs = new ArrayList<>();
    ArrayList<Position> playerPositions=  new ArrayList<>();
    boolean playerArrived;


    //initial game state
    GameState(Level l, int playerNumber, int adversaryNumber) {
        if (playerNumber >4 || playerNumber <1) {
            throw new IllegalArgumentException("Invalid player number");
        }
        if (adversaryNumber <0) {
            throw new IllegalArgumentException("Invalid adversary number");
        }
        this.l = l;
        createPlayers(playerNumber);
        createAdversaries(adversaryNumber);

    }

    //intermidate game state
    GameState(HashMap<Integer, Position> playerPositons, HashMap<Integer, Position> adversaryPositions, boolean exitStatus) {
        for (Integer id: playerPositons.keySet()) {
            l.UpdateLevel(playerPositons.get(id), id);
        }
        for (Integer id: adversaryPositions.keySet()) {
            l.UpdateLevel(adversaryPositions.get(id),id);
        }
        l.setExitStatus(exitStatus);
    }


    //create the playerList
    public void createPlayers(int playerNumber) {
        for (int i =0; i< playerNumber; i++) {
            Player curr = new Player(i);
            players.add(curr);
            l.findUnoccupiedLeftmost(curr.getId());
        }
    }

    //create the adversaryList
    public void createAdversaries(int adNumber) {
        for (int i =0; i< adNumber; i++) {
            Adversary curr = new Adversary(i);
            adversaries.add(curr);
            l.findUnoccupiedRightmost(curr.getId());
            adversaryIDs.add(curr.getId());
        }
    }

    //Modify the game state after a player moves
    public void updatePlayerState(int id, Position newP) {
        Player curr = players.get(id -10);
        curr.setPosition(newP);
        //if the player interacts with a key
        if (l.getLevelLayout().get(newP) == 7) {
            l.setExitStatus(true);

        } // if the player interacts with an exit
        else if (l.getLevelLayout().get(newP) == 8) {
            //call the rule checker to determine if the level has ended
            playerArrived = l.getExitStatus();

            } else if (adversaryIDs.contains(l.getLevelLayout().get(newP))) {
            //kill the player
            players.remove(curr);
        }


    }

    //Modify the game state after an adversary moves
    public void updateAdversaryState(int id, Position newP) {
        playerPositions = new ArrayList<>();
        Adversary curr = adversaries.get(100-id);
        curr.setPosition(newP);
        for (int i =0;i<players.size();i++) {
            Player temp  =players.get(i);
          playerPositions.add(temp.getP());

        }
        if (playerPositions.contains(newP)) {
            //kill the player
            players.remove(curr);
        }
    }

}
