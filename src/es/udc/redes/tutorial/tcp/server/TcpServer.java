package es.udc.redes.tutorial.tcp.server;
import java.io.IOException;
import java.net.*;

/** Multithread TCP echo server. */

public class TcpServer {

  public static void main(String argv[]) throws IOException {
    if (argv.length != 1) {
      System.err.println("Format: es.udc.redes.tutorial.tcp.server.TcpServer <port>");
      System.exit(-1);
    }

    Socket socket = null;

    try {
      // Create a server socket
      int port = Integer.parseInt(argv[0]);
      ServerSocket serverSocket = new ServerSocket(port);

      // Set a timeout of 300 secs
      serverSocket.setSoTimeout(300000);

      while (true) {
        // Wait for connections
        socket = serverSocket.accept();

        // Create a ServerThread object, with the new connection as parameter
        ServerThread serverThread = new ServerThread(socket);

        // Initiate thread using the start() method
        serverThread.start();

      }
    // Uncomment next catch clause after implementing the logic
     } catch (SocketTimeoutException e) {
      System.err.println("Nothing received in 300 secs");

    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
      e.printStackTrace();

     } finally{
	    //Close the socket
      if(!socket.isClosed() && socket != null) {
        socket.close();
      }
      else{
        System.err.println("Cannot close");
      }
    }
  }
}
