import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import org.junit.Test;

public class GhostTests {
  Position testPosition1 = new Position(0, 0);
  Position nonwallTile = new Position(2, 2);
  ArrayList<Position> tiles = new ArrayList<>();
  Room testRoom;
  ArrayList<Position> doors = new ArrayList<>();
  Position testDoor1 = new Position(3, 0);
  Position testDoor2 = new Position(11, 3);

  Position room2start = new Position(6, 6);
  Position nonwallTile2 = new Position(8, 8);
  ArrayList<Position> tiles2 = new ArrayList<>();
  Room testRoom2;
  ArrayList<Position> doors2 = new ArrayList<>();
  Position testDoor3 = new Position(8, 6);
  Level valid;
  Level invalid;
  Ghost ghost;
  HashMap<String, Position> playerPositions = new HashMap<>();
  HashMap<String, Position> adversaryPositions = new HashMap<>();



  void valid() {
    tiles.add(nonwallTile);
    tiles.add(testPosition1);
    tiles.add(new Position(1, 0));
    doors.add(testDoor1);
    testRoom = new Room(testPosition1, 4, 4, tiles, doors);

    tiles2.add(nonwallTile2);
    doors2.add(testDoor3);
    testRoom2 = new Room(room2start, 4, 4, tiles2, doors2);
    ArrayList<Position> validWayPoints = new ArrayList<>();
    validWayPoints.add(new Position(8, 0));
    Hallway validHallway = new Hallway(testDoor1,testDoor3,validWayPoints);
    valid = new Level(testRoom,testRoom2,validHallway);
    playerPositions.put("player", nonwallTile);
    ghost = new Ghost(valid, playerPositions,  adversaryPositions, testPosition1);
    //   Hallway flipped = new Hallway(testDoor3,testDoor1,validWayPoints);
  }

  void valid2() {
    tiles.add(nonwallTile);
    tiles.add(testPosition1);
    tiles.add(new Position(1, 0));
    doors.add(testDoor1);
    testRoom = new Room(testPosition1, 4, 4, tiles, doors);

    tiles2.add(nonwallTile2);
    doors2.add(testDoor3);
    testRoom2 = new Room(room2start, 4, 4, tiles2, doors2);
    ArrayList<Position> validWayPoints = new ArrayList<>();
    validWayPoints.add(new Position(8, 0));
    Hallway validHallway = new Hallway(testDoor1,testDoor3,validWayPoints);
    valid = new Level(testRoom,testRoom2,validHallway);
    playerPositions.put("player", nonwallTile2);
    ghost = new Ghost(valid, playerPositions,  adversaryPositions, testPosition1);
  }

  @Test
  public void testUpdatePosition() {
    valid();
    assertEquals(ghost.currentPosition, new Position(0, 0));
    ghost.updatePosition();
    assertEquals(ghost.currentPosition, new Position(1, 0));
  }

  @Test
  public void testUpdatePosition2() {
    valid2();
    assertEquals(ghost.currentPosition, new Position(0, 0));
    ghost.updatePosition();
    assertEquals(ghost.currentPosition, nonwallTile2);
  }

  @Test
  public void testIsSameRoom() {
    valid();
    assertEquals(ghost.inSameRoom("player"), true);
    valid2();
    assertEquals(ghost.inSameRoom("player"), false);
  }


  @Test
  public void testFindWallTile() {
    valid();
    assertEquals(testRoom2.getRoomLayout().containsKey(ghost.findWallTile()), true);
    valid2();
    assertEquals(testRoom2.getRoomLayout().containsKey(ghost.findWallTile()), true);

  }


  @Test
  public void testGenerateRandom() {
    valid();
    assertEquals(testRoom2.getRoomLayout().containsKey(ghost.generateRandom(0)), true);
    valid2();
    assertEquals(testRoom2.getRoomLayout().containsKey(ghost.generateRandom(0)), true);
  }

}
