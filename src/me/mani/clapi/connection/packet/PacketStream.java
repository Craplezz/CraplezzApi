package me.mani.clapi.connection.packet;

import java.nio.BufferUnderflowException;
import java.util.function.Consumer;

public abstract class PacketStream implements Consumer<Packet> {

	private PacketBuilder packetBuilder;
	
	public void write(byte b) {
		try {
			if (packetBuilder == null) {
				packetBuilder = PacketBuilder.id(b);
			}
			else if (packetBuilder instanceof PacketBuilder.IdentifiedEmptyPacket) {
				if (packetBuilder.put(b)) {
					packetBuilder = ((PacketBuilder.IdentifiedEmptyPacket) packetBuilder).next();
				}
			}
			else if (packetBuilder instanceof PacketBuilder.IdentifiedPacket) {
				if (packetBuilder.put(b)) {
					accept(((PacketBuilder.IdentifiedPacket) packetBuilder).create());
					packetBuilder = null;
				}
			}
		}
		catch (BufferUnderflowException e) {
			System.out.println("Bad packet.");
			accept(null);
		}
	}
	
}
