import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

/**
 * To test all methods in the TownNetwork class
 */
public class TownNetworksTests {
  Town boston = new Town("boston");
  Town waltham = new Town("waltham");
  Town newton = new Town("newton");
  Character noah = new Character("noah");
  Character benjamin = new Character("benjamin");
  TownNetwork massTowns = new TownNetwork();

  /**
   * To test the addTown method
   */
  @Test
  public void testAddTown() {
    massTowns.addTown(boston);
    massTowns.addTown(waltham);
    ArrayList<Town> townArrayList = new ArrayList<>();
    townArrayList.add(boston);
    townArrayList.add(waltham);
    assertEquals(townArrayList, massTowns.towns);
  }

  /**
   * To test that an IllegalArgumentException is thrown when trying to add a Town
   * that is already in the TownNetwork
   */
  @Test(expected = IllegalArgumentException.class)
  public void testAddTownError1() {
    massTowns.addTown(boston);
    massTowns.addTown(boston);
  }

  /**
   * To test the addPath method
   */
  @Test
  public void testAddPath() {
    massTowns.addTown(boston);
    massTowns.addTown(waltham);
    massTowns.addPath(boston, waltham);
    assertEquals("boston", massTowns.paths.get(0)[0].townName);
    assertEquals("waltham", massTowns.paths.get(0)[1].townName);
  }

  /**
   * To test that an IllegalArgumentException is thrown when trying to add a path between Towns
   * that are not both in the TownNetwork
   */
  @Test(expected = IllegalArgumentException.class)
  public void testAddPathError1() {
    massTowns.addTown(boston);
    massTowns.addPath(boston, waltham);
  }

  /**
   * To test that an IllegalArgumentException is thrown when trying to add a path
   * that already exists
   */
  @Test(expected = IllegalArgumentException.class)
  public void testAddPathError2() {
    massTowns.addTown(boston);
    massTowns.addTown(waltham);
    massTowns.addPath(boston, waltham);
    massTowns.addPath(waltham, boston);
  }

  /**
   * To test the placeCharacter method
   */
  @Test
  public void testPlaceCharacter() {
    massTowns.addTown(boston);
    massTowns.placeCharacter(noah, boston);
    assertEquals(massTowns.characterPositions.get(boston), "noah");
    massTowns.addTown(waltham);
    massTowns.placeCharacter(noah, waltham);
    assertEquals(massTowns.characterPositions.get(waltham), "noah");
    assertEquals(massTowns.characterPositions.get(boston), "");
  }

  /**
   * To test that an IllegalArgumentException is thrown when trying to place a Character
   * in a Town that is already occupied
   */
  @Test(expected = IllegalArgumentException.class)
  public void testPlaceCharacterError1() {
    massTowns.addTown(boston);
    massTowns.placeCharacter(noah, boston);
    massTowns.placeCharacter(benjamin, boston);
  }

  /**
   * To test the canReach method
   */
  @Test
  public void testCanReach() {
    massTowns.addTown(boston);
    massTowns.addTown(waltham);
    massTowns.addTown(newton);
    massTowns.addPath(boston, waltham);
    massTowns.placeCharacter(noah, boston);
    assertEquals(massTowns.canReach(noah, boston), true);
    assertEquals(massTowns.canReach(noah, waltham), true);
    assertEquals(massTowns.canReach(noah, newton), false);
    massTowns.addPath(waltham, newton);
    assertEquals(massTowns.canReach(noah, newton), true);
  }


}
