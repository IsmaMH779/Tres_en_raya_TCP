package server.tresEnRaya;

public class GameRules {
    private char[][] board;

    public GameRules(char[][] board) {
        this.board = board;
    }



    public boolean play(int p1, int p2, int turn) {
        // seleccionar la pieza segun el turno
        char piece = turn == 1 ? 'o' : 'x';
        // colocar la pieza
        board[p1][p2] = piece;

        // verificar si ha ganado
        int horizontalCount = verifyHorizontal( p1, p2, piece, "initial" );
        int verifyCount = verifyVertical( p1, p2, piece, "initial" );
        int rightDiagonal = verifyRightDiagonal( p1, p2, piece, "initial" );
        int leftDiagonal =verifyLeftDiagonal( p1, p2, piece, "initial" );

        return horizontalCount == 3 || verifyCount == 3 || rightDiagonal == 3 || leftDiagonal == 3;
    }

    public char[][] getBoard() {
        return board;
    }

    // Verificar de forma horizontal
    private int verifyHorizontal(int p1, int p2, char piece, String direction) {
        if (p1 < 0 || p1 >= 3 || p2 < 0 || p2 >= 3 || board[p1][p2] != piece) {
            return 0;
        }
        int left = direction.equals("left") || direction.equals("initial") ? verifyHorizontal(p1, p2 - 1, piece, "left") : 0;
        int right = direction.equals("right") || direction.equals("initial") ? verifyHorizontal(p1, p2 + 1, piece, "right") : 0;

        return 1 + left + right;
    }

    // Verificar de forma vertical
    private int verifyVertical(int p1, int p2, char piece, String direction) {
        if (p1 < 0 || p1 >= 3 || p2 < 0 || p2 >= 3 || board[p1][p2] != piece) {
            return 0;
        }
        int up = direction.equals("up") || direction.equals("initial") ? verifyVertical(p1 - 1, p2, piece, "up") : 0;
        int down = direction.equals("down") || direction.equals("initial") ? verifyVertical(p1 + 1, p2, piece, "down") : 0;
        return 1 + up + down;
    }

    // Verificar diagonal derecha
    private int verifyRightDiagonal(int p1, int p2, char piece, String direction) {
        if (p1 < 0 || p1 >= 3 || p2 < 0 || p2 >= 3 || board[p1][p2] != piece) {
            return 0;
        }
        int right = direction.equals("right") || direction.equals("initial") ? verifyRightDiagonal(p1 - 1, p2 + 1, piece, "right") : 0;
        int left = direction.equals("left") || direction.equals("initial") ? verifyRightDiagonal(p1 + 1, p2 - 1, piece, "left") : 0;
        return 1 + right + left;
    }

    // Verificar diagonal izquierda
    private int verifyLeftDiagonal(int p1, int p2, char piece, String direction) {
        if (p1 < 0 || p1 >= 3 || p2 < 0 || p2 >= 3 || board[p1][p2] != piece) {
            return 0;
        }
        int right = direction.equals("right") || direction.equals("initial") ? verifyLeftDiagonal(p1 + 1, p2 + 1, piece, "right") : 0;
        int left = direction.equals("left") || direction.equals("initial") ? verifyLeftDiagonal(p1 - 1, p2 - 1, piece, "left") : 0;
        return 1 + right + left;
    }
}
