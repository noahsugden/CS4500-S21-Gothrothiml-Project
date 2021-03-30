import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;

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


    ArrayList<String> playerUsernames = new ArrayList<>();
    ArrayList<String> adversaryUsernames;
    ArrayList<String> expelledPlayers = new ArrayList<>();
    GameState currentGameState;
    Level l;
    HashMap<Integer, Position> playerIDpositions;
    HashMap<Integer, Position> adversaryIDpositions;
    static HashMap<String, Integer> playerExitCount;
    static HashMap<String, Integer> playerKeyCount;
    boolean levelEnd;
    int turnNumber;
    HashMap<Position, Integer> levelLayout;
    RuleChecker ruleChecker;
    Integer zombieNumber;
    Integer ghostNumber;
    ArrayList<Zombie> zombies = new ArrayList<>();
    ArrayList<Ghost> ghosts = new ArrayList<>();
    ArrayList<Player> players = new ArrayList<>();
    Integer currentLevelIndex;

    static String fileName = "snarl.levels";
    static Integer playerNumber =1;
    static Integer startLevel =1;
    static boolean observe = false;
    static ArrayList<Level> levels = new ArrayList<>();
    static Integer natural;
    /**
     * This constructor takes in the current level of the game.
     */
    GameManager(Level l) {
        this.l = l;
    }

    GameManager(Integer currentLevelIndex) {
        this.zombieNumber = currentLevelIndex/2 +1;
        this.ghostNumber = (currentLevelIndex-1)/2;
        for (int i=0;i<zombieNumber;i++) {
            Zombie temp = new Zombie(playerNumber+i);
            zombies.add(temp);
        }
        for (int i=0;i<ghostNumber;i++) {
            Ghost temp = new Ghost(playerNumber+zombieNumber+i);
            ghosts.add(temp);
        }
        for (int i=0;i<playerNumber;i++) {
            Player temp = new Player(i);
            players.add(temp);
        }
        generateInitialPositions(currentLevelIndex);
        this.currentLevelIndex = currentLevelIndex;
        RuleChecker ruleChecker = new RuleChecker(levels.get(currentLevelIndex));
    }

    /**
     * This constructor is for testing purposes.
     */
    GameManager(Level l, HashMap<String, Position> adversaryPositions, HashMap<String,
        Position> playerOrigins) {
        this.l = l;
        this.currentGameState = new GameState(l, playerOrigins, adversaryPositions);
        this.turnNumber = 0;
        levelLayout = l.getLevelLayout();
        this.ruleChecker = new RuleChecker(l);

    }

    public GameState getCurrentGameState() {
        return this.currentGameState;
    }

    /**
     * Register between 1 and 4 players. The order of registration determines the order in which they
     * take turns.
     *
     * @throws IOException when the input is invalid
     */
    public void registerPlayers() throws IOException {
        ArrayList<String> usernames = new ArrayList<>();

        BufferedReader inPlayers = new BufferedReader(new InputStreamReader(System.in));
        while (inPlayers.readLine() != null) {
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
     *
     * @throws IOException when the input is invalid
     */
    public void registerAdversaries() throws IOException {
        ArrayList<String> usernames = new ArrayList<>();

        BufferedReader inPlayers = new BufferedReader(new InputStreamReader(System.in));
        while (inPlayers.readLine() != null) {
            String username = inPlayers.readLine();
            usernames.add(username);
        }

        adversaryUsernames = usernames;
    }

    /**
     * Starts the game. Updates the current game state, player positions, and adversary positions.

    public void startGame() {
        currentGameState = new GameState(l, playerUsernames.size(), adversaryUsernames.size());
        playerIDpositions = currentGameState.getPlayerIDPositions();
        adversaryIDpositions = currentGameState.getAdversaryIDPositions();
    }
     */

    public void startLocalGame(){
        Scanner sc = new Scanner(System.in);
        for (int i=1; i<playerNumber+1;i++) {
            System.out.print("Player#"+i+": Please enter your username.");
            String username = sc.nextLine();
            playerUsernames.add(username);
        }
        GameState initialGameState = new GameState(playerIDpositions, adversaryIDpositions,
                levels.get(currentLevelIndex), zombieNumber );
        currentGameState = initialGameState;
        runGame();

    }

    public void runGame() {
        Scanner sc = new Scanner(System.in);
        while(true) {
            for (int i=0; i<playerUsernames.size();i++) {
                String username = playerUsernames.get(i);
                if (!expelledPlayers.contains(username)) {
                    Position current = currentGameState.getPlayerPositionsMap().get(String.valueOf(i));
                    System.out.print(username+", this is your current game state.\n");
                    printUpdate(current);
                    System.out.print("Please enter the x position of your next move:");
                    String moveX = sc.nextLine();
                    Integer x = Integer.parseInt(moveX);
                    System.out.print("Please enter the y position of your next move:");
                    String moveY = sc.nextLine();
                    Integer y = Integer.parseInt(moveY);
                    if (RuleChecker.isValidPlayerMoveTest("", current, new Position(x,y),
                            currentGameState.getPlayerPositionsMap())) {
                        ArrayList<Position> adversaryPositions =
                                currentGameState.fromMapToArrayList(currentGameState.getAdversaryPositionsMap());
                        Integer result = RuleChecker.determinePlayerInteractionTest(String.valueOf(i),new Position(x, y), adversaryPositions, currentGameState.getPlayerPositionsMap(), currentGameState.exitStatus );
                        if (result ==3) {
                            expelledPlayers.add(username);
                            System.out.print("Player "+username + " was expelled.");
                        }
                        if (result ==0) {
                            System.out.print("Player "+username + " found the key.");
                            if (playerKeyCount.containsKey(username)) {
                                Integer temp = playerKeyCount.get(username);
                                playerKeyCount.remove(username);
                                playerKeyCount.put(username, temp+1);
                            } else {
                                playerKeyCount.put(username, 1);
                            }
                        }
                        if (result ==2) {
                            System.out.print("Player "+username + " exited.");
                            if (playerExitCount.containsKey(username)) {
                                Integer temp = playerExitCount.get(username);
                                playerExitCount.remove(username);
                                playerExitCount.put(username, temp+1);
                            } else {
                                playerExitCount.put(username, 1);
                            }
                            if (currentLevelIndex !=natural) {
                                currentLevelIndex +=1;
                                expelledPlayers = new ArrayList<>();
                                startLocalGame();
                            } else {
                                System.out.print("You have won the game!");
                                endGame();
                            }
                        }
                        this.currentGameState.updatePlayerState(String.valueOf(i), result,new Position(x, y));
                    }
                    printUpdate(new Position(x,y));
                  if (expelledPlayers.size() == playerNumber) {
                      System.out.print("All players are ejected. The game is over. You failed in Level" + currentLevelIndex);
                      endGame();
                      break;

                  }
                }
            }
        }
    }

    public void endGame() {
        System.out.print(playerUsernames.get(0) +" exited"+ playerExitCount.get(playerUsernames.get(0))+ " times.");
        System.out.print(playerUsernames.get(0) +" found the key"+ playerKeyCount.get(playerUsernames.get(0))+ " times.");
    }

    public void printUpdate(Position current) {
        int[][] visibleTiles = generatePlayerUpdate(current);
        l.print2D(visibleTiles);
        HashMap<String,Position> actorMap = getVisibleActors(current);
        printInformation(actorMap);
    }
    public void printInformation(HashMap<String,Position> actorMap) {
        for(String s:actorMap.keySet()) {
            Integer index = Integer.parseInt(s);
            Position current = actorMap.get(index);
            if(index>=playerNumber && index<playerNumber+zombieNumber) {
                System.out.print("The adversary at ("+ current.getx()+","+current.gety() +") is a zombie.");
            } else if (index >=playerNumber+zombieNumber) {
                System.out.print("The adversary at ("+ current.getx()+","+current.gety() +") is a ghost.");
            }
        }
    }

    /**
     * Sends a modified version of the current game state to each player. For now, we are sending a
     * list of visible tiles.
     */
    public void sendCurrentState() {

        HashMap<String, Position> playerPositionsMap = currentGameState.getPlayerPositionsMap();
        for (Position p : playerPositionsMap.values()) {
            ArrayList<Position> visibleTiles = RuleChecker.calculatePlayerVisibleTiles(p);
            for (int i = 0; i < visibleTiles.size(); i++) {
                visibleTiles.get(i).print();
            }
        }
    }

    /**
     * Receives a move request from a player, and updates that player's position if the move is
     * valid.
     *
     * @param pos    represents the position the player is trying to move to
     * @param player represents the player that is trying to move

    public void receiveMove(Position pos, Player player) {
        if (RuleChecker.isValidPlayerMove(pos, player, playerIDpositions)) {
            currentGameState.updatePlayerState(player.getId() - 10, pos);
        }
    }
     */

    /**
     * Requests a move from the next player. This function will be implemented once networking is
     * incorporated into our design.
     *
     * @param player represents the player whose turn it is to move
     * @return a position representing where the next player wants to move
     */
    public Position requestMove(Player player) {
        String nextTurn = "It's your turn to move.";
        return new Position(-1, -1);
    }


    /**
     * Determines the interaction result.
     *
     * @param player represents the player that is interacting

    public void interact(Player player) {
        int result = RuleChecker.determinePlayerInteraction(player,
            adversaryIDpositions, playerIDpositions, currentGameState.getL().getExitStatus());
        if (result == 0) {
            currentGameState.setExitStatus(true);
        } else if (result == 2) {
            this.levelEnd = true;
        } else if (result == 3) {
            GameState.ejectPlayer(player);
        }

    }
     */

    /**
     * Gets visible area to the Player in the given Position
     *
     * @param pos Player position
     * @return int[][] representing visible area
     */
    public int[][] getVisibleArea(Position pos) {
        int[][] visibleArea = new int[5][5];
        int posX = pos.getx();
        int posY = pos.gety();
        for (int i = posX - 2; i < posX + 3; i++) {
            for (int j = posY - 2; j < posY + 3; j++) {
                if (i > 0 && j > 0) {
                    if (levelLayout.containsKey((new Position(i, j)))) {
                        visibleArea[i - posX + 2][j - posY + 2] = levelLayout
                            .get(new Position(i, j));
                    } else {
                        visibleArea[i - posX + 2][j - posY + 2] = 0;
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
        if (!isExitOpen) {
            Position key = l.getKey();
            int keyX = key.getx();
            int keyY = key.gety();
            if (keyX >= posX - 2 && keyX <= posX + 2 && keyY >= posY - 2 && keyY <= posY + 2) {
                result.put("key", key);
            }
        }
        int exitX = exit.getx();
        int exitY = exit.gety();
        if (exitX >= posX - 2 && exitX <= posX + 2 && exitY >= posY - 2 && exitY <= posY + 2) {
            result.put("exit", exit);
        }

        return result;

    }

    public HashMap<String, Position> getVisibleActors(Position pos) {
        ArrayList<Position> playerPos = new ArrayList<>();
        HashMap<String, Position> adversaryPosMap = currentGameState.getAdversaryPositionsMap();
        HashMap<String, Position> playerPosMap = currentGameState.getPlayerPositionsMap();
        ArrayList<Position> adversaryPos = new ArrayList<>();
        playerPos.addAll(playerPosMap.values());
        adversaryPos.addAll(adversaryPosMap.values());
        HashMap<String, Position> result = new HashMap<>();
        int posX = pos.getx();
        int posY = pos.gety();
        //Adds visible players from given Position
        for (int i = 0; i < playerPos.size(); i++) {
            int currX = playerPos.get(i).getx();
            int currY = playerPos.get(i).gety();
            if (currX >= posX - 2 && currX <= posX + 2 && currY >= posY - 2 && currY <= posY + 2) {
                String name = "";
                for (Entry<String, Position> e : playerPosMap.entrySet()) {
                    if (playerPos.get(i).equals(e.getValue())) {
                        name = e.getKey();
                    }
                }
                result.put(name, playerPos.get(i));
            }
        }
        //Adds visible adversaries from given Position
        for (int i = 0; i < adversaryPos.size(); i++) {
            int currX = adversaryPos.get(i).getx();
            int currY = adversaryPos.get(i).gety();
            if (currX >= posX - 2 && currX <= posX + 2 && currY >= posY - 2 && currY <= posY + 2) {
                String name = "";
                for (Entry<String, Position> e : adversaryPosMap.entrySet()) {
                    if (adversaryPos.get(i).equals(e.getValue())) {
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
        return RuleChecker.isValidPlayerMoveTest(name, current, move, playerPosMap);

    }

    public int interactAfterMove(String name, Position curr) {
        HashMap<String, Position> playerPosMap = currentGameState.getPlayerPositionsMap();
        ArrayList<Position> adversaryPositions = currentGameState.getAdversaryPositions();
        boolean exitStatus = currentGameState.getExitStatus();
        int result = RuleChecker
            .determinePlayerInteractionTest(name, curr, adversaryPositions, playerPosMap,
                exitStatus);
//        this.currentGameState.updatePlayerState(name, result,curr);
        return result;
    }

    public void updatePlayerState(String name, Position move) {
        HashMap<String, Position> playerPosMap = currentGameState.getPlayerPositionsMap();
        ArrayList<Position> adversaryPositions = currentGameState.getAdversaryPositions();
        boolean exitStatus = currentGameState.getExitStatus();
        int result = RuleChecker
            .determinePlayerInteractionTest(name, move, adversaryPositions, playerPosMap,
                exitStatus);
        this.currentGameState.updatePlayerState(name, result, move);
    }

    public int[][] generatePlayerUpdate(Position position) {
        HashMap<String, Position> actorPositionsMap = getVisibleActors(position);
        HashMap<String, Position> objectPositionsMap = currentGameState.getObjectPositionsMap();
        int[][] visibleTiles = getVisibleArea(position);
        HashMap<String, Position> surroundingPositions = new HashMap<>();
        surroundingPositions.putAll(actorPositionsMap);
        surroundingPositions.putAll(objectPositionsMap);
        for (String s : surroundingPositions.keySet()) {
            if (s.equals("key")) {
                Position key = surroundingPositions.get(s);
                int keyX = key.getx();
                int keyY = key.gety();
                visibleTiles[keyX][keyY] = 7;
            }
            if (s.equals("exit")) {
                Position exit = surroundingPositions.get(s);
                int exitX = exit.getx();
                int exitY = exit.gety();
                visibleTiles[exitX][exitY] = 8;
            }
            if (Integer.parseInt(s) <playerNumber) {
                Position player = surroundingPositions.get(s);
                int playerX = player.getx();
                int playerY = player.gety();
                visibleTiles[playerX][playerY] = 3;
            }
            if (Integer.parseInt(s)>=playerNumber) {
                Position adversary = surroundingPositions.get(s);
                int adversaryX = adversary.getx();
                int adversaryY = adversary.gety();
                visibleTiles[adversaryX][adversaryY] = 9;
            }
        }
        return visibleTiles;

    }

    public static Level readLevel(String json) throws JSONException {
        JSONObject level = new JSONObject(json);
        Level l = TestLevel.getLevel(level);
        return l;
    }

    public static ArrayList<Level> generateLevels(String fileName) throws IOException {
        ArrayList<Level> levels = new ArrayList<>();
        String currentPath = System.getProperty("user.dir");
        File snarlLevels = new File(currentPath+"\\"+fileName);
        FileReader fr = new FileReader(snarlLevels);
        BufferedReader bufferedReader = new BufferedReader(fr);
        StringBuilder currentLevel = new StringBuilder();
        natural = Integer.parseInt(bufferedReader.readLine());
        String temp = bufferedReader.readLine();
        while (temp != null) {
            currentLevel.append(temp);
            String tempLevel = currentLevel.toString();
            try {
                temp = bufferedReader.readLine();
                Level level = readLevel(tempLevel);
                levels.add(level);
                currentLevel = new StringBuilder();
            } catch (JSONException ignored) {

            }
        }

        return levels;
    }

    public void generateInitialPositions(Integer currentLevelNumber){
        Level currentLevel = levels.get(currentLevelNumber);
        HashMap<Position, Integer> levelLayout = currentLevel.getLevelLayout();
        ArrayList<Position> traversables = new ArrayList<>();
        for (Position p: levelLayout.keySet()) {
            Integer tileType = levelLayout.get(p);
            if (tileType ==2) {
                traversables.add(p);
            }
        }
        for (int i=0;i<players.size();i++) {
            Random r = new Random();
            int high = traversables.size();
            int result = r.nextInt(high);
            playerIDpositions.put(players.get(i).getId(),traversables.get(result) );
            traversables.remove(result);
        }
        for (int i=0;i<zombies.size();i++) {
            Random r = new Random();
            int high = traversables.size();
            int result = r.nextInt(high);
            zombies.get(i).setPosition(traversables.get(result));
            adversaryIDpositions.put(zombies.get(i).getId(), traversables.get(result));
            traversables.remove(result);
        }
        for (int i=0;i<ghosts.size();i++) {
            Random r = new Random();
            int high = traversables.size();
            int result = r.nextInt(high);
            ghosts.get(i).setPosition(traversables.get(result));
            adversaryIDpositions.put(ghosts.get(i).getId(), traversables.get(result));
            traversables.remove(result);
        }
    }

    public static void main(String[] args) throws IOException {
        for (int i=0;i<args.length;i++) {
            String argument = args[i];
            if (argument.equals("--levels")) {
                fileName = args[i+1];
                levels = generateLevels(fileName);
            } else if (argument.equals("--players")) {
                playerNumber = Integer.parseInt(args[i+1]);
                if (playerNumber < 1 ||playerNumber >4) {
                    System.out.print("Not valid player number.");
                    return;
                }
            } else if (argument.equals("--start")) {
                startLevel = Integer.parseInt(args[i+1]);
                if (startLevel > levels.size()|| startLevel <1) {
                    System.out.print("Not valid start number.");
                    return;
                }
            } else if (argument.equals("--observe")) {
                playerNumber = 1;
                observe = true;
            }
        }
        playerExitCount = new HashMap<>();
        playerKeyCount = new HashMap<>();
    }
}






