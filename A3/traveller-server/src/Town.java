import java.util.ArrayList;

//
public class Town {
  public ArrayList<Town> reachableTowns;
  public String townName;

  public Town(String tn) {
    this.reachableTowns = new ArrayList<>();
    this.townName = tn;

  }

  public void addPath(Town t) {
    if (!(this.townName.equals(t.townName))) {
      if (!(this.reachableTowns.contains(t))) {
        this.reachableTowns.add(t);
        for (Town town : t.reachableTowns) {
          if (!(this.reachableTowns.contains(town)) && !(this.equals(town))) {
            this.reachableTowns.add(town);
          }
        }
      }
      if (!(t.reachableTowns.contains(this))) {
        t.reachableTowns.add(this);
        for (Town town : this.reachableTowns) {
          if (!(t.reachableTowns.contains(town)) && !(t.equals(town))) {
            t.reachableTowns.add(town);
          }
        }
      }

      for (Town town : this.reachableTowns) {
        if (!(town.reachableTowns.contains(t))) {
          town.addPath(t);
        }
      }
    }
  }

  public boolean canReach(Town t) {
    return reachableTowns.contains(t) || this.townName.equals(t.townName);
  }


}
