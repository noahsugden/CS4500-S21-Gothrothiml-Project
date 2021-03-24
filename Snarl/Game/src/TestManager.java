import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TestManager {
  static ArrayList<String> names;
  static Level level;
  static JSONObject levelObject;
  static int natural;
  static ArrayList<Position> positionList;
  static ArrayList<Position> adversaryPositions;
  static HashMap<String, Position> playerOrigins = new HashMap<>();
  static HashMap<String, ArrayList<Position>> playerMoves = new HashMap<>();
  static GameManager gameManager;
  static ArrayList<String> alivePlayers;
  static HashMap<String,Position> adversaryOrigins = new HashMap<>();


  static void readString(String s) throws JSONException {
    JSONArray manager = new JSONArray(s);
    JSONArray nameList = manager.getJSONArray(0);
    names = new ArrayList<>();
    for(int i = 0; i < nameList.length(); i++) {
      names.add(nameList.getString(i));
    }
    alivePlayers = names;
    levelObject = manager.getJSONObject(1);
    level = TestLevel.getLevel(levelObject);
    natural = manager.getInt(2);
    JSONArray pointList = manager.getJSONArray(3);
    positionList = new ArrayList<>();
    for(int i = 0; i < pointList.length(); i++) {
      positionList.add(modifyPosition(pointList.getJSONArray(i)));
    }
    generatePlayerAndAdversaryPositionsMap();
    for (int i=0;i<adversaryPositions.size();i++) {
      adversaryOrigins.put("zombie#"+i, adversaryPositions.get(i));
    }
    JSONArray actorMoveListList = manager.getJSONArray(4);
    generatePlayerMovesMap(actorMoveListList);

    gameManager = new GameManager(level, adversaryOrigins, playerOrigins);
    JSONArray output = new JSONArray();
    JSONArray managerTrace = generateManagerTrace();
    JSONObject state = generateState();
    output.put(state);
    output.put(managerTrace);
    System.out.print(output.toString());
  }

  public static void generatePlayerAndAdversaryPositionsMap() {
    adversaryPositions = new ArrayList<>();
    for(int i = 0; i < names.size(); i++) {
      playerOrigins.put(names.get(i), positionList.get(i));
    }
    for(int i = names.size(); i < positionList.size(); i++) {
      adversaryPositions.add(positionList.get(i));
    }
  }

  public static JSONArray generateManagerTrace() throws JSONException {
    JSONArray trace = new JSONArray();
    //generate the initial updates
    JSONArray initialUpdate = generatePlayerUpdate();
    for (int i = 0;i < initialUpdate.length();i++) {
      trace.put(initialUpdate.get(i));
    }
    int turn = 0;
    while (turn<natural && alivePlayers.size()>0) {
      for(int i =0;i<alivePlayers.size();i++) {
        String name = alivePlayers.get(i);
        ArrayList<Position> moves = playerMoves.get(name);
        if (moves.size()==0) {
          return trace;
        }
        JSONArray actorMove = generateActorMove(name, moves.get(0));
        trace.put(actorMove);
        while (moves.size()!=0 &&!gameManager.determineValidMove(name, moves.get(0))) {
          moves.remove(0);
        }
        if (moves.size()==0) {
          return trace;
        }
        JSONArray updates = generatePlayerUpdate();
        for (int j = 0;j < updates.length();j++) {
          trace.put(updates.get(j));
        }
      }
      turn = turn +1;
    }
    return trace;

  }

  public static JSONObject generateState() throws JSONException {
    HashMap<String, Position> finalPositionsMap = gameManager.getCurrentGameState().getPlayerPositionsMap();
    boolean exitStatus = gameManager.getCurrentGameState().getExitStatus();
    JSONObject state = new JSONObject();
    state.put("type", "state");
    if (exitStatus) {
      JSONArray objects = levelObject.getJSONArray("objects");
      objects.remove(0);
      levelObject.remove("objects");
      levelObject.put("objects", objects);
      state.put("exit-locked", false);
      state.put("level", levelObject);
    } else {
      state.put("level", levelObject);
    }
    JSONArray actorPositionList = new JSONArray();
    for (int i=0;i<alivePlayers.size();i++) {
      String name = alivePlayers.get(i);
      Position curr = gameManager.getCurrentGameState().getPlayerPositionsMap().get(name);
      JSONArray positionArray = positionToArray(curr);
      JSONObject actorObject = new JSONObject();
      actorObject.put("type", "player");
      actorObject.put("name", name);
      actorObject.put("position", positionArray);
      actorPositionList.put(actorObject);
    }
    state.put("players", actorPositionList);
    JSONArray adversaryPositionList = new JSONArray();
    for (String s: adversaryOrigins.keySet()){
      Position curr = adversaryOrigins.get(s);
      JSONArray positionArray = positionToArray(curr);
      JSONObject adversaryObject = new JSONObject();
      adversaryObject.put("type", "zombie");
      adversaryObject.put("name", s);
      adversaryObject.put("position", positionArray);
      adversaryPositionList.put(adversaryObject);
    }
    state.put("adversaries", adversaryPositionList);
    if(exitStatus) {
      state.put("exit-locked", false);
    } else {
      state.put("exit-locked", true);
    }
return state;
  }




  public static void generatePlayerMovesMap(JSONArray actorMoveListList) throws JSONException {
    playerMoves = new HashMap<>();
    Position destination;

    for(int i = 0; i < actorMoveListList.length(); i++) {
      JSONArray curr = actorMoveListList.getJSONArray(i);
      ArrayList<Position> positions = new ArrayList<>();
      for(int j = 0; j < curr.length(); j++) {
        JSONObject temp = curr.getJSONObject(j);
        if(temp.get("to")==JSONObject.NULL) {
          destination = new Position(-1, -1);
        } else {
          JSONArray move = temp.getJSONArray("to");
          destination = modifyPosition(move);
        }
        positions.add(destination);
      }
      playerMoves.put(names.get(i), positions);
    }
  }

  public static Position modifyPosition(JSONArray jsonArray) throws JSONException {
    int x = jsonArray.getInt(0);
    int y = jsonArray.getInt(1);
    return new Position(x, y);
  }

  public static JSONArray generateActorMove(String name, Position move) throws JSONException {
    JSONArray result = new JSONArray();
    result.put(name);
    if (move.equals(new Position(-1, -1))) {
      JSONObject moveObject = new JSONObject();
      moveObject.put("type", "move");
      moveObject.put("to", JSONObject.NULL);
      result.put(moveObject);
      result.put("OK");
      return result;
    } else {
      JSONObject moveObject = new JSONObject();
      moveObject.put("type", "move");
      moveObject.put("to", positionToArray(move));
      result.put(moveObject);
      boolean valid = gameManager.determineValidMove(name, move);
      if (!valid) {
        result.put("Invalid");
        return result;
      } else {
        int interactResult = gameManager.interactAfterMove(name, move);
        if (interactResult == 0) {
          result.put("Key");
          return result;
        } else if(interactResult ==2) {
          result.put("Exit");
          alivePlayers.remove(name);
          return result;
        } else if (interactResult ==3) {
          result.put("Eject");
          alivePlayers.remove(name);
          return result;
        }
      }
    }

    return result;
  }

  public static JSONArray generatePlayerUpdate() throws JSONException {
    JSONArray result = new JSONArray();
    GameState gs = gameManager.getCurrentGameState();
    HashMap<String,Position> playerPositionsMap = gs.getPlayerPositionsMap();
    for (int i =0; i< alivePlayers.size();i++) {
      JSONArray playerUpdateArray = new JSONArray();
      playerUpdateArray.put(alivePlayers.get(i));
      JSONObject playerUpdate = new JSONObject();
      playerUpdate.put( "type","player-update");
      Position curr = playerPositionsMap.get(alivePlayers.get(i));
      JSONArray tileLayout = getTileLayout(curr);
      playerUpdate.put( "layout", tileLayout);
      JSONArray position = positionToArray(curr);
      playerUpdate.put("position", position);
      HashMap<String, Position> objects = gameManager.getVisibleObjects(curr);
      JSONArray objectArray = new JSONArray();
      for (String s:objects.keySet()) {
        if(s.equals("key")) {
          JSONObject keyObject = new JSONObject();
          keyObject.put("type", "key");
          keyObject.put("position", positionToArray(objects.get("key")));
          objectArray.put(keyObject);
        } else if(s.equals("exit")) {
          JSONObject exitObject = new JSONObject();
          exitObject.put("type", "exit");
          exitObject.put("position", positionToArray(objects.get("exit")));
          objectArray.put(exitObject);
        }
      }
      playerUpdate.put("objects", objectArray);
      playerUpdate.put("actors", generateActorPositionList(curr));
      result.put(playerUpdate);
    }
    return result;
  }

  public static JSONArray generateActorPositionList(Position pos) throws JSONException {
    JSONArray result = new JSONArray();
    HashMap<String, Position> actors = gameManager.getVisibleActors(pos);
    for(String s: actors.keySet()) {
      if(alivePlayers.contains(s)){
        JSONObject playerObject = new JSONObject();
        playerObject.put("type", "player");
        playerObject.put("name", s);
        playerObject.put("position", positionToArray(actors.get(s)));
      } else {
        JSONObject adversaryObject = new JSONObject();
        adversaryObject.put("type", "zombie");
        adversaryObject.put("name", s);
        adversaryObject.put("position", positionToArray(actors.get(s)));
      }
    }
    return result;
  }

  public static JSONArray positionToArray(Position p){
    JSONArray result = new JSONArray();
    result.put(p.getx());
    result.put(p.gety());
    return result;
  }

  public static JSONArray getTileLayout(Position pos) {
    int[][] tileArray = gameManager.getVisibleArea(pos);
    JSONArray result = new JSONArray();
    for(int i = 0; i < tileArray.length; i++) {
      JSONArray curr = new JSONArray();
      for(int j = 0; j < tileArray.length; j++) {
        if(tileArray[j][i] == 1) {
          curr.put(0);
        } else if(tileArray[j][i] == 2 || tileArray[j][i] == 7 || tileArray[j][i] == 8 || tileArray[j][i]==5 || tileArray[j][i]==6) {
          curr.put(1);
        } else if(tileArray[j][i] == 4) {
          curr.put(2);
        } else {
          curr.put(0);
        }
      }
      result.put(curr);
    }
    return result;
  }

  public static void main(String[] args) throws JSONException{
    readString("[ [\"ferd\", \"dio\"]\n" +
            ", { \"type\": \"level\",\n" +
            "      \"rooms\": [ { \"type\": \"room\",\n" +
            "        \"origin\": [ 0, 0 ],\n" +
            "        \"bounds\": { \"rows\": 5, \"columns\": 5 },\n" +
            "        \"layout\": [ [ 0, 0, 2, 0, 0 ],\n" +
            "          [ 0, 1, 1, 0, 0 ],\n" +
            "          [ 0, 1, 1, 0, 2 ],\n" +
            "          [ 0, 1, 0, 1, 0 ],\n" +
            "          [ 0, 0, 0, 0, 0 ]] },\n" +
            "        { \"type\": \"room\",\n" +
            "          \"origin\": [ 8, 9 ],\n" +
            "          \"bounds\": { \"rows\": 4, \"columns\": 3 },\n" +
            "          \"layout\": [ [ 0, 0, 1 ],\n" +
            "            [ 0, 1, 1 ],\n" +
            "            [ 2, 1, 1 ],\n" +
            "            [ 0, 1, 2 ]] },\n" +
            "        { \"type\": \"room\",\n" +
            "          \"origin\": [ 2, 16 ],\n" +
            "          \"bounds\": { \"rows\": 4, \"columns\": 4 },\n" +
            "          \"layout\": [ [ 0, 0, 2, 0 ],\n" +
            "            [ 0, 1, 1, 1 ],\n" +
            "            [ 0, 1, 1, 2 ],\n" +
            "            [ 0, 1, 1, 1 ]] } ],\n" +
            "      \"objects\": [ { \"type\": \"key\", \"position\": [ 2, 2 ] },\n" +
            "        { \"type\": \"exit\", \"position\": [ 5, 17 ] } ],\n" +
            "      \"hallways\": [ { \"type\": \"hallway\",\n" +
            "        \"from\": [ 2, 4 ],\n" +
            "        \"to\": [ 10, 9 ],\n" +
            "        \"waypoints\": [ [2, 5], [ 10, 5 ]] },\n" +
            "        { \"type\": \"hallway\",\n" +
            "          \"from\": [ 11, 11 ],\n" +
            "          \"to\": [ 4, 19 ],\n" +
            "          \"waypoints\": [ [ 11, 20 ], [ 4, 20 ]] } ]\n" +
            "    }" +
            ", 5\n" +
            ", [ [3, 2], [5, 8], [14, 12] ]\n" +
            ", [ [ { \"type\": \"move\", \"to\": [4, 2] }\n" +
            "    , { \"type\": \"move\", \"to\": [5, 3] } \n" +
            "    ]\n" +
            "  , [ { \"type\": \"move\", \"to\": [3, 6] }\n" +
            "    , { \"type\": \"move\", \"to\": [4, 7] }\n" +
            "    , { \"type\": \"move\", \"to\": [6, 7] } \n" +
            "    ]\n" +
            "  ]\n" +
            "]");
  }
}
