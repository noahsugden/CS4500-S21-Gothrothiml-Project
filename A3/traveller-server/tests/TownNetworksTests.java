import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

public class TownNetworksTests {
  Town boston = new Town("boston");
  Town waltham = new Town("waltham");
  Town newton = new Town("newton");
  Town stoughton = new Town("stoughton");
  Town watertown = new Town("watertown");
  Character noah = new Character("noah");
  Character benjamin = new Character("benjamin");
  TownNetwork massTowns = new TownNetwork();

  @Test
  public void testAddTown() {
    massTowns.addTown(boston);
    massTowns.addTown(waltham);
    ArrayList<Town> townArrayList = new ArrayList<>();
    townArrayList.add(boston);
    townArrayList.add(waltham);
    assertEquals(townArrayList, massTowns.towns);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddTownError1() {
    massTowns.addTown(boston);
    massTowns.addTown(boston);
  }

  @Test
  public void testAddPath() {
    massTowns.addTown(boston);
    massTowns.addTown(waltham);
    massTowns.addPath(boston, waltham);
    assertEquals("boston", massTowns.paths.get(0)[0].townName);
    assertEquals("waltham", massTowns.paths.get(0)[1].townName);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddPathError1() {
    massTowns.addTown(boston);
    massTowns.addPath(boston, waltham);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddPathError2() {
    massTowns.addTown(boston);
    massTowns.addTown(waltham);
    massTowns.addPath(boston, waltham);
    massTowns.addPath(waltham, boston);
  }

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

  @Test(expected = IllegalArgumentException.class)
  public void testPlaceCharacterError1() {
    massTowns.addTown(boston);
    massTowns.placeCharacter(noah, boston);
    massTowns.placeCharacter(benjamin, boston);
  }

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
