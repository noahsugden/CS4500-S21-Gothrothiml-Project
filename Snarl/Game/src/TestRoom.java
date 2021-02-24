import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import org.json.*;

public class TestRoom {

  public static void readString(String s) throws JSONException {
    JSONArray array = new JSONArray(s);

    JSONObject room = array.getJSONObject(0);
    Room result = readRoomObject(room);
    JSONArray startpoint = array.getJSONArray(1);
    int x = startpoint.getInt(0);
    int y = startpoint.getInt(1);
    Position start = new Position(x, y);
    JSONArray finalArray = writeOutput(result, start);
    System.out.println(finalArray.toString());
  }

  public static Room readRoomObject(JSONObject room) throws JSONException {
    ArrayList<Position> nonWallTiles = new ArrayList<>();
    ArrayList<Position> doors = new ArrayList<>();
    JSONArray originArray = room.getJSONArray("origin");
    int x = originArray.getInt(0);
    int y = originArray.getInt(1);
    Position origin = new Position(x, y);
    JSONObject bounds = room.getJSONObject("bounds");
    int rows = bounds.getInt("rows");
    int columns = bounds.getInt("columns");
    JSONArray layout = room.getJSONArray("layout");
    for (int i = 0; i < layout.length(); i++) {
      JSONArray curr = layout.getJSONArray(i);
      for (int j = 0; j < curr.length(); j++) {
        int temp = curr.getInt(j);
        if (temp == 1) {
          Position tile = new Position(j + x, i + y);
          nonWallTiles.add(tile);
        } else if (temp == 2) {
          Position door = new Position(j + x, i + y);
          doors.add(door);
        }

      }
    }
    Room jsonRoom = new Room(origin, columns, rows, nonWallTiles, doors);
    return jsonRoom;
  }

  public static ArrayList<Position> getTraversablePoints(Room room, Position startPoint) {
    HashMap<Position, Integer> roomLayout = room.getRoomLayout();
    ArrayList<Position> result = new ArrayList<>();
    if (!roomLayout.containsKey(startPoint)) {
      throw new IllegalArgumentException("Start point not in room!");
    }
    int x = startPoint.getx();
    int y = startPoint.gety();
    Position origin = room.getUpperLeft();
    int upperBound = origin.gety();
    int leftBound = origin.getx();
    int rightBound = origin.getx() + room.getWidth() - 1;
    int lowerBound = origin.gety() + room.getHeight() - 1;
    if (y - 1 >= upperBound) {
      Position up = new Position(x, y - 1);
      if (roomLayout.get(up) == 2 || roomLayout.get(up) == 4) {
        result.add(up);
      }
    }
    if (x - 1 >= leftBound) {
      Position left = new Position(x - 1, y);
      if (roomLayout.get(left) == 2 || roomLayout.get(left) == 4) {
        result.add(left);
      }
    }

    if (x + 1 <= rightBound) {
      Position right = new Position(x + 1, y);
      if (roomLayout.get(right) == 2 || roomLayout.get(right) == 4) {
        result.add(right);
      }
    }

    if (y + 1 <= lowerBound) {
      Position lower = new Position(x, y + 1);
      if (roomLayout.get(lower) == 2 || roomLayout.get(lower) == 4) {
        result.add(lower);
      }
    }

    return result;
  }

  public static JSONArray writeOutput(Room room, Position p) {
    JSONArray output = new JSONArray();
    JSONArray origin = new JSONArray();
    Position upperLeft = room.getUpperLeft();
    origin.put(upperLeft.getx());
    origin.put(upperLeft.gety());
    JSONArray point = new JSONArray();
    point.put(p.getx());
    point.put(p.gety());
    try {
      ArrayList<Position> result = getTraversablePoints(room, p);
      output.put("Success: Traversable points from ");
      output.put(point);
      output.put(" in room at ");
      output.put(origin);
      output.put(" are ");
      JSONArray traversablePoints = new JSONArray();
      for (int i = 0; i < result.size(); i++) {
        JSONArray temp = new JSONArray();
        int x = result.get(i).getx();
        int y = result.get(i).gety();
        temp.put(x);
        temp.put(y);
        traversablePoints.put(temp);

      }
      output.put(traversablePoints);
    } catch (Exception e) {
      output.put("Failure: Point ");
      output.put(point);
      output.put(" is not in room at ");
      output.put(origin);

    }
    return output;

  }

  public static void main(String[] args) throws JSONException {
   Scanner sc = new Scanner(System.in);
   StringBuilder sb = new StringBuilder();
   while (sc.hasNextLine()){
     String curr = sc.nextLine();
     sb.append(curr);
    }
   String input = sb.toString();
    readString(input);

  }
}
