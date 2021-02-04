import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class representing the entirety of the town network, maintaining towns, paths,
 * and character positions.
  */

public class TownNetwork {
  public ArrayList<Town> towns;
  public ArrayList<Town[]> paths;
  public HashMap<Town, String> characterPositions;

  /**
   * Constructor of a TownNetwork that sets its paths and towns to an empty ArrayList and
   * sets its character positions to a HashMap, in which the key is a Town and its value is the
   * name of the Character at that Town.
   */
  public TownNetwork() {
    this.towns = new ArrayList<Town>();
    this.paths = new ArrayList<Town[]>();
    this.characterPositions = new HashMap<Town, String>();
  }

  /**
   * Adds the given town to this TownNetwork. If the Town is already is part of the TownNetwork,
   * it throws an IllegalArgumentException.
   * @param t Town to be added
   */
  public void addTown(Town t) {
    //Throws an IllegalArgumentException if Town t was already added.
    if (this.towns.contains(t)) {
      throw new IllegalArgumentException("Town is already in network.");
    }
    this.towns.add(t);
    //Places Town t in HashMap with an empty String as its Character
    this.characterPositions.put(t, "");
  }

  /**
   * Adds a path between the two given Towns. If either of the towns are not already part of this
   * TownNetwork or a path between them already exists, it throws an IllegalArgumentException.
   * @param t1 Town that is part of a new path to be added
   * @param t2 Town that is part of a new path to be added
   */
  public void addPath(Town t1, Town t2) {
    if (!this.towns.contains(t1) || !this.towns.contains(t2) || this.pathExists(t1, t2)) {
      throw new IllegalArgumentException("Unable to add path.");
    }
    Town[] newPath = new Town[2];
    newPath[0] = t1;
    newPath[1] = t2;
    //Adds new array of size two to paths, which represent the paths in this TownNetwork
    paths.add(newPath);
    //Adds t2 to t1.reachableTowns
    t1.addPath(t2);
  }



  /**
   * Places the given Character at the given Town.  If the given Town is already occupied by a
   * Character, it throws an IllegalArgumentException.
   * @param c Character to be placed
   * @param t Town in which given Character will be placed in
   */
  public void placeCharacter(Character c, Town t) {
    //Makes sure Town is not occupied by another Character
    if (characterPositions.get(t).equals("")) {
      //Iterates through HashMap to check if Character c is in another Town
      for (Map.Entry<Town, String> entry : characterPositions.entrySet()) {
        if (entry.getValue().equals(c.getName())) {
          //Replaces CharacterName with an empty String
          characterPositions.replace(entry.getKey(), "");
        }
      }
      characterPositions.replace(t, c.getName());
    } else {
      throw new IllegalArgumentException("Unable to place character.  Town already contains character.");
    }
  }



  /**
   * Determines if a path exists that allows the given Character to travel to the given Town.
   * @param c Character that wants to reach given Town
   * @param t Town to be reached
   * @return boolean stating whether Character c can reach Town t
   */
  public boolean canReach(Character c, Town t) {
    boolean hasTown = false;
    Town startTown = new Town("");

    //Checks whether Character c is already in TownNetwork
    for (Map.Entry<Town, String> entry : characterPositions.entrySet()) {
      if (entry.getValue().equals(c.getName())) {
        hasTown = true;
        startTown = entry.getKey();
      }
    }
    if (hasTown) {
      return startTown.canReach(t);
    } else {
      return false;
    }
  }


  /**
   * Private helper function used to determine if a path between two given Towns already exists
   * in the TownNetwork.
   * @param t1 Town
   * @param t2 Town
   * @return boolean stating whether there already exists a path between t1 and t2
   */
  private boolean pathExists(Town t1, Town t2) {
    if (this.paths.isEmpty()) {
      return false;
    }
    for (int i = 0; i < this.paths.size(); i++) {
      if ((this.paths.get(i)[0].equals(t1) && this.paths.get(i)[1].equals(t2)) ||
              (this.paths.get(i)[0].equals(t2) && this.paths.get(i)[1].equals(t1))) {
        return true;
      }
    }
    return false;
  }


}