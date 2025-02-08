package client;

import data.Match;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Array;
import java.util.Scanner;

public class Client extends Thread{
    private Socket socket;

    private InputStream in = null;
    private OutputStream out = null;

    private BufferedReader inputMessage;
    private PrintStream outputMessage;

    private boolean ingame;
    private Match match;
    private int playerNumber;
    private Scanner sc;

    private ObjectOutputStream oos;
    private ObjectInputStream ooi;

    // constructor
    public Client(String hostname, int port) {
        try {
            socket = new Socket(InetAddress.getByName(hostname), port);
            InputStream inStream = socket.getInputStream();
            OutputStream outStream = socket.getOutputStream();

            // Enlazar los canales de comunicacion
            out = socket.getOutputStream();
            in = socket.getInputStream();

            outputMessage = new PrintStream(out);
            inputMessage = new BufferedReader(new InputStreamReader(in));

            oos = new ObjectOutputStream(out);
            ooi = new ObjectInputStream(in);

            sc = new Scanner(System.in);

            ingame = true;
        } catch (UnknownHostException ex) {
            System.out.println("Error de conexion. No existe el host: " + ex.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {

        try {
            // Obtener el estado inicial del juego
            outputMessage.println("GET_STATUS");
            outputMessage.flush();
            getMatchStatus();

            // Obtener el número de jugador
            playerNumber = match.getPlayersCount();

            if (playerNumber > 2) {
                System.out.println("No te has podido conectar, hay dos jugadores en la partida.");
                socket.close();
                return;
            }

            System.out.println("Te has conectado a la partida.");
            System.out.println("Eres el jugador " + playerNumber);

            while (ingame) {
                System.out.println("Turno del jugador " + match.getTurn());
                showBoard(match);

                if ( match.getWinner() != 0 ) {
                    if (match.getWinner() == playerNumber) {
                        System.out.println("¡Felicidades has ganado!");
                        socket.close();
                        return;
                    } else {
                        System.out.println("Ha ganado el jugador " + match.getWinner() + " suerte la proxima.");
                        socket.close();
                        return;
                    }
                }

                if (match.getMovesCount() == 9) {
                    System.out.println("Habeis empatado.");
                    socket.close();
                    return;
                }

                if (playerNumber == match.getTurn()) {
                    // Es tu turno
                    System.out.println("Es tu turno.");
                    playerMove();

                    // Enviar el movimiento al servidor
                    outputMessage.println("MOVE");
                    outputMessage.flush();
                    sendMatchStatus();

                    outputMessage.println("GET_STATUS");
                    getMatchStatus();
                } else {
                    // No es tu turno, espera y actualiza el estado del juego
                    System.out.println("Espera tu turno...");
                    waitMyTurn();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void waitMyTurn() throws InterruptedException {
        while (match.getTurn() != playerNumber) {
                // Enviar solicitud para obtener el estado del juego
                outputMessage.println("GET_STATUS");
                outputMessage.flush();

                // Obtener el estado del juego
                getMatchStatus();

                Thread.sleep(1000);
        }
    }

    private void sendMatchStatus() {
        try {
            oos.reset();

            oos.writeObject(match);
            oos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // esperar a la jugada y despues cambiar el turno del jugador
    private void playerMove() {
        boolean waitingMove = true;
        String positionString = "";
        String regex = "^[1-3],[1-3]$";
        String[] position;

        while (waitingMove) {
            System.out.print("Haz tu jugada (p1,p2) -> ");
            positionString = sc.nextLine();

            if (positionString.matches(regex)) {
                position = positionString.split(",");
                int p1 = Integer.parseInt(position[0]) - 1;
                int p2 = Integer.parseInt(position[1]) - 1;

                if (match.getBoard()[p1][p2] != '-') {
                    System.out.println("Tu jugada debe ser en un campo vacio ('-')");
                } else {

                    match.setPosition(new int[]{p1, p2});
                    match.setTurn(match.getTurn() == 1 ? 2 : 1);
                    match.setMovesCount(match.getMovesCount() + 1);
                    waitingMove = false;
                }
            } else {
                System.out.println("\nFormato inválido.");
                System.out.println("Normas:  Ingresa la jugada en el formato correcto: (ej. 1,1). El numero debe estar entre el 1 y el 3\n");
            }
        }
    }

    // recibir el objeto match
    private void getMatchStatus() {
        try {
            match = (Match) ooi.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    // mostrar el tablero
    private static void showBoard(Match match) {
        char[][] board = match.getBoard();

        System.out.println("   1   2   3");
        for (int i = 0; i < 3; i++) {
            System.out.print((i + 1) + " ");
            for (int j = 0; j < 3; j++) {
                if (j > 0) {
                    System.out.print("|");
                }
                System.out.print(" " + board[i][j] + " ");
            }
            System.out.println();
            if (i < 2) {
                System.out.println("  ---+---+---");
            }
        }
    }


    public static void main(String[] args) {
        Client client = new Client("localhost", 5558);
        client.start();
    }
}
