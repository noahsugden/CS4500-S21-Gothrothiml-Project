
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class is a test harness executable testState, which, given a state specification and a player with a destination
 * , will output an updated state or an error message if the player cannot be placed in the destination.
 */
public class TestState {
  static Level currentLevel;
  static HashMap<String, Position> playerPositions = new HashMap<>();
  static HashMap<String, Position> zombiePositions = new HashMap<>();
  static HashMap<String, Position> ghostPositions = new HashMap<>();
  static GameState gameState;
  static boolean exitStatus;
  static JSONObject state;
  static JSONObject level;

  /**
   * Reads the json string and prints the result
   * @param s represents the input json string
   * @throws JSONException when the json string is not valid
   */
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
    System.out.println(output(name, pos).toString());

  }


  /**
   * Transforms the given json array of positions to a hashmap of string to position
   * @param positions represents the given json array
   * @throws JSONException when the json array is not valid
   */
  static void readPositions(JSONArray positions) throws JSONException {
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

  /**
   * Writes an updated state or an error message in a json array
   *
   * @param name represents the player's name
   * @param pos represents the player's next destination
   * @return an json array representing the result
   * @throws JSONException when the name or position is not valid
   */
  static JSONArray output(String name, Position pos) throws JSONException {
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

  /**
   * Update the current state based on the result
   * @param name represents the player's name
   * @param p represents the player's next destination
   * @param prev represents the previous game state
   * @param removePlayer represents if a player should be removed from the previous game state
   * @param removeKey represents if a key should be removed from the previous game state
   * @return a json object represents the modified game state
   * @throws JSONException when the previous game state is not valid
   */
  static JSONObject modifyState(String name, Position p, JSONObject prev, boolean removePlayer, boolean removeKey)
          throws JSONException {
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
  Scanner sc = new Scanner(System.in);
  StringBuilder sb = new StringBuilder();
 while (sc.hasNextLine()){
    String curr = sc.nextLine();
    sb.append(curr);
   }
  String input = sb.toString();

    readJsonState(input);

  }
}
