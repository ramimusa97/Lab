// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com

package il.cshaifasweng.OCSFMediatorExample.client.ocsf;

import java.io.*;
import java.net.*;

/**
* The <code> AbstractClient </code> contains all the methods necessary to set
* up the client side of a client-server architecture. When a client is thus
* connected to the server, the two programs can then exchange
* <code> Object </code> instances.<p>
*
* Method <code> handleMessageFromServer </code> must be defined by a concrete
* subclass. Several other hook methods may also be overriden.<p>
*
* Project Name: OCSF (Object Client-Server Framework)<p>
*
* @author Dr. Robert Lagani&egrave;re
* @author Dr. Timothy C. Lethbridge
* @author Fran&ccedil;ois  B&eacutel;langer
* @author Paul Holden
* @version December 2003 (2.31)
*/
public abstract class AbstractClient implements Runnable
{

// INSTANCE VARIABLES ***********************************************

  /**
  * Sockets are used in the operating system as channels
  * of communication between two processes.
  */
  private Socket clientSocket;

  /**
  * The stream to handle data going to the server.
  */
  private ObjectOutputStream output;

  /**
  * The stream to handle data from the server.
  */
  private ObjectInputStream input;

  /**
  * The thread created to read data from the server.
  */
  private Thread clientReader;

  /**
  * Indicates if the thread is ready to stop.
  */
  private boolean readyToStop = false;

  /**
  * The server's host name.
  */
  private String host;

  /**
  * The port number.
  */
  private int port;

// CONSTRUCTORS *****************************************************

  /**
   * Constructs the client.
   *
   * @param  host  the server's host name.
   * @param  port  the port number.
   */
  public AbstractClient(String host, int port)
  {
    this.host = host;
    this.port = port;
  }

// INSTANCE METHODS *************************************************

  /**
   * Opens the connection with the server.
   *
   * @exception IOException if an I/O error occurs when opening.
   */
  final public void openConnection() throws IOException
  {
    if(isConnected())
      return;

    try
    {
      clientSocket = new Socket(host, port);
      output = new ObjectOutputStream(clientSocket.getOutputStream());
      input = new ObjectInputStream(clientSocket.getInputStream());
    }
    catch (IOException ex)
    {
      try
      {
        closeAll();
      }
      catch (Exception exc) { }

      throw ex; // Rethrow the exception.
    }

    clientReader = new Thread(this);  //Create the data reader thread
    readyToStop = false;
    clientReader.start();  //Start the thread
  }

  /**
   * Sends an object to the server.
   *
   * @param msg   The message to be sent.
   * @exception IOException if an I/O error occurs when sending
   */
  public void sendToServer(Object msg) throws IOException
  {
    if (clientSocket == null || output == null) {
      throw new SocketException("socket does not exist");
    }
    output.reset();
    output.writeObject(msg);
  }

  /**
   * Closes the connection to the server.
   *
   * @exception IOException if an I/O error occurs when closing.
   */
  final public void closeConnection() throws IOException
  {
    readyToStop = true;
    closeAll();
  }

// ACCESSING METHODS ------------------------------------------------

  /**
   * @return true if the client is connnected.
   */
  final public boolean isConnected()
  {
    return clientReader != null && clientReader.isAlive();
  }

  /**
   * @return the port number.
   */
  final public int getPort()
  {
    return port;
  }

  /**
   * Sets the server port number for the next connection.
   *
   * @param port the port number.
   */
  final public void setPort(int port)
  {
    this.port = port;
  }

  /**
   * @return the host name.
   */
  final public String getHost()
  {
    return host;
  }

  /**
   * Sets the server host for the next connection.
   *
   * @param host the host name.
   */
  final public void setHost(String host)
  {
    this.host = host;
  }

  /**
   * returns the client's description.
   *
   * @return the client's Inet address.
   */
  final public InetAddress getInetAddress()
  {
    return clientSocket.getInetAddress();
  }

// RUN METHOD -------------------------------------------------------

  /**
   * Waits for messages from the server. Not to be explicitly called.
   */
  final public void run()
  {
    connectionEstablished();

    // The message from the server
    Object msg;

    try
    {
      while(!readyToStop)
      {
        try { // added in version 2.31

          msg = input.readObject();

          if (!readyToStop) {  // Added in version 2.2
            handleMessageFromServer(msg);
          }

        } catch(ClassNotFoundException ex) { // when an unknown class is received

          connectionException(ex);

        } catch (RuntimeException ex) { // thrown by handleMessageFromServer

          connectionException(ex);
        }
      }
    }
    catch (Exception exception)
    {
      if(!readyToStop)
      {
        try
        {
          closeAll();
        }
        catch (Exception ex) { }

        clientReader = null;
        connectionException(exception);
      }
    } finally {

        clientReader = null;
        connectionClosed();   // moved here in version 2.31
    }
  }

// METHODS DESIGNED TO BE OVERRIDDEN BY CONCRETE SUBCLASSES ---------

  /**
   * Hook method called after the connection has been closed.
   */
  protected void connectionClosed() {}

  /**
   * Hook method called each time an exception is thrown by the
   * client's thread that is reading messages from the server.
   *
   * @param exception the exception raised.
   */
  protected void connectionException(Exception exception) {}

  /**
   * Hook method called after a connection has been established.
   */
  protected void connectionEstablished() {}

  /**
   * Handles a message sent from the server to this client.
   * This MUST be implemented by subclasses.
   *
   * @param msg   the message sent.
   */
  protected abstract void handleMessageFromServer(Object msg);


// METHODS TO BE USED FROM WITHIN THE FRAMEWORK ONLY ----------------

  /**
   * Closes all aspects of the connection to the server.
   *
   * @exception IOException if an I/O error occurs when closing.
   */
  final private void closeAll() throws IOException
  {
    try
    {
      if (clientSocket != null)
        clientSocket.close();

      if (output != null)
        output.close();

      if (input != null)
        input.close();
    }
    finally
    {
      output = null;
      input = null;
      clientSocket = null;
    }
  }
}
// end of AbstractClient class
