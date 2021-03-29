import java.util.HashMap;

public class Zombie extends Adversary {
  int cardinalMoves;
  HashMap<Position, Integer> levelLayout;
  HashMap<String, Position> playerPositions;
  HashMap<String, Position> adversaryPositions;
  Position currentPosition;

  public Zombie(int id) {
    super(id);
    this.cardinalMoves = 1;
  }

  public Zombie(HashMap<Position, Integer> levelLayout, HashMap<String, Position> playerPositions,
                HashMap<String, Position> adversaryPositions, Position currentPosition) {
    this.levelLayout = levelLayout;
    this.playerPositions = playerPositions;
    this.adversaryPositions = adversaryPositions;
    this.currentPosition = currentPosition;

  }

  public void updatePosition() {
    double distance = 0;
    String name = "";
    for (String s : playerPositions.keySet()) {
      Position p = playerPositions.get(s);
      if (compareTwoPositions(p) < distance) {
        distance = compareTwoPositions(p);
        name = s;
      }
      if (distance == 0) {
        distance = compareTwoPositions(p);
        name = s;
      }
    }
    Position move = findTile(name);
    currentPosition = move;
  }

  public double compareTwoPositions(Position otherPos) {
    int x1 = currentPosition.getx();
    int y1 = currentPosition.gety();
    int x2 = otherPos.getx();
    int y2 = otherPos.gety();

    return Math.sqrt((x2 - x1)*(x2 - x1) + (y2 - y1)*(y2 - y1));
  }

  public Position findTile(String name) {
    Position playerPos = playerPositions.get(name);
    int playerX = playerPos.getx();
    int playerY = playerPos.gety();
    int currX = currentPosition.getx();
    int currY = currentPosition.gety();
    Position leftPos = new Position(currX - 1, currY);
    Position rightPos = new Position(currX + 1, currY);
    Position topPos = new Position(currX, currY - 1);
    Position bottomPos = new Position(currX, currY + 1);

    if (playerX < currX) {
      if (isTraversable(leftPos)) {
        return leftPos;
      }
    } else if (playerY < currY) {
      if (isTraversable(topPos)) {
        return topPos;
      }
    } else if (playerX > currX) {
      if (isTraversable(rightPos)) {
        return rightPos;
      }
    } else if (playerY > currY) {
      if (isTraversable(bottomPos)) {
        return bottomPos;
      }
    }
    return currentPosition;
  }

  public boolean isTraversable(Position p) {
    return levelLayout.get(p) == 2 || levelLayout.get(p) == 7 || levelLayout.get(p) == 8 && !adversaryPositions.containsValue(p);
  }

}
