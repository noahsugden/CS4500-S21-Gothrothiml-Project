import java.util.HashMap;

/**
 * This class represents an Adversary with its respective id number, position in the game,
 * and the number of cardinalMoves it can make.
 */
public class Adversary {
    private int id;
    private Position p ;
    private int cardinalMoves;
    String type;


    public Adversary(HashMap<Position, Integer> levelLayout, HashMap<String, Position> playerPositions,
                     HashMap<String, Position> adversaryPositions) {

    }

    /**
     * Constructor for an Adversary that takes in the Adversary's registration order.
     * @param id represents the Adversary's registration order
     *           We make this.id 100-id because Adversary IDs start at 100.
     */
    public Adversary(int id) {
        this.id = 100-id;
    }


    /**
     * Constructor for an Adversary that takes in the Adversary's position and number of cardinal moves.
     * @param p represents the Adversary's Position
     * @param cardinalMoves represents the number of cardinal moves the Adversary can make
     */
    public Adversary(Position p, int cardinalMoves ) {
        this.p = p;
        this.cardinalMoves = cardinalMoves;
    }

    protected Adversary() {
    }

    /**
     * Gets the Adversary ID.
     * @return an int representing the Adversary's ID
     */
    public int getId() {
        return id;
    }


    /**
     * Sets the position of the Adversary.
     * @param p represents the current Position
     */
    public void setPosition(Position p) {
        this.p = p;
    }


    /**
     * Gets the current Position of the Adversary.
     * @return the current Position of the Adversary
     */
    public Position getP() {
        return this.p;
    }


    /**
     * Gets the number of cardinal moves an Adversary can make.
     * @return an int representing the number of cardinal moves the Adversary can make
     */
    public int getCardinalMoves() {return cardinalMoves;}
}
