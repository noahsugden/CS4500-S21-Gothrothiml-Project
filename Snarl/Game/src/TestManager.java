import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TestManager {
  ArrayList<String> names;
  Level level;
  int natural;
  ArrayList<Position> positionList;
  ArrayList<Position> adversaryPositions;
  HashMap<String, Position> playerOrigins = new HashMap<>();
  HashMap<String, ArrayList<Position>> playerMoves = new HashMap<>();
  GameManager gameManager;


  void readString(String s) {
    JSONArray manager = new JSONArray(s);
    JSONArray nameList = manager.getJSONArray(0);
    names = new ArrayList<>();
    for(int i = 0; i < nameList.length(); i++) {
      names.add(nameList.getString(i));
    }
    level = TestLevel.getLevel(manager.getJSONObject(1));
    natural = manager.getInt(2);
    JSONArray pointList = manager.getJSONArray(3);
    positionList = new ArrayList<>();
    for(int i = 0; i < pointList.length(); i++) {
      positionList.add(modifyPosition(pointList.getJSONArray(i)));
    }
    generatePlayerAndAdversaryPositionsMap();
    JSONArray actorMoveListList = manager.getJSONArray(4);
    generatePlayerMovesMap(actorMoveListList);
    gameManager = new GameManager(level, adversaryPositions, playerOrigins);

  }

  public void generatePlayerAndAdversaryPositionsMap() {
    adversaryPositions = new ArrayList<>();
    for(int i = 0; i < names.size(); i++) {
      playerOrigins.put(names.get(i), positionList.get(i));
    }
    for(int i = names.size(); i < positionList.size(); i++) {
      adversaryPositions.add(positionList.get(i));
    }
  }

  public void generatePlayerMovesMap(JSONArray actorMoveListList) {
    playerMoves = new HashMap<>();

    for(int i = 0; i < actorMoveListList.length(); i++) {
      JSONArray curr = actorMoveListList.getJSONArray(i);
      ArrayList<Position> positions = new ArrayList<>();
      for(int j = 0; j < curr.length(); j++) {
        JSONObject temp = curr.getJSONObject(j);
        JSONArray move = temp.getJSONArray("to");
        Position destination = modifyPosition(move);
        positions.add(destination);
      }
      playerMoves.put(names.get(i), positions);
    }
  }

  public Position modifyPosition(JSONArray jsonArray) {
    int x = jsonArray.getInt(0);
    int y = jsonArray.getInt(1);
    return new Position(x, y);
  }

  public JSONArray getTileLayout(int[][] tileArray) {
    JSONArray result = new JSONArray();
    for(int i = 0; i < tileArray.length; i++) {
      JSONArray curr = new JSONArray();
      for(int j = 0; j < tileArray.length; j++) {
        if(tileArray[j][i] == 1) {
          curr.put(0);
        }
        if(tileArray[j][i] == 2 || tileArray[j][i] == 7 || tileArray[j][i] == 8) {
          curr.put(1);
        }
        if(tileArray[j][i] == 4) {
          curr.put(2);
        }
      }
      result.put(curr);
    }
    return result;
  }
}
