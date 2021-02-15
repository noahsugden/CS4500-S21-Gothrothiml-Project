import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import org.junit.Test;


public class RoomTests {
  Position testPosition1 = new Position(2, 3);
  Position testPosition2 = new Position(5, 4);
  ArrayList<Position> tiles = new ArrayList<>();
  Room testRoom;
  ArrayList<Position> doors = new ArrayList<>();
  Position testDoor1 = new Position(2, 7);
  Position testDoor2 = new Position(11, 3);

  void invalid() {
    tiles.add(testPosition2);
    doors.add(testDoor1);
    doors.add(testDoor2);
    testRoom = new Room(testPosition1, 8, 5, tiles, doors);
  }

  void valid() {
    tiles.add(testPosition2);
    doors.add(testDoor1);
    testRoom = new Room(testPosition1, 8, 5, tiles, doors);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPutBoundaries() {
    invalid();
  }

  @Test
  public void testRoomLayout() {
    valid();

    int doorValue = testRoom.getRoomLayout().get(testDoor1);
    assertEquals(doorValue, 4);
  }
}
