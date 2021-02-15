public class Position
{

  private int x;
  private int y;

  public int getx()
  {
    return x;
  }//method getx

  public int gety()
  {
    return y;
  }//method gety

  public void setx(int a)
  {
    x = a;
  }//method setx

  public void sety(int a)
  {
    y = a;
  }//method sety

  @Override
  public boolean equals(final Object o) {
    if (o == null) {
      return false;
    }
    Position temp = (Position) o;
    if (this.x == temp.getx() && this.y == temp.gety()) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return 31* this.x + this.y;
  }

  Position ()
  {
    x = 0;
    y = 0;
  }

  Position (int a, int b) {
    x = a;
    y = b;
  }

  public void print() {
    System.out.print("X Hposition is: " + this.getx() + " Y Hposition is: " + this.gety()+ "\n")  ;
  }

}

// from: http://faculty.washington.edu/moishe/javademos/ch06%20Code/Position.java

