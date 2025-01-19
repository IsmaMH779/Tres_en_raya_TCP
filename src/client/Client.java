package client;

import data.Match;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
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

            sc = new Scanner(System.in);

            ingame = true;
        } catch (UnknownHostException ex) {
            System.out.println("Error de conexion. No existe el host: " + ex.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        sc = new Scanner(System.in);
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
                return;
            }

            System.out.println("Te has conectado a la partida.");
            System.out.println("Eres el jugador " + playerNumber);

            while (ingame) {
                System.out.println("Turno del jugador " + match.getTurn());
                showBoard(match);

                if (playerNumber == match.getTurn()) {
                    // Es tu turno
                    System.out.println("Es tu turno.");
                    playerMove();

                    // Enviar el movimiento al servidor
                    outputMessage.println("MOVE");
                    outputMessage.flush();
                    sendMatchStatus();
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

    private void waitMyTurn() {
        while (match.getTurn() != playerNumber) {
            try {
                // Enviar solicitud para obtener el estado del juego
                outputMessage.println("GET_STATUS");
                outputMessage.flush();

                // Obtener el estado del juego
                getMatchStatus();

                System.out.println("Turno actual: " + match.getTurn() + " (Jugador: " + playerNumber + ")");
                // Pausar antes de la siguiente solicitud
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException("Error durante la espera: " + e.getMessage());
            }
        }
    }

    private void sendMatchStatus() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.reset();

            oos.writeObject(match);
            oos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    // esperar a la jugada y despues cambiar el turno del jugador
    private void playerMove() {
        System.out.print("Haz tu jugada (p1,p2) -> ");
        String positionString = sc.nextLine();
        String[] position = positionString.split(",");

        match.setPosition(position);
        match.setTurn(match.getTurn() == 1 ? 2 : 1);
    }


    // recibir el objeto match
    private void getMatchStatus() {
        try {
            ObjectInputStream ooi = new ObjectInputStream(in);
            match = (Match) ooi.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    // mostrar el tablero
    private static void showBoard(Match match) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print(match.getBoard()[i][j]);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Client client = new Client("localhost", 5558);
        client.start();
    }
}
