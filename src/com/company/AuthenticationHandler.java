package com.company;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;

public class AuthenticationHandler implements Runnable {

    private InetAddress clientInetAddress;
    private int clientPort;

    private DatagramSocket sendingSocket;
    private ServerSocket serverSocket;
    private Socket clientSocket;

    public AuthenticationHandler(InetAddress clientInetAddress, int clientPort) {
        this.clientInetAddress = clientInetAddress;
        this.clientPort = clientPort;
    }

    @Override
    public void run() {

        try {
            serverSocket = new ServerSocket(0);
        } catch (Exception e) {
            e.printStackTrace();
        }


        byte[] buffer = (serverSocket.getLocalPort()+"").getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, clientInetAddress, clientPort);

        try {
            sendingSocket = new DatagramSocket();
            sendingSocket.send(packet);
            System.out.println("Sent the invite packet to " + clientInetAddress + ":" + clientPort);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            clientSocket = serverSocket.accept();
            System.out.println("Accepted client socket connection from " + clientInetAddress);

            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

//            String query = clientInetAddress + " says: " + reader.readLine();
//            System.out.println(query);
//            String response = "Responding to " + clientInetAddress + " with 'Hello, " + clientInetAddress + "'!";
//            System.out.println(response);

            writer.write("Hello, " + clientInetAddress + "! You have been successfully authenticated!");
            writer.flush();
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
