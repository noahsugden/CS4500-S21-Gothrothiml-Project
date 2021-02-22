public class Adversary {
    int id;
    Position p ;

    Adversary(int id) {
        this.id = 100-id;
    }

    public int getId() {
        return id;
    }

    public void setPosition(Position p) {
        this.p = p;
    }
}
