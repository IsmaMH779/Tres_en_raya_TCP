package server;

import data.Match;
import server.tresEnRaya.GameThread;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private int port;

    private Match match;

    private Server(int port ) {
        this.port = port;
        match = new Match();
    }

    public  void listen() {
        ServerSocket serverSocket = null;
        Socket clientSocket = null;

        onInit();

        try {
            serverSocket = new ServerSocket(port);
            // esperar conexion del cliente para lanzar el thread
            while (true) {
                clientSocket = serverSocket.accept();
                InetAddress clientAdress = clientSocket.getInetAddress();
                System.out.println( clientAdress + " connected");

                // lanzar el thread y establecer la comunicacion
                GameThread playerThread = new GameThread(clientSocket, match);
                Thread player = new Thread(playerThread);
                player.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void onInit() {
        System.out.println("Servidor levantado, escuchando puerto: " + port);
    }

    public static void main(String[] args) {
        Server srv = new Server(5558);
        srv.listen();
    }
}
