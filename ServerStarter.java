package com.example.kalgr_projekt_bat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerStarter implements Runnable {
    public static final int PORT_NUMBER = 42069;
    protected ServerSocket serverSocket;
    protected ArrayList<ServerMatrix> onlineusers;
    protected ArrayList<Palya> Palya10;
    protected ArrayList<Palya> Palya8;
    protected ArrayList<Palya> Palya6;

    public ServerStarter() throws IOException {
        serverSocket = new ServerSocket(PORT_NUMBER);
        onlineusers = new ArrayList<>();
        Palya10 = new ArrayList<>();
        Palya8 = new ArrayList<>();
        Palya6 = new ArrayList<>();
        ServerMatrix ures = new ServerMatrix(null, null);
        ures.username = "Új játék";
        Palya10.add(new Palya(10, ures, null));
        Palya8.add(new Palya(8, ures, null));
        Palya6.add(new Palya(6, ures, null));
    }

    @Override
    public void run() {
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                try {
                    synchronized (onlineusers) {
                        onlineusers.add(new ServerMatrix(new ServerThr(clientSocket, onlineusers,
                                Palya10, Palya8, Palya6), clientSocket));
                        onlineusers.get(onlineusers.size() - 1).szal.start();
                    }
                } catch (IOException e) {
                    System.err.println("Failed to communicate with client!");
                }
            }
        } catch (IOException e) {
            System.out.println("Accept failed!");
        }
        System.out.println("BIG Server sudoku");

        try {
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Server socket did not sudoku");
        }
    }

    public static void main(String[] args) {
        try {
            new Thread(new ServerStarter()).start();
            System.out.println("A Szerver elindult");
        } catch (IOException e) {
            System.out.println("A Szerver nem indult el");
        }
    }
}
