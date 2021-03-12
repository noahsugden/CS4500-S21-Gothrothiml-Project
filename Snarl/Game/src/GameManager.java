import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

// Unit tests for our GameManager are pointless at this point, since it is very likely that it will
// change. Once we incorporate networking, we will implement the unit tests.
public class GameManager {

    ArrayList<String> playerUsernames;
    ArrayList<String> adversaryUsernames;
    GameState currentGameState;
    Level l;
    HashMap<Integer,Position> playerIDpositions;
    HashMap<Integer,Position> adversaryIDpositions;
    boolean levelEnd;

    GameManager(Level l) {
        this.l = l;
    }

    //Register between 1 and 4 players. The order of registration determines the order in which they take turns.
    public void registerPlayers() throws IOException {
        ArrayList<String> usernames = new ArrayList<>();

       BufferedReader inPlayers = new BufferedReader(new InputStreamReader(System.in));
       while(inPlayers.readLine() != null) {
           String username = inPlayers.readLine();
           usernames.add(username);
       }

       playerUsernames = usernames;

    }

    //Register adversaries
    public void registerAdversaries() throws IOException {
        ArrayList<String> usernames = new ArrayList<>();

        BufferedReader inPlayers = new BufferedReader(new InputStreamReader(System.in));
        while(inPlayers.readLine() != null) {
            String username = inPlayers.readLine();
            usernames.add(username);
        }


        adversaryUsernames = usernames;
    }

    public void startGame() {
        currentGameState = new GameState(l,playerUsernames.size(), adversaryUsernames.size() );
        playerIDpositions = currentGameState.getPlayerIDPositions();
        adversaryIDpositions = currentGameState.getAdversaryIDPositions();
    }

    public void sendCurrentState() {
        //for each player, send a gamestate that they could see
        //for now, we are sending a list of traversable tiles
        HashMap<String, Position> playerPositionsMap = currentGameState.getPlayerPositionsMap();
        for (Position p: playerPositionsMap.values()) {
            ArrayList<Position> visibleTiles = RuleChecker.calculatePlayerVisibleTiles(p);
            for (int i=0;i<visibleTiles.size();i++) {
                visibleTiles.get(i).print();
            }
        }
    }

    public void requestMove(Position pos, Player player) {
        if (RuleChecker.isValidPlayerMove(pos,player, playerIDpositions)){
            currentGameState.updatePlayerState(player.getId()-10, pos);
        }
    }

    //The method will return different integers for different cases. For example, 0 for key event,1 for locked exit event,
//2 for unlocked exit event, 3 for adversary event, 4 for an empty tile, 5 for another player.
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
