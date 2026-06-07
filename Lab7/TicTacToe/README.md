# Multiplayer Tic-Tac-Toe (OCSF + EventBus)

A two-player networked Tic-Tac-Toe game built on the OCSF (Object Client-Server
Framework) with a JavaFX client. The client uses GreenRobot **EventBus** as the
mediator between the networking layer and the GUI (publish/subscribe pattern).

## Modules

1. **entities** – shared, `Serializable` message classes exchanged over the
   socket: `Move`, `GameStartMessage`, `MoveMessage`, `GameOverMessage`.
2. **server** – an OCSF `AbstractServer`. It matches two players, randomly
   assigns X/O and who starts, validates every move, detects win/draw, and
   broadcasts the authoritative state to both clients. Game rules live in
   `Game`.
3. **client** – a JavaFX app. `SimpleClient` (an OCSF `AbstractClient`) turns
   each server message into an EventBus event; `GameController` subscribes and
   updates the board on the JavaFX thread.

## How it works

- A player connects from the connection screen and the client sends `"join"`.
- The first player waits ("Waiting for another player to join...").
- When a second player joins, the server creates a `Game`, randomly assigns
  symbols and the starting player, and notifies both with a `GameStartMessage`.
- On a click the client sends a `Move`. The server validates it (correct turn,
  empty cell), applies it, and broadcasts a `MoveMessage` to **both** players,
  so both boards stay perfectly in sync.
- When a line is completed or the board fills up, the server sends a
  `GameOverMessage` (win/lose/draw) to both players. If a player disconnects
  mid-game, the other is told it wins.

## Running

1. Run **Maven install** in the parent project (`OCSFMediatorExample`).
2. Start the **server**: `server` module, goal `exec:java`
   (or run `il.cshaifasweng.OCSFMediatorExample.server.App`). Default port 3000.
3. Start the **client**: `client` module, goal `javafx:run`. Enable
   *Allow multiple instances* in the run configuration and launch it twice to
   get two players.
4. In each client window, enter the host/port and click **Connect**.

### Running as JARs

`mvn clean install` produces:
- `server/target/server-0.0.1-SNAPSHOT-jar-with-dependencies.jar`
- `client/target/client-0.0.1-SNAPSHOT.jar` (shaded)

Run with `java -jar <jar>`. The client pom auto-selects the correct JavaFX
native libraries for the build machine (Windows / macOS Intel / macOS Apple
Silicon / Linux) via Maven OS-detection profiles.

### Two computers

Start the server on one machine, then on the client machine enter the server
machine's IP address (instead of `localhost`) and the same port. See the
"communication between 2 computers" note from the lab for firewall/IP setup.
