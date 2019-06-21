package com.techverx.impart;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

class Client implements Runnable {

    private Socket clientSocket;
    private OnPacketReceived onPacketReceived;
    private String host;
    private int port;
    private String id;
    private boolean connected = false;
    private boolean keepGoing = true;
    private MessageListener messageListener;


    public Client(Socket clientSocket, OnPacketReceived onPacketReceived) {
        this.clientSocket = clientSocket;
        this.onPacketReceived = onPacketReceived;
        this.host = clientSocket.getInetAddress().getHostName();
        this.port = clientSocket.getLocalPort();
        this.id = host + ":" + port;
    }

    private Client() {

    }

    public Client(String id) {
        this.id = id;
    }

    public Client setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
        return this;
    }

    public static Client newClient() {
        return new Client();
    }

    public void send(String packet) {
        new Thread(() -> {
            try {
                if (clientSocket != null && clientSocket.getOutputStream() != null) {
                    ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                    outputStream.writeObject(packet);
                    outputStream.flush();
                    if (messageListener != null)
                        messageListener.onMessageSent(getId(), packet);
                    Log.d("MESSAGE_SENT", packet);
                }
            } catch (IOException e) {
                if (messageListener != null)
                    messageListener.onMessageSent(getId(), packet);
                e.printStackTrace();

            }
        }).start();
    }

    @Override
    public void run() {
        readPacket();
    }

    public void readPacket() {
        try {
            while (keepGoing) {
                if (!(clientSocket == null || clientSocket.getInputStream() == null)) {
                    if (clientSocket.getInputStream().available() > 0) {
                        ObjectInputStream br = new ObjectInputStream(clientSocket.getInputStream());
                        String content = (String) br.readObject();
                        if (onPacketReceived != null)
                            onPacketReceived.onPacketReceived(this.getId(), content);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Client) {
            return (((Client) obj).getId()).equalsIgnoreCase(getId());
        } else {
            return super.equals(obj);
        }
    }

    public String getHost() {
        return host;
    }

    public Client setHost(String host) {
        this.host = host;
        return this;
    }

    public int getPort() {
        return port;
    }

    public Client setPort(int port) {
        this.port = port;
        return this;
    }

    public Client setPacketReceivedListener(OnPacketReceived onPacketReceived) {
        this.onPacketReceived = onPacketReceived;
        return this;
    }

    public Client build() throws IOException {
        if (host == null || host.equalsIgnoreCase("")) {
            throw new IllegalArgumentException("host name cannot be empty or null");
        } else if (port <= 0) {
            throw new IllegalArgumentException("port number should not be zero and negative");
        } else if (this.onPacketReceived == null) {
            throw new IllegalArgumentException("No callback found for packet receiving please setPacketReceivedListener");
        } else {
            clientSocket = new Socket(host, port);
            this.id = host + ":" + port;
            Log.d("Client ", "Client created : host=" + host + "     port=" + port);
        }
        return this;
    }

    public String getId() {
        return id;
    }

    public void stop() {
        keepGoing = false;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}
