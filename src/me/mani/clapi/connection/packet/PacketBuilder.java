package me.mani.clapi.connection.packet;

import java.nio.ByteBuffer;

/**
 * @author Overload
 * @version 1.0
 */
public abstract class PacketBuilder {

    private static final PacketBuilder INSTANCE = new PacketBuilder() {

        @Override
        public boolean put(byte b) {
            return false;
        }

    };

    private PacketBuilder() {}

    public static IdentifiedEmptyPacket id(byte packetId) {
        return INSTANCE.new IdentifiedEmptyPacket(packetId);
    }

    public abstract boolean put(byte b);


    public class IdentifiedEmptyPacket extends PacketBuilder {

        private ByteBuffer length;
        private byte packetId;

        public IdentifiedEmptyPacket(byte packetId) {
            this.packetId = packetId;
        }

        @Override
        public boolean put(byte b) {
            length.put(b);
            System.out.println(length.position());
            return length.position() >= 2;
        }

        public IdentifiedPacket next() {
            return new IdentifiedPacket(packetId, length.getShort());
        }

    }

    public class IdentifiedPacket extends PacketBuilder {

        private ByteBuffer data;
        private byte packetId;

        public IdentifiedPacket(byte packetId, short length) {
            this.packetId = packetId;
            data = ByteBuffer.allocate(length);
        }

        @Override
        public boolean put(byte b) {
            data.put(b);
            return data.capacity() <= data.position();
        }

        public Packet create() {
            return PacketSerializer.createPacket(packetId, data);
        }

    }

}
