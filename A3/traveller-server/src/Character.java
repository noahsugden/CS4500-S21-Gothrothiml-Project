/**
 * Represents a Character that can be placed in a Town
 */

public class Character {
  String name;

  /**
   * Constructor that initializes a Character with its given name
   * @param s Represents the name of the Character
   */
  public Character(String s) {
    this.name = s;
  }

  /**
   * Getter method that retrieves the Character's name
   * @return Name of the Character
   */
  public String getName() {
    return this.name;
  }
}
