package server.tresEnRaya;

import data.Match;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class GameThread implements Runnable{
    private Socket clientSocket = null;

    private InputStream in = null;
    private OutputStream out = null;

    private BufferedReader inputMessage;
    private PrintStream outputMessage;

    private Match match;
    private int playerNumber;

    ObjectOutputStream oos;
    ObjectInputStream ois;

    // constructor
    public GameThread(Socket clientSocket, Match match) throws IOException {
        this.clientSocket = clientSocket;
        this.match = match;

        // Enlazar los canales de comunicacion
        out = clientSocket.getOutputStream();
        in = clientSocket.getInputStream();

        outputMessage = new PrintStream(out);
        inputMessage = new BufferedReader(new InputStreamReader(in));

        oos = new ObjectOutputStream(out);
        ois = new ObjectInputStream(in);
    }

    @Override
    public void run() {
        // añadir un jugador si se conectan mas de 2 rechaza la conexion
        match.setPlayersCount(match.getPlayersCount() + 1);
        playerNumber = match.getPlayersCount();

        while (!clientSocket.isClosed()) {
            try {
                String clientRequest = inputMessage.readLine();

                if ("GET_STATUS".equals(clientRequest)) {
                    // Cliente solicita el estado del juego
                    sendMatchState();

                } else if ("MOVE".equals(clientRequest)) {
                    // Cliente envía su jugada
                    readMatchState();

                    // registrar el movimiento
                    plerMove();

                    // Enviar el estado actualizado
                    sendMatchState();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void plerMove() {
        GameRules gameRules = new GameRules(match.getBoard());
        int p1 = match.getPosition()[0];
        int p2 = match.getPosition()[1];

        if ( gameRules.play(p1,p2,match.getTurn()) )  {
            match.setWinner(playerNumber);
        }

        match.setBoard(gameRules.getBoard());
    }

    private void readMatchState() {
        try {
            Object obj = ois.readObject();

            // compara para ver si el objeto recibido es un string o Match asi evitamos errores
            if (obj instanceof String) {
                String command = (String) obj;
                System.out.println("Comando recibido: " + command);
            } else if (obj instanceof Match) {
                Match tempMatch = (Match) obj;
                match.updateMatch(tempMatch);
            } else {
                System.out.println("Objeto desconocido recibido.");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMatchState() {
        try {

            oos.reset();

            oos.writeObject(match);
            out.flush();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
