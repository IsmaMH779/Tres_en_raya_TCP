package server.game;

public class Match {

    private int turn;
    private int plays;
    private final TresEnRaya tresEnRaya;



    private int numberPlayersConected;

    public Match() {
        this.turn = 1;
        this.tresEnRaya = new TresEnRaya();
        this.numberPlayersConected = 0;
    }

    public boolean play(int p1, int p2) {
        if ( plays == 0 ) {
            plays++;
            return tresEnRaya.play(p1, p2, turn);
        }
        else {
            turn = turn == 1 ? 2 : 1;
            plays++;
            return tresEnRaya.play(p1, p2, turn);
        }

    }

    // metodo para añadir un jugador
    public boolean addPlayer() {
        if (numberPlayersConected == 2) {
            return false;
        }else {
            numberPlayersConected++;
            return true;
        }
    }

    // getter
    public int getPlays() {
        return plays;
    }

    public int getTurn() { return turn; }

    public int getNumberPlayersConected() { return numberPlayersConected; }

    public String getBoard() {
        return tresEnRaya.toString();
    }

}
