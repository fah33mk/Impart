package com.techverx.impart;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidParameterException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Server implements Runnable {

    ExecutorService clientProcessingPool;
    private int maxClient;
    private int port;
    private OnClientAdded clientAddedListener;
    private OnPacketReceived onPacketReceived;
    private ServerSocket serverSocket;
    private boolean keepGoing = true;

    private Server() {
    }

    public static synchronized Server builder() {
        return new Server();
    }

    public Server setMaxClient(int MAX_CLIENT) {
        this.maxClient = MAX_CLIENT;
        return this;
    }

    public Server setOnClientAddedListener(OnClientAdded onClientAddedListener) {
        this.clientAddedListener = onClientAddedListener;
        return this;
    }

    public Server setOnPacketReceived(OnPacketReceived onPacketReceived) {
        this.onPacketReceived = onPacketReceived;
        return this;
    }

    public Server build() {
        if (port == 0) {
            throw new InvalidParameterException("Port number cannot be zero please set a valid port number");
        } else if (maxClient == 0) {
            throw new InvalidParameterException("Max client cannot be zero please set a value more than zero with setMaxClient() method.");
        } else {
            // clientProcessingPool = Executors.newFixedThreadPool(maxClient);
            clientProcessingPool = Executors.newFixedThreadPool(maxClient, r -> {
                Thread t = new Thread(r);
                t.setDaemon(true);
                return t;
            });
            Log.d("Server ", "Server created with port=>" + port);
        }
        return this;
    }

    public Server setPort(int port) {
        this.port = port;
        return this;
    }


    @Override
    public void run() {
        try {
            if (serverSocket == null)
                serverSocket = new ServerSocket(port);
            while (keepGoing) {
                Socket clientSocket = serverSocket.accept();
                Client client = new Client(clientSocket, onPacketReceived);
                clientProcessingPool.execute(client);
                if (clientAddedListener != null) {
                    clientAddedListener.OnClientAdded(client , true);
                }
                Log.d("Server ", "Client connected");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Server ", "Port already occupied");
        }
    }

    public void addClient(Client client , boolean newClient) {
        clientProcessingPool.execute(client);
        if (clientAddedListener != null) {
            clientAddedListener.OnClientAdded(client , newClient);
            Log.d("Server ", "Client added");
        }
    }

    public void stop() {
        this.keepGoing = false;
        clientProcessingPool.shutdownNow();
    }
}