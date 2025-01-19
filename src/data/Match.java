package data;

import java.io.Serializable;

public class Match implements Serializable {
    char[][] board;
    private int playersCount;
    private int turn;
    private int winner;
    private int movesCount;
    private String[] position;

    public Match() {
        // inicializar el board con '-'
        board = new char[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = '-';
            }
        }
        // elige quien juega primero
        turn = (int) (Math.random() * 2) + 1;
        winner = 0;
        movesCount = 0;
    }


    public char[][] getBoard() {
        return board;
    }

    public void setBoard(char[][] board) {
        this.board = board;
    }

    public int getPlayersCount() {
        return playersCount;
    }

    public void setPlayersCount(int playersCount) {
        this.playersCount = playersCount;
    }

    public void setPosition(String[] position) {
        this.position = position;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public int getWinner() {
        return winner;
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }

    public int getMovesCount() {
        return movesCount;
    }

    public void setMovesCount(int movesCount) {
        this.movesCount = movesCount;
    }

    public String[] getPosition() {
        return position;
    }
}


