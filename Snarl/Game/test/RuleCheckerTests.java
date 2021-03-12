import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import org.junit.Test;
public class RuleCheckerTests {

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
  RuleChecker ruleChecker;
  ArrayList<Position> positionArrayList;
  Adversary adversary;
  HashMap<Integer, Position> adversaryPositions;
  Player player;
  HashMap<Integer, Position> playerPositions;




  void valid() {
    tiles.add(nonwallTile);
    doors.add(testDoor1);
    tiles.add(new Position(2,1));
    tiles.add(new Position(2,2));
    tiles.add(new Position(3,2));
    tiles.add(new Position(3, 3));
    tiles.add(new Position(2, 0));
    testRoom = new Room(testPosition1, 4, 4, tiles, doors);
    tiles2.add(nonwallTile2);
    doors2.add(testDoor3);
    testRoom2 = new Room(room2start, 4, 4, tiles2, doors2);
    ArrayList<Position> validWayPoints = new ArrayList<>();
    validWayPoints.add(new Position(8, 0));
    Hallway validHallway = new Hallway(testDoor1,testDoor3,validWayPoints);
    valid = new Level(testRoom,testRoom2,validHallway);
    valid.putTestKeyAndExit(new Position(10, 10), new Position(10, 11));

    player = new Player(new Position(2,2));
    playerPositions = new HashMap<>();
    playerPositions.put(1, new Position(3,3));

    adversary = new Adversary(new Position(2,2), 1);
    adversaryPositions = new HashMap<>();
    adversaryPositions.put(1, new Position(3, 2));

    positionArrayList = new ArrayList<>();
    positionArrayList.add(new Position(2,1));
    positionArrayList.add(new Position(3,2));


    //   Hallway flipped = new Hallway(testDoor3,testDoor1,validWayPoints);
    ruleChecker = new RuleChecker(valid);

  }

  @Test
  public void testValidTile() {
    valid();
    assertEquals(ruleChecker.validTile(new Position(3, 0)), true);
  }

  @Test
  public void testInvalidTile() {
    valid();
    assertEquals(ruleChecker.validTile(new Position(20, 0)), false);
  }

  @Test
  public void testCalculate1Cardinal() {
    valid();
    assertEquals(positionArrayList, ruleChecker.calculate1Cardinal(new Position(2,2)));
  }

  @Test
  public void testIsValidAdversaryMove() {
    valid();
    assertEquals(true, ruleChecker.isValidAdversaryMove(new Position(2,1),
        adversary, adversaryPositions));
    assertEquals(false, ruleChecker.isValidAdversaryMove(new Position(3,2),
        adversary, adversaryPositions));
  }

  @Test
  public void testCalculatePlayerVisibleTiles() {
    valid();
    ArrayList<Position> out = new ArrayList<>();
    out.add(new Position(2,2));
    out.add(new Position(2,1));
    out.add(new Position(3,2));
    out.add(new Position(3, 3));
    out.add(new Position(2,0));
    assertEquals(true, ruleChecker.calculatePlayerVisibleTiles(
        new Position(2, 2)).containsAll(out));
  }

  @Test
  public void testIsValidPlayerMove() {
    valid();
    assertEquals(true, ruleChecker.isValidPlayerMove(new Position(2,0), player,
        playerPositions));
    assertEquals(false,ruleChecker.isValidPlayerMove(new Position(3,3), player,
        playerPositions));
  }

  @Test
  public void testIsGameStateValid() {
    valid();
    GameState validGS = new GameState(valid, 2, 1);
    assertEquals(true, ruleChecker.isGameStateValid(validGS));
    HashMap<String, Position> playerPositionsMap = new HashMap<>();
    playerPositionsMap.put("Emma", new Position(2,2));
    playerPositionsMap.put("Benjamin", new Position(2, 2));
    HashMap<String, Position> empty = new HashMap<>();
    GameState invalidGS = new GameState(playerPositionsMap, empty, empty, true, valid);
    assertEquals(false, ruleChecker.isGameStateValid(invalidGS));
  }

  @Test
  public void testAdversaryInteraction() {
    valid();
    assertEquals(0, ruleChecker.determineAdversaryInteraction(adversary,
        adversaryPositions, playerPositions));

    assertEquals(1, ruleChecker.determineAdversaryInteraction(new Adversary(new Position(3,3), 1),
        adversaryPositions, playerPositions));

  }

  @Test(expected = IllegalArgumentException.class)
  public void testAdversaryInteractionError() {
    valid();
    ruleChecker.determineAdversaryInteraction(new Adversary(new Position(3,3), 1),
        playerPositions, adversaryPositions);
  }

  @Test
  public void testPlayerInteraction() {
    valid();
    HashMap<Integer, Position> empty = new HashMap<>();
    assertEquals(0, ruleChecker.determinePlayerInteraction(new Player
            (new Position(10, 10)),
        empty, empty, false));
    assertEquals(1, ruleChecker.determinePlayerInteraction(new Player
            (new Position(10, 11)),
        empty, empty, false));
    assertEquals(2, ruleChecker.determinePlayerInteraction(new Player
            (new Position(10, 11)),
        empty, empty, true));
    assertEquals(3, ruleChecker.determinePlayerInteraction(new Player
            (new Position(3, 2)),
        adversaryPositions, playerPositions, false));
    assertEquals(4, ruleChecker.determinePlayerInteraction(new Player
            (new Position(3, 2)),
        empty, empty, false));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidPlayerInteraction() {
    valid();
    HashMap<Integer, Position> empty = new HashMap<>();
    ruleChecker.determinePlayerInteraction(new Player
        (new Position(3,3)), empty, playerPositions, false);
  }
}
