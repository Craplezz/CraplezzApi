package me.mani.clapi.connection.server;

import me.mani.clapi.connection.packet.Packet;

/**
 * @author Overload
 * @version 1.0
 */
public abstract class Server {

    private int port;
    private ClientAcceptor clientAcceptor;

    public Server(int port) {
        this.port = port;
        clientAcceptor = new ClientAcceptor(this);
        clientAcceptor.start();
    }

    public int getPort() {
        return port;
    }

    public void onClientConnect(ClientConnection clientConnection) {}

    public void onClientDisconnect(ClientConnection clientConnection) {}

    public void onPacketRecieve(ClientConnection clientConnection, Packet packet) {}

}
