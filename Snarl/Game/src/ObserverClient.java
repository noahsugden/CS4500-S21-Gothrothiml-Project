import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

public class ObserverClient implements Observer {

  GameState currentGameState;

  public ObserverClient(GameState currentGameState) {
    this.currentGameState = currentGameState;
  }

  @Override
  public void register() {

  }

  @Override
  public void receiveGameState() {

  }

  @Override
  public void renderGameState() {
    HashMap<String, Position> surroundingPositions = new HashMap<>();
    HashMap<String, Position> playerPositionsMap = currentGameState.getPlayerPositionsMap();
    HashMap<String, Position> adversaryPositionsMap = currentGameState.getAdversaryPositionsMap();
    HashMap<String, Position> objectPositionsMap = currentGameState.getObjectPositionsMap();
    surroundingPositions.putAll(playerPositionsMap);
    surroundingPositions.putAll(adversaryPositionsMap);
    surroundingPositions.putAll(objectPositionsMap);
    int[][] visibleTiles = currentGameState.l.get2Darray();

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
      if (playerPositionsMap.containsKey(s)) {
        Position player = surroundingPositions.get(s);
        int playerX = player.getx();
        int playerY = player.gety();
        visibleTiles[playerX][playerY] = 3;
      }
      if (adversaryPositionsMap.containsKey(s)) {
        Position adversary = surroundingPositions.get(s);
        int adversaryX = adversary.getx();
        int adversaryY = adversary.gety();
        visibleTiles[adversaryX][adversaryY] = 9;
      }
    }

    HashMap<Position, Integer> levelLayout = currentGameState.l.getLevelLayout();
    int keyX = -1;
    int keyY = -1;
    for(Entry<Position, Integer> e : levelLayout.entrySet()) {
      if(levelLayout.get(e.getKey()) == 7) {
        keyX = e.getKey().getx();
        keyY = e.getKey().gety();
      }
    }
    if(keyX == -1 || keyY == -1) {
      throw new IllegalArgumentException("Didn't find key in level layout!");
    }
    if(!surroundingPositions.containsKey("key")) {
      visibleTiles[keyX][keyY] = 2;
    }
    print2D(visibleTiles);
  }

  @Override
  public void unregister() {

  }


  @Override
  public void print2D(int[][] array)
  {
    String original = "";
    String formatted = "";
    for (int[] row : array) {
      original = Arrays.toString(row);
      int size = original.length();
      formatted = original.substring(1, size - 2);
      char[] chars= formatted.toCharArray();
      for(int i =0; i<chars.length;i++) {
        char temp = chars[i];
        //3 means a player
        if (temp == '3') {
          chars[i] = 'p';
          //0 means nothing
        } else if (temp == '0') {
          chars[i] = ' ';
        }
        //1 means wall tile
        else if (temp == '1') {
          chars[i] = '*';
          //2 is non-wall tile
        } else if (temp == '2') {
          chars[i] = '.';
          //4 is a door
        } else if (temp == '4') {
          chars[i]='<';
          //5 is a horizontal hallway
        } else if (temp == '5') {
          chars[i]='-';
          //6 is a vertical hallway
        } else if (temp == '6') {
          chars[i]='|';
          //7 is a key
        } else if (temp == '7') {
          chars[i] = 'k';
          //8 is an exit
        } else if (temp == '8') {
          chars[i] = 'e';
        } else if (temp == '9') {
          chars[i] = 'A';
        }
        else if (chars[i] == ',') {
          chars[i] = (char) 0;
        }
      }
      String lastString = new String(chars);
      System.out.println(lastString);
    }
  }
}
