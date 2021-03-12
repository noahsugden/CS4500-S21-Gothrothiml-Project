
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TestState {
  static Level currentLevel;
  static HashMap<String, Position> playerPositions = new HashMap<>();
  static HashMap<String, Position> zombiePositions = new HashMap<>();
  static HashMap<String, Position> ghostPositions = new HashMap<>();
  static GameState gameState;
  static boolean exitStatus;
  static JSONObject state;
  static JSONObject level;

  static void readJsonState(String s) throws JSONException {
    JSONArray input = new JSONArray(s);
    state = input.getJSONObject(0);
    level = state.getJSONObject("level");
    currentLevel = TestLevel.getLevel(level);
    JSONArray players = state.getJSONArray("players");
    readPositions(players);
    JSONArray adversaries = state.getJSONArray("adversaries");
    readPositions(adversaries);
    exitStatus = state.getBoolean("exit-locked");
    String name = input.getString(1);
    JSONArray position = input.getJSONArray(2);
    int x = position.getInt(0);
    int y = position.getInt(1);
    Position pos = new Position(x, y);
    gameState = new GameState(playerPositions, zombiePositions,
            ghostPositions, exitStatus, currentLevel);
    System.out.println(output(gameState, name, pos).toString());

  }

  static void readPositions(JSONArray positions) throws JSONException {
    HashMap<String, Position> temp = new HashMap<>();
    for (int i = 0; i < positions.length(); i++) {
      JSONObject curr = positions.getJSONObject(i);
      String type = curr.getString("type");
      String name = curr.getString("name");
      JSONArray currPosition = curr.getJSONArray("position");
      int x = currPosition.getInt(0);
      int y = currPosition.getInt(1);
      Position position = new Position(x, y);
      if (type.equals("player")) {
        playerPositions.put(name, position);
      } else if (type.equals("zombie")) {
        zombiePositions.put(name, position);
      } else if (type.equals("ghost")) {
        ghostPositions.put(name, position);
      }
    }
  }

  static JSONArray output(GameState gs, String name, Position pos) throws JSONException {
    JSONArray result = new JSONArray();
    //Invalid player name input
    if (!playerPositions.containsKey(name)) {
      result.put("Failure");
      result.put("Player");
      result.put(name);
      result.put(" is not a part of the game.");
      return result;
    }
    HashMap<Position, Integer> levelLayout = currentLevel.getLevelLayout();
    int tileType = levelLayout.get(pos);

    //Destination is non-traversable
    if (tileType != 2 && tileType != 4 && tileType != 5 && tileType != 6 && tileType != 7
            && tileType != 8 || playerPositions.containsValue(pos)) {
      result.put("Failure");
      result.put("The destination position ");
      JSONArray position = new JSONArray();
      position.put(pos.getx());
      position.put(pos.gety());
      result.put(position);
      result.put(" is invalid.");
      return result;
    }
    //For when player exits
    if (tileType == 8 && !exitStatus) {
      result.put("Success");
      result.put("Player ");
      result.put(name);
      result.put(" exited.");
      JSONObject newState = modifyState(name, pos, state, true, false);
      result.put(newState);
      return result;
    }

    //If destination has adversary
    if (zombiePositions.containsValue(pos) || ghostPositions.containsValue(pos)) {
      result.put("Success");
      result.put("Player ");
      result.put(name);
      result.put(" was ejected.");
      JSONObject newState = modifyState(name, pos, state, true, false);
      result.put(newState);
      return result;
    }

    //If destination contains a key
    if (tileType == 7) {
      JSONObject newState = modifyState(name, pos, state, false, true);
      result.put("Success");
      result.put(newState);
      return result;
    }
    //If the given player exists in the input state, can be moved to the given position, and
    //the position is not occupied by an exit or an adversary
    JSONObject newState = modifyState(name, pos, state, false, false);
    result.put("Success");
    result.put(newState);


    return result;
  }

  static JSONObject modifyState(String name, Position p, JSONObject prev, boolean removePlayer, boolean removeKey) throws JSONException {
    prev.remove("players");

    JSONArray players = new JSONArray();
    for (String s : playerPositions.keySet()) {
      JSONObject curr = new JSONObject();
      curr.put("type", "player");
      curr.put("name", s);

      if (s.equals(name)) {
        if (!removePlayer) {
          JSONArray temp = new JSONArray();
          temp.put(p.getx());
          temp.put(p.gety());
          curr.put("position", temp);
        } else {
          continue;
        }
      } else {
        JSONArray temp = new JSONArray();
        Position currPosition = playerPositions.get(s);
        temp.put(currPosition.getx());
        temp.put(currPosition.gety());
        curr.put("position", temp);

      }
      players.put(curr);
    }
    prev.put("players", players);

    if (removeKey) {
      JSONArray objects = level.getJSONArray("objects");
      objects.remove(0);
      level.remove("objects");
      level.put("objects", objects);

      prev.remove("exit-locked");
      prev.remove("level");
      prev.put("exit-locked", false);
      prev.put("level", level);
      return prev;



    }
    return prev;
  }

  public static void main(String[] args) throws JSONException {
//   Scanner sc = new Scanner(System.in);
//   StringBuilder sb = new StringBuilder();
//   while (sc.hasNextLine()){
//     String curr = sc.nextLine();
//     sb.append(curr);
//    }
//   String input = sb.toString();
    String input = "[\n" +
            "  {\n" +
            "    \"exit-locked\": false,\n" +
            "    \"players\": [\n" +
            "      { \"name\": \"ferd\", \"type\": \"player\", \"position\": [ 4, 3 ] },\n" +
            "      { \"name\": \"joe\", \"type\": \"player\", \"position\": [ 12, 8 ] }\n" +
            "    ],\n" +
            "    \"type\": \"state\",\n" +
            "    \"adversaries\": [\n" +
            "      { \"name\": \"zombie1\", \"type\": \"zombie\", \"position\": [ 7, 15 ] }\n" +
            "    ],\n" +
            "    \"level\": {\n" +
            "      \"type\" : \"level\",\n" +
            "      \"rooms\": [\n" +
            "        {\n" +
            "          \"origin\": [ 10, 5 ],\n" +
            "          \"layout\": [\n" +
            "            [ 0, 0, 0, 0, 0 ],\n" +
            "            [ 0, 1, 1, 1, 0 ],\n" +
            "            [ 2, 1, 1, 1, 0 ],\n" +
            "            [ 0, 1, 1, 1, 0 ],\n" +
            "            [ 0, 0, 0, 0, 0 ]\n" +
            "          ],\n" +
            "          \"type\": \"room\",\n" +
            "          \"bounds\": { \"rows\": 5, \"columns\": 5 }\n" +
            "        },\n" +
            "        {\n" +
            "          \"origin\": [ 4, 14 ],\n" +
            "          \"layout\": [\n" +
            "            [ 0, 0, 2, 0, 0 ],\n" +
            "            [ 0, 1, 1, 1, 0 ],\n" +
            "            [ 0, 1, 1, 1, 0 ],\n" +
            "            [ 0, 1, 1, 1, 0 ],\n" +
            "            [ 0, 0, 0, 0, 0 ]\n" +
            "          ],\n" +
            "          \"type\": \"room\",\n" +
            "          \"bounds\": { \"rows\": 5, \"columns\": 5 }\n" +
            "        },\n" +
            "        {\n" +
            "          \"origin\": [ 3, 1 ],\n" +
            "          \"layout\": [\n" +
            "            [ 0, 0, 2, 0 ],\n" +
            "            [ 0, 1, 1, 0 ],\n" +
            "            [ 0, 1, 1, 0 ],\n" +
            "            [ 0, 2, 0, 0 ]\n" +
            "          ],\n" +
            "          \"type\": \"room\",\n" +
            "          \"bounds\": { \"rows\": 4, \"columns\": 4 }\n" +
            "        }\n" +
            "      ],\n" +
            "      \"objects\": [\n" +
            "        { \"type\": \"exit\", \"position\": [ 7, 17 ] }\n" +
            "      ],\n" +
            "      \"hallways\": [\n" +
            "        {\n" +
            "          \"waypoints\": [ [ 1, 3 ], [ 1, 16 ] ],\n" +
            "          \"to\": [ 4, 16 ],\n" +
            "          \"from\": [ 3, 3 ],\n" +
            "          \"type\": \"hallway\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"waypoints\": [ [ 12, 2 ] ],\n" +
            "          \"to\": [ 12, 5 ], \n" +
            "          \"from\": [ 6, 2 ],\n" +
            "          \"type\": \"hallway\"\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  },\n" +
            "  \"ferd\",\n" +
            "  [ 7, 15 ]\n" +
            "]\n";
    readJsonState(input);

  }
}
