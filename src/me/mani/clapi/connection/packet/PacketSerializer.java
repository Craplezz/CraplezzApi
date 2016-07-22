package me.mani.clapi.connection.packet;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;

public class PacketSerializer {

	public static Packet createPacket(ByteBuffer buffer) {
		try {
			return Packet.getPacket(buffer.get()).getConstructor(ByteBuffer.class).newInstance(buffer);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
