package es.udc.redes.tutorial.tcp.server;

import java.net.*;
import java.io.*;

/**
 * MonoThread TCP echo server.
 */
public class MonoThreadTcpServer {
    public static void main(String argv[]) throws IOException {
        Socket socket = null;

        if (argv.length != 1) {
            System.err.println("Format: es.udc.redes.tutorial.tcp.server.MonoThreadTcpServer <port>");
            System.exit(-1);
        }
        try {
            // Create a server socket
            int port = Integer.parseInt(argv[0]);
            ServerSocket serverSocket = new ServerSocket(port);
            // Set a timeout of 300 secs
            serverSocket.setSoTimeout(300000);

            while (true) {
                // Wait for connections
                socket = serverSocket.accept();

                // Set the input channel
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Set the output channel
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

                // Receive the client message
                String clientMessage = input.readLine();

                if(clientMessage != null){
                    System.out.println("Received" +clientMessage+ "Sending");
                }else{
                    System.out.println("No valid messages");
                }
                // Send response to the client
                output.println(clientMessage);

                // Close the streams
                input.close();
                output.close();
            }
        // Uncomment next catch clause after implementing the logic            
        } catch (SocketTimeoutException e) {
            System.err.println("Nothing received in 300 secs ");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();

        } finally {
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
