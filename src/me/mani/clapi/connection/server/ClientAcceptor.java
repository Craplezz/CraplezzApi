package me.mani.clapi.connection.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class ClientAcceptor extends Thread {

	private final Server server;
	private final List<ClientConnection> clients = new ArrayList<>();

	public ClientAcceptor(Server server) {
		this.server = server;
	}

	@Override
	public void run() {
		try ( ServerSocket serverSocket = new ServerSocket(server.getPort()) ) {
			while (!serverSocket.isClosed()) {
				connect(new ClientConnection(server, serverSocket.accept()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void connect(ClientConnection clientConnection) {
		clients.add(clientConnection);
		server.onClientConnect(clientConnection);
	}

	public void disconnect(ClientConnection clientConnection) {
		clients.remove(clientConnection);
		server.onClientDisconnect(clientConnection);
	}

	public List<ClientConnection> getClients() {
		return clients;
	}
}
