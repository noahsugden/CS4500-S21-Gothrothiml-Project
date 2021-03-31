/**
 * This class represents a Player with its respective id number, name represented as a string, and
 * position in the game.
 */
public class Player {
    int id;
    String name;
    Position p;


    /**
     * Constructor for a Player that takes in the Player's registration order.
     * @param id represents the player's order of registration
     *           10 is added to the ID because, when rendering our level, IDs 1-9 are already used
     *
     */
    public Player(int id) {
        this.id = id;
    }

    /**
     * Constructor for a Player that takes in the Player's position.
     * @param p represents the player's Position
     */
    public Player(Position p) {
        this.p = p;
    }

    /**
     * Gets the ID of this Player.
     * @return an int representing this Player's ID
     */
    public int getId() {
        return this.id;
    }

    /**
     * Gets the Position of this Player.
     * @return a Position representing this Player's position
     */
    public Position getP() {
        return this.p;
    }

    /**
     * Sets this Player's Position to the given Position
     * @param p the current Position
     */
    public void setPosition(Position p) {
        this.p = p;
    }

}
