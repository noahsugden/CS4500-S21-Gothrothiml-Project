import java.util.ArrayList;

public class Town {
  public ArrayList<Town> reachableTowns;
  public Town() {
    this.reachableTowns = new ArrayList<>();

  }

  public void addPath(Town t) {
    this.reachableTowns.add(t);
  }

  public boolean canReach(Town t) {
    return reachableTowns.contains(t);
  }

}
