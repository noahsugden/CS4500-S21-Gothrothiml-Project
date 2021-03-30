import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Ghost extends Adversary {

  int cardinalMoves;
  Level l;
  HashMap<Position, Integer> levelLayout;
  HashMap<String, Position> playerPositions;
  HashMap<String, Position> adversaryPositions;
  Position currentPosition;
  ArrayList<HashMap<Position, Integer>> roomLayouts;

  public Ghost(int id) {
    super(id);
    this.cardinalMoves = 1;
    this.levelLayout = l.getLevelLayout();
    this.roomLayouts = l.getRoomLayouts();
  }

  public Ghost(Level level, HashMap<String, Position> playerPositions,
                HashMap<String, Position> adversaryPositions, Position currentPosition) {
    this.l = level;
    this.levelLayout = l.getLevelLayout();
    this.roomLayouts = l.getRoomLayouts();
    this.playerPositions = playerPositions;
    this.adversaryPositions = adversaryPositions;
    this.currentPosition = currentPosition;

  }
  public void setPosition(Position p) {
    this.currentPosition = p;
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
    if (!inSameRoom(name)) {
      currentPosition = findWallTile();
    } else {
      Position move = findTile(name);
      currentPosition = move;
    }
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
    return levelLayout.get(p) == 2 || levelLayout.get(p) == 7 || levelLayout.get(p) == 8 ||
            levelLayout.get(p) == 4 || levelLayout.get(p) == 5 || levelLayout.get(p) == 6;
  }

  public boolean isTraversableRoom(Position p) {
    return levelLayout.get(p) == 2 || levelLayout.get(p) == 7 || levelLayout.get(p) == 8;
  }

  public boolean inSameRoom(String name) {
    for (int i = 0; i < roomLayouts.size(); i++) {
      if (roomLayouts.get(i).containsKey(playerPositions.get(name)) && roomLayouts.get(i).containsKey(currentPosition)) {
        return true;
      }
    }
    return false;
  }

  public Position findWallTile() {
    int roomIndex = -1;
    for (int i = 0; i < roomLayouts.size(); i++) {
      if (roomLayouts.get(i).containsKey(currentPosition)) {
        roomIndex = i;
      }
    }
    HashMap<Position, Integer> roomLayout = roomLayouts.get(roomIndex);
    ArrayList<Position> wallTiles = new ArrayList<>();
    for (Position p : roomLayout.keySet()) {
      if (roomLayout.get(p) == 1) {
        wallTiles.add(p);
      }
    }
    double distance = 0;
    int wallIndex = -1;
    for (int i = 0; i < wallTiles.size(); i++) {
      double curr = compareTwoPositions(wallTiles.get(i));
      if (distance > curr) {
        distance = curr;
        wallIndex = i;
      }
      if (distance == 0) {
        distance = curr;
        wallIndex = i;
      }

    }
    Position wall = wallTiles.get(wallIndex);
    int wallX = wall.getx();
    int wallY = wall.gety();
    int currX = currentPosition.getx();
    int currY = currentPosition.gety();
    Position leftPos = new Position(currX - 1, currY);
    Position rightPos = new Position(currX + 1, currY);
    Position topPos = new Position(currX, currY - 1);
    Position bottomPos = new Position(currX, currY + 1);

    if (wallX < currX) {
      if (isTraversableRoom(leftPos)) {
        return leftPos;
      } else if (levelLayout.get(leftPos) == 1) {
        return generateRandom(roomIndex);
      }
    } else if (wallY < currY) {
      if (isTraversableRoom(topPos)) {
        return topPos;
      }else if (levelLayout.get(topPos) == 1) {
        return generateRandom(roomIndex);
      }
    } else if (wallX > currX) {
      if (isTraversableRoom(rightPos)) {
        return rightPos;
      }else if (levelLayout.get(rightPos) == 1) {
        return generateRandom(roomIndex);
      }
    } else if (wallY > currY) {
      if (isTraversableRoom(bottomPos)) {
        return bottomPos;
      }else if (levelLayout.get(bottomPos) == 1) {
        return generateRandom(roomIndex);
      }
    }
    return currentPosition;
  }

  public Position generateRandom(int currentIndex) {
    Random r = new Random();
    int low = 0;
    int high = roomLayouts.size();
    int result = r.nextInt(high-low) + low;

    while (result == currentIndex) {
      Random t = new Random();
      result = t.nextInt(high-low) + low;
    }

    HashMap<Position, Integer> moveRoom = roomLayouts.get(result);
    ArrayList<Position> traversable = new ArrayList<>();
    for (Position p : moveRoom.keySet()) {
      if (moveRoom.get(p) != 1) {
        traversable.add(p);
      }
    }
    Random n = new Random();
    int low2 = 0;
    int high2 = traversable.size();
    int result2 = n.nextInt(high2-low2) + low2;

    return traversable.get(result2);
  }
}
