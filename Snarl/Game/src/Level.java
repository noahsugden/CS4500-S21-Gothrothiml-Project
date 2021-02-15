import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Level {
  ArrayList<Room> rooms;
  ArrayList<Hallway> hallways;
  //value 7 for key, value 8 for exit
  HashMap<Position, Integer> levelLayout = new HashMap<>();
  Position key;
  Position exit;
  static int[][] ascii = new int[15][15];

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

  public HashMap<Position,Integer> getLevelLayout() {
    return this.levelLayout;
  }

  public void fill2DArray() {
    for (Position p:levelLayout.keySet()) {
      int x = p.getx();
      int y = p.gety();
      ascii[x][y] = levelLayout.get(p);
    }
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

  private void putKeyandExit() {
    if (levelLayout.get(key) == 2) {
      levelLayout.put(key, 7);
    } else {
      throw new IllegalArgumentException("key must be inside a room");
    }

    if (levelLayout.get(exit) == 2) {
      levelLayout.put(key, 8);
    } else {
      throw new IllegalArgumentException("exit must be inside a room");
    }
  }


  public static void print2D()
  {

    String original = "";
    String formatted = "";
    for (int[] row : ascii) {
      original = Arrays.toString(row);
      int size = original.length();
      formatted = original.substring(1, size - 2);
      char[] chars= formatted.toCharArray();
      for(int i =0; i<chars.length;i++) {
        char temp = chars[i];
        //0 means nothing
        if (temp == '0') {
          chars[i] = ' ';
          //1 is wall tile
        } else if (temp == '1') {
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
        } else if (temp == '8') {
          chars[i] = 'e';
        } else if (chars[i] == ',') {
          chars[i] = ' ';
        }
      }
      String lastString = new String(chars);
      System.out.println(lastString);
    }
  }



}
