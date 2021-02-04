import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

/**
 * To test all methods in the Character class
 */
public class CharacterTests {
  Character noah = new Character("noah");
  Character benjamin = new Character("benjamin");

  /**
   * Tests the getName method in the Character class.
   */
  @Test
  public void getNameTest() {
    assertEquals(noah.getName().equals("noah"), true);
    assertEquals(benjamin.getName().equals("benjamin"), true);
  }
}
