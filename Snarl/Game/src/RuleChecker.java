

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class represents a RuleChecker that will be used by the Game Manager to determine the
 * validity of the game.
 * It contains the current level and a Hashmap of Position to Integer representing the level layout.
 */
public class RuleChecker {
    static Level l;
    static HashMap<Position, Integer> levellayout;

    /**
     * Constructor for a RuleChecker.
     * @param level represents the current level.
     *              the levellayout of the given level is used to initialize this levellayout
     */
    public RuleChecker(Level level) {
        l = level;
        levellayout = level.getLevelLayout();
    }

    /**
     * Determines the interaction between the given Player and the object on the destination tile.
     * @param player represents the given Player
     * @param adversaryPositions represents a Hashmap of Adversary ID's to their Positions
     * @param playerPositions represents a Hashmap of Player ID's to their Positions
     * @param exitStatus represents a boolean determining the current exitStatus
     * @return different integers for different cases; 0 for key event, 1 for locked exit event,
     *         2 for unlocked exit event, 3 for adversary event, 4 for an empty tile
     */
    static int determinePlayerInteraction(Player player,
                                          HashMap<Integer, Position> adversaryPositions, HashMap<Integer, Position> playerPositions, Boolean exitStatus) {
        Position curr = player.getP();
        int tileType = levellayout.get(curr);
        if (tileType ==7) {
            return 0;
        } else if (tileType == 8 && !exitStatus) {
            return 1;
        } else if (tileType == 8 && exitStatus) {
            return 2;
        } else if (adversaryPositions.containsValue(curr)) {
            return 3;
        } else if (playerPositions.containsValue(curr)) {
            throw new IllegalArgumentException("A player interacts with another player!");
        } else {
            return 4;
        }
    }

    static int determinePlayerInteractionTest(Position curr,
                                          ArrayList<Position> adversaryPositions, HashMap<String, Position> playerPositions, Boolean exitStatus) {
        if (curr.equals(new Position(-1, -1))) {
            return 4;
        }
        int tileType = levellayout.get(curr);
        if (adversaryPositions.contains(curr)) {
            return 3;
        } else if (tileType == 8 && !exitStatus) {
            return 1;
        } else if (tileType == 8 && exitStatus) {
            return 2;
        } else if (tileType ==7) {
            return 0;
        } else if (playerPositions.containsValue(curr)) {
            throw new IllegalArgumentException("A player interacts with another player!");
        } else {
            return 4;
        }
    }

    /**
     * Determines the interaction between the given Adversary and the object on the destination tile.
     * @param adversary represents the given Adversary
     * @param adversaryPositions represents a Hashmap of Adversary ID's to their Positions
     * @param playerPositions represents a Hashmap of Player ID's to their Positions
     * @return an int representing the result; 1 represents the adversary kills a player,
     *         and 0 represents the tile is empty and there is no interaction
     */
    int determineAdversaryInteraction(Adversary adversary, HashMap< Integer, Position> adversaryPositions,
                                      HashMap< Integer, Position> playerPositions) {
        Position curr = adversary.getP();
        if (adversaryPositions.containsValue(curr)) {
            throw new IllegalArgumentException("An adversary interacts with another adversary!");
        } else if (playerPositions.containsValue(curr)) {
            return 1;
        } else {
            return 0;
        }
    }

    //playerArrived is a boolean that is saved in gamestate, and it will be 0 if any player has arrived the level exit
//while the exit has been unlocked, 1 if the the level is not end, 2 if the level has ended and the current level is the
//final level, 3 if all players are expelled

    /**
     * Determines if the level has ended and the current circumstances of the game.
     * @param playerArrived represents if a player has arrived at the exit
     * @param isFinalLevel represents if this level is the final level
     * @param players represents an ArrayList of the active Players
     * @return an int representing the result; 0 if any player has arrived the level exit while the
     *         exit has been unlocked, 1 if the the level is not end, 2 if the level has ended and
     *         the current level is the final level, 3 if all players are expelled
     */
    int isLevelEnd(boolean playerArrived, boolean isFinalLevel, ArrayList<Player> players) {
        boolean exitStatus = l.getExitStatus();
        if (playerArrived && exitStatus) {
            return 0;
        } else if (!playerArrived) {
            return 1;
        } else if (isFinalLevel) {
            return 2;
        } else if (allPlayersExpelled(players)) {
            return 3;
        }
        return -1;
    }

    /**
     * Checks if all players have been expelled from the current level.
     * @param players represents an ArrayList of the active Players
     * @return a boolean representing if all of the players have been expelled
     */
    boolean allPlayersExpelled(ArrayList<Player> players) {
        return players.size() ==0;
    }

    /**
     * Checks if the game state is valid (no duplicate player or adversary positions and that players
     * and adversaries are on traversable tiles).
     * @param gameState represents the current GameState
     * @return a boolean determining if the given GameState is valid
     */
    boolean isGameStateValid(GameState gameState) {
        ArrayList<Position> playerPositions = gameState.getPlayerPositions();
        ArrayList<Position> adversaryPositions = gameState.getAdversaryPositions();
        for (int i =0;i<playerPositions.size() ;i++) {
            Position curr = playerPositions.get(i);
            if(!validTile(curr)) {
                System.out.print(curr.getx() + " "+curr.gety() + " causes invalid game state" );
                return false;
            }
            if(checkDuplicates(curr, playerPositions)) {
                System.out.print("duplicate players on " + curr.getx() + " "+curr.gety());
                return false;
            }
        }
        for (int i =0;i<adversaryPositions.size() ;i++) {
            Position curr = adversaryPositions.get(i);
            if(!validTile(curr)) {
                System.out.print(curr.getx() + " "+curr.gety() + "causes invalid game state" );
                return false;
            }
            adversaryPositions.remove(curr);
            if(checkDuplicates(curr, adversaryPositions)) {
                System.out.print("duplicate adversaries on " + curr.getx() + " "+curr.gety());
                return false;
            }
        }

        return true;
    }

    /**
     * Helper method for isGameStateValid that checks if there are duplicate player or adversary positions.
     * @param pos represents a given position
     * @param positionArrayList represents an arraylist of positions
     * @return a boolean determining if there are duplicate positions in the given arraylist
     */
    public boolean checkDuplicates(Position pos, ArrayList<Position> positionArrayList) {
        int counter = 0;
        for (int i = 0; i < positionArrayList.size(); i++) {
//            pos.print();
//            positionArrayList.get(i).print();
            if(pos.equals(positionArrayList.get(i))) {
                counter++;
            }
        }
        return counter > 1;
    }

    /**
     * Checks if a player's next move is within the 2 cardinal moves range and not overlapped with other
     * players' positions
     * @param pos represents the player's next destination
     * @param player represents the given player
     * @param playerPositons represents an array list of current player positions
     * @return a boolean determining the player's next move is within the 2 cardinal moves range and not overlapped
     * with other players' positions
     */
   public static boolean isValidPlayerMove(Position pos, Player player, HashMap<Integer, Position> playerPositons) {
        Position curr = player.getP();
        ArrayList<Position> possiblePositions = new ArrayList<>();
        possiblePositions.add(curr);
        ArrayList<Position> firstCardinal = calculate1Cardinal(curr);
        possiblePositions.addAll(firstCardinal);
        for (int i =0;i< firstCardinal.size();i++) {
            ArrayList<Position> temp;
            temp = calculate1Cardinal(firstCardinal.get(i));
            possiblePositions.addAll(temp);
        }
        return possiblePositions.contains(pos) && !playerPositons.containsValue(pos);

    }

    public static boolean isValidPlayerMoveTest(String name, Position current, Position move, HashMap<String, Position> playerPositions) {
       if (move.equals(new Position(-1, -1))) {
           return true;
       }
        ArrayList<Position> possiblePositions = new ArrayList<>();
        possiblePositions.add(current);
        ArrayList<Position> firstCardinal = calculate1Cardinal(current);
        possiblePositions.addAll(firstCardinal);
        for (int i =0;i< firstCardinal.size();i++) {
            ArrayList<Position> temp;
            temp = calculate1Cardinal(firstCardinal.get(i));
            possiblePositions.addAll(temp);
        }
        return possiblePositions.contains(move) && !playerPositions.containsValue(move);

    }


    /**
     * Calculate tiles that are visible to a player
     * @param pos represents the player's current position
     * @return an array list of positions representing the visible tiles
     */
    public static ArrayList<Position> calculatePlayerVisibleTiles(Position pos) {
        ArrayList<Position> possiblePositions = new ArrayList<>();
        possiblePositions.add(pos);
        ArrayList<Position> firstCardinal = calculate1Cardinal(pos);
        possiblePositions.addAll(firstCardinal);
        for (int i =0;i< firstCardinal.size();i++) {
            ArrayList<Position> temp;
            temp = calculate1Cardinal(firstCardinal.get(i));
            if(!possiblePositions.containsAll(temp)) {
                possiblePositions.addAll(temp);
            }


        }
        return possiblePositions;
    }

    /**
     * Checks if an adversary's next destination is within the 1 cardinal move range and not overlapped with other
     * adversaries' positions
     * @param pos represents the adversary's next destination
     * @param adversary represents the given adversary
     * @param adversaryPositons represents an array list of positions of the current adversaries
     * @return a boolean determing if the adversary's next destination is valid
     */
    boolean isValidAdversaryMove(Position pos, Adversary adversary, HashMap<Integer, Position> adversaryPositons) {
        Position curr = adversary.getP();
        //In case adversaries are modified in the future
        int cardinalMoves = adversary.getCardinalMoves();
        ArrayList<Position> cardinalSoFar = calculate1Cardinal(curr);
        for (int j =0; j<cardinalMoves;j++) {
            int cardinalCurr = cardinalSoFar.size();
            for (int i = 0; i <  cardinalCurr; i++) {
                ArrayList<Position> temp;
                temp = calculate1Cardinal(cardinalSoFar.get(i));
                cardinalSoFar.addAll(temp);
            }

        }

        return cardinalSoFar.contains(pos) && !adversaryPositons.containsValue(pos);

    }

    /**
     * Calculate tiles that are 1 cardinal move from the current tile
     * @param pos represents the current position
     * @return an array list of positions represents the 1 cardinal move tiles
     */
    public static ArrayList<Position> calculate1Cardinal(Position pos) {
        ArrayList<Position> results = new ArrayList<>();
        int x = pos.getx();
        int y = pos.gety();
        //upper position
        if (y>=1) {
            Position temp = new Position(x, y-1);
            if (validTile(temp)) {
                results.add(temp);
            }
        }
        //left position
        if (x>=1) {
            Position temp = new Position(x-1,y);
            if (validTile(temp)) {
                results.add(temp);
            }
        }
        //right
        Position temp = new Position(x+1,y);
        if (validTile(temp)) {
            results.add(temp);
        }
        //bottom
        Position bottom = new Position(x, y+1);
        if (validTile(bottom)) {
            results.add(bottom);
        }
        return results;
    }

    /**
     * Checks if a tile is traversable
     * @param pos represents the given position
     * @return a boolean determining if a tile is traversable
     */
    public static Boolean validTile(Position pos) {
        if(levellayout.get(pos) == null) {
            return false;
        }
        int tileType = levellayout.get(pos);
        return (tileType == 2 || tileType == 4 || tileType == 5 || tileType == 6 || tileType == 7
                || tileType == 8);

    }
}
