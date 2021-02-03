import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TravellerNetwork {
  private List<Town> towns;
  private List<Path> paths;

  public List<Town> getTowns() {
    return towns;
  }

  public void setTowns(List<Town> towns) {
    this.towns = towns;
  }

  public List<Path> getPaths() {
    return paths;
  }

  public void setPaths(List<Path> paths) {
    this.paths = paths;
  }

  public TravellerNetwork() {
    this.towns = new ArrayList<>();
    this.paths = new ArrayList<>();
  }

  public void addTown(String name) {
    Town town = new Town(name);
    this.towns.add(town);
  }

  public void addPath(String to, String from) {
    Town toTown;
    Town fromTown;
    List<Town> neighbors = new ArrayList<>();

    for(Town t: this.towns) {
      if (t.getName().equals(to)) {
        toTown = t;
        neighbors.add(toTown);
      }
      if (t.getName().equals(from)) {
        fromTown = t;
        neighbors.add(fromTown);
      }
    }
    int id = this.paths.size();
    Path newPath = new Path(id, neighbors);
    this.paths.add(newPath);
  }

  public boolean isPassageSafe(String characterName, String townName) {
    Town startingPoint = null;

    boolean destinationExist = false;

    for(Town t: this.towns) {
      Person p = t.getCharacter();
      if (p != null && p.getName().equals(characterName)) {
        startingPoint = t;
      }
      if(t.getName().equals(townName)) {
        destinationExist = true;
      }
    }
    if (!destinationExist) {
      throw new IllegalArgumentException("The destination town does not exist");
    }
    if (startingPoint == null) {
      throw new IllegalArgumentException("The character has not been placed in the game yet");
    }

    LinkedList<Town> townQueue = new LinkedList<>();
    List<String> visitedTowns = new ArrayList<>();
    townQueue.add(startingPoint);

    while(!townQueue.isEmpty()) {
      Town current = townQueue.removeFirst();

      if (visitedTowns.contains(current.getName())) {
        continue;
      }

      visitedTowns.add(current.getName());

      if (current.getName().equals(townName) && !current.isOccupied()) {
        return true;
      }

      if(!current.isOccupied() || current.getName().equals(startingPoint.getName())) {
        for (Path p: this.paths) {
          List<Town> neighbors = p.getTowns();

          if(neighbors.get(0).getName().equals(current.getName())) {
            townQueue.add(neighbors.get(1));
          }
          else if(neighbors.get(1).getName().equals(current.getName())) {
            townQueue.add(neighbors.get(0));
          }
          }
        }
      }
    return false;
  }

}
