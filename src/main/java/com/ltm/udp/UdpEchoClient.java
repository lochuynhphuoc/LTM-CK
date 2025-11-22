package com.ltm.udp;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class UdpEchoClient {
    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 9876;

        try (DatagramSocket socket = new DatagramSocket();
                Scanner console = new Scanner(System.in)) {
            InetAddress address = InetAddress.getByName(hostname);
            byte[] buffer;

            String text;

            System.out.println("Connected to UDP Server. Type messages (type 'bye' to exit):");

            do {
                System.out.print("Client: ");
                text = console.nextLine();
                buffer = text.getBytes();

                DatagramPacket request = new DatagramPacket(buffer, buffer.length, address, port);
                socket.send(request);

                byte[] responseBuffer = new byte[1024];
                DatagramPacket response = new DatagramPacket(responseBuffer, responseBuffer.length);
                socket.receive(response);

                String quote = new String(response.getData(), 0, response.getLength());
                System.out.println("Server: " + quote);

            } while (!"bye".equalsIgnoreCase(text));

        } catch (SocketException ex) {
            System.out.println("Socket error: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}
