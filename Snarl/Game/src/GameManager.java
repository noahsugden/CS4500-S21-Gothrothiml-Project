import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;


/**
 * The game manager needs to have components for the following
 *
 * - the level, which in turn needs to represent rooms and/or hallways
 * - a random level generator that produces a level from a random seed
 * - some idea of the valid non-entity objects in this game
 * - representation of the game’s current state and its rules
 */
public class GameManager {


    JSONArray jsonLevels = new JSONArray();
    static ArrayList<String> playerUsernames = new ArrayList<>();
    static ArrayList<String> adversaryUsernames;
    ArrayList<String> expelledPlayers = new ArrayList<>();
    ArrayList<String> exitedPlayers  = new ArrayList<>();
    GameState currentGameState;
    String keyPlayer = "";
    static Level l;
    static HashMap<Integer, Position> playerIDpositions;
    static HashMap<Integer, Position> adversaryIDpositions;
    HashMap<String, Integer> playerExitCount;
    HashMap<String, Integer> playerKeyCount;
    HashMap<String, Integer> playerEjectCount;
    int zombieClientNumber =0;
    int ghostClientNumber=0;
    boolean levelEnd;
    static int turnNumber;
    static HashMap<Position, Integer> levelLayout;
    static RuleChecker ruleChecker;
    static Integer zombieNumber;
    static Integer ghostNumber;
    static ArrayList<Zombie> zombies = new ArrayList<>();
    static ArrayList<Ghost> ghosts = new ArrayList<>();
    static ArrayList<Player> players = new ArrayList<>();
    static Integer currentLevelIndex;

    static String fileName = "snarl.levels";
    static Integer playerNumber =1;
    static Integer startLevel =1;
    static boolean observe = true;
    static ArrayList<Level> levels = new ArrayList<>();
    static Integer natural;
    static GameManager gameManager;
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
        playerIDpositions = new HashMap<>();
        adversaryIDpositions = new HashMap<>();
        l = levels.get(currentLevelIndex);
        levelLayout = l.getLevelLayout();
        generateInitialPositions(currentLevelIndex);
        this.currentLevelIndex = currentLevelIndex;
        ruleChecker = new RuleChecker(levels.get(currentLevelIndex));

    }
    GameManager(String fileName, ArrayList<String> usernames) throws IOException {
        playerUsernames = usernames;
        playerNumber = playerUsernames.size();
        levels = generateLevels(fileName);
        currentLevelIndex = 0;
        l = levels.get(currentLevelIndex);
        levelLayout = l.getLevelLayout();
        createAdversaries(currentLevelIndex);
        for (int i=0;i<playerNumber;i++) {
            Player temp = new Player(i);
            players.add(temp);
        }
        playerIDpositions = new HashMap<>();
        adversaryIDpositions = new HashMap<>();
        generateInitialPositions(currentLevelIndex);
        ruleChecker = new RuleChecker(levels.get(currentLevelIndex));
        currentGameState = new GameState(playerIDpositions, adversaryIDpositions,
                levels.get(currentLevelIndex), zombieNumber );
        updateAdversaries();
        playerKeyCount = new HashMap<>();
        playerExitCount = new HashMap<>();
        playerEjectCount = new HashMap<>();
    }

    GameManager(String fileName, ArrayList<String> usernames, int zombieClientNumber, int ghostClientNumber) throws IOException {
        playerUsernames = usernames;
        playerNumber = playerUsernames.size();
        levels = generateLevels(fileName);
        currentLevelIndex = 0;
        this.zombieClientNumber = zombieClientNumber;
        this.ghostClientNumber = ghostClientNumber;
        l = levels.get(currentLevelIndex);
        levelLayout = l.getLevelLayout();
        createAdversaries(currentLevelIndex);
        for (int i=0;i<playerNumber;i++) {
            Player temp = new Player(i);
            players.add(temp);
        }
        playerIDpositions = new HashMap<>();
        adversaryIDpositions = new HashMap<>();
        generateInitialPositions(currentLevelIndex);
        ruleChecker = new RuleChecker(levels.get(currentLevelIndex));
        currentGameState = new GameState(playerIDpositions, adversaryIDpositions,
                levels.get(currentLevelIndex), zombieNumber );
        updateAdversaries();
        playerKeyCount = new HashMap<>();
        playerExitCount = new HashMap<>();
        playerEjectCount = new HashMap<>();
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

    public void startNewLevel() {
        currentLevelIndex+=1;
        System.out.print("currentLevelIndex is "+currentLevelIndex+"\n");
        l = levels.get(currentLevelIndex);
        levelLayout = l.getLevelLayout();
        createAdversaries(currentLevelIndex);
        playerIDpositions = new HashMap<>();
        adversaryIDpositions = new HashMap<>();
        generateInitialPositions(currentLevelIndex);
        ruleChecker = new RuleChecker(l);
        currentGameState = new GameState(playerIDpositions, adversaryIDpositions,
                levels.get(currentLevelIndex), zombieNumber );
        updateAdversaries();
        levelEnd = false;
        expelledPlayers = new ArrayList<>();
        exitedPlayers = new ArrayList<>();
    }

    public void createAdversaries(Integer currentLevelIndex) {
        zombies = new ArrayList<>();
        ghosts = new ArrayList<>();
        zombieNumber = currentLevelIndex / 2 + 1;
        ghostNumber = (currentLevelIndex - 1) / 2;
        if (zombieClientNumber>zombieNumber) {
            zombieNumber = zombieClientNumber;
        }
        if (ghostClientNumber>ghostNumber) {
            ghostNumber = ghostClientNumber;
        }
        for (int i=0;i<zombieNumber;i++) {
            Zombie temp = new Zombie(playerNumber+i);
            zombies.add(temp);
        }
        for (int i=0;i<ghostNumber;i++) {
            Ghost temp = new Ghost(playerNumber+zombieNumber+i);
            ghosts.add(temp);
        }

    }

    public void updateAdversaries() {
        for (int i = 0; i < zombies.size(); i++) {
            zombies.get(i).setMaps(currentGameState);
        }
        for (int i = 0; i < ghosts.size(); i++) {
            ghosts.get(i).setMaps(currentGameState);
        }
    }



    public JSONObject generateStartLevel() throws JSONException {
        JSONObject startLevel = new JSONObject();
        JSONArray namesArray = new JSONArray();
        startLevel.put("type", "start-level");
        startLevel.put("level", currentLevelIndex);
        for (String playerUsername : playerUsernames) {
                namesArray.put(playerUsername);
        }
        startLevel.put("players",namesArray);
        return startLevel;

    }

    public GameState getCurrentGameState() {
        return this.currentGameState;
    }

    public  ArrayList<Zombie> getZombies() {
        return zombies;
    }

    public ArrayList<Ghost> getGhosts() {
        return ghosts;
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
        currentGameState = new GameState(playerIDpositions, adversaryIDpositions,
                levels.get(currentLevelIndex), zombieNumber );
        for (int i = 0; i < zombies.size(); i++) {
            zombies.get(i).setMaps(currentGameState);
        }
        for (int i = 0; i < ghosts.size(); i++) {
            ghosts.get(i).setMaps(currentGameState);
        }
        //runGame();

    }

    /**
    public void runGame() {
        Scanner sc = new Scanner(System.in);
        for(int j = 0; j < 100; j++) {
            for (int i=0; i<playerUsernames.size();i++) {
                String username = playerUsernames.get(i);
                if (expelledPlayers.size() > 0) {
                    System.out.println(expelledPlayers.get(0));
                }
                if (!expelledPlayers.contains(username)) {
                    currentGameState.getPlayerPositionsMap().get(String.valueOf(0)).print();
                    Position current = currentGameState.getPlayerPositionsMap().get(String.valueOf(0));
                    current.print();
                    System.out.print(username+", this is your current game state.\n");
                    printUpdate(current);
                    System.out.print("Please enter the x position of your next move:");
                    String moveY = sc.nextLine();
                    Integer x = Integer.parseInt(moveY);
                    System.out.print("Please enter the y position of your next move:");
                    String moveX = sc.nextLine();
                    Integer y = Integer.parseInt(moveX);
                    if (RuleChecker.isValidPlayerMoveTest("", current, new Position(x,y),
                            currentGameState.getPlayerPositionsMap())) {
                        ArrayList<Position> adversaryPositions =
                                currentGameState.fromMapToArrayList(currentGameState.getAdversaryPositionsMap());
                        Integer result = RuleChecker.determinePlayerInteractionTest(String.valueOf(0),
                            new Position(x, y), adversaryPositions, currentGameState.getPlayerPositionsMap(),
                            currentGameState.exitStatus );
//                        System.out.print(result);
                        if (result ==3) {
                            expelledPlayers.add(username);
                            System.out.println("Player "+username + " was expelled.");
                        }
                        if (result ==0) {
                            System.out.println("Player "+username + " found the key.");
                            if (playerKeyCount.containsKey(username)) {
                                Integer temp = playerKeyCount.get(username);
                                playerKeyCount.remove(username);
                                playerKeyCount.put(username, temp+1);
                            } else {
                                playerKeyCount.put(username, 1);
                            }
                            currentGameState.setExitStatus(true);
                        }
                        if (result ==2) {
                            System.out.println("Player "+username + " exited.");
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
                                System.out.println("You have won the game!");
                                endGame();
                            }
                        }
                        current = new Position(x, y);
                        currentGameState.updatePlayerState(String.valueOf(0), result, current);
                    }
                    else {
                        System.out.print("Not valid position. Please enter again.");
                    }
//                    printUpdate(new Position(x,y));
                  if (expelledPlayers.size() == playerNumber) {
                      System.out.println("All players are ejected. The game is over. "
                          + "You failed in Level " + currentLevelIndex);
                      endGame();
                      break;

                  }
                }
            }
            for(int i = 0; i < zombieNumber; i++) {
                Zombie temp = zombies.get(i);
                temp.updatePlayerPositions(currentGameState);
                temp.updatePosition();
                Position newP = temp.getP();
                if (currentGameState.getPlayerPositionsMap().containsValue(newP)) {
                    expelledPlayers.add(playerUsernames.get(0));
                }
                currentGameState.updateAdversaryMap(String.valueOf(i + 1), temp.getP());
            }

            for(int i = 0; i < ghostNumber; i++) {
                Ghost temp = ghosts.get(i);
                temp.updatePlayerPositions(currentGameState);
                temp.updatePosition();
                Position newP = temp.getP();
                if (currentGameState.getPlayerPositionsMap().containsValue(newP)) {
                    expelledPlayers.add(playerUsernames.get(0));
                }
                currentGameState.updateAdversaryMap(String.valueOf(i + 1), temp.getP());
            }
        }
    }

     **/
    public boolean isClientInActive(int id) {
        return expelledPlayers.contains(playerUsernames.get(id)) || exitedPlayers.contains(playerUsernames.get(id));
    }

    public void adversaryMove(Adversary temp){
        temp.updatePlayerPositions(this.currentGameState);
        temp.updatePosition();
        Position newP = temp.getP();
        HashMap<String, Position> playerPositionsMap = this.currentGameState.getPlayerPositionsMap();

        for (String s:playerPositionsMap.keySet()) {
            if (playerPositionsMap.get(s).equals(newP)) {
                this.expelledPlayers.add(playerUsernames.get((Integer.parseInt(s))));
                addCount(playerEjectCount, s);
            }

        }
        this.currentGameState.updateAdversaryMap(String.valueOf(temp.getId()), temp.getP());
        levelEnd = ruleChecker.isLevelEnd(expelledPlayers, exitedPlayers, playerUsernames);

    }

    public void addCount(HashMap<String, Integer> countMap, String name) {
        if(countMap.containsKey(name)) {
            int ejectCount = countMap.get(name) +1;
            countMap.put(name, ejectCount);
        } else {
            countMap.put(name, 1);
        }
    }

    public boolean isLastLevel() {
        return levels.size()-1 == currentLevelIndex;
    }

    public String performOneMove(int id, Position move) throws Exception {
        if (!this.determineValidMove(String.valueOf(id), move)) {
            return "Invalid";
        } else {
            int interactResult = this.interactAfterMove(String.valueOf(id), move);
            this.updatePlayerState(String.valueOf(id), move);
            switch(interactResult) {
                case 0:
                    return "Key";
                case 1:
                case 4:
                    return "OK";
                case 2:
                    return "Exit";
                case 3:
                    return "Eject";
            }
        }

        throw new Exception("invalid interaction");

    }

    public void observe() {
        System.out.print("===========" +"\n");
            printAscii(generateObserver());
        System.out.print("===========" +"\n");
    }

    public void endGame() {
        int exitCount = -1;
        int keyCount = -1;
        if (playerExitCount.get(playerUsernames.get(0)) == null) {
            exitCount = 0;
        } else {
            exitCount = playerExitCount.get(playerUsernames.get(0));
        }
        if (playerKeyCount.get(playerUsernames.get(0)) == null) {
            keyCount = 0;
        } else {
            keyCount = playerKeyCount.get(playerUsernames.get(0));
        }

        System.out.println(playerUsernames.get(0) +" exited "+ exitCount + " times.");
        System.out.println(playerUsernames.get(0) +" found the key "+ keyCount + " times.");
    }

    public  void printUpdate(Position current) {
        if(observe) {
            l.print2D(generateObserverUpdate());
            HashMap<String, Position> actorMap = currentGameState.getAdversaryPositionsMap();
            printInformation(actorMap);
            return;
        }
        int[][] visibleTiles = generatePlayerUpdate(current);
        l.print2D(visibleTiles);
        HashMap<String,Position> actorMap = getVisibleActors(current);
//        printInformation(actorMap);
    }
    public void printInformation(HashMap<String,Position> actorMap) {
        for(String s:actorMap.keySet()) {
            Integer index = Integer.parseInt(s);
            Position current = actorMap.get(s);
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
    public  int[][] getVisibleArea(Position pos) {
        int[][] visibleArea = new int[5][5];
        int posX = pos.getx();
        int posY = pos.gety();
        for (int i = posX - 2; i < posX + 3; i++) {
            for (int j = posY - 2; j < posY + 3; j++) {
                if (i >= 0 && j >= 0) {
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
        for (Position p : adversaryPosMap.values()) {
            int currX = p.getx();
            int currY = p.gety();
            if (currX >= posX - 2 && currX <= posX + 2 && currY >= posY - 2 && currY <= posY + 2) {
                String name = "";
                for (Entry<String, Position> e : adversaryPosMap.entrySet()) {
                    if (p.equals(e.getValue())) {
                        name = e.getKey();
                    }
                }
                result.put(name, p);
            }
        }
        return result;
    }

    public boolean determineValidMove(String name, Position move) {
        HashMap<String, Position> playerPosMap = currentGameState.getPlayerPositionsMap();
        Position current = playerPosMap.get(name);
        ArrayList<String> inActivePlayers = new ArrayList<>();
        for (int i=0;i<exitedPlayers.size();i++) {
            int index = playerUsernames.indexOf(exitedPlayers.get(i));
            inActivePlayers.add(String.valueOf(index));
        }
        for (int i=0;i<expelledPlayers.size();i++) {
            int index = playerUsernames.indexOf(expelledPlayers.get(i));
            inActivePlayers.add(String.valueOf(index));
        }
        return RuleChecker.isValidPlayerMoveTest(name, current, move, playerPosMap, inActivePlayers);

    }

    public int interactAfterMove(String name, Position curr) {
        HashMap<String, Position> playerPosMap = currentGameState.getPlayerPositionsMap();
        HashMap<String, Position> adversaryPosMap = currentGameState.getAdversaryPositionsMap();
        ArrayList<Position> adversaryPositions = currentGameState.fromMapToArrayList(adversaryPosMap);
        boolean exitStatus = currentGameState.getExitStatus();
        int result = RuleChecker
            .determinePlayerInteractionTest(name, curr, adversaryPositions, playerPosMap,
                exitStatus);
        if (result ==3) {
            expelledPlayers.add(playerUsernames.get(Integer.parseInt(name)));
            addCount(playerEjectCount,name);
        } else if (result ==2) {
            exitedPlayers.add(playerUsernames.get(Integer.parseInt(name)));
           addCount(playerExitCount, name);
        } else if (result == 0) {
            keyPlayer = name;
            addCount(playerKeyCount, name);

        }
        levelEnd = ruleChecker.isLevelEnd(expelledPlayers,exitedPlayers,  playerUsernames);
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
        currentGameState.updatePlayerState(name, result, move);
    }

    public  int[][] generatePlayerUpdate(Position position) {
        HashMap<String, Position> actorPositionsMap = getVisibleActors(position);
        HashMap<String, Position> objectPositionsMap = getVisibleObjects(position);
        int[][] visibleTiles = getVisibleArea(position);
        HashMap<String, Position> surroundingPositions = new HashMap<>();
        surroundingPositions.putAll(actorPositionsMap);
        surroundingPositions.putAll(objectPositionsMap);
        Position player = surroundingPositions.get(String.valueOf(0));
        int playerX = player.getx();
        int playerY = player.gety();
        visibleTiles[2][2] = 3;
        for (String s : surroundingPositions.keySet()) {
            if (s.equals("key")) {
                Position key = surroundingPositions.get(s);
                int keyX = key.getx();
                int keyY = key.gety();
                visibleTiles[keyX - playerX + 2][keyY - playerY + 2] = 7;
            }
            else if (s.equals("exit")) {
                Position exit = surroundingPositions.get(s);
                int exitX = exit.getx();
                int exitY = exit.gety();
                visibleTiles[exitX - playerX + 2][exitY - playerY + 2] = 8;
            }
            else if (Integer.parseInt(s)>=playerNumber) {
                Position adversary = surroundingPositions.get(s);
                int adversaryX = adversary.getx();
                int adversaryY = adversary.gety();
                visibleTiles[adversaryX - playerX + 2][adversaryY - playerY + 2]  = 9;
            }
        }
        return visibleTiles;

    }

    public int[][] generateObserver() {
        int[][] layout = currentGameState.getL().get2Darray();
        int[][] visibleTiles = copyofArray(layout);

        HashMap<String, Position> playerPositionsMap = this.currentGameState.getPlayerPositionsMap();
        HashMap<String, Position> adversaryPositionsMap = this.currentGameState.getAdversaryPositionsMap();
        for (String s: playerPositionsMap.keySet()) {
            String name = playerUsernames.get(Integer.parseInt(s));
            if (!exitedPlayers.contains(name)&&!expelledPlayers.contains(name)) {
                Position current = playerPositionsMap.get(s);
                int x = current.getx();
                int y = current.gety();
                visibleTiles[y][x] = 100+Integer.parseInt(s);
            }
        }
        for (String s:adversaryPositionsMap.keySet()) {
            int index = Integer.parseInt(s);
            Position current = adversaryPositionsMap.get(s);
            int x = current.getx();
            int y = current.gety();
            if (index>=playerNumber&&index<playerNumber+zombieNumber) {
                visibleTiles[y][x]=11;
            } else {
                visibleTiles[y][x]=12;
            }
        }

        return visibleTiles;
    }

    public void printAscii(int[][] ascii) {
        for (int[] x : ascii)
        {
            for (int y : x)
            {
                switch(y) {
                    case 0:
                        System.out.print("   ");
                        break;
                    case 1:
                        System.out.print(" * ");
                        break;
                    case 2:
                        System.out.print(" . ");
                        break;
                    case 3:

                        break;
                    case 4:
                        System.out.print(" < ");
                        break;
                    case 5:
                        System.out.print(" - ");
                        break;
                    case 6:
                        System.out.print(" | ");
                        break;
                    case 7:
                        System.out.print(" k ");
                        break;
                    case 8:
                        System.out.print(" $ ");
                        break;
                    case 11:
                        System.out.print(" z ");
                        break;
                    case 12:
                        System.out.print(" g ");
                        break;
                    case 100:
                        char[] nameArray = playerUsernames.get(0).toCharArray();
                        System.out.print(" "+nameArray[0]+" ");
                        break;
                    case 101:
                        nameArray = playerUsernames.get(1).toCharArray();
                        System.out.print(" "+nameArray[0]+" ");
                        break;
                    case 102:
                        nameArray = playerUsernames.get(2).toCharArray();
                        System.out.print(" "+nameArray[0]+" ");
                        break;
                    case 103:
                        nameArray = playerUsernames.get(3).toCharArray();
                        System.out.print(" "+nameArray[0]+" ");
                        break;
                }
            }
            System.out.println();
        }
    }

    public int[][] generateObserverUpdate() {
        HashMap<String, Position> actorPositionsMap = new HashMap<>();
        actorPositionsMap.putAll(currentGameState.getPlayerPositionsMap());
        actorPositionsMap.putAll(currentGameState.getAdversaryPositionsMap());
        int[][] visibleTiles = currentGameState.getL().get2Darray();
        HashMap<String, Position> objectPositionsMap = currentGameState.getObjectPositionsMap();
        HashMap<String, Position> surroundingPositions = new HashMap<>();
        surroundingPositions.putAll(actorPositionsMap);
        surroundingPositions.putAll(objectPositionsMap);
        for (String s : surroundingPositions.keySet()) {
            if (s.equals("key")) {
                Position key = surroundingPositions.get(s);
                int keyX = key.gety();
                int keyY = key.getx();
                visibleTiles[keyX][keyY] = 7;
            }else if (s.equals("exit")) {
                Position exit = surroundingPositions.get(s);
                int exitX = exit.gety();
                int exitY = exit.getx();
                visibleTiles[exitX][exitY] = 8;
            }else if (Integer.parseInt(s) <playerUsernames.size()) {
                Position player = surroundingPositions.get(s);
                int playerX = player.gety();
                int playerY = player.getx();
                visibleTiles[playerX][playerY] = 3;
            } else if (Integer.parseInt(s)>=playerUsernames.size()) {
                Position adversary = surroundingPositions.get(s);
                int adversaryX = adversary.gety();
                int adversaryY = adversary.getx();
                visibleTiles[adversaryX][adversaryY] = 9;
            }
        }
        return visibleTiles;

    }

    public Level readLevel(String json) throws JSONException {
        JSONObject level = new JSONObject(json);
        Level l = TestLevel.getLevel(level);
        return l;
    }



    public ArrayList<Level> generateLevels(String fileName) throws IOException {
        ArrayList<Level> levels = new ArrayList<>();
        String currentPath = System.getProperty("user.dir");
        File snarlLevels = new File(currentPath+"/"+fileName);
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
                JSONObject tempLevelObject = new JSONObject(tempLevel);
                jsonLevels.put(tempLevelObject);
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
            playerIDpositions.put(players.get(i).getId(),traversables.get(result));
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

    public JSONObject generateEndGame() throws JSONException {
        JSONObject endGame = new JSONObject();
        endGame.put("type", "end-game");

        JSONArray playerScoreList = new JSONArray();

        for(int i=0;i<playerUsernames.size();i++) {
            JSONObject playerScore = new JSONObject();
            playerScore.put("type", "player-score");
            playerScore.put("name", playerUsernames.get(i));
            String name =String.valueOf(i);
            if (playerExitCount.containsKey(name)) {
                playerScore.put("exits", playerExitCount.get(name));
            } else {
                playerScore.put("exits", 0);
            }
            if (playerEjectCount.containsKey(name)) {
                playerScore.put("ejects", playerEjectCount.get(name));
            } else {
                playerScore.put("ejects", 0);
            }
            if (playerKeyCount.containsKey(name)) {
                playerScore.put("keys", playerKeyCount.get(name));
            } else {
                playerScore.put("keys", 0);
            }
            playerScoreList.put(playerScore);
        }
        endGame.put("scores", playerScoreList);
        return endGame;
    }


    public JSONObject generateAdversaryUpdate(int id) throws JSONException {
        JSONObject adversaryUpdate= new JSONObject();
        adversaryUpdate.put("type", "adversary-update");
        JSONArray playerPositions = new JSONArray();
        JSONArray adversaryPositions = new JSONArray();
        HashMap<String, Position> playerPositionsMap = currentGameState.getPlayerPositionsMap();
        HashMap<String, Position> adversaryPositionsMap = currentGameState.getAdversaryPositionsMap();
        for (String s:playerPositionsMap.keySet()) {
            Position curr = playerPositionsMap.get(s);
            JSONArray positionArray = new JSONArray();
            positionArray.put(curr.getx());
            positionArray.put(curr.gety());
            playerPositions.put(positionArray);
        }
        for (String s:adversaryPositionsMap.keySet()) {
            Position curr = adversaryPositionsMap.get(s);
            JSONArray positionArray = new JSONArray();
            positionArray.put(curr.getx());
            positionArray.put(curr.gety());
            adversaryPositions.put(positionArray);
        }
        adversaryUpdate.put("players", playerPositions);
        adversaryUpdate.put("adversaries", adversaryPositions);
        JSONArray current = new JSONArray();
        Position curr = adversaryPositionsMap.get(String.valueOf(playerNumber+id));
        current.put(curr.getx());
        current.put(curr.gety());
        adversaryUpdate.put("position", current);




        return adversaryUpdate;
    }



    //need to add message
    public JSONObject generatePlayerUpdate(int id) throws JSONException {
        if (expelledPlayers.contains(playerUsernames.get(id))) {
            return null;
        }
        if (exitedPlayers.contains(playerUsernames.get(id))) {
            return null;
        }
        JSONObject playerUpdate = new JSONObject();
        playerUpdate.put("type", "player-update");
        Position curr = currentGameState.getPlayerPositionsMap().get(String.valueOf(id));
        //layout
        JSONArray tileLayout = getTileLayout(curr);
        playerUpdate.put("layout", tileLayout);

        //position
        JSONArray position = positionToArray(curr);
        playerUpdate.put("position", position);

        //objects
       JSONArray objectArray = generateObjectArray(curr);
       playerUpdate.put("objects", objectArray);

       //actors
        JSONArray actorArray = generateActorPositionList(id, curr);
        playerUpdate.put("actors", actorArray);

        if (expelledPlayers.contains(playerUsernames.get(id))) {
            playerUpdate.put("message", playerUsernames.get(id) +" has been expelled.");

        } else if  (exitedPlayers.contains(playerUsernames.get(id))) {
            playerUpdate.put("message", playerUsernames.get(id) +" has exited.");
        }
        else {
            playerUpdate.put("message", JSONObject.NULL);
        }


        return playerUpdate;
    }

    public JSONArray generateActorPositionList(Integer id, Position pos) throws JSONException {
        JSONArray result = new JSONArray();
        HashMap<String, Position> actors = getVisibleActors(pos);
        for (String s : actors.keySet()) {
            if (!s.equals(String.valueOf(id))) {
                Integer index = Integer.parseInt(s);
                String name = playerUsernames.get(id);
                //the actor is an alive player
                if (!expelledPlayers.contains(name) && !exitedPlayers.contains(name) && index <playerUsernames.size()) {
                    JSONObject playerObject = new JSONObject();
                    playerObject.put("type", "player");
                    playerObject.put("name", playerUsernames.get(index));
                    playerObject.put("position", positionToArray(actors.get(s)));
                    result.put(playerObject);
                } else if (!expelledPlayers.contains(name)&& !exitedPlayers.contains(name)) {
                    JSONObject adversaryObject = new JSONObject();
                    adversaryObject.put("type", determineAdversaryType(s));
                    adversaryObject.put("name", s);
                    adversaryObject.put("position", positionToArray(actors.get(s)));
                    result.put(adversaryObject);
                }
            }
        }
        return result;
    }

    public String determineAdversaryType(String s) {
            Integer index = Integer.parseInt(s);
            if(index>=playerNumber && index<playerNumber+zombieNumber) {
               return "zombie";
            } else if (index >=playerNumber+zombieNumber) {
               return "ghost";
            } else {
                return "invalid";
            }

    }

    public JSONArray generateObjectArray(Position curr) throws JSONException {
        HashMap<String, Position> objects = getVisibleObjects(curr);
        JSONArray objectArray = new JSONArray();
        for (String s : objects.keySet()) {
            if (s.equals("key")) {
                JSONObject keyObject = new JSONObject();
                keyObject.put("type", "key");
                keyObject.put("position", positionToArray(objects.get("key")));
                objectArray.put(keyObject);
            } else if (s.equals("exit")) {
                JSONObject exitObject = new JSONObject();
                exitObject.put("type", "exit");
                exitObject.put("position", positionToArray(objects.get("exit")));
                objectArray.put(exitObject);
            }
        }
        return objectArray;
    }

    public JSONArray getWholeLevelLayout() {
        int[][] levelLayout = l.get2Darray();
        JSONArray result = new JSONArray();
        for (int i = 0; i < levelLayout.length; i++) {
            JSONArray curr = new JSONArray();
            for (int j = 0; j < levelLayout.length; j++) {
                if (levelLayout[i][j] == 1) {
                    curr.put(0);
                } else if (levelLayout[i][j] == 2 || levelLayout[i][j] == 7 || levelLayout[i][j] == 8
                        || levelLayout[i][j] == 5 || levelLayout[i][j] == 6) {
                    curr.put(1);
                } else if (levelLayout[i][j] == 4) {
                    curr.put(2);
                } else {
                    curr.put(0);
                }
            }
            result.put(curr);
        }
        return result;

    }

    public JSONArray getTileLayout(Position pos) {
        int[][] tileArray = getVisibleArea(pos);
        JSONArray result = new JSONArray();
        for (int i = 0; i < tileArray.length; i++) {
            JSONArray curr = new JSONArray();
            for (int j = 0; j < tileArray.length; j++) {
                if (tileArray[i][j] == 1) {
                    curr.put(0);
                } else if (tileArray[i][j] == 2 || tileArray[i][j] == 7 || tileArray[i][j] == 8 || tileArray[i][j] == 5 || tileArray[i][j] == 6) {
                    curr.put(1);
                } else if (tileArray[i][j] == 4) {
                    curr.put(2);
                } else {
                    curr.put(0);
                }
            }
            result.put(curr);
        }
        return result;
    }
    public  JSONArray positionToArray(Position p) {
        JSONArray result = new JSONArray();
        result.put(p.getx());
        result.put(p.gety());
        return result;
    }

    public int[][] copyofArray(int[][] ascii) {
        int[][] result = new int[ascii.length][ascii.length];
        for (int i=0;i<ascii.length;i++) {
            for (int j=0;j<ascii.length;j++) {
                result[i][j] = ascii[i][j];
            }
        }


        return result;
    }

    public static void main(String[] args) throws IOException {
        startLevel = 0;
        currentLevelIndex = 0;
        for (int i=0;i<args.length;i++) {
            String argument = args[i];
            if (argument.equals("--levels")) {
                fileName = args[i+1];

            } else if (argument.equals("--players")) {
                playerNumber = Integer.parseInt(args[i+1]);
                if (playerNumber != 1) {
                    System.out.print("Only one player is supported!");
                    return;
                }
            } else if (argument.equals("--start")) {
                startLevel = Integer.parseInt(args[i+1]);
                currentLevelIndex = startLevel - 1;
                if (startLevel > levels.size()|| startLevel <1) {
                    System.out.print("Not valid start number.");
                    return;
                }
            } else if (argument.equals("--observe")) {
                playerNumber = 1;
                observe = true;
            }
        }
        //levels = generateLevels(fileName);
       // playerExitCount = new HashMap<>();
       // playerKeyCount = new HashMap<>();
        gameManager = new GameManager(currentLevelIndex);





    }
}






