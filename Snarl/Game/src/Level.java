import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

/**
 * This class represents a Level in the game.
 * It contains an ArrayList of Rooms, an ArrayList of Hallways, a key Position, an exit Position,
 * a boolean to determine if this is the final level, and a boolean to determine if the exit is unlocked.
 * It also contains a nested Array representing the ascii representation of the level.
 */
public class Level {
  static ArrayList<Room> rooms;
  static ArrayList<Hallway> hallways;
  //value 7 for key, value 8 for exit
  HashMap<Position, Integer> levelLayout = new HashMap<>();
  Position key;
  Position exit;
  static boolean finalLevel;
  //use 2d array to print

  int rightMostX;
  int rightMostY;
  int[][] ascii;
  //an example of level data

    //true for unlocked
  boolean exitStatus;
  ArrayList<HashMap<Position, Integer>> roomLayouts = new ArrayList<>();


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

  /**
   * Constructor for an empty level.
   */
  public Level() {
    this.rooms = new ArrayList<>();
    this.hallways = new ArrayList<>();
    this.putLevelLayout();
  }

  /**
   * Constructor for a complete level.
   * @param r represents the list of rooms
   * @param h represents the list of hallways
   * @param k represents the key Position
   * @param e represents the exit Position
   */
  public Level(ArrayList<Room> r, ArrayList<Hallway> h, Position k, Position e) {
    this.rooms = r;
    this.hallways = h;
    if (k.gety() !=-1 && k.getx() !=-1) {
      this.key = k;
    }
    this.exit = e;
    this.putLevelLayout();
    for (int i = 0; i < r.size(); i++) {
      HashMap<Position, Integer> curr = r.get(i).getRoomLayout();
      roomLayouts.add(curr);
    }
    Position rightBottom = this.findRightMost();
    rightMostX = rightBottom.getx();
    rightMostY = rightBottom.gety();
    ascii = new int[rightMostY+5][rightMostX+5];
    this.fill2DArray();
  }


  /**
   * Constructor for testing levels with two rooms.
   * @param room1 represents a room in the level
   * @param room2 represents a room in the level
   */
  public Level(Room room1, Room room2) {
    this.rooms = new ArrayList<>();
    rooms.add(room1);
    rooms.add(room2);
    this.hallways = new ArrayList<>();
    this.putLevelLayout();
    this.fill2DArray();
    for (int i = 0; i < rooms.size(); i++) {
      HashMap<Position, Integer> curr = rooms.get(i).getRoomLayout();
      roomLayouts.add(curr);
    }
  }

  /**
   * Constructor for testing levels with two rooms and a hallway.
   * @param room1 represents a room in the level
   * @param room2 represents a room in the level
   * @param h represents a hallway in the level
   */
  public Level(Room room1, Room room2, Hallway h) {
    this.rooms = new ArrayList<>();
    rooms.add(room1);
    rooms.add(room2);
    this.hallways = new ArrayList<>();
    hallways.add(h);
    this.putLevelLayout();
    this.fill2DArray();
    for (int i = 0; i < rooms.size(); i++) {
      HashMap<Position, Integer> curr = rooms.get(i).getRoomLayout();
      roomLayouts.add(curr);
    }
  }

  public Position getKey() {
    return key;
  }

  public Position getExit() {
    return exit;
  }


  /**
   * Puts the key and exit at the given positions respectively.
   * This method is for testing.
   * @param p1 the key Position
   * @param p2 the exit Position
   */
  public void putTestKeyAndExit(Position p1, Position p2) {
    this.levelLayout.put(p1, 7);
    this.levelLayout.put(p2, 8);

  }

  /**
   * Gets the level layout.
   * @return a Hashmap of Position to Integer representing the level layout
   */
  public HashMap<Position,Integer> getLevelLayout() {
    return this.levelLayout;
  }

  /**
   * Gets the current exit status.
   * @return a boolean representing the current exit status
   */
  public boolean getExitStatus() {
    return this.exitStatus;
  }

  /**
   * Fills the ascii art array.
   */
  public void fill2DArray() {
    for (Position p:levelLayout.keySet()) {
      int x = p.getx();
      int y = p.gety();
      ascii[y][x] = levelLayout.get(p);
    }
  }

  /**
   * Gets a list of the hallways.
   * @return an ArrayList of the Hallways
   */
  public static ArrayList<Hallway> getHallways() {
    return hallways;
  }

  /**
   * Gets a list of the rooms.
   * @return an ArrayList of the Rooms
   */
  public static ArrayList<Room> getRooms() {
    return rooms;
  }

  /**
   * Fills the level layout Hashmap.
   */
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

  /**
   * Sets this exit status to the given exit status.
   * @param exitStatus represents the exitStatus
   */
  public void setExitStatus(boolean exitStatus) {
    this.exitStatus = exitStatus;
  }

  /**
   * Puts the key and exit in the level layout Hashmap.
   */
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


  /**
   * Renders the current level layout.
   * @param array represents the ascii art Array
   */
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
          chars[i] = ' ';
        }
      }
      String lastString = new String(chars);
      System.out.println(lastString);
    }
  }




  /**
   * Finds an unoccupied position in the left-most room
   * @param id represents the ID of the Player we want to place
   * @return a Position representing the unoccupied leftmost tile
   */
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

  public Position findRightMost() {
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


    return bottomRightRoom.getRightBottom();
  }


  /**
   * Finds an unoccupied position in the right-most room
   * @param id represents the ID of the Adversary we want to place
   * @return a Position representing the unoccupied rightmost tile
   */
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

  public ArrayList<HashMap<Position, Integer>> getRoomLayouts() {
    return this.roomLayouts;
  }

  /**
   * Gets the 2D Array representing our level layout.
   * @return a 2D Array representing our level layout
   */
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
