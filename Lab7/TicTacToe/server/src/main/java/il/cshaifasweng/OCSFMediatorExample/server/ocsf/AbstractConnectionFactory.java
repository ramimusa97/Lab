// This file contains material supporting the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com

package il.cshaifasweng.OCSFMediatorExample.server.ocsf;

import java.net.*;
import java.io.*;

/**
* The <code> AbstractConnectionFactory </code> is an abstract class
* that must be subclassed when one want to use a
* subclass of <code> ConnectionToClient </code> class.<p>
*
* Project Name: OCSF (Object Client-Server Framework)<p>
*
* @author Dr Robert Lagani&egrave;re
* @author Dr Timothy C. Lethbridge
* @author Fran&ccedil;ois B&eacute;langer
* @author Paul Holden
* @version August 2003 (2.3)
*/
public abstract class AbstractConnectionFactory
{
// METHOD DESIGNED TO BE OVERRIDDEN BY CONCRETE SUBCLASSES ---------

  /**
   * Hook method called each time a new client connection must be created.
   *
   * @param group the thread group that contains the connections.
   * @param clientSocket contains the client's socket.
   * @param server a reference to the server that created this instance.
   * @exception IOException if an I/O error occur when creating the connection.
   */
  protected abstract ConnectionToClient createConnection(ThreadGroup group,
     Socket clientSocket, AbstractServer server) throws IOException;
}
