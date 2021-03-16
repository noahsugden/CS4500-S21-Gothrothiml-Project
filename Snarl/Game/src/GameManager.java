import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

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

    /**
     * This constructor takes in the current level of the game.
      */
    GameManager(Level l) {
        this.l = l;
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






}
