import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import org.json.*;

/**
 * This class represents the executable that is given a room and a point in the world will output a list of points in the
 * room 1 cardinal move away that are traversable.
 */
public class TestRoom {

  /**
   * Reads the given json string and prints the result string
   * @param s represents the json string
   * @throws JSONException when the json string is not valid
   */
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

  /**
   * Transforms the json room object to a room
   * @param room represents the json room object
   * @return a room
   * @throws JSONException when the json room object is not valid
   */
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
    for (int i = 0; i < rows; i++) {
      JSONArray curr = layout.getJSONArray(i);
      for (int j = 0; j < columns; j++) {
        int temp = curr.getInt(j);
        if (temp == 1) {
          Position tile = new Position(i + x, j + y);
          nonWallTiles.add(tile);
        } else if (temp == 2) {
          Position door = new Position(i + x, j + y);
          doors.add(door);
        }

      }
    }
    Room jsonRoom = new Room(origin, rows, columns, nonWallTiles, doors);
    return jsonRoom;
  }

  /**
   * Calculates the traversable points around a position in a room
   * @param room represents the room
   * @param startPoint represents the start point
   * @return an arraylist of positions represents the traversable points
   */
  public static ArrayList<Position> getTraversablePoints(Room room, Position startPoint) {
    HashMap<Position, Integer> roomLayout = room.getRoomLayout();
    ArrayList<Position> result = new ArrayList<>();
    if (!roomLayout.containsKey(startPoint)) {
      throw new IllegalArgumentException("Start point not in room!");
    }
    int x = startPoint.getx();
    int y = startPoint.gety();
    Position origin = room.getUpperLeft();
    int upperBound = origin.getx();
    int leftBound = origin.gety();
    int rightBound = origin.gety() + room.getWidth() - 1;
    int lowerBound = origin.getx() + room.getHeight() - 1;
    if (x - 1 >= upperBound) {
      Position up = new Position(x - 1, y);
      if (roomLayout.get(up) == 2 || roomLayout.get(up) == 4) {
        result.add(up);
      }
    }
    if (y - 1 >= leftBound) {
      Position left = new Position(x, y - 1);
      if (roomLayout.get(left) == 2 || roomLayout.get(left) == 4) {
        result.add(left);
      }
    }

    if (y + 1 <= rightBound) {
      Position right = new Position(x, y + 1);
      if (roomLayout.get(right) == 2 || roomLayout.get(right) == 4) {
        result.add(right);
      }
    }

    if (x + 1 <= lowerBound) {
      Position lower = new Position(x + 1, y);
      if (roomLayout.get(lower) == 2 || roomLayout.get(lower) == 4) {
        result.add(lower);
      }
    }

    return result;
  }

  /**
   * Writes the result of determining if a position is in a room in an json array
   * @param room represents the given room
   * @param p represents the given position
   * @return a json array represents the result
   */
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
//   Scanner sc = new Scanner(System.in);
//   StringBuilder sb = new StringBuilder();
//   while (sc.hasNextLine()){
//     String curr = sc.nextLine();
//     sb.append(curr);
//    }
//   String input = sb.toString();
    String input = "[ { \"type\" : \"room\",\n" +
            "    \"origin\" : [0, 1],\n" +
            "    \"bounds\" : { \"rows\" : 3,\n" +
            "                 \"columns\" : 5 },\n" +
            "    \"layout\" : [ [0, 0, 2, 0, 0],\n" +
            "                 [0, 1, 1, 1, 0],\n" +
            "                 [0, 0, 2, 0, 0]\n" +
            "               ]\n" +
            "  },\n" +
            "  [5, 4]\n" +
            "]";
    readString(input);

  }
}
