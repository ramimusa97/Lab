package il.cshaifasweng.OCSFMediatorExample.client;

import org.greenrobot.eventbus.EventBus;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;
import il.cshaifasweng.OCSFMediatorExample.entities.GameOverMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.GameStartMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.MoveMessage;

/**
 * The Tic-Tac-Toe client. Extends the OCSF {@link AbstractClient} and, on every
 * message from the server, translates it into an EventBus event. This is the
 * mediator (publish/subscribe) pattern: the networking code never touches the
 * GUI directly — it just posts events, and the controller (a subscriber)
 * reacts. That keeps the two loosely coupled.
 */
public class SimpleClient extends AbstractClient {

    private static SimpleClient client = null;

    private SimpleClient(String host, int port) {
        super(host, port);
    }

    /** Returns the singleton client, creating it for the given host/port the first time. */
    public static SimpleClient getClient(String host, int port) {
        if (client == null) {
            client = new SimpleClient(host, port);
        }
        return client;
    }

    /** Returns the existing client (defaults to localhost:3000 if not created yet). */
    public static SimpleClient getClient() {
        return getClient("localhost", 3000);
    }

    @Override
    protected void handleMessageFromServer(Object msg) {
        if (msg instanceof GameStartMessage) {
            EventBus.getDefault().post(new GameStartEvent((GameStartMessage) msg));
        } else if (msg instanceof MoveMessage) {
            EventBus.getDefault().post(new MoveEvent((MoveMessage) msg));
        } else if (msg instanceof GameOverMessage) {
            EventBus.getDefault().post(new GameOverEvent((GameOverMessage) msg));
        } else if (msg instanceof String) {
            String text = (String) msg;
            if (text.equals("waiting for opponent")) {
                EventBus.getDefault().post(new WaitingEvent());
            } else if (text.equals("opponent disconnected")) {
                EventBus.getDefault().post(new OpponentLeftEvent());
            }
        }
    }
}
