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

  public boolean equals(Position p) {
    if (this.x == p.getx() && this.y == p.gety()) {
      return true;
    } else {
      return false;
    }
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


}

// from: http://faculty.washington.edu/moishe/javademos/ch06%20Code/Position.java

