import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

// Unit tests for our GameManager are pointless at this point, since it is very likely that it will
// change. Once we incorporate networking, we will implement the unit tests.

/**
 * The game manager needs to have components for the following
 *
 * - the level, which in turn needs to represent rooms and/or hallways
 * - a random level generator that produces a level from a random seed
 * - some idea of the valid non-entity objects in this game
 * - representation of the game’s current state and its rules
 */
public class GameManager {

    ArrayList<String> playerUsernames;
    ArrayList<String> adversaryUsernames;
    GameState currentGameState;
    Level l;
    HashMap<Integer,Position> playerIDpositions;
    HashMap<Integer,Position> adversaryIDpositions;
    boolean levelEnd;
    int turnNumber;
    HashMap<Position, Integer> levelLayout;
    RuleChecker ruleChecker;

    /**
     * This constructor takes in the current level of the game.
      */
    GameManager(Level l) {
        this.l = l;
    }

    /**
     * This constructor is for testing purposes.
     */
    GameManager(Level l, HashMap<String,Position> adversaryPositions, HashMap<String,
        Position> playerOrigins) {
        this.l = l;
        this.currentGameState = new GameState(l, playerOrigins, adversaryPositions);
        this.turnNumber = 0;
        levelLayout = l.getLevelLayout();
        this.ruleChecker = new RuleChecker(l);

    }

    public GameState getCurrentGameState(){
        return this.currentGameState;
    }

    /**
     * Register between 1 and 4 players. The order of registration determines the order in which they take turns.
     * @throws IOException when the input is invalid
     */
    public void registerPlayers() throws IOException {
        ArrayList<String> usernames = new ArrayList<>();

       BufferedReader inPlayers = new BufferedReader(new InputStreamReader(System.in));
       while(inPlayers.readLine() != null) {
           String username = inPlayers.readLine();
           usernames.add(username);
       }
       if (usernames.size() > 4 || usernames.size() < 1) {
           throw new IOException("Cannot register this number of players.");
       }
       playerUsernames = usernames;

    }

    /**
     * Register adversaries with their usernames.
     * @throws IOException when the input is invalid
     */
    public void registerAdversaries() throws IOException {
        ArrayList<String> usernames = new ArrayList<>();

        BufferedReader inPlayers = new BufferedReader(new InputStreamReader(System.in));
        while(inPlayers.readLine() != null) {
            String username = inPlayers.readLine();
            usernames.add(username);
        }

        adversaryUsernames = usernames;
    }

    /**
     * Starts the game. Updates the current game state, player positions, and adversary positions.
     */
    public void startGame() {
        currentGameState = new GameState(l,playerUsernames.size(), adversaryUsernames.size() );
        playerIDpositions = currentGameState.getPlayerIDPositions();
        adversaryIDpositions = currentGameState.getAdversaryIDPositions();
    }

    /**
     * Sends a modified version of the current game state to each player.
     * For now, we are sending a list of traversable tiles.
     */
    public void sendCurrentState() {

        HashMap<String, Position> playerPositionsMap = currentGameState.getPlayerPositionsMap();
        for (Position p: playerPositionsMap.values()) {
            ArrayList<Position> visibleTiles = RuleChecker.calculatePlayerVisibleTiles(p);
            for (int i=0;i<visibleTiles.size();i++) {
                visibleTiles.get(i).print();
            }
        }
    }

    /**
     * Receives a move request from a player, and updates that player's position if the move is valid.
     * @param pos represents the position the player is trying to move to
     * @param player represents the player that is trying to move
     */
    public void receiveMove(Position pos, Player player) {
        if (RuleChecker.isValidPlayerMove(pos,player, playerIDpositions)){
            currentGameState.updatePlayerState(player.getId()-10, pos);
        }
    }

    /**
     * Requests a move from the next player.
     * This function will be implemented once networking is incorporated into our design.
     * @param player represents the player whose turn it is to move
     * @return a position representing where the next player wants to move
     */
    public Position requestMove(Player player) {
        String nextTurn = "It's your turn to move.";
        return new Position(-1, -1);
    }



    /**
     * Determines the interaction result.
     * @param player represents the player that is interacting
     */
    public void interact(Player player) {
        int result = RuleChecker.determinePlayerInteraction(player,
                adversaryIDpositions, playerIDpositions, currentGameState.getL().getExitStatus());
        if (result ==0) {
            currentGameState.setExitStatus(true);
        } else if (result ==2) {
            this.levelEnd=true;
        } else if(result ==3) {
            GameState.ejectPlayer(player);
        }

    }

    /**
     * Gets visible area to the Player in the given Position
     * @param pos Player position
     * @return int[][] representing visible area
     */
    public int[][] getVisibleArea(Position pos) {
        int[][] visibleArea = new int[5][5];
        int posX = pos.getx();
        int posY = pos.gety();
        for(int i = posX - 2; i < posX + 3; i++){
            for(int j = posY - 2; j < posY + 3; j++) {
                if(i > 0 && j > 0 ) {
                    if (levelLayout.containsKey((new Position(i, j)))) {
                        visibleArea[i-posX+2][j-posY+2] = levelLayout.get(new Position(i, j));
                    } else {
                        visibleArea[i-posX+2][j-posY+2] =0;
                    }
                }
            }
        }
        return visibleArea;
    }

    public HashMap<String, Position> getVisibleObjects(Position pos) {
        boolean isExitOpen = currentGameState.getExitStatus();
        HashMap<String, Position> result = new HashMap<>();
        int posX = pos.getx();
        int posY = pos.gety();
        Position exit = l.getExit();
        if(!isExitOpen) {
            Position key = l.getKey();
            int keyX = key.getx();
            int keyY = key.gety();
            if(keyX >= posX - 2 && keyX <= posX + 2 && keyY >= posY - 2 && keyY <= posY + 2) {
                result.put("Key", key);
            }
        }
        int exitX = exit.getx();
        int exitY = exit.gety();
        if(exitX >= posX - 2 && exitX <= posX + 2 && exitY >= posY - 2 && exitY <= posY + 2) {
            result.put("Exit", exit);
        }

        return result;

    }

    public HashMap<String, Position> getVisibleActors(Position pos) {
        ArrayList<Position> playerPos = new ArrayList<>();
        HashMap<String, Position> adversaryPosMap = currentGameState.getZombiePositionsMap();
        HashMap<String, Position> playerPosMap = currentGameState.getPlayerPositionsMap();
        ArrayList<Position> adversaryPos = new ArrayList<>();
        playerPos.addAll(playerPosMap.values());
        adversaryPos.addAll(adversaryPosMap.values());
        HashMap<String, Position> result = new HashMap<>();
        int posX = pos.getx();
        int posY = pos.gety();
        //Adds visible players from given Position
        for(int i = 0; i < playerPos.size(); i++) {
            int currX = playerPos.get(i).getx();
            int currY = playerPos.get(i).gety();
            if(currX >= posX - 2 && currX <= posX + 2 && currY >= posY - 2 && currY <= posY + 2) {
                String name = "";
                for(Entry<String, Position> e: playerPosMap.entrySet()) {
                    if(playerPos.get(i).equals(e.getValue())) {
                        name = e.getKey();
                    }
                }
                result.put(name, playerPos.get(i));
            }
        }
        //Adds visible adversaries from given Position
        for(int i = 0; i < adversaryPos.size(); i++) {
            int currX = adversaryPos.get(i).getx();
            int currY = adversaryPos.get(i).gety();
            if(currX >= posX - 2 && currX <= posX + 2 && currY >= posY - 2 && currY <= posY + 2) {
                String name = "";
                for(Entry<String, Position> e: adversaryPosMap.entrySet()) {
                    if(adversaryPos.get(i).equals(e.getValue())) {
                        name = e.getKey();
                    }
                }
                result.put(name, adversaryPos.get(i));
            }
        }
        return result;
    }

    public boolean determineValidMove(String name, Position move) {
        HashMap<String, Position> playerPosMap = currentGameState.getPlayerPositionsMap();
        Position current = playerPosMap.get(name);
        return ruleChecker.isValidPlayerMoveTest(current, move, playerPosMap);

    }

    public int interactAfterMove(String name, Position curr){
        HashMap<String, Position> playerPosMap = currentGameState.getPlayerPositionsMap();
        ArrayList<Position> adversaryPositions = currentGameState.getAdversaryPositions();
        boolean exitStatus = currentGameState.getExitStatus();
        int result = RuleChecker.determinePlayerInteractionTest(curr, adversaryPositions, playerPosMap, exitStatus);
        this.currentGameState.updatePlayerState(name, result,curr);
        return result;
    }






}
