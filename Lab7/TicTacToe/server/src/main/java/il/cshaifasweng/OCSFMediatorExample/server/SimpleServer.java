package il.cshaifasweng.OCSFMediatorExample.server;

import java.io.IOException;

import il.cshaifasweng.OCSFMediatorExample.entities.Move;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

/**
 * The Tic-Tac-Toe server. It extends the OCSF {@link AbstractServer} and acts
 * as the mediator between players:
 *  - a player sends "join" when it wants to play;
 *  - the first joiner waits until a second player arrives;
 *  - once two players are present a {@link Game} is created, symbols and the
 *    starting player are randomly assigned, and both are notified;
 *  - subsequent {@link Move} messages are routed to the relevant game.
 *
 * Because the framework calls {@code handleMessageFromClient} and
 * {@code clientDisconnected} on the synchronized server monitor, access to the
 * {@code waitingPlayer} field is safe without extra locking.
 */
public class SimpleServer extends AbstractServer {

    /** A player that has joined and is waiting for an opponent (or null). */
    private ConnectionToClient waitingPlayer = null;

    public SimpleServer(int port) {
        super(port);
    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        try {
            if (msg instanceof String && msg.equals("join")) {
                handleJoin(client);
            } else if (msg instanceof Move) {
                Game game = (Game) client.getInfo("game");
                if (game != null) {
                    game.handleMove(client, (Move) msg);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Matchmaking: pair waiting players two at a time into a new game. */
    private void handleJoin(ConnectionToClient client) throws IOException {
        if (waitingPlayer == null || waitingPlayer == client) {
            waitingPlayer = client;
            client.sendToClient("waiting for opponent");
            System.out.println("Player is waiting for an opponent: " + client);
        } else {
            ConnectionToClient firstPlayer = waitingPlayer;
            waitingPlayer = null;
            Game game = new Game(firstPlayer, client);
            game.start();
            System.out.println("Game started between " + firstPlayer + " and " + client);
        }
    }

    @Override
    protected synchronized void clientDisconnected(ConnectionToClient client) {
        if (waitingPlayer == client) {
            waitingPlayer = null;
        }
        Game game = (Game) client.getInfo("game");
        if (game != null) {
            try {
                game.handleDisconnect(client);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Client disconnected: " + client);
    }

    @Override
    protected void clientConnected(ConnectionToClient client) {
        System.out.println("Client connected: " + client);
    }

    @Override
    protected void serverStarted() {
        System.out.println("Server is listening for connections on port " + getPort());
    }

    @Override
    protected void serverStopped() {
        System.out.println("Server has stopped listening for connections.");
    }
}
