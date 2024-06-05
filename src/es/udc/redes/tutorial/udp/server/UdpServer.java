package es.udc.redes.tutorial.udp.server;

import java.net.*;

/**
 * Implements a UDP echo server.
 */
public class UdpServer {

    public static void main(String argv[]) {
        if (argv.length != 1) {
            System.err.println("Format: es.udc.redes.tutorial.udp.server.UdpServer <port_number>");
            System.exit(-1);
        }

        DatagramSocket datagramSocket = null;
        DatagramPacket datagramPacket = null;
        DatagramPacket response = null;
        byte[] buff = new byte[512];

        try {
            // Create a server socket
            int port = Integer.parseInt(argv[0]);
            datagramSocket = new DatagramSocket(port);

            // Set maximum timeout to 300 secs
            datagramSocket.setSoTimeout(300000);

            while (true) {
                // Prepare datagram for reception
                datagramPacket = new DatagramPacket(buff, buff.length);

                // Receive the message
                datagramSocket.receive(datagramPacket);
                System.out.println("We hear you");

                // Prepare datagram to send response
                response = new DatagramPacket(buff, datagramPacket.getLength(), datagramPacket.getAddress(), datagramPacket.getPort());

                // Send response
                datagramSocket.send(response);
                System.out.println("Echo");
            }
          
        // Uncomment next catch clause after implementing the logic
        } catch (SocketTimeoutException e) {
            System.err.println("No requests received in 300 secs ");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();

        } finally {
        // Close the socket
            if(datagramSocket != null){
                datagramSocket.close();
            }
            else{
                System.err.println("Cannot close the socket");
            }
        }
    }
}
