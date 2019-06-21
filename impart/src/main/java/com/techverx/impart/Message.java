package com.techverx.impart;

public class Message {
    private Client client;
    private String packet;

    public Message() {}

    public Message(Client client, String packet) {
        this.client = client;
        this.packet = packet;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getPacket() {
        return packet;
    }

    public void setPacket(String packet) {
        this.packet = packet;
    }

    public void send() {
        if (client != null)
            client.send(packet);
    }
}
