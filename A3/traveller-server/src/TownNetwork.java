import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

// Represents the entirety of the town network, maintaining towns, paths, and character positions.
public class TownNetwork {
  public ArrayList<Town> towns;
  public ArrayList<Town[]> paths;
  public HashMap<Town, String> characterPositions;

  public TownNetwork() {
    this.towns = new ArrayList<Town>();
    this.paths = new ArrayList<Town[]>();
    this.characterPositions = new HashMap<Town, String>();
  }

  // Adds the given town to this TownNetwork.  If the Town is already is part of the TownNetwork,
  // it throws an IllegalArgumentException.
  public void addTown(Town t) {
    if (this.towns.contains(t)) {
      throw new IllegalArgumentException("Town is already in network.");
    }
    this.towns.add(t);
    this.characterPositions.put(t, "");
  }

  // Adds a path between the two given Towns.  If either of the towns are not already part of this
  // TownNetwork or a path between them already exists, it throws an IllegalArgumentException.
  public void addPath(Town t1, Town t2) {
    if (!this.towns.contains(t1) || !this.towns.contains(t2) || this.pathExists(t1, t2)) {
      throw new IllegalArgumentException("Unable to add path.");
    }
    Town[] newPath = new Town[2];
    newPath[0] = t1;
    newPath[1] = t2;
    paths.add(newPath);
    t1.addPath(t2);
  }

  // Places the given Character at the given Town.  If the given Town is already occupied by a
  // Character, it throws an IllegalArgumentException.
  public void placeCharacter(Character c, Town t) {
    if (characterPositions.get(t).equals("")) {
      for (Map.Entry<Town, String> entry : characterPositions.entrySet()) {
        if (entry.getValue().equals(c.getName())) {
          characterPositions.replace(entry.getKey(), "");
        }
      }
      characterPositions.replace(t, c.getName());
    } else {
      throw new IllegalArgumentException("Unable to place character.  Town already contains character.");
    }
  }

  // Determines if a path exists that allows the given Character to travel to the given Town.
  public boolean canReach(Character c, Town t) {
    boolean hasTown = false;
    Town startTown = new Town("");

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

  // Private helper function used to determine if a path between two given Towns already exists
  // in the TownNetwork.
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