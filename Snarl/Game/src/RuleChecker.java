import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.HashMap;

public class RuleChecker {
    static Level l;
    static HashMap<Position, Integer> levellayout;
    RuleChecker(Level l) {
        this.l = l;
        this.levellayout = l.getLevelLayout();
    }

    //To determine the interaction between the player and the object on the destination tile
//The method will return different integers for different cases. For example, 0 for key event,1 for locked exit event,
//2 for unlocked exit event, 3 for adversary event, 4 for an empty tile, 5 for another player.
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

    //1 represents the adversary kills a player, and 0 represents the tile is empty and there is no interaction
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

    boolean allPlayersExpelled(ArrayList<Player> players) {
        return players.size() ==0;
    }

    boolean isGameStateValid(GameState gameState) {
        ArrayList<Position> playerPositions = gameState.getPlayerPositions();
        ArrayList<Position> adversaryPositions = gameState.getAdversaryPositions();
        for (int i =0;i<playerPositions.size() ;i++) {
            Position curr = playerPositions.get(i);
            if(validTile(curr)) {
                System.out.print(curr.getx() + " "+curr.gety() + "causes invalid game state" );
                return false;
            }
            playerPositions.remove(curr);
            if(playerPositions.contains(curr)) {
                System.out.print("duplicate players on " + curr.getx() + " "+curr.gety());
                return false;
            }
        }
        for (int i =0;i<adversaryPositions.size() ;i++) {
            Position curr = adversaryPositions.get(i);
            if(validTile(curr)) {
                System.out.print(curr.getx() + " "+curr.gety() + "causes invalid game state" );
                return false;
            }
            adversaryPositions.remove(curr);
            if(adversaryPositions.contains(curr)) {
                System.out.print("duplicate adversaries on " + curr.getx() + " "+curr.gety());
                return false;
            }
        }
        return true;
    }

   public static boolean isValidPlayerMove(Position pos, Player player, HashMap<Integer, Position> playerPositons) {
        Position curr = player.getP();
        ArrayList<Position> possiblePositions = new ArrayList<>();
        possiblePositions.add(curr);
        ArrayList<Position> firstCardinal = calculate1Cardinal(curr);
        for (int i =0;i< firstCardinal.size();i++) {
            ArrayList<Position> temp;
            temp = calculate1Cardinal(firstCardinal.get(i));
            possiblePositions.addAll(temp);
        }
        return possiblePositions.contains(pos) && !playerPositons.containsValue(pos);

    }

    public static ArrayList<Position> calculatePlayerVisibleTiles(Position pos) {
        ArrayList<Position> possiblePositions = new ArrayList<>();
        possiblePositions.add(pos);
        ArrayList<Position> firstCardinal = calculate1Cardinal(pos);
        for (int i =0;i< firstCardinal.size();i++) {
            ArrayList<Position> temp;
            temp = calculate1Cardinal(firstCardinal.get(i));
            possiblePositions.addAll(temp);
        }
        return possiblePositions;
    }

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

    static ArrayList<Position> calculate1Cardinal(Position pos) {
        ArrayList<Position> results = new ArrayList<>();
        int x = pos.getx();
        int y = pos.gety();
        //upper position
        if (y>1) {
            Position temp = new Position(x, y-1);
            if (validTile(temp)) {
                results.add(temp);
            }
        }
        //left position
        if (x>1) {
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

    static Boolean validTile(Position pos) {
        int tileType = levellayout.get(pos);
        return (tileType == 2 || tileType == 4 || tileType == 5 || tileType == 6 || tileType == 7
                || tileType == 8);

    }
}
