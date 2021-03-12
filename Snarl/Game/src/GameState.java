

import java.util.ArrayList;
import java.util.HashMap;

public class GameState {
    Level l;
    static ArrayList<Player> players = new ArrayList<>();
    ArrayList<Adversary> adversaries = new ArrayList<>();
    HashMap<Integer, Adversary> adversaryIDs = new HashMap<>();
    HashMap<Integer, Player> playerIDs = new HashMap<>();
    ArrayList<Position> playerPositions=  new ArrayList<>();
    ArrayList<Position> adversaryPositions=  new ArrayList<>();
    HashMap<String, Position> playerPositionsMap = new HashMap<>();
    HashMap<String, Position> zombiePositionsMap = new HashMap<>();
    HashMap<String, Position> ghostPositionsMap = new HashMap<>();
    HashMap<Integer,Position> playerIDPositions = new HashMap<>();
    HashMap<Integer,Position> adversaryIDPositions = new HashMap<>();
    int[][] out;


    boolean exitStatus;
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

        playerPositions = playerPositionArrayList(players);
        adversaryPositions = adversaryPositionArrayList(adversaries);
        exitStatus = false;
    }

    //intermediate game state
    GameState(HashMap<Integer, Position> playerPositonMap, HashMap<Integer,
        Position> adversaryPositionMap, boolean exitStatus, GameState prevGameState) {
        this.l = prevGameState.getL();
        this.players = prevGameState.getPlayers();
        this.adversaries = prevGameState.getAdversaries();
        for (Integer id: playerPositonMap.keySet()) {
            Player curr = players.get(id);
            curr.setPosition(playerPositonMap.get(id));
        }
        for (Integer id:adversaryPositionMap.keySet()) {
            Adversary curr = adversaries.get(id);
            curr.setPosition(adversaryPositionMap.get(id));
        }
        this.exitStatus = exitStatus;
        playerPositions = playerPositionArrayList(players);
        adversaryPositions = adversaryPositionArrayList(adversaries);
    }

    GameState(HashMap<String, Position> playerPositionsMap, HashMap<String, Position> zombiePositions,
        HashMap<String, Position> ghostPositions, boolean exitStatus, Level l) {
        this.l = l;
        this.playerPositionsMap = playerPositionsMap;
        this.zombiePositionsMap = zombiePositions;
        this.ghostPositionsMap = ghostPositions;
        this.exitStatus = exitStatus;
        playerPositions = fromMapToArrayList(playerPositionsMap);
        zombiePositions.putAll(ghostPositions);
        adversaryPositions.addAll(zombiePositions.values());


    }

    public ArrayList<Position> fromMapToArrayList(HashMap<String, Position> hashMap) {
        ArrayList<Position> results = new ArrayList<>();
        for(Position p : hashMap.values()) {
            results.add(p);
        }
        return results;
    }

    public static void ejectPlayer(Player player) {
        players.remove(player);
    }

    public Player getPlayer(int index) {
        if(index >= players.size() || index < 0) {
            throw new IllegalArgumentException("Not a valid index!");
        }
        return this.players.get(index);
    }

    public HashMap<String, Position> getPlayerPositionsMap() {
        return playerPositionsMap;
    }

    public void setExitStatus(boolean exitStatus) {
        this.exitStatus = exitStatus;
    }

    public boolean getPlayerArrived() {
        return playerArrived;
    }

    public ArrayList<Position> getPlayerPositions() {
        return playerPositions;
    }

    public ArrayList<Position> getAdversaryPositions() {
        return adversaryPositions;
    }

    public Level getL() {
        return l;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public ArrayList<Adversary> getAdversaries() {
        return adversaries;
    }

    public HashMap<Integer, Position> getPlayerIDPositions() {
        return playerIDPositions;
    }

    public HashMap<Integer, Position> getAdversaryIDPositions() {
        return adversaryIDPositions;
    }

    public Adversary getAdversary(int index) {
        if(index >= adversaries.size() || index < 0) {
            throw new IllegalArgumentException("Not a valid index!");
        }
        return this.adversaries.get(index);
    }

    public ArrayList<Position> playerPositionArrayList(ArrayList<Player> players) {
        ArrayList<Position> results = new ArrayList<>();
        for (int i =0 ;i<players.size();i++) {
            Position curr = players.get((i)).getP();
            results.add(curr);
        }
        return results;
    }

    public ArrayList<Position> adversaryPositionArrayList(ArrayList<Adversary> adversaries) {
        ArrayList<Position> results = new ArrayList<>();
        for (int i =0 ;i<adversaries.size();i++) {
            Position curr = adversaries.get((i)).getP();
            results.add(curr);
        }
        return results;
    }

    //create the playerList
    public void createPlayers(int playerNumber) {
        for (int i =0; i< playerNumber; i++) {
            Player curr = new Player(i);
            players.add(curr);
            Position p = l.findUnoccupiedLeftmost(curr.getId());
            curr.setPosition(p);
            playerIDs.put(curr.getId(), curr);
            playerIDPositions.put(curr.getId(),p);
        }
    }

    //create the adversaryList
    public void createAdversaries(int adNumber) {
        for (int i =0; i< adNumber; i++) {
            Adversary curr = new Adversary(i);
            adversaries.add(curr);
            Position p = l.findUnoccupiedRightmost(curr.getId());
            curr.setPosition(p);
            adversaryIDs.put(curr.getId(), curr);
            adversaryIDPositions.put(curr.getId(), p);
        }
    }

    //Modify the game state after a player moves
    public void updatePlayerState(int index, Position newP) {
        Player curr = players.get(index);
        curr.setPosition(newP);
        for(int i = 0; i < adversaries.size(); i++) {
            Adversary temp = adversaries.get(i);
            Position position = temp.getP();
            adversaryPositions.add(position);
        }
        //if the player interacts with a key
        if (l.getLevelLayout().get(newP) == 7) {
            exitStatus = true;

        } // if the player interacts with an exit
        else if (l.getLevelLayout().get(newP) == 8) {
            //call the rule checker to determine if the level has ended
            playerArrived = exitStatus;
        }
        if(adversaryPositions.contains(newP)) {
            players.remove(curr);
        }


    }

    //Modify the game state after an adversary moves
    public void updateAdversaryState(int index, Position newP) {
        HashMap<Position, Player> playerHashMap = new HashMap<>();
        playerPositions = new ArrayList<>();
        Adversary curr = adversaries.get(index);
        curr.setPosition(newP);
        for (int i =0; i<players.size(); i++) {
            Player temp  =players.get(i);
            playerHashMap.put(temp.getP(), temp);
          playerPositions.add(temp.getP());
        }
        if(playerHashMap.get(newP) != null) {
            players.remove(playerHashMap.get(newP));
        }
    }

    public void render(int pID, int aID) {
        out = l.get2Darray();
        if(playerIDs.containsKey(pID)) {
            Player curr = playerIDs.get(pID);
            Position temp = curr.getP();
            int x = temp.getx();
            int y = temp.gety();
            out[y][x] = 3;
        }
        else {
            throw new IllegalArgumentException("Invalid player ID given!");
        }
        if(adversaryIDs.containsKey(aID)) {
            Adversary curr = adversaryIDs.get(aID);
            Position temp = curr.getP();
            int x = temp.getx();
            int y = temp.gety();
            out[y][x] = 9;
        }
        else {
            throw new IllegalArgumentException("Invalid adversary ID given!");
        }
        l.print2D(out);

    }

}
