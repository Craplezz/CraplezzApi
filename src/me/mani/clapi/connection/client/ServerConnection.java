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
	private Socket socket;
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
		
		service.execute(() -> {
			PacketStream packetStream = new PacketStream() {

				@Override
				public void accept(Packet packet) {
					handlePacket(packet);
				}

			};
			// Try to hold the connection during hole runtime

			int retries = 0;

			while (true) {
				if (socket.isClosed()) {
					try {
						socket = new Socket(client.getHost(), client.getPort());

						retries = 0;
						System.out.println("[SINFO] Reconnection was successful.");
					} catch (IOException e) {
						e.printStackTrace();
						retries++;
						if (retries >= 5) {
							System.out.println("[SINFO] Connection could not be established.");
							client.onDisconnect(this);
							break;
						}

						System.out.println("[SINFO] Lost connection, trying to reconnect in 5 seconds.");

						// Try again after 5 seconds
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}

						continue;
					}
				}
				try (Socket ignored = this.socket) {
					byte b;
					while ((b = (byte) inputReader.read()) != -1) {
						packetStream.write(b);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private void handlePacket(Packet packet) {
		client.onPacketRecieve(this, packet);
	}
	
	public void sendPacket(Packet packet) {
		try {
			outputWriter.write(packet.toBuffer().array());
			outputWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Socket getSocket() {
		return socket;
	}
}
