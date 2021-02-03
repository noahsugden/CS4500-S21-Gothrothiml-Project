import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TownNetwork {
  public ArrayList<Town> towns;
  public ArrayList<Town[]> paths;
  public HashMap<Town, String> characterPositions;

  public TownNetwork() {
    this.towns = new ArrayList<Town>();
    this.paths = new ArrayList<Town[]>();
    this.characterPositions = new HashMap<Town, String>();
  }

  public void addTown(Town t) {
    if(this.towns.contains(t)) {
      throw new IllegalArgumentException("town is already in network");
    }
    this.towns.add(t);
    this.characterPositions.put(t, "");
  }

  public void addPath(Town t1, Town t2) {
    if(!this.towns.contains(t1) || !this.towns.contains(t2) || this.pathExists(t1, t2)) {
      throw new IllegalArgumentException("unable to add path");
    }
    Town[] newPath = new Town[2];
    newPath[0] = t1;
    newPath[1] = t2;
    paths.add(newPath);
    t1.addPath(t2);
    for(Town town: t1.reachableTowns) {
      if (!(town.reachableTowns.contains(t2))) {
        town.addPath(t2);
      }

    }
  }

  public void placeCharacter(Character c, Town t) {
    if (characterPositions.get(t).equals("")) {
      for (Map.Entry<Town,String> entry : characterPositions.entrySet()) {
        if (entry.getValue().equals(c.getName())) {
          characterPositions.replace(entry.getKey(), "");
        }
      }
      characterPositions.replace(t, c.getName());
    } else {
      throw new IllegalArgumentException("Unable to place character.  Town already contains character.");
    }
  }

  public boolean canReach(Character c, Town t) {
    boolean hasTown = false;
    Town startTown = new Town("");

    for (Map.Entry<Town,String> entry : characterPositions.entrySet()) {
      if (entry.getValue().equals(c.getName())) {
        hasTown = true;
        startTown = entry.getKey();
      }
    }
      if (hasTown) {
      if (characterPositions.get(t).equals(c.getName())) {
        return true;
      } else {
        for (int i = 0; i < startTown.reachableTowns.size(); i++) {
          System.out.println("t " + startTown.reachableTowns.get(i).townName);
        }

        System.out.println(" " + startTown.canReach(t));
        return startTown.canReach(t);
      }
    } else {
      return false;
    }

  }

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