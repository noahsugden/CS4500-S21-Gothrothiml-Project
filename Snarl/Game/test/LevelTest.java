import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import org.junit.Test;


public class LevelTest {

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



    void valid() {
        tiles.add(nonwallTile);
        doors.add(testDoor1);
        testRoom = new Room(testPosition1, 4, 4, tiles, doors);

        tiles2.add(nonwallTile2);
        doors2.add(testDoor3);
        testRoom2 = new Room(room2start, 4, 4, tiles2, doors2);
        ArrayList<Position> validWayPoints = new ArrayList<>();
        validWayPoints.add(new Position(8, 0));
        Hallway validHallway = new Hallway(testDoor1,testDoor3,validWayPoints);
        valid = new Level(testRoom,testRoom2,validHallway);
     //   Hallway flipped = new Hallway(testDoor3,testDoor1,validWayPoints);
    }

    void invalid() {
        tiles.add(nonwallTile);
        doors.add(testDoor1);
        testRoom = new Room(testPosition1, 10, 10, tiles, doors);

        tiles2.add(nonwallTile2);
        doors2.add(testDoor3);
        testRoom2 = new Room(room2start, 4, 4, tiles2, doors2);
        invalid = new Level(testRoom, testRoom2);

    }


    @Test(expected = IllegalArgumentException.class)
    public void testInvalidLevel() {
        invalid();
    }


    @Test
    public void testValidLevel() {
        valid();
        int HorizInt = valid.getLevelLayout().get(new Position(8, 0));
        int VertInt = valid.getLevelLayout().get(new Position(8,1));
        assertEquals(5, HorizInt);
        assertEquals(6, VertInt);

        valid.print2D();

    }
}
