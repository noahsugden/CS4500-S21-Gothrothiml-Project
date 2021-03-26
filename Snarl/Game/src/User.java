import java.util.HashMap;
import java.util.List;

public interface User {

  /**
   * Sends the User's name to the GameManager server.
   * @param uniqueName represents the User's name
   */
  void sendName(String uniqueName);

  /**
   * Receives the visible tiles from the User's current position.
   * @return 2-D array representing the tile layout
   */
  void receiveVisibleTiles();

  /**
   * Sends the next destination to the GameManager server.
   * @param pos Position representing the destination
   */
  void sendMove(Position pos);


  /**
   * Receives the game level result from the GameManager server.
   * @return game level result
   */
  String receiveResult();

  /**
   * Receives the User's initial position from the GameManager server.
   * @return User's initial position
   */
  Position receiveInitialPosition();

  /**
   * Connects to the GameManager server.
   */
  void createConnection();

  /**
   * Renders the updated game state to the user.
   */
  void renderUpdate();

  /**
   Renders the current surrounding layout.
   @param array surrounding layout
   */
  void print2D(int[][] array);

}
