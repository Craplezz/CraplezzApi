package me.mani.clapi.connection.client;

import java.io.IOException;
import java.net.Socket;

public class ServerConnector extends Thread {

	private Client client;
	
	public ServerConnector(Client client) {
		this.client = client;
	}
	
	@Override
	public void run() {
		try {
			Socket socket = new Socket(client.getHost(), client.getPort());
			client.onConnect(new ServerConnection(client, socket));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
