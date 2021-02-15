import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import org.junit.Test;


public class RoomTests {
  Position testPosition1 = new Position(0, 0);
  Position nonwallTile = new Position(2, 2);
  ArrayList<Position> tiles = new ArrayList<>();
  Room testRoom;
  ArrayList<Position> doors = new ArrayList<>();
  Position testDoor1 = new Position(3, 0);
  Position testDoor2 = new Position(11, 3);

  void invalid() {
    tiles.add(nonwallTile);
    doors.add(testDoor1);
    doors.add(testDoor2);
    testRoom = new Room(testPosition1, 4, 4, tiles, doors);
  }

  void valid() {
    tiles.add(nonwallTile);
    doors.add(testDoor1);
    testRoom = new Room(testPosition1, 4, 4, tiles, doors);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPutBoundaries() {
    invalid();
  }

  @Test
  public void testRoomLayout() {
    valid();
    int doorValue = testRoom.getRoomLayout().get(testDoor1);
    assertEquals(4, doorValue);
  }
}
