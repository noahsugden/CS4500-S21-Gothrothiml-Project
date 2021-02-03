import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import org.junit.Test;
public class TownNetworksTests {
  Town boston = new Town();
  Town waltham = new Town();
  Town newton = new Town();
  Town stoughton = new Town();
  Town waterTown = new Town();
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
}
