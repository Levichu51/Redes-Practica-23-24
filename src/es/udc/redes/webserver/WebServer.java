package es.udc.redes.webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class WebServer {
    public static void main(String[] args){
        if(args.length != 1){
            System.err.println("Format: es.udc.redes.tutorial.tcp.server.WebServer <port>");
            System.exit(-1);
        }

        Socket socket = null;
        ServerSocket serverSocket = null;
        ServerThread serverThread;

        try{
            int port = Integer.parseInt(args[0]);
            serverSocket = new ServerSocket(port);

            // Set a timeout of 300 secs
            serverSocket.setSoTimeout(300000);

            while (true){
                socket = serverSocket.accept();

                serverThread = new ServerThread(socket);

                serverThread.start();
            }

        } catch (SocketTimeoutException e) {
            System.err.println("Nothing received in 300 secs");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();

        } finally {
            //Close the socket
            try {
                if(serverSocket != null && !serverSocket.isClosed()){
                    serverSocket.close();
                }
                if(socket != null && !socket.isClosed()){
                    socket.close();
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
