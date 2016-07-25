package me.mani.clapi.connection.packet;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

public abstract class PacketStream implements Consumer<Packet> {

	private boolean dataMode;
	private ByteBuffer buffer = ByteBuffer.allocate(2);
	private short count;
	private short length;
	
	public void write(byte b) {
		try {
			buffer.put(b);
			if (!dataMode && ++count == 2) {
				buffer.position(0);
				length = buffer.getShort();
				buffer = ByteBuffer.allocate(length);
				dataMode = true;
			}
			else if (this.buffer.position() == length) {
				buffer.position(0);
				accept(PacketSerializer.createPacket(buffer));
					
				// Reset
				buffer = ByteBuffer.allocate(2);
				count = 0;
				dataMode = false;
			}
		}
		catch (BufferUnderflowException e) {
			System.out.println("Bad packet.");
			accept(null);
		}
	}
	
}
