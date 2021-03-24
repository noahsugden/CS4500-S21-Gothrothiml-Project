import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class represents a Hallway.  It contains two positions representing the doors of the rooms that
 * it connects, an ArrayList of Positions representing the waypoints (where the hallway switches directions),
 * and a Hashmap of Position to Integer representing the layout of the hallway.
 */
public class Hallway {
  Position init;
  Position end;
  ArrayList<Position> waypoints;
  // mapped to: 5 if horizontal, 6 if vertical
  HashMap<Position, Integer> hallwayLayout = new HashMap<Position, Integer>();

  /**
   * Constructor for a Hallway.
   * @param i represents the initial position (first door)
   * @param e represents the end position (last door)
   * @param w represents the ArrayList fo waypoints
   */
  public Hallway(Position i, Position e, ArrayList<Position> w) {
    this.init = i;
    this.end = e;
    this.waypoints = w;
    this.putHallwayLayout();
  }

  /**
   * Initializes the Hashmap of Position to Integer representing the hallway layout.
   */
  private void putHallwayLayout() {
    if (waypoints.size() ==0) {
      compareTwoPositions(init,end);
    }
    if (waypoints.size() ==1) {
      Position curr = waypoints.get(0);
      this.compareTwoPositions(this.init, curr);
      this.compareTwoPositions(curr,end);
    }
    if (waypoints.size() >1) {
      for (int i = 0; i < waypoints.size(); i++) {
        Position curr = waypoints.get(i);
        if (i == 0) {
          this.compareTwoPositions(this.init, curr);
          this.compareTwoPositions(curr, waypoints.get(1));
        } else if (i == waypoints.size() - 1) {
          this.compareTwoPositions(curr, this.end);
        } else {
          this.compareTwoPositions(curr, waypoints.get(i + 1));
        }
      }
    }

    if (this.hallwayLayout.get(init) != null || this.hallwayLayout.get(end) != null) {
      this.hallwayLayout.remove(init);
      this.hallwayLayout.remove(end);
    }
  }

  /**
   * Gets the Hallway layout.
   * @return a Hashmap of Position to Integer representing the Hallway layout.
   */
  public HashMap<Position, Integer> getHallwayLayout() {
    return this.hallwayLayout;
  }

  /**
   * Gets the initial position (the position of the first door).
   * @return a Position
   */
  public Position getInit() {
    return this.init;
  }

  /**
   * Gets the end position (the position of the last door).
   * @return a Position
   */
  public Position getEnd() {
    return this.end;
  }

  /**
   * Helper for putHallwayLayout that compares two waypoints to generate all of the positions
   * between the waypoints.
   * @param pre represents the previous waypoint
   * @param next represents the next waypoint
   */
  private void compareTwoPositions(Position pre, Position next) {
    if ((next.getx() - pre.getx() > 0) && (next.gety() == pre.gety())) {
      for (int j = pre.getx()+1; j <= next.getx(); j++) {
        Position horizTemp = new Position(j, next.gety());
        hallwayLayout.put(horizTemp, 5);
      }
    } else if ((next.gety() - pre.gety() > 0) && (next.getx() == pre.getx())) {
      for (int j = pre.gety()+1; j <= next.gety(); j++) {
        Position vertTemp = new Position(next.getx(), j);
        hallwayLayout.put(vertTemp, 6);
      }
    } else if ((pre.getx() - next.getx() > 0) && (next.gety() == pre.gety())) {
      for (int j = next.getx()+1; j <= pre.getx(); j++) {
        Position horizTemp = new Position(j, pre.gety());
        hallwayLayout.put(horizTemp, 5);
      }
    } else if ((pre.gety() - next.gety() > 0) && (next.getx() == pre.getx())) {
      for (int j = next.gety()+1; j <= pre.gety(); j++) {
        Position vertTemp = new Position(pre.getx(), j);
        hallwayLayout.put(vertTemp, 6);
      }
    } else {
      throw new IllegalArgumentException("hallway invalid");
    }
  }

}
