package me.mani.clapi.connection.client;

import me.mani.clapi.connection.packet.Packet;

/**
 * @author Overload
 * @version 1.0
 */
public abstract class Client {

    private String host;
    private int port;
    private ServerConnector serverConnector;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
        serverConnector = new ServerConnector(this);
        serverConnector.start();
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public void onConnect(ServerConnection serverConnection) {}

    public void onDisconnect(ServerConnection serverConnection) {}

    public void onPacketRecieve(ServerConnection serverConnection, Packet packet) {}

}
