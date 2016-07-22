package me.mani.clapi.connection.packet;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public abstract class Packet {
	
	private static Map<Byte, Class<? extends Packet>> packets = new HashMap<>();
	
	public static <T extends Packet> void registerPacket(Class<T> clazz, byte packetId) {
		packets.put(packetId, clazz);
	}
	
	public static Class<? extends Packet> getPacket(byte packetId) {
		return packets.get(packetId);
	}

	public abstract byte getPacketId();
	
	public abstract ByteBuffer toBuffer();
	
}