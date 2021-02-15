import java.util.ArrayList;
import java.util.HashMap;


public class Room {
  Position upperLeft;
  int width;
  int height;
  ArrayList<Position> tiles;
  ArrayList<Position> doors;
  // mapped to: 1 if wall tile, 2 if non-wall tile, 3 if boundary, 4 if door
  HashMap<Position, Integer> roomLayout = new HashMap<>();

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

  public HashMap<Position, Integer> getRoomLayout() {
    return this.roomLayout;
  }

  private boolean doorsValid(ArrayList<Position> doorList) {
    for (int i = 0; i < doorList.size(); i++) {
      Position curr = doorList.get(i);
      System.out.println(roomLayout.get(curr) + " TETS");
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

  private void putBoundaries() {
    roomLayout.put(upperLeft, 3);
    //Adds upper part boundary
    for (int i = 1; i <= width; i++) {
      Position curr = new Position(i + upperLeft.getx(), upperLeft.gety());
      roomLayout.put(curr, 3);
      System.out.println("X Wposition is: " + curr.getx() + " Y Wposition is: " + curr.gety());
    }

    //Adds left part boundary
    for (int i = 1; i <= height; i++) {
      Position curr = new Position(upperLeft.getx(), i + upperLeft.gety());
      roomLayout.put(curr, 3);
      System.out.println("X Hposition is: " + curr.getx() + " Y Hposition is: " + curr.gety());
    }

    //Adds bottom part boundary
    for (int i = 1; i <= width; i++) {
      Position curr = new Position(i + upperLeft.getx(), upperLeft.gety() + height);
      roomLayout.put(curr, 3);
      System.out.println("X Wposition is: " + curr.getx() + " Y Wposition is: " + curr.gety());
    }

    //Add right part boundary
    for (int i = 1; i <= height; i++) {
      Position curr = new Position(upperLeft.getx() + width, i + upperLeft.gety());
      roomLayout.put(curr, 3);
      System.out.println("X Hposition is: " + curr.getx() + " Y Hposition is: " + curr.gety());
    }

  }

  private void putDoors() {
    for (int i = 0; i < this.doors.size(); i++) {
      Position curr = this.doors.get(i);
      roomLayout.put(curr, 4);
    }
  }

  private void putNonWallTiles() {
    for (int i = 0; i < this.tiles.size(); i++) {
      Position curr = this.tiles.get(i);
      roomLayout.put(curr, 2);
    }
  }

  private void putWallTiles() {
    for (int x = upperLeft.getx(); x <= upperLeft.getx() + width; x++) {
      for (int y = upperLeft.gety(); y <= upperLeft.gety() + height; y++) {
        Position curr = new Position(x, y);
        if (roomLayout.get(curr) != 2 || roomLayout.get(curr) != 3 || roomLayout.get(curr) != 4) {
          roomLayout.put(curr, 1);
        }
      }
    }
  }
}
