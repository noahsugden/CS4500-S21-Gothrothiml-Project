public class Player {
    int id;
    String name;
    Position p;



    Player(int id) {
        this.id = id +10;
    }

    public int getId() {
        return this.id;
    }

    public Position getP() {
        return this.p;
    }

    public void setPosition(Position p) {
        this.p = p;
    }

}
