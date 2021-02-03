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
    this.towns.add(t);
    this.characterPositions.put(t, "");
  }

  public void addPath(Town t1, Town t2) {
    Town[] newPath = new Town[2];
    newPath[0] = t1;
    newPath[1] = t2;
    paths.add(newPath);
    t1.addPath(t2);
    t2.addPath(t1);
  }

  public void placeCharacter(Character c, Town t) {
    if (characterPositions.get(t).equals("")) {
      characterPositions.replace(t, c.getName());
      characterPositions.forEach((town, characterName) -> {
        if (characterName.equals(c.getName())) {
          characterPositions.replace(town, "");
        }
      });
    } else {
      System.out.println("Unable to place character.  Town already contains character.");
    }
  }

  public boolean canReach(Character c, Town t) {
    boolean hasTown = false;
    Town startTown;
    characterPositions.forEach((town, characterName) -> {
      if (characterName.equals(c.getName())) {
        hasTown = true;
        startTown = town;
      }
    });
    if (hasTown) {
      if (characterPositions.get(t).equals(c.getName())) {
        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }

  }

  private boolean isReachable(Town t1, Town t2) {
    ArrayList<Town> visitedSoFar = new ArrayList<>();

  }

}