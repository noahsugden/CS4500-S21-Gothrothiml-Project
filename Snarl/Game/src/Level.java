import java.util.ArrayList;
import java.util.HashMap;

public class Level {
  ArrayList<Room> rooms;
  ArrayList<Hallway> hallways;
  HashMap<Position, Integer> levelLayout;

  public Level() {
    this.rooms = new ArrayList<>();
    this.hallways = new ArrayList<>();
    this.putLevelLayout();
  }

  private void putLevelLayout() {
    for(int i = 0; i < rooms.size(); i++) {
      Room curr = rooms.get(i);
      HashMap<Position, Integer> temp = curr.getRoomLayout();
      for(Position p: temp.keySet()) {
        if(levelLayout.get(p) == null) {
          levelLayout.put(p, temp.get(p));
        }
        else {
          throw new IllegalArgumentException("Rooms overlap!");
        }
      }
    }

    for(int j = 0; j < hallways.size(); j++) {
      Hallway curr = hallways.get(j);
      HashMap<Position, Integer> temp = curr.getHallwayLayout();

      //To check if a Hallway starts and ends with a door
      if(levelLayout.get(curr.getInit()) != 4 || levelLayout.get(curr.getEnd()) != 4) {
        throw new IllegalArgumentException("Hallway should start and end with a door!");
      }
      for(Position p: temp.keySet()) {
        if(levelLayout.get(p) == null) {
          levelLayout.put(p, temp.get(p));
        }
        else {
          throw new IllegalArgumentException("Rooms overlap!");
        }
      }
    }
  }

}
