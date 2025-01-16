package server;

import server.game.Match;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class MainServer {

    int port;
    Match match;

    public MainServer(int port) {
        this.port = port;
        this.match = new Match();
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
                PlayerThread playerThread = new PlayerThread(clientSocket, match);
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
        MainServer srv = new MainServer(5558);
        srv.listen();
    }

}
