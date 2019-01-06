package com.company;

import java.net.*;
import java.util.ArrayList;

public class ServerProcess implements Runnable {
    private int port;

    ServerProcess(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            DatagramSocket datagramSocket = new DatagramSocket(port);
            Server.portNumbers.add(datagramSocket.getLocalPort());

            System.out.println("Listening on port " + datagramSocket.getLocalPort());
            byte[] buffer = "ShouldThisReallyBeALongBuffer?".getBytes();


            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                datagramSocket.receive(packet);
                System.out.println("Received a packet knocking to: " + datagramSocket.getLocalPort() + " from " + packet.getAddress());

                if (Server.authAttempts.containsKey(packet.getAddress())) {
                    //Considering the case when the person is currently trying to authenticate (not the first datagram)
                    //adding the number of this port to knocked path

                        Server.authAttempts.get(packet.getAddress()).add(datagramSocket.getLocalPort());

                        //validating the auth sequence
                    if (Server.authAttempts.get(packet.getAddress()).equals(Server.authSequence)) {

                        //running a handler process for this client
                        Server.authAttempts.remove(packet.getAddress());
                        System.out.println("State of the map: " + Server.authAttempts);

                        new Thread(new AuthenticationHandler(packet.getAddress(), packet.getPort())).start();

                        } else if (Server.authAttempts.get(packet.getAddress()).size() == Server.authSequence.size() &&
                                                !Server.authAttempts.get(packet.getAddress()).equals(Server.authSequence)) {
                        System.out.println("Invalid order of knocked ports, auth unsuccessful");
                        Server.authAttempts.remove(packet.getAddress());
                    }
                } else {
                    //If auth attempts don't contain this client, add him there with current port as first one
                    //If he or she is there, check if the current sequence is valid above
                    Server.authAttempts.put(packet.getAddress(), new ArrayList<>());
                    Server.authAttempts.get(packet.getAddress()).add(datagramSocket.getLocalPort());

                    System.out.println("Added user with address " + packet.getAddress() + " to the auth attempts map");
                    System.out.println("State of the map: " + Server.authAttempts);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error when starting the process on port " + port);
            System.exit(-1);
        }
    }
}