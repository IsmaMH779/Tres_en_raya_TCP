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

    // constructor
    public GameThread(Socket clientSocket, Match match) throws IOException {
        this.clientSocket = clientSocket;
        this.match = match;

        // Enlazar los canales de comunicacion
        out = clientSocket.getOutputStream();
        in = clientSocket.getInputStream();

        outputMessage = new PrintStream(out);
        inputMessage = new BufferedReader(new InputStreamReader(in));
    }


    @Override
    public void run() {
        // añadir un jugador si se conectan mas de 2 rechaza la conexion
        match.setPlayersCount(match.getPlayersCount() + 1);
        playerNumber = match.getPlayersCount();

        while (true) {
            try {
                String clientRequest = inputMessage.readLine();

                if ("GET_STATUS".equals(clientRequest)) {
                    // Cliente solicita el estado del juego

                    System.out.println(match.getTurn());
                    sendMatchState();
                } else if ("MOVE".equals(clientRequest)) {
                    // Cliente envía su jugada
                    readMatchState();
                    // Enviar el estado actualizado
                    sendMatchState();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }
    }



    private void waitMyTurn() {
        while (match.getTurn() != playerNumber) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void readMatchState() {
        try {
            ObjectInputStream ois = new ObjectInputStream(in);
            match = (Match) ois.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMatchState() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.reset();

            oos.writeObject(match);
            out.flush();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
