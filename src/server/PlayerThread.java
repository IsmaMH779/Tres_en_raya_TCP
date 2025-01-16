package server;



import server.game.Match;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class PlayerThread implements Runnable{
    Socket clientSocket = null;
    BufferedReader in = null;
    PrintStream out = null;
    String inMsg, outMsg;
    boolean finished;
    int moves;
    Match match;

    public PlayerThread(Socket clientSocket, Match match) throws IOException {
        this.clientSocket = clientSocket;
        this.match = match;
        finished = false;
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintStream(clientSocket.getOutputStream());
    }

    @Override
    public void run() {
        boolean inGame = match.addPlayer();

        // si se conecta a la partida este jugador podra jugar si no
        // le mandara un false y este no podra jugar
        if ( inGame ) {
            // manda la confirmacion
            out.println(true);

            // esperar al segundo jugador
            if ( match.getNumberPlayersConected() == 1 ) {
                boolean firstIteration = true;
                while (match.getNumberPlayersConected() !=2) {
                    if (firstIteration) {
                        out.println("wait");
                        out.flush();
                        firstIteration = false;
                    }

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                out.println("start");
                out.flush();
            }

            try {
                while (!finished) {

                    out.println(match.getNumberPlayersConected());

                    inMsg = in.readLine();

                    String[] position = inMsg.split(",");

                    //hacer la jugada mandando las posiciones convertidas en integers
                    boolean win = match.play(Integer.parseInt(position[0]), Integer.parseInt(position[1]));

                    out.println(match.getBoard());
                    out.println(match.getPlays());
                    out.println(match.getTurn());
                    out.println(win);

                    out.flush();

                    finished = win || match.getPlays() == 9;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            out.println(false);
            out.flush();
        }
    }
}
