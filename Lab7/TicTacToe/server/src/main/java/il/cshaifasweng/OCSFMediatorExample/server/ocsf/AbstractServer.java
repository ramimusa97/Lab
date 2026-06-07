// This file contains material supporting section 3.8 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com

package il.cshaifasweng.OCSFMediatorExample.server.ocsf;

import java.net.*;
import java.util.*;
import java.io.*;

/**
* The <code> AbstractServer </code> class maintains a thread that waits
* for connection attempts from clients. When a connection attempt occurs
* it creates a new <code> ConnectionToClient </code> instance which
* runs as a thread. When a client is thus connected to the
* server, the two programs can then exchange <code> Object </code>
* instances.<p>
*
* Method <code> handleMessageFromClient </code> must be defined by
* a concrete subclass. Several other hook methods may also be
* overriden.<p>
*
* Project Name: OCSF (Object Client-Server Framework)<p>
*
* @author Dr Robert Lagani&egrave;re
* @author Dr Timothy C. Lethbridge
* @author Fran&ccedil;ois B&eacute;langer
* @author Paul Holden
* @version December 2003 (2.31)
*/
public abstract class AbstractServer implements Runnable
{
  // INSTANCE VARIABLES *********************************************

  /**
   * The server socket: listens for clients who want to connect.
   */
  private ServerSocket serverSocket = null;

  /**
   * The connection listener thread.
   */
  private Thread connectionListener = null;

  /**
   * The port number
   */
  private int port;

  /**
   * The server timeout while for accepting connections.
   * Set to half a second by default.
   */
  private int timeout = 500;

  /**
   * The maximum queue length; i.e. the maximum number of clients that
   * can be waiting to connect. Set to 10 by default.
   */
  private int backlog = 10;

  /**
   * The thread group associated with client threads. Each member of the
   * thread group is a <code> ConnectionToClient </code>.
   */
  private ThreadGroup clientThreadGroup;

  /**
   * Indicates if the listening thread is ready to stop.
   */
  private boolean readyToStop = true; // modified in version 2.31

  /**
   * The factory used to create new connections to clients.
   */
  private AbstractConnectionFactory connectionFactory = null;

// CONSTRUCTOR ******************************************************

  /**
   * Constructs a new server.
   *
   * @param port the port number on which to listen.
   */
  public AbstractServer(int port)
  {
    this.port = port;

    this.clientThreadGroup =
      new ThreadGroup("ConnectionToClient threads")
      {
        // All uncaught exceptions in connection threads will
        // be sent to the clientException callback method.
        public void uncaughtException(
          Thread thread, Throwable exception)
        {
          clientException((ConnectionToClient)thread, exception);
        }
      };
  }


// INSTANCE METHODS *************************************************

  /**
   * Begins the thread that waits for new clients.
   *
   * @exception IOException if an I/O error occurs
   * when creating the server socket.
   */
  final public void listen() throws IOException
  {
    if (!isListening())
    {
      if (serverSocket == null)
      {
        serverSocket = new ServerSocket(getPort(), backlog);
      }

      serverSocket.setSoTimeout(timeout);

      connectionListener = new Thread(this);
      connectionListener.start();
    }
  }

  /**
   * Causes the server to stop accepting new connections.
   */
  final public void stopListening()
  {
    readyToStop = true;
  }

  /**
   * Closes the server socket and the connections with all clients.
   *
   * @exception IOException if an I/O error occurs while
   * closing the server socket.
   */
  final public void close() throws IOException
  {
    if (serverSocket == null)
      return;
    stopListening();

    try
    {
      serverSocket.close();
    }
    finally
    {
      synchronized (this)
      {
        // Close the client sockets of the already connected clients
        Thread[] clientThreadList = getClientConnections();
        for (int i=0; i<clientThreadList.length; i++)
        {
          try
          {
            ((ConnectionToClient)clientThreadList[i]).close();
          }
          catch(Exception ex) {}
        }
        serverSocket = null;
      }

      try
      {
        connectionListener.join(); // Wait for the end of listening thread.
      }
      catch(InterruptedException ex) {}
      catch(NullPointerException ex) {} // When thread already dead.

      serverClosed();
    }
  }

  /**
   * Sends a message to every client connected to the server.
   *
   * @param msg   Object The message to be sent
   */
  public void sendToAllClients(Object msg)
  {
    Thread[] clientThreadList = getClientConnections();

    for (int i=0; i<clientThreadList.length; i++)
    {
      try
      {
        ((ConnectionToClient)clientThreadList[i]).sendToClient(msg);
      }
      catch (Exception ex) {}
    }
  }


// ACCESSING METHODS ------------------------------------------------

  /**
   * Returns true if the server is ready to accept new clients.
   *
   * @return true if the server is listening.
   */
  final public boolean isListening()
  {
    return connectionListener!=null && connectionListener.isAlive(); // modified in version 2.31
  }

  /**
   * Returns true if the server is closed.
   *
   * @return true if the server is closed.
   */
  final public boolean isClosed()
  {
    return (serverSocket == null);
  }

  /**
   * Returns an array containing the existing client connections.
   *
   * @return an array of <code>Thread</code> containing
   * <code>ConnectionToClient</code> instances.
   */
  synchronized final public Thread[] getClientConnections()
  {
    Thread[] clientThreadList = new
      Thread[clientThreadGroup.activeCount()];

    clientThreadGroup.enumerate(clientThreadList);

    return clientThreadList;
  }

  /**
   * Counts the number of clients currently connected.
   *
   * @return the number of clients currently connected.
   */
  final public int getNumberOfClients()
  {
    return clientThreadGroup.activeCount();
  }

  /**
   * Returns the port number.
   *
   * @return the port number.
   */
  final public int getPort()
  {
    return port;
  }

  /**
   * Sets the port number for the next connection.
   *
   * @param port the port number.
   */
  final public void setPort(int port)
  {
    this.port = port;
  }

  /**
   * Sets the timeout time when accepting connections.
   *
   * @param timeout the timeout time in ms.
   */
  final public void setTimeout(int timeout)
  {
    this.timeout = timeout;
  }

  /**
   * Sets the maximum number of waiting connections accepted by the
   * operating system.
   *
   * @param backlog the maximum number of connections.
   */
  final public void setBacklog(int backlog)
  {
    this.backlog = backlog;
  }

  /**
   * Sets the connection factory.
   *
   * @param factory the connection factory.
   */
  final public void setConnectionFactory(AbstractConnectionFactory factory)
  {
    this.connectionFactory = factory;
  }

// RUN METHOD -------------------------------------------------------

  /**
   * Runs the listening thread that allows clients to connect.
   * Not to be called.
   */
  final public void run()
  {
    // call the hook method to notify that the server is starting
    readyToStop= false;  // added in version 2.31
    serverStarted();

    try
    {
      while(!readyToStop)
      {
        try
        {
          // Wait here for new connection attempts, or a timeout
          Socket clientSocket = serverSocket.accept();

          synchronized(this)
          {
            if (!readyToStop)  // added in version 2.2
            {
              if (connectionFactory == null) {

                new ConnectionToClient(
                  this.clientThreadGroup, clientSocket, this);

              } else {        // added in version 2.3

                connectionFactory.createConnection(
                  this.clientThreadGroup, clientSocket, this);
              }
            }
          }
        }
        catch (InterruptedIOException exception)
        {
          // This will be thrown when a timeout occurs.
        }
      }
    }
    catch (IOException exception)
    {
      if (!readyToStop)
      {
        listeningException(exception);
      }
    }
    finally
    {
      readyToStop = true;
      connectionListener = null;

      // call the hook method to notify that the server has stopped
      serverStopped(); // moved in version 2.31
    }
  }


// METHODS DESIGNED TO BE OVERRIDDEN BY CONCRETE SUBCLASSES ---------

  /**
   * Hook method called each time a new client connection is accepted.
   *
   * @param client the connection connected to the client.
   */
  protected void clientConnected(ConnectionToClient client) {}

  /**
   * Hook method called each time a client disconnects.
   *
   * @param client the connection with the client.
   */
  synchronized protected void clientDisconnected(
    ConnectionToClient client) {}

  /**
   * Hook method called each time an exception is thrown in a
   * ConnectionToClient thread.
   *
   * @param client the client that raised the exception.
   * @param exception the exception thrown.
   */
  synchronized protected void clientException(
    ConnectionToClient client, Throwable exception) {}

  /**
   * Hook method called when the server stops accepting
   * connections because an exception has been raised.
   *
   * @param exception the exception raised.
   */
  protected void listeningException(Throwable exception) {}

  /**
   * Hook method called when the server starts listening for connections.
   */
  protected void serverStarted() {}

  /**
   * Hook method called when the server stops accepting connections.
   */
  protected void serverStopped() {}

  /**
   * Hook method called when the server is closed.
   */
  protected void serverClosed() {}

  /**
   * Handles a command sent from one client to the server.
   * This MUST be implemented by subclasses.
   *
   * @param msg   the message sent.
   * @param client the connection connected to the client that
   *  sent the message.
   */
  protected abstract void handleMessageFromClient(
    Object msg, ConnectionToClient client);


// METHODS TO BE USED FROM WITHIN THE FRAMEWORK ONLY ----------------

  /**
   * Receives a command sent from the client to the server.
   *
   * @param msg   the message sent.
   * @param client the connection connected to the client that
   *  sent the message.
   */
  final synchronized void receiveMessageFromClient(
    Object msg, ConnectionToClient client)
  {
    this.handleMessageFromClient(msg, client);
  }
}
// End of AbstractServer Class
