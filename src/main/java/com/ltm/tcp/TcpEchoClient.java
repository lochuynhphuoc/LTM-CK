package com.ltm.tcp;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class TcpEchoClient {
    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 12345;

        try (Socket socket = new Socket(hostname, port);
                Scanner console = new Scanner(System.in)) {
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            String text;

            System.out.println("Connected to the server. Type messages (type 'bye' to exit):");

            do {
                System.out.print("Client: ");
                text = console.nextLine();
                writer.println(text);

                String response = reader.readLine();
                System.out.println("Server: " + response);

            } while (!"bye".equalsIgnoreCase(text));

        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}
