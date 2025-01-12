import game.TresEnRaya;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        TresEnRaya tresEnRaya = new TresEnRaya();
        Scanner sc = new Scanner(System.in);

        int turn = 1;
        boolean win = false;
        boolean end = false;
        int count = 0;


        while (!end) {

            showBoard(tresEnRaya.getBoard());

            int p1 = sc.nextInt();
            int p2 = sc.nextInt();
            count++;

            win = tresEnRaya.play(p1, p2 , turn);

            turn = turn == 1 ? 2 : 1;

            if (win || count == 9 ) {
                end = true;
            }
        }

        showBoard(tresEnRaya.getBoard());
    }

    private static void showBoard(char[][] board) {
        for ( int i = 0; i < 3 ; i++) {
            for ( int j = 0; j < 3 ; j++) {
                System.out.print(board[i][j]);
            }
            System.out.println();
        }
    }
}