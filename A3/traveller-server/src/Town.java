import java.util.ArrayList;

public class Town {
  public ArrayList<Town> reachableTowns;
  public Town() {
    this.reachableTowns = new ArrayList<>();

  }

  public void addPath(Town t) {
    this.reachableTowns.add(t);
    for(Town town: t.reachableTowns) {
      if(!(this.reachableTowns.contains(town))) {
        this.reachableTowns.add(town);
      }
    }
    for(Town town: this.reachableTowns) {
      if(!(t.reachableTowns.contains(town))) {
        t.reachableTowns.add(town);
      }
    }
  }

  public boolean canReach(Town t) {
    return reachableTowns.contains(t);
  }


}
