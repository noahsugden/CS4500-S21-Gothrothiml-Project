import java.util.ArrayList;
import java.util.HashMap;

public class Hallway {
  Position init;
  Position end;
  ArrayList<Position> waypoints;
  // mapped to: 5 if horizontal, 6 if vertical
  HashMap<Position, Integer> hallwayLayout;

  public Hallway(Position i, Position e, ArrayList<Position> w) {
    this.init = i;
    this.end = e;
    this.waypoints = w;
    this.putHallwayLayout();
  }

  private void putHallwayLayout() {
    for (int i = 0; i < waypoints.size(); i++) {
      Position curr = waypoints.get(i);
      if (i == 0) {
        this.compareTwoPositions(this.init, curr);
      } else if (i == waypoints.size() - 1) {
        this.compareTwoPositions(curr, this.end);
      } else {
        this.compareTwoPositions(curr, waypoints.get(i + 1));
      }
    }
  }

  public HashMap<Position, Integer> getHallwayLayout() {
    return this.hallwayLayout;
  }

  public Position getInit() {
    return this.init;
  }

  public Position getEnd() {
    return this.end;
  }

  private void compareTwoPositions(Position pre, Position next) {
    if ((next.getx() - pre.getx() > 0) && (next.gety() == pre.gety())) {
      for (int j = pre.getx(); j <= next.getx(); j++) {
        Position horizTemp = new Position(j, next.gety());
        hallwayLayout.put(horizTemp, 5);
      }
    } else if ((next.gety() - pre.gety() > 0) && (next.getx() == pre.getx())) {
      for (int j = pre.gety(); j <= next.gety(); j++) {
        Position vertTemp = new Position(next.getx(), j);
        hallwayLayout.put(vertTemp, 6);
      }
    } else if ((pre.getx() - next.getx() > 0) && (next.gety() == pre.gety())) {
      for (int j = next.getx(); j <= pre.getx(); j++) {
        Position horizTemp = new Position(j, pre.gety());
        hallwayLayout.put(horizTemp, 5);
      }
    } else if ((pre.gety() - next.gety() > 0) && (next.getx() == pre.getx())) {
      for (int j = next.gety(); j <= pre.gety(); j++) {
        Position vertTemp = new Position(pre.getx(), j);
        hallwayLayout.put(vertTemp, 6);
      }
    } else {
      throw new IllegalArgumentException("hallway invalid");
    }
  }

}
