import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class represents a Room.
 * It contains the upper-left Position of the Room, the Room's width and height, an ArrayList of
 * non-wall tile Positions in the Room, an ArrayList of door Positions in the Room, and a Hashmap of
 * Position to Integer representing the Room layout.
 */
public class Room {
  Position upperLeft;
  int width;
  int height;
  ArrayList<Position> tiles;
  static ArrayList<Position> doors;
  // mapped to: 1 if wall tile, 2 if non-wall tile, 3 if boundary, 4 if door
  HashMap<Position, Integer> roomLayout = new HashMap<>();

  /**
   * Constructor for a Room.
   * @param uL represents the upper-left Position in the Room
   * @param w represents the width of the Room
   * @param h represents the height of the Room
   * @param t represents an ArrayList of non-wall tile Positions in the Room
   * @param d represents an ArrayList of door Positions in the Room
   */
  public Room(Position uL, int w, int h, ArrayList<Position> t, ArrayList<Position> d) {
    this.upperLeft = uL;
    this.width = w;
    this.height = h;
    this.putBoundaries();
    this.tiles = t;
    this.putNonWallTiles();
    if (doorsValid(d)) {
      this.doors = d;
    } else {
      throw new IllegalArgumentException("not all doors are within boundaries");
    }
    this.putDoors();
    this.putWallTiles();
  }

  /**
   * Gets the Room layout.
   * @return a Hashmap of Position to Integer representing the layout of the Rooom
   */
  public HashMap<Position, Integer> getRoomLayout() {
    return this.roomLayout;
  }

  /**
   * Helper method for the constructor to determine if the given list of door Positions is valid.
   * @param doorList represents an ArrayList of door Positions in the Room
   * @return a boolean representing if the given list of door Positions is valid
   */
  private boolean doorsValid(ArrayList<Position> doorList) {
    if(doorList.size() == 0) {
      throw new IllegalArgumentException("Given no doors!");
    }
    for (int i = 0; i < doorList.size(); i++) {
      Position curr = doorList.get(i);
      if(roomLayout.get(curr) == null) {
        System.out.println("Invalid door x is: " + curr.getx() + " Y is: " + curr.gety());
        return false;
      }
      else if (roomLayout.get(curr) != 3) {
        System.out.println("test 2 Invalid door x is: " + curr.getx() + " Y is: " + curr.gety());
        return false;
      }

    }

    return true;
  }

  /**
   * Gets an ArrayList of the door Positions in the Room.
   * @return an ArrayList of the door Positions in the Room
   */
  public static ArrayList<Position> getDoors() {
    return doors;
  }

  /**
   * Gets the upper-left position of the Room.
   * @return a Position representing the upperLeft Position of the Room
   */
  public Position getUpperLeft() {
    return upperLeft;
  }

  /**
   * Gets the right-bottom position of the Room.
   * @return a Position representing the right-bottom Position of the Room
   */
  public Position getRightBottom() {
    int x = upperLeft.getx() + width;
    int y = upperLeft.gety() + height;

    return new Position(x,y);
  }

  /**
   * Gets an ArrayList of the non-wall tiles in the Room.
   * @return an ArrayList of the non-wall tiles in the Room
   */
  public ArrayList<Position> getTiles() {
    return tiles;
  }

  /**
   * Gets the width of the Room.
   * @return an int representing the width of the Room
   */
  public int getWidth() {
    return width;
  }

  /**
   * Gets the height of the Room.
   * @return an int representing the height of the Room
   */
  public int getHeight() {
    return height;
  }


  /**
   * Adds the Room boundaries to the Room layout Hashmap.
   */
  private void putBoundaries() {
    roomLayout.put(upperLeft, 3);
    //Adds upper part boundary
    for (int i = 1; i < width; i++) {
      Position curr = new Position(i + upperLeft.getx(), upperLeft.gety());
      roomLayout.put(curr, 3);
     // System.out.println("X Wposition is: " + curr.getx() + " Y Wposition is: " + curr.gety());
    }

    //Adds left part boundary
    for (int i = 1; i < height; i++) {
      Position curr = new Position(upperLeft.getx(), i + upperLeft.gety());
      roomLayout.put(curr, 3);
    //  System.out.println("X Hposition is: " + curr.getx() + " Y Hposition is: " + curr.gety());
    }

    //Adds bottom part boundary
    for (int i = 1; i < width; i++) {
      Position curr = new Position(i + upperLeft.getx(), upperLeft.gety() + height-1);
      roomLayout.put(curr, 3);
   //   System.out.println("X Wposition is: " + curr.getx() + " Y Wposition is: " + curr.gety());
    }

    //Add right part boundary
    for (int i = 1; i < height; i++) {
      Position curr = new Position(upperLeft.getx() + width -1, i + upperLeft.gety());
      roomLayout.put(curr, 3);
    //  System.out.println("X Hposition is: " + curr.getx() + " Y Hposition is: " + curr.gety());
    }

  }

  /**
   * Adds the doors to the Room layout Hashmap.
   */
  private void putDoors() {
    for (int i = 0; i < this.doors.size(); i++) {
      Position curr = this.doors.get(i);
   //   curr.print();
      roomLayout.put(curr, 4);
    }
  }

  /**
   * Adds the non-wall tiles to the Room layout Hashmap.
   */
  private void putNonWallTiles() {
    for (int i = 0; i < this.tiles.size(); i++) {
      Position curr = this.tiles.get(i);
      roomLayout.put(curr, 2);
    }
  }

  /**
   * Adds the wall tiles to the Room layout Hashmap.
   */
  private void putWallTiles() {
    for (int x = upperLeft.getx(); x < upperLeft.getx() + width; x++) {
      for (int y = upperLeft.gety(); y < upperLeft.gety() + height; y++) {
        Position curr = new Position(x, y);
        if (roomLayout.get(curr) ==null) {
          roomLayout.put(curr,1);
        }
          else if (roomLayout.get(curr) != 2 &&  roomLayout.get(curr) != 4) {
          roomLayout.put(curr, 1);
        }
      }
    }
  }
}
