import java.util.ArrayList;

public class Town {
  public ArrayList<Town> reachableTowns;
  public String townName;

  public Town(String tn) {
    this.reachableTowns = new ArrayList<>();
    this.townName = tn;

  }

  public void addPath(Town t) {
    if(!(t instanceof Town)) {
      throw new IllegalArgumentException("Not given a town!");
    }
    this.reachableTowns.add(t);
    for(Town town: t.reachableTowns) {
      if(!(this.reachableTowns.contains(town)) && !(this.equals(town))) {
        this.reachableTowns.add(town);
      }
    }
    t.reachableTowns.add(this);
    for(Town town: this.reachableTowns) {
      if(!(t.reachableTowns.contains(town)) && !(t.equals(town))) {
        t.reachableTowns.add(town);
      }
    }


  }

  public boolean canReach(Town t) {
//    for (int i = 0; i < this.reachableTowns.size(); i++) {
//      System.out.println("t " + this.reachableTowns.get(i).townName);
//    }

    return reachableTowns.contains(t);
  }


}
