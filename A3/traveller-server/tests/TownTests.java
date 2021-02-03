import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class TownTests {
  Town boston = new Town("boston");
  Town waltham = new Town("waltham");
  Town newton = new Town("newton");
  Town stoughton = new Town("stoughton");
  Town watertown = new Town("watertown");
  Character noah = new Character("noah");
  Character benjamin = new Character("benjamin");
  TownNetwork massTowns = new TownNetwork();

  @Test
  public void testAddPath() {
    massTowns.addTown(boston);
    massTowns.addTown(waltham);
    massTowns.addTown(newton);
    boston.addPath(waltham);
    waltham.addPath(newton);
    assertEquals(boston.reachableTowns.size() == 2, true);
    assertEquals(boston.reachableTowns.contains(waltham), true);
    assertEquals(boston.reachableTowns.contains(newton), true);
    assertEquals(boston.reachableTowns.contains(boston), false);
    assertEquals(waltham.reachableTowns.size() == 2, true);
    assertEquals(waltham.reachableTowns.contains(waltham), false);
    assertEquals(waltham.reachableTowns.contains(newton), true);
    assertEquals(waltham.reachableTowns.contains(boston), true);
    assertEquals(newton.reachableTowns.size() == 2, true);
    assertEquals(newton.reachableTowns.contains(waltham), true);
    assertEquals(newton.reachableTowns.contains(newton), false);
    assertEquals(newton.reachableTowns.contains(boston), true);
    boston.addPath(waltham);
    assertEquals(boston.reachableTowns.size() == 2, true);
    assertEquals(boston.reachableTowns.contains(waltham), true);
    assertEquals(boston.reachableTowns.contains(newton), true);
    assertEquals(boston.reachableTowns.contains(boston), false);
  }

  @Test
  public void testCanReach() {
    massTowns.addTown(boston);
    massTowns.addTown(waltham);
    massTowns.addTown(newton);
    massTowns.addPath(boston, waltham);
    assertEquals(boston.canReach(boston), true);
    assertEquals(boston.canReach(waltham), true);
    assertEquals(boston.canReach(newton), false);
    assertEquals(waltham.canReach(boston), true);
    assertEquals(waltham.canReach(waltham), true);
    assertEquals(waltham.canReach(newton), false);
    assertEquals(newton.canReach(boston), false);
    assertEquals(newton.canReach(waltham), false);
    assertEquals(newton.canReach(newton), true);
    massTowns.addPath(waltham, newton);
    assertEquals(boston.canReach(boston), true);
    assertEquals(boston.canReach(waltham), true);
    assertEquals(boston.canReach(newton), true);
    assertEquals(waltham.canReach(boston), true);
    assertEquals(waltham.canReach(waltham), true);
    assertEquals(waltham.canReach(newton), true);
    assertEquals(newton.canReach(boston), true);
    assertEquals(newton.canReach(waltham), true);
    assertEquals(newton.canReach(newton), true);

  }
}
