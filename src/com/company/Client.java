package com.company;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;

public class Client {


    public static void main(String[] args) throws Exception {

        boolean isAuthenticated = false;

        //Handle invalid input
        int portsCount = args.length - 1;
        if (portsCount == 0 || portsCount == -1) {
            System.out.println("Invalid input parameters. Should be [Server address] [port number] [port number] [port number]...");
            System.exit(-1);
        }

        //Knock all the ports from the args, wait for 2 seconds for an answer
        DatagramSocket sendingSocket = new DatagramSocket();
        sendingSocket.setSoTimeout(2000);

        byte[] buffer = "01234567897567657".getBytes();

        for (int i = 1; i < args.length; i++) {
            InetAddress receiverAddress = InetAddress.getByName(args[0]);

            DatagramPacket packet = new DatagramPacket(
                    buffer, buffer.length, receiverAddress, Integer.parseInt(args[i]));
            sendingSocket.send(packet);
            //to ensure the packets are sent in order
            Thread.sleep(100);
        }

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        while (true) {
            try {
                sendingSocket.receive(packet);
                //will proceed only if the server responds
                isAuthenticated = true;
                int serverTcpPortNumber = Integer.parseInt(new String(packet.getData(), 0, packet.getLength()));
                System.out.println("Invited to connect with server on TCP port " + serverTcpPortNumber);
                Socket serverConnectionSocket = new Socket(args[0], serverTcpPortNumber);
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(serverConnectionSocket.getInputStream()));
                    System.out.println("Server says: " + reader.readLine());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (SocketTimeoutException e) {
                if (!isAuthenticated) {
                    System.out.println("Received no response from the server, auth unsuccessful");
                }
                System.exit(0);
            }

        }


    }
}
