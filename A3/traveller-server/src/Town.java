import java.util.ArrayList;

/**
 * Class that represents a Town that can be placed in a TownNetwork.
 */
public class Town {
  public ArrayList<Town> reachableTowns;
  public String townName;

  /**
   * Constructor of a Town that sets reachableTowns to an empty ArrayList and sets the given String
   * as its townName
   * @param tn
   */
  public Town(String tn) {
    this.reachableTowns = new ArrayList<>();
    this.townName = tn;

  }

  /**
   * To add a path to the given Town
   * @param t Town that will have a path to this Town
   */
  public void addPath(Town t) {
    //Checks that the given Town t is not the same as this Town
    if (!(this.townName.equals(t.townName))) {
      //Avoids the existence of duplicate Towns in reachableTowns
      if (!(this.reachableTowns.contains(t))) {
        this.reachableTowns.add(t);
        //Adds all reachableTowns from Town t to this.reachableTowns, while avoiding duplicates.
        for (Town town : t.reachableTowns) {
          if (!(this.reachableTowns.contains(town)) && !(this.equals(town))) {
            this.reachableTowns.add(town);
          }
        }
      }

      //Adds all reachableTowns from this Town to t.reachableTowns, while avoiding duplicates.
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

  /**
   * To determine if this Town can reach a given Town t.
   * @param t Town to be reached
   * @return boolean stating whether given Town t can be reached from this Town
   */
  public boolean canReach(Town t) {
    return reachableTowns.contains(t) || this.townName.equals(t.townName);
  }


}
