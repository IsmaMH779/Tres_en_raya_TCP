package game;

public class TresEnRayaMain {

    private int turn;
    private final TresEnRaya tresEnRaya;

    public TresEnRayaMain() {
        this.turn = 1;
        this.tresEnRaya = new TresEnRaya();
    }

    // setter
    public void setTurn(int turn) {
        this.turn = turn;
    }
    // getter
    public TresEnRaya getTresEnRaya() {
        return tresEnRaya;
    }
}
