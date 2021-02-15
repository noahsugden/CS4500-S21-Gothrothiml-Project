import java.util.ArrayList;
import java.util.HashMap;

public class Hallway {
  Position init;
  Position end;
  ArrayList<Position> waypoints;
  // mapped to: 5 if horizontal, 6 if vertical
  HashMap<Position, Integer> hallwayLayout = new HashMap<Position, Integer>();

  public Hallway(Position i, Position e, ArrayList<Position> w) {
    this.init = i;
    this.end = e;
    this.waypoints = w;
    this.putHallwayLayout();
  }

  private void putHallwayLayout() {
    if (waypoints.size() ==0) {
      compareTwoPositions(init,end);
    }
    if (waypoints.size() ==1) {
      Position curr = waypoints.get(0);
      this.compareTwoPositions(this.init, curr);
      this.compareTwoPositions(curr,end);
    }
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

    if (this.hallwayLayout.get(init) != null || this.hallwayLayout.get(end) != null) {
      this.hallwayLayout.remove(init);
      this.hallwayLayout.remove(end);
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
