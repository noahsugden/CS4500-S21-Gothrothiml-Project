import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class TestLevel {

  static ArrayList<Room> roomList = new ArrayList<>();
  static ArrayList<Hallway> hallwayList = new ArrayList<>();

  public static JSONObject readLevel(String json) throws JSONException {
    JSONArray command = new JSONArray(json);

    JSONObject level = command.getJSONObject(0);
    JSONArray rooms = level.getJSONArray("rooms");
    for (int i = 0; i < rooms.length(); i++) {
      Room curr = TestRoom.readRoomObject(rooms.getJSONObject(i));
      roomList.add(curr);
    }
    JSONArray objects = level.getJSONArray("objects");
    JSONObject key = objects.getJSONObject(0);
    JSONObject exit = objects.getJSONObject(1);
    JSONArray keyPos = key.getJSONArray("position");
    Position keyPosition = new Position(keyPos.getInt(0), keyPos.getInt(1));
    JSONArray exitPos = exit.getJSONArray("position");
    Position exitPosition = new Position(exitPos.getInt(0), exitPos.getInt(1));
    JSONArray hallways = level.getJSONArray("hallways");
    for (int i = 0; i < hallways.length(); i++) {
      Hallway curr = readHallwayObject(hallways.getJSONObject(i));
      hallwayList.add(curr);
    }
    JSONArray inputPos = command.getJSONArray(1);
    Position input = new Position(inputPos.getInt(0), inputPos.getInt(1));

    Level l = new Level(roomList, hallwayList, keyPosition, exitPosition);

    JSONObject result = output(l, input);
    return result;
  }

  public static Hallway readHallwayObject(JSONObject hallway) throws JSONException {
    JSONArray from = hallway.getJSONArray("from");
    JSONArray to = hallway.getJSONArray("to");
    JSONArray waypoints = hallway.getJSONArray("waypoints");

    Position fromPos = new Position(from.getInt(0), from.getInt(1));
    Position toPos = new Position(to.getInt(0), to.getInt(1));
    ArrayList<Position> waypointsList = new ArrayList<>();

    for (int i = 0; i < waypoints.length(); i++) {
      JSONArray currArray = waypoints.getJSONArray(i);
      Position currPos = new Position(currArray.getInt(0), currArray.getInt(1));
      waypointsList.add(currPos);
    }
    Hallway result = new Hallway(fromPos, toPos, waypointsList);
    return result;
  }

  public static JSONObject output(Level l, Position p) throws JSONException {
    JSONObject result = new JSONObject();
    HashMap<Position, Integer> levelLayout = l.getLevelLayout();
    if (!levelLayout.containsKey(p)) {
      result.put("traversable", false);
      result.put("object", JSONObject.NULL);
      result.put("type", "void");
      JSONArray voidReach = new JSONArray();
      result.put("reachable", voidReach);
      return result;
    }
    int tileType = levelLayout.get(p);
    if (tileType == 2 || tileType == 4 || tileType == 5 || tileType == 6 || tileType == 7
            || tileType == 8) {
      result.put("traversable", true);
    } else {
      result.put("traversable", false);
    }

    if (tileType == 7) {
      result.put("object", "key");
    } else if (tileType == 8) {
      result.put("object", "exit");
    } else {
      result.put("object", JSONObject.NULL);
    }

    if (tileType == 1 || tileType == 2 || tileType == 4 || tileType == 7 || tileType == 8) {
      result.put("type", "room");
    } else if (tileType == 5 || tileType == 6) {
      result.put("type", "hallway");
    } else {
      result.put("type", "void");
    }

    if (tileType == 5 || tileType == 6) {
      JSONArray hallwayReach = hallwayReachable(l, p);
      result.put("reachable", hallwayReach);
    } else if (tileType == 1 || tileType == 2 || tileType == 4 || tileType == 7 || tileType == 8) {
      JSONArray roomReach = roomReachable(l, p);
      result.put("reachable", roomReach);
    } else {
      JSONArray voidReach = new JSONArray();
      result.put("reachable", voidReach);
    }

    return result;
  }

  public static JSONArray roomReachable(Level l, Position p) throws JSONException {
    ArrayList<Hallway> hallways = l.getHallways();
    ArrayList<Room> rooms = l.getRooms();
    Room room;
    Integer index = -1;
    for (int i = 0; i < rooms.size(); i++) {
      HashMap<Position, Integer> currMap = rooms.get(i).getRoomLayout();
      if (currMap.containsKey(p)) {
        index = i;
      }
    }
    room = rooms.get(index);
    ArrayList<Position> doors = room.getDoors();
    ArrayList<Position> otherDoors = new ArrayList<>();
    for (int i = 0; i < doors.size(); i++) {
      Position currDoor = doors.get(i);
      Position otherDoor = new Position(-1, -1);
      for (int j = 0; j < hallways.size(); j++) {
        Hallway currHallway = hallways.get(j);
        if (currHallway.getInit().equals(currDoor)) {
          otherDoor = currHallway.getEnd();
        } else if (currHallway.getEnd().equals(currDoor)) {
          otherDoor = currHallway.getInit();
        }
      }
      if (otherDoor.getx() != -1 && otherDoor.gety() != -1) {
        otherDoors.add(otherDoor);
      }
    }
    ArrayList<Position> roomULs = new ArrayList<>();
    for (int i = 0; i < otherDoors.size(); i++) {
      Position currOtherDoor = otherDoors.get(i);
      for (int j = 0; j < rooms.size(); j++) {
        Room currRoom = rooms.get(j);
        HashMap<Position, Integer> currMap = currRoom.getRoomLayout();
        if (currMap.containsKey(currOtherDoor)) {
          Position currUL = currRoom.getUpperLeft();
          roomULs.add(currUL);
        }
      }
    }
    JSONArray result = new JSONArray();
    for (int i = 0; i < roomULs.size(); i++) {
      Position currUL = roomULs.get(i);
      JSONArray curr = new JSONArray();
      curr.put(currUL.getx());
      curr.put(currUL.gety());
      result.put(curr);
    }

    return result;
  }

  public static JSONArray hallwayReachable(Level l, Position p) throws JSONException {
    ArrayList<Hallway> hallways = l.getHallways();
    ArrayList<Room> rooms = l.getRooms();
    Hallway hallway;
    Integer index = -1;
    for (int i = 0; i < hallways.size(); i++) {
      HashMap<Position, Integer> currMap = hallways.get(i).getHallwayLayout();
      if (currMap.containsKey(p)) {
        index = i;
      }
    }
    hallway = hallways.get(index);
    Position start = hallway.getInit();
    Position end = hallway.getEnd();
    Integer startIndex = -1;
    Integer endIndex = -1;
    for (int i = 0; i < rooms.size(); i++) {
      HashMap<Position, Integer> currMap = rooms.get(i).getRoomLayout();
      if (currMap.containsKey(start)) {
        startIndex = i;
      } else if (currMap.containsKey(end)) {
        endIndex = i;
      }
    }
    Room startRoom = rooms.get(startIndex);
    Room endRoom = rooms.get(endIndex);
    Position startUL = startRoom.getUpperLeft();
    Position endUL = endRoom.getUpperLeft();

    JSONArray result = new JSONArray();
    JSONArray startArray = new JSONArray();
    startArray.put(startUL.getx());
    startArray.put(startUL.gety());
    result.put(startArray);
    JSONArray endArray = new JSONArray();
    endArray.put(endUL.getx());
    endArray.put(endUL.gety());
    result.put(endArray);

    return result;
  }

  public static void main(String[] args) throws JSONException {
    Scanner sc = new Scanner(System.in);
    StringBuilder sb = new StringBuilder();
    while (sc.hasNextLine()) {
      String curr = sc.nextLine();
      sb.append(curr);
    }
    String input = sb.toString();

    JSONObject result = readLevel(input);
    System.out.print(result.toString());
  }

}
