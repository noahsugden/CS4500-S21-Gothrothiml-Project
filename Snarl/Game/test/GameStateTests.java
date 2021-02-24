import java.util.ArrayList;
import java.util.HashMap;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class GameStateTests {

  Position posOne = new Position(0, 0);
  ArrayList<Position> tiles = new ArrayList<>();
  Position tileOne = new Position(3, 2);
  Position tileTwo = new Position(2, 1);
  Position tileThree = new Position(3, 3);
  Position tileFour = new Position(2, 2);
  Position doorOne = new Position(4, 4);
  ArrayList<Position> doors = new ArrayList<>();
  Room roomOne;

  Position posTwo = new Position(8, 8);
  ArrayList<Position> tilesTwo = new ArrayList<>();
  Position tileFive = new Position(9, 11);
  Position tileSix = new Position(9, 10);
  Position tileSeven = new Position(9, 9);
  Position doorTwo = new Position(10, 12);
  ArrayList<Position> doorsTwo = new ArrayList<>();
  Room roomTwo;

  Level level;
  GameState initial;

  HashMap<Integer, Position> playerPos = new HashMap<>();
  HashMap<Integer, Position> adversaryPos = new HashMap<>();
  GameState intermediate;

  void init() {
    tiles.add(tileOne);
    tiles.add(tileTwo);
    tiles.add(tileThree);
    tiles.add(tileFour);
    doors.add(doorOne);
    roomOne = new Room(posOne, 5, 5, tiles, doors);

    tilesTwo.add(tileFive);
    tilesTwo.add(tileSix);
    tilesTwo.add(tileSeven);
    doorsTwo.add(doorTwo);
    roomTwo = new Room(posTwo, 3, 5, tilesTwo, doorsTwo);

    level = new Level(roomOne, roomTwo);
    initial = new GameState(level, 4, 3);
    playerPos.put(0, tileSeven);
    adversaryPos.put(2, tileOne);
    level.putTestKeyAndExit(tileTwo, tileThree);
    intermediate = new GameState(playerPos, adversaryPos, false, initial);
    int[][] out = initial.out;
    initial.render(11, 99);


  }

  @Test
  public void testId() {
    init();
    Player temp = initial.getPlayer(0);
    Adversary curr = initial.getAdversary(2);
    assertEquals(temp.getId(), 10);
    assertEquals(curr.getId(), 98);

  }

  @Test
  public void testPositions() {
    init();
    Player temp = initial.getPlayer(0);
    Adversary curr = initial.getAdversary(2);

    assertEquals(temp.getP(), tileOne);
    assertEquals(curr.getP(), tileSeven);

    //System.out.println("Tile: " + til);

  }

  @Test
  public void testIntermediateGameState() {
    init();
    Player temp = intermediate.getPlayer(0);
    Adversary curr = intermediate.getAdversary(2);
    assertEquals(temp.getP(), tileSeven);
    assertEquals(curr.getP(), tileOne);
    assertEquals(intermediate.getL().getExitStatus(), false);
  }

  @Test
  public void testUpdatePlayerStatus() {
    init();
    Player temp = intermediate.getPlayer(0);
    intermediate.updatePlayerState(0, tileTwo);
    assertEquals(intermediate.getL().getExitStatus(), true);
    intermediate.updatePlayerState(0, tileThree);
    assertEquals(intermediate.getPlayerArrived(), true);
    intermediate.updatePlayerState(0, tileSix);
    assertEquals(intermediate.getPlayers().contains(temp), false);
  }

  @Test
  public void testUpdateAdversaryStatus() {
    init();
    Player curr = intermediate.getPlayer(3);
    Adversary temp = intermediate.getAdversary(1);
    intermediate.updateAdversaryState(1, tileFour);
    assertEquals(intermediate.getPlayers().contains(curr), false);
  }

}
