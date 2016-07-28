package me.mani.clapi.connection.client;

import me.mani.clapi.connection.packet.Packet;
import me.mani.clapi.connection.packet.PacketStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerConnection {

	private static final ExecutorService service = Executors.newCachedThreadPool();

	private final Client client;
	private final Socket socket;
	private InputStream inputReader;
	private OutputStream outputWriter;
	
	public ServerConnection(Client client, Socket socket) throws IOException {
		this.client = client;
		this.socket = socket;
		openConnection();
	}
	
	private void openConnection() throws IOException {
		inputReader = socket.getInputStream();
		outputWriter = socket.getOutputStream();
		
		service.submit(() -> {
			PacketStream packetStream = new PacketStream() {
				
				@Override
				public void accept(Packet packet) {
					handlePacket(packet);
				}
				
			};
			try {
				byte b;
				while ((b = (byte) inputReader.read()) != -1) {
					packetStream.write(b);
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				try {
					closeConnection();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		client.onDisconnect(this);
	}
	
	private void closeConnection() throws IOException {
		socket.close();
	}
	
	private void handlePacket(Packet packet) {
		client.onPacketRecieve(this, packet);
	}
	
	public void sendPacket(Packet packet) {
		try {
			outputWriter.write(packet.toBuffer().array());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Socket getSocket() {
		return socket;
	}
}
