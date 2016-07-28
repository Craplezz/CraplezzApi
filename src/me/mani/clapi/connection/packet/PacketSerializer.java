package me.mani.clapi.connection.packet;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;

public class PacketSerializer {

	public static Packet createPacket(byte packetId, ByteBuffer data) {
		try {
			return Packet.getPacket(packetId).getConstructor(ByteBuffer.class).newInstance(data);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
