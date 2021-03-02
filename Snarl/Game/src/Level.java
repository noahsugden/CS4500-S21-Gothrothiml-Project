import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class Level {
  static ArrayList<Room> rooms;
  static ArrayList<Hallway> hallways;
  //value 7 for key, value 8 for exit
  HashMap<Position, Integer> levelLayout = new HashMap<>();
  Position key;
  Position exit;
  static Level finalLevel;
  //use 2d array to print
  int[][] ascii = new int[30][30];
  //an example of level data

  boolean exitStatus;


  //this is an example level data of three rooms, two hallways, an exit and a key
  static String exampleString2 = "[\n" +
          "  {\n" +
          "    \"name\": \"room\",\n" +
          "    \"upperleft\": [\n" +
          "      1,\n" +
          "      1\n" +
          "    ],\n" +
          "    \"width\": 5,\n" +
          "    \"height\": 5,\n" +
          "    \"tiles\": [\n" +
          "      [\n" +
          "        3,\n" +
          "        3\n" +
          "      ],\n" +
          "      [\n" +
          "        3,\n" +
          "        4\n" +
          "      ],\n" +
          "      [\n" +
          "        2,\n" +
          "        3\n" +
          "      ]\n" +
          "    ],\n" +
          "    \"doors\": [\n" +
          "      [\n" +
          "        5,\n" +
          "        5\n" +
          "      ]\n" +
          "    ]\n" +
          "  },\n" +
          "  {\n" +
          "    \"name\": \"room\",\n" +
          "    \"upperleft\": [\n" +
          "      8,\n" +
          "      9\n" +
          "    ],\n" +
          "    \"width\": 3,\n" +
          "    \"height\": 3,\n" +
          "    \"tiles\": [\n" +
          "      [\n" +
          "        9,\n" +
          "        10\n" +
          "      ],\n" +
          "      [\n" +
          "        9,\n" +
          "        9\n" +
          "      ]\n" +
          "    ],\n" +
          "    \"doors\": [\n" +
          "      [\n" +
          "        10,\n" +
          "        11\n" +
          "      ]\n" +
          "    ]\n" +
          "  },\n" +
          "  {\n" +
          "    \"name\": \"room\",\n" +
          "    \"upperleft\": [\n" +
          "      14,\n" +
          "      1\n" +
          "    ],\n" +
          "    \"width\": 8,\n" +
          "    \"height\": 6,\n" +
          "    \"tiles\": [\n" +
          "      [\n" +
          "        20,\n" +
          "        4\n" +
          "      ],\n" +
          "      [\n" +
          "        20,\n" +
          "        5\n" +
          "      ],\n" +
          "      [\n" +
          "        20,\n" +
          "        6\n" +
          "      ]\n" +
          "    ],\n" +
          "    \"doors\": [\n" +
          "      [\n" +
          "        14,\n" +
          "        6\n" +
          "      ],\n" +
          "      [\n" +
          "        21,\n" +
          "        1\n" +
          "      ]\n" +
          "    ]\n" +
          "  },\n" +
          "  {\n" +
          "    \"name\": \"hallway\",\n" +
          "    \"init\": [\n" +
          "      21,\n" +
          "      1\n" +
          "    ],\n" +
          "    \"end\": [\n" +
          "      10,\n" +
          "      11\n" +
          "    ],\n" +
          "    \"waypoints\": [\n" +
          "      [\n" +
          "        23,\n" +
          "        1\n" +
          "      ],\n" +
          "      [\n" +
          "        23,\n" +
          "        11\n" +
          "      ]\n" +
          "    ]\n" +
          "  },\n" +
          "  {\n" +
          "    \"name\": \"hallway\",\n" +
          "    \"init\": [\n" +
          "      5,\n" +
          "      5\n" +
          "    ],\n" +
          "    \"end\": [\n" +
          "      14,\n" +
          "      6\n" +
          "    ],\n" +
          "    \"waypoints\": [\n" +
          "      [\n" +
          "        5,\n" +
          "        6\n" +
          "      ]\n" +
          "    ]\n" +
          "  },\n" +
          "  {\n" +
          "    \"name\": \"key\",\n" +
          "    \"position\": [\n" +
          "      3,\n" +
          "      3\n" +
          "    ]\n" +
          "  },\n" +
          "  {\n" +
          "    \"name\": \"exit\",\n" +
          "    \"position\": [\n" +
          "      20,\n" +
          "      6\n" +
          "    ]\n" +
          "  }\n" +
          "]";

  //this is an example level data for two rooms and a hallway
  static String exampleString = "[\n" +
          "  {\n" +
          "    \"name\": \"room\",\n" +
          "    \"upperleft\": [\n" +
          "      0,\n" +
          "      0\n" +
          "    ],\n" +
          "    \"width\": 4,\n" +
          "    \"height\": 4,\n" +
          "    \"tiles\": [\n" +
          "      [\n" +
          "        2,\n" +
          "        2\n" +
          "      ],\n" +
          "      [\n" +
          "        2,\n" +
          "        3\n" +
          "      ]\n" +
          "    ],\n" +
          "    \"doors\": [\n" +
          "      [\n" +
          "        3,\n" +
          "        0\n" +
          "      ]\n" +
          "    ]\n" +
          "  },\n" +
          "  {\n" +
          "    \"name\": \"room\",\n" +
          "    \"upperleft\": [\n" +
          "      6,\n" +
          "      6\n" +
          "    ],\n" +
          "    \"width\": 4,\n" +
          "    \"height\": 4,\n" +
          "    \"tiles\": [\n" +
          "      [\n" +
          "        8,\n" +
          "        8\n" +
          "      ]\n" +
          "    ],\n" +
          "    \"doors\": [\n" +
          "      [\n" +
          "        8,\n" +
          "        6\n" +
          "      ]\n" +
          "    ]\n" +
          "  },\n" +
          "  {\n" +
          "    \"name\": \"hallway\",\n" +
          "    \"init\": [\n" +
          "      3,\n" +
          "      0\n" +
          "    ],\n" +
          "    \"end\": [\n" +
          "      8,\n" +
          "      6\n" +
          "    ],\n" +
          "    \"waypoints\": [\n" +
          "      [\n" +
          "        8,\n" +
          "        0\n" +
          "      ]\n" +
          "    ]\n" +
          "  },\n" +
          "  {\n" +
          "    \"name\": \"key\",\n" +
          "    \"position\": [\n" +
          "      2,\n" +
          "      2\n" +
          "    ]\n" +
          "  },\n" +
          "  {\n" +
          "    \"name\": \"exit\",\n" +
          "    \"position\": [\n" +
          "      2,\n" +
          "      3\n" +
          "    ]\n" +
          "  }\n" +
          "]";

  public Level() {
    this.rooms = new ArrayList<>();
    this.hallways = new ArrayList<>();
    this.putLevelLayout();
  }

  public Level(ArrayList<Room> r, ArrayList<Hallway> h, Position k, Position e) {
    this.rooms = r;
    this.hallways = h;
    this.key = k;
    this.exit = e;
    this.putLevelLayout();
    this.fill2DArray();
  }

  //constructor for testing
  public Level(Room room1, Room room2) {
    this.rooms = new ArrayList<>();
    rooms.add(room1);
    rooms.add(room2);
    this.hallways = new ArrayList<>();
    this.putLevelLayout();
    this.fill2DArray();
  }

  //constructor for testing
  public Level(Room room1, Room room2, Hallway h) {
    this.rooms = new ArrayList<>();
    rooms.add(room1);
    rooms.add(room2);
    this.hallways = new ArrayList<>();
    hallways.add(h);
    this.putLevelLayout();
    this.fill2DArray();
  }

  //For testing purposes
  public void putTestKeyAndExit(Position p1, Position p2) {
    this.levelLayout.put(p1, 7);
    this.levelLayout.put(p2, 8);

  }

  public HashMap<Position,Integer> getLevelLayout() {
    return this.levelLayout;
  }

  public boolean getExitStatus() {
    return this.exitStatus;
  }

  public void fill2DArray() {
    for (Position p:levelLayout.keySet()) {
      int x = p.getx();
      int y = p.gety();
      ascii[y][x] = levelLayout.get(p);
    }
  }

  public static ArrayList<Hallway> getHallways() {
    return hallways;
  }

  public static ArrayList<Room> getRooms() {
    return rooms;
  }

  private void putLevelLayout() {
    for(int i = 0; i < rooms.size(); i++) {
      Room curr = rooms.get(i);
      HashMap<Position, Integer> temp = curr.getRoomLayout();
      for(Position p: temp.keySet()) {
        if(levelLayout.get(p) == null) {
          levelLayout.put(p, temp.get(p));
        }
        else {
          throw new IllegalArgumentException("Rooms overlap!");
        }
      }
    }

    for(int j = 0; j < hallways.size(); j++) {
      Hallway curr = hallways.get(j);
      HashMap<Position, Integer> temp = curr.getHallwayLayout();

      //To check if a Hallway starts and ends with a door
      if(levelLayout.get(curr.getInit()) != 4 || levelLayout.get(curr.getEnd()) != 4) {
        curr.getInit().print();
        curr.getEnd().print();
        System.out.println(levelLayout.get(curr.getInit()));
        System.out.println(levelLayout.get(curr.getEnd()));
        throw new IllegalArgumentException("Hallway should start and end with a door!");
      }
      for(Position p: temp.keySet()) {
        if(levelLayout.get(p) == null) {
          levelLayout.put(p, temp.get(p));
        }
        else {
          p.print();
          throw new IllegalArgumentException("Hallways overlap!");
        }
      }
    }

    //key and exit must both exist
    if (key != null && exit != null) {
      putKeyandExit();
    }
  }

  public void setExitStatus(boolean exitStatus) {
    this.exitStatus = exitStatus;
  }

  private void putKeyandExit() {
    if (levelLayout.get(key) == 2) {
      levelLayout.put(key, 7);
    } else {
      throw new IllegalArgumentException("key must be inside a room");
    }

    if (levelLayout.get(exit) == 2) {
      levelLayout.put(exit, 8);
    } else {
      throw new IllegalArgumentException("exit must be inside a room");
    }
  }


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

  //To return an unoccupied position in the left-most room
  public Position findUnoccupiedLeftmost(int id) {
    Position min = new Position(-1 ,-1);
    int upperLeftIndex = -1;
    for (int i =0;i< rooms.size();i++) {
      int minX = min.getx();
      int minY = min.gety();
      Room curr = rooms.get(i);
      Position upperLeft = curr.getUpperLeft();
      int upperX = upperLeft.getx();
      int upperY = upperLeft.gety();
      if (minX== -1 && minY == -1) {
        min = upperLeft;
        upperLeftIndex = i;
      } else if (minX > upperX && minY > upperY) {
        min = upperLeft;
        upperLeftIndex = i;
      }
    }


    Room upperLeftRoom = rooms.get(upperLeftIndex);
    ArrayList<Position> unoccupiedTiles = upperLeftRoom.getTiles();
    Position temp = unoccupiedTiles.get(id - 10);

    return temp;
  }


  //To return an unoccupied position in the right-most room
  public Position findUnoccupiedRightmost(int id) {
    Position min = new Position(-1, -1);
    int bottomRightIndex = -1;
    for (int i =0;i< rooms.size();i++) {
      int minX = min.getx();
      int minY = min.gety();
      Room curr = rooms.get(i);
      Position rightBottom = curr.getRightBottom();
      int bottomX = rightBottom.getx() +curr.getWidth();
      int bottomY = rightBottom.gety() +curr.getHeight();
      if (minX== -1 && minY == -1) {
        min = rightBottom;
        bottomRightIndex = i;
      } else if (minX <bottomX && minY<bottomY) {
        min = rightBottom;
        bottomRightIndex = i;
      }
    }
    Room bottomRightRoom = rooms.get(bottomRightIndex);
    ArrayList<Position> unoccupiedTiles = bottomRightRoom.getTiles();
    Position temp = unoccupiedTiles.get(100 - id);

    return temp;
  }


  public void UpdateLevel(Position p, int id) {
    this.levelLayout.put(p,id);
  }

  public int[][] get2Darray() {
    return this.ascii;
  }

  /* public static void main(String[] args) throws JSONException {
    //Scanner sc = new Scanner(System.in);
    //String exampleString = sc.nextLine();
    //Here is where the level date gets computed. To change to another exmaple input, just change the argument of JsonParser.readString().
    JsonParser.readString(exampleString2);
    finalLevel = JsonParser.getCompletelevel();
    finalLevel.print2D();
  }*/


}
