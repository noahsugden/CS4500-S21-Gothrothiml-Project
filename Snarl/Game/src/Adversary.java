public class Adversary {
    int id;
    Position p ;
    int cardinalMoves;

    Adversary(int id) {
        this.id = 100-id;
    }

    public int getId() {
        return id;
    }

    public void setPosition(Position p) {
        this.p = p;
    }

    public Position getP() {
        return this.p;
    }

    public int getCardinalMoves() {return cardinalMoves;}
}
