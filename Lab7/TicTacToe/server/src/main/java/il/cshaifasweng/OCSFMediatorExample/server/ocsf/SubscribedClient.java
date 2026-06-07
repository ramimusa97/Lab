package il.cshaifasweng.OCSFMediatorExample.server.ocsf;

/**
 * A small wrapper that holds a reference to a connected client.
 * Kept from the OCSF mediator template; useful if the server needs to
 * maintain a subscriber list for broadcast-style messages.
 */
public class SubscribedClient {
    private ConnectionToClient client;

    public SubscribedClient(ConnectionToClient client) {
        this.client = client;
    }

    public ConnectionToClient getClient() {
        return client;
    }

    public void setClient(ConnectionToClient client) {
        this.client = client;
    }
}
