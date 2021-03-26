public interface Observer {

  /**
   * To send the registration request to the GameManager server.
   */
  void register();

  /**
   * To receive an updated game state from the GameManager server every time the game state changes.
   */
  void receiveGameState();

  /**
   * Renders the current game state.
   */
  void renderGameState();

  /**
   * Disconnects from the GameManager server.
   */
  void unregister();

  /**
   * Prints the current game state
   * @param array represents the game layout
   */
  void print2D(int[][] array);

}
