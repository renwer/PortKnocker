package com.company;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    //All the port numbers where datagram sockets are listening
    static List<Integer> portNumbers = new ArrayList<>();
    //All server processes listening on datagram sockets
    private static List<ServerProcess> serverProcesses = new ArrayList<>();
    //Sequence of ports required for successful auth
    static List<Integer> authSequence = new ArrayList<>();

    static Map<InetAddress, ArrayList<Integer>> authAttempts = new ConcurrentHashMap<>();

    public static void main(String[] args) throws Exception {

        if (args.length < 5) {
            System.out.println("There should be at least 5 ports to knock to.");
            System.exit(-1);
        }

        for (int i = 0; i < args.length; i++) {
            serverProcesses.add(new ServerProcess(Integer.parseInt(args[i])));
        }

        //Start all threads
        for (ServerProcess sp : serverProcesses) {
            Thread thread = new Thread(sp);
            //increasing the likelihood of all of them starting before the sublist below is created
            thread.setPriority(8);
            thread.start();
        }

        //Sleep here to let more port numbers initialize before forming an auth sequence
        Thread.sleep(100);

        //Randomly rearranging the port numbers
        Collections.shuffle(portNumbers);

        //Setting up an auth sequence from non-null shuffled port numbers (check for null as some are not yet initialized)
        for (Integer i : portNumbers) {
            if (i != null) {
                authSequence.add(i);
            }
        }
        System.out.println("Auth sequence is " + authSequence);
    }
}
