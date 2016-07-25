package me.mani.clapi.connection.server;

import me.mani.clapi.connection.packet.Packet;
import me.mani.clapi.connection.packet.PacketStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientConnection {

	private static final ExecutorService service = Executors.newCachedThreadPool();

	private final Server server;
	private final Socket socket;
	private InputStream inputReader;
	private OutputStream outputWriter;
	
	public ClientConnection(Server server, Socket socket) throws IOException {
		this.server = server;
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

			server.onClientDisconnect(this);
		});
	}
	
	public void sendPacket(Packet packet) {
		ByteBuffer packetBuffer = packet.toBuffer();
		ByteBuffer totalBuffer = ByteBuffer.allocate(packetBuffer.capacity() + 3);
		totalBuffer.putShort((short) packetBuffer.capacity()).put(packet.getPacketId()).put(packetBuffer);
		try {
			outputWriter.write(totalBuffer.array());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void handlePacket(Packet packet) {
		server.onPacketRecieve(this, packet);
	}

	private void closeConnection() throws IOException {
		socket.close();
	}

	public Socket getSocket() {
		return socket;
	}
}
