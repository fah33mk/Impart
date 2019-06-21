package com.techverx.impart;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.techverx.impart.network.model.Error;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Impart implements OnClientAdded, OnPacketReceived, MessageListener {

    private Thread serverThread;
    private Server server;
    private ArrayList<Client> clients;
    public List<Message> messagesQue = new ArrayList<>();
    private Config config;
    private static Impart INSTANCE;
    private boolean isStarted = false;
    private boolean sendingInProcess = false;

    private Impart() {
        clients = new ArrayList<>();
    }

    public static synchronized Impart instance() {
        if (INSTANCE == null)
            INSTANCE = new Impart();
        return INSTANCE;
    }

    /**
     * @Param Config
     * @Always creates a new instance of object
     */

    public Impart start(Config config) {
        if (server != null) {
            stop();
            server = null;
            serverThread = null;
        }
        this.config = config;
        this.server = Server.builder()
                .setMaxClient(config.getMaxClients())
                .setOnClientAddedListener(this)
                .setOnPacketReceived(this)
                .setPort(config.getPort())
                .build();
        this.serverThread = new Thread(server);
        this.serverThread.start();
        this.isStarted = true;
        if (config.isPingClients()) {
            initConnectionHandler();
        }
        return this;
    }

    public void stop() {
        for (Client client : clients) {
            client.stop();
        }
        clients.clear();
        server.stop();
        isStarted = false;
    }

    public ArrayList<Client> getClients() {
        return clients;
    }

    @Override
    public void OnClientAdded(Client client , boolean newClient) {
        addClient(client);
        if(newClient) {
            if (config != null)
                config.getCallback().newClientAdded(client.getId());
        }
    }

    private void addClient(Client client) {
        int index = clients.indexOf(new Client(client.getId()));
        if (index > -1) {
            clients.get(index).stop();
            clients.remove(index);
        }
        client.setMessageListener(this);
        clients.add(client);
    }

    @Override
    public void onPacketReceived(String id, String packet) {
        Log.d("newPacket", packet);
        if (respondPing(id, packet))
            return;
        if (config != null) {
            config.getCallback().messageReceived(id, packet);
            Log.d("MESSAGE_RECEIVE", packet);
        }
    }

    private boolean respondPing(String id, String packet) {
        if (packet.equalsIgnoreCase(Const.PING)) {
            sendMessage(id, Const.PING_BACK);
            return true;
        } else if (packet.equalsIgnoreCase(Const.PING_BACK)) {
            int index = getClients().indexOf(new Client(id));
            if (index > -1) {
                clients.get(index).setConnected(true);
            }
            return true;
        }
        return false;
    }

    private void updateMessageQue(String id, String packet) {

        if (messagesQue.size() > 0) {
            boolean messageExist = false;
            for (int message = 0; message < messagesQue.size(); message++) {
                if (id.equalsIgnoreCase(messagesQue.get(message).getClient().getId()) && packet.equalsIgnoreCase(messagesQue.get(message).getPacket())) {
                    messagesQue.remove(message); // remove message from waiting que , our confirmation message
                    messageExist = true;
                }
            }

            if (!messageExist)
                sendMessageConfirmation(id, packet); // message not from our device , its confirmation message
        } else {
            sendMessageConfirmation(id, packet); // message not from our device , its confirmation message
        }

    }

    public void createClient(String host, int port, boolean newClient) {
        try {
            Client client = Client.newClient()
                    .setHost(host)
                    .setPort(port)
                    .setPacketReceivedListener(this)
                    .setMessageListener(this)
                    .build();
            server.addClient(client, newClient);
        } catch (Exception e) {
            config.getCallback().createClientFailed(new Error(host + ":" + port, e.getMessage()));
        }
    }


    public void sendMessage(String id, String packet) {
        int index = getClients().indexOf(new Client(id));
        if (index > -1) {
            if (sendingInProcess)
                messagesQue.add(new Message(clients.get(index), packet));
            else {
                sendingInProcess = true;
                clients.get(index).send(packet);
            }
        } else {
            config.getCallback().sendMessageFailed(new Error(id, "client not found"));
        }
    }

    public void sendMessageConfirmation(String id, String packet) {
        int index = getClients().indexOf(new Client(id));
        if (index > -1) {
            clients.get(index).send(packet);
        } else {
            config.getCallback().sendMessageFailed(new Error(id, "client not found"));
        }
    }

    public List<Message> getMessagesQue() {
        return messagesQue;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void initConnectionHandler() {
        final Handler handler = new Handler(Looper.getMainLooper());
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> connectionHandler());
            }
        };
        timer.schedule(doAsynchronousTask, 0, 20000);
    }

    private void connectionHandler() {
        if (clients.size() > 0) {
            for (int client = 0; client < clients.size(); client++) {
                clients.get(client).setConnected(false);
                clients.get(client).send(Const.PING);
            }
        }

        new Handler().postDelayed(() -> {
            if (clients.size() > 0) {
                for (int client = 0; client < clients.size(); client++) {
                    if (!clients.get(client).isConnected()) {
                        int finalClient = client;
                        new Thread(() -> { createClient(clients.get(finalClient).getHost(), clients.get(finalClient).getPort(), false); }).start();
                    }
                }
            }
        }, 1000);
    }


    @Override
    public void onMessageSent(String clientID, String packet) {
        sendingInProcess = false;
        if (messagesQue.size() > 0) {
            messagesQue.get(0).send();
            messagesQue.remove(0);
            sendingInProcess = true;
        }
    }
}