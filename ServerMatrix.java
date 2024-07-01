package com.example.kalgr_projekt_bat;

import java.net.Socket;
import java.util.concurrent.SynchronousQueue;

public class ServerMatrix {
    public Socket socket;
    public String username;
    public Thread szal;
    public SynchronousQueue<String> uzenetneki_que;
    public ServerMatrix enemy10 = null, enemy8 = null, enemy6 = null;

    public ServerMatrix(Thread szal, Socket socket) {
        this.socket = socket;
        this.username = "";
        this.szal = szal;
        uzenetneki_que = new SynchronousQueue<>();
    }
}
