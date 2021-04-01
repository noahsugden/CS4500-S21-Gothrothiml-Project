

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import javax.print.attribute.HashDocAttributeSet;

/**
 * This class represents a game state.
 *   - representation of the game’s current state and its rules
 *   - Includes a level
 *   - Enemy and player placement
 *   - Object placement and status of the exit
 *   - Players that have exited vs. ejected
 *
 * The current fields will be refactored in the future when we get rid of some of the hashmaps.
 * So, we will add field comments later.
 */
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
    HashMap<String, Position> adversaryPositionsMap = new HashMap<>();
    HashMap<String, Position> objectPositionsMap = new HashMap<>();
    int[][] out;


    boolean exitStatus;
    boolean playerArrived;




    /**
     * Constructor that represents an initial game state.
     * @param l represents the current level.
     * @param playerNumber represents the number of players.
     * @param adversaryNumber represents the number of adversaries.
     */
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


    /**
     * Constructor that represents an intermediate game state.
     * @param playerPositonMap represents a mapping of player ID's to their positions
     * @param adversaryPositionMap represents a mapping of adversary ID's to their positions
     * @param exitStatus represents whether the exit is locked or not
     * @param prevGameState represents the previous game state
     */
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

    /**
     * Constructor that represents a game state with zombies and ghosts.
     * @param playerPositionsMap represents a mapping of player ID's to their positions
     * @param zombiePositions represents a mapping of zombie ID's to their positions
     * @param ghostPositions represents a mapping of ghost ID's to their positions
     * @param exitStatus represents whether the exit is locked or not
     * @param l represents the current level
     */
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



    GameState(Level l, HashMap<String, Position> playerPositionsMap,
        HashMap<String,Position> adversaryPositionsMap) {
        this.l = l;
        this.playerPositionsMap = playerPositionsMap;
        this.zombiePositionsMap = adversaryPositionsMap;
        adversaryPositions.addAll(zombiePositionsMap.values());
        this.exitStatus = false;

    }

    public GameState(HashMap<String, Position> playerPositionsMap,
        HashMap<String, Position> adversaryPositionsMap, Level l) {
        this.playerPositionsMap = playerPositionsMap;
        this.adversaryPositionsMap = adversaryPositionsMap;
        this.l = l;
        initializeObjectsMap();
    }


    public GameState(HashMap<Integer,Position> playerIDpositions, HashMap<Integer,Position>
            adversaryIDpositions, Level level, Integer zombieNumber) {
        this.playerPositionsMap = transformHashMap(playerIDpositions);
        this.adversaryPositionsMap = transformHashMap(adversaryIDpositions);
        this.l = level;
        initializeObjectsMap();
    }

    public HashMap<String, Position> getAdversaryPositionsMap() {
        return adversaryPositionsMap;
    }

    public HashMap<String, Position> getObjectPositionsMap() {
        return objectPositionsMap;
    }

    /**
     * Converts a Hashmap of String to Position into an ArrayList of Positions.
     * @param hashMap represents a mapping of actor names to their positions.
     * @return an ArrayList of Positions
     */
    public ArrayList<Position> fromMapToArrayList(HashMap<String, Position> hashMap) {
        ArrayList<Position> results = new ArrayList<>();
        for(Position p : hashMap.values()) {
            results.add(p);
        }
        return results;
    }

    public HashMap<String, Position> transformHashMap(HashMap<Integer, Position> intMap) {
        HashMap<String, Position> result = new HashMap<>();
        for (Integer i: intMap.keySet()) {
            result.put(i.toString(), intMap.get(i));
        }
        return result;
    }

    public boolean getExitStatus() {
        return exitStatus;
    }

    /**
     * Removes a player from the list of players in the level when they are killed in the game.
     * @param player represents the player who was killed
     */
    public static void ejectPlayer(Player player) {
        players.remove(player);
    }

    /**
     * Gets the player at the given index (index corresponds to the order they are registered).
     * This method is only used for testing purposes.
     * @param index represents the player's order in the game
     * @return the Player at the given index
     */
    public Player getPlayer(int index) {
        if(index >= players.size() || index < 0) {
            throw new IllegalArgumentException("Not a valid index!");
        }
        return this.players.get(index);
    }

    /**
     * Gets the Hashmap of Player names to their positions.
     * @return the Hashmap of Player names to their positions
     */
    public HashMap<String, Position> getPlayerPositionsMap() {
        return playerPositionsMap;
    }

    /**
     * Sets the current exit status.
     * @param exitStatus represents the current exit status.
     */
    public void setExitStatus(boolean exitStatus) {
        this.exitStatus = exitStatus;
    }

    /**
     * Gets the boolean representing whether a player has arrived at the exit or not.
     * @return boolean representing whether a player has arrived at the exit or not
     */
    public boolean getPlayerArrived() {
        return playerArrived;
    }

    /**
     * Gets the list of player positions.
     * @return an ArrayList of the players' positions
     */
    public ArrayList<Position> getPlayerPositions() {
        return playerPositions;
    }

    /**
     * Gets the list of adversary positions.
     * @return an ArrayList of the adversaries' positions
     */
    public ArrayList<Position> getAdversaryPositions() {
        return adversaryPositions;
    }

    /**
     * Gets the current level.
     * @return the current level
     */
    public Level getL() {
        return l;
    }

    /**
     * Gets the list of active players (those who haven't been removed).
     * @return an ArrayList of the active players
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * Gets the list of adversaries.
     * @return an ArrayList of the adversaries
     */
    public ArrayList<Adversary> getAdversaries() {
        return adversaries;
    }

    /**
     * Gets a Hashmap of Player IDs to their positions.
     * @return a Hashmap of Player IDs to their positions
     */
    public HashMap<Integer, Position> getPlayerIDPositions() {
        return playerIDPositions;
    }

    /**
     * Gets a Hashmap of Adversary IDs to their positions.
     * @return a Hashmap of Adversary IDs to their positions
     */
    public HashMap<Integer, Position> getAdversaryIDPositions() {
        return adversaryIDPositions;
    }

    /**
     * Gets the Adversary at the given index (where they are in the ArrayList).
     * This method is only used for testing purposes.
     * @param index
     * @return
     */
    public Adversary getAdversary(int index) {
        if(index >= adversaries.size() || index < 0) {
            throw new IllegalArgumentException("Not a valid index!");
        }
        return this.adversaries.get(index);
    }

    public HashMap<String, Position> getZombiePositionsMap() {
        return zombiePositionsMap;
    }

    /**
     * Generates an ArrayList of Player positions.
     * @param players represents an ArrayList of the active players
     * @return an ArrayList of the Positions of the active players
     */
    public ArrayList<Position> playerPositionArrayList(ArrayList<Player> players) {
        ArrayList<Position> results = new ArrayList<>();
        for (int i =0 ;i<players.size();i++) {
            Position curr = players.get((i)).getP();
            results.add(curr);
        }
        return results;
    }

    /**
     * Generates an ArrayList of Adversary positions.
     * @param adversaries represents an ArrayList of the adversaries
     * @return an ArrayList of the Positions of the adversaries
     */
    public ArrayList<Position> adversaryPositionArrayList(ArrayList<Adversary> adversaries) {
        ArrayList<Position> results = new ArrayList<>();
        for (int i =0 ;i<adversaries.size();i++) {
            Position curr = adversaries.get((i)).getP();
            results.add(curr);
        }
        return results;
    }


    /**
     * Creates a list of X Players given a specific number X.
     * @param playerNumber represents the number of players to create
     */
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

    /**
     * Creates a list of X Adversaries given a specific number X.
     * @param adNumber represents the number of adversaries to create
     */
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

    /**
     * Updates/modifies the current game state after a player moves.
     * @param index represents the index of the next player
     * @param newP represents the position that the next player is trying to move to
     */
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
            objectPositionsMap.remove("key");

        } // if the player interacts with an exit
        else if (l.getLevelLayout().get(newP) == 8) {
            //call the rule checker to determine if the level has ended
            playerArrived = exitStatus;

        }
        if(adversaryPositions.contains(newP)) {
            players.remove(curr);
        }
    }

    public void updatePlayerState(String name, int result, Position move){
        //when player meets an adversary
        if (result ==3 || result ==2) {
            this.playerPositionsMap.remove(name);
        } //when the player picks a key
        else if (result ==0) {
            this.exitStatus = true;
            this.playerPositionsMap.remove(name);
            this.playerPositionsMap.put(name, move);
            objectPositionsMap.remove("key");
        } else {
            this.playerPositionsMap.remove(name);
            this.playerPositionsMap.put(name, move);
        }
    }


    //Modify the game state after an adversary moves

    /**
     * Updates/modifies the current game state after an adversary moves.
     * @param index represents the index of the next adversary
     * @param newP represents the position that the next adversary is trying to move to
     */
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

    public void updateAdversaryMap(String s, Position p) {
        adversaryPositionsMap.remove(s);
        adversaryPositionsMap.put(s, p);
        if (playerPositionsMap.containsValue(p)) {
            playerPositionsMap.remove(String.valueOf(0));
        }
    }

    /**
     * Renders the current game state with one player and one adversary using their IDs.
     * @param pID represents the Player ID
     * @param aID represents the Adversary ID
     */
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

    public void initializeObjectsMap() {
        HashMap<Position, Integer> levelLayout = l.getLevelLayout();
        for(Entry<Position, Integer> e : levelLayout.entrySet()) {
            if(levelLayout.get(e.getKey()) == 7) {
                objectPositionsMap.put("key", e.getKey());
            }
            if(levelLayout.get(e.getKey()) == 8) {
                objectPositionsMap.put("exit", e.getKey());
            }
        }
    }

}
