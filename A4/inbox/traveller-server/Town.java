public class Town {
  private String name;
  private Person character;
  private boolean isOccupied;

  public Town(String name) {
    this.name = name;
    this.character = null;
    isOccupied = false;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Person getCharacter() {
    return character;
  }

  public void setCharacter(Person character) {
    this.character = character;
  }

  public boolean isOccupied() {
    return isOccupied;
  }

  public void setOccupied(boolean occupied) {
    isOccupied = occupied;
  }

  public void placeCharacter(Person character) {
    this.character = character;
    this.isOccupied = true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Town town = (Town) o;

    return name.equals(town.name);
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }
}
