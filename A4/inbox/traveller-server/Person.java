public class Person {
  int Id;
  String name;
  Path path;

  public int getId() {
    return Id;
  }

  public void setId(int id) {
    Id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Path getPath() {
    return path;
  }

  public void setPath(Path path) {
    this.path = path;
  }

  public Person(int id, String name, Path path) {
    this.Id = id;
    this.path = path;
  }

  public Person(int id, String name) {
    this.Id = id;
    this.name = name;
    this.path = null;
  }
}
