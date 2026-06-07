package il.cshaifasweng.OCSFMediatorExample.server;

import java.io.IOException;

/**
 * Entry point of the Tic-Tac-Toe server.
 * Starts a {@link SimpleServer} and listens for client connections.
 * The port may be passed as the first command-line argument (default 3000).
 */
public class App {

    private static SimpleServer server;

    public static void main(String[] args) throws IOException {
        int port = 3000;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {
                System.out.println("Invalid port argument, falling back to 3000.");
            }
        }
        server = new SimpleServer(port);
        server.listen();
    }
}
