import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

public class CharacterTests {
  Character noah = new Character("noah");
  Character benjamin = new Character("benjamin");

  @Test
  public void testAddPath() {
    assertEquals(noah.getName().equals("noah"), true);
    assertEquals(benjamin.getName().equals("benjamin"), true);
  }
}
