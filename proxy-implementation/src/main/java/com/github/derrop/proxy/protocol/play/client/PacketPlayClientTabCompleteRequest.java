package com.github.derrop.proxy.protocol.play.client;

import com.github.derrop.proxy.protocol.ProtocolIds;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PacketPlayClientTabCompleteRequest extends DefinedPacket {

    private int transactionId;
    private String cursor;
    private boolean assumeCommand;
    private boolean hasPositon;
    private long position;

    public PacketPlayClientTabCompleteRequest(int transactionId, String cursor) {
        this.transactionId = transactionId;
        this.cursor = cursor;
    }

    public PacketPlayClientTabCompleteRequest(String cursor, boolean assumeCommand, boolean hasPosition, long position) {
        this.cursor = cursor;
        this.assumeCommand = assumeCommand;
        this.hasPositon = hasPosition;
        this.position = position;
    }

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        if (protocolVersion >= ProtocolConstants.MINECRAFT_1_13) {
            transactionId = readVarInt(buf);
        }
        cursor = readString(buf);

        if (protocolVersion < ProtocolConstants.MINECRAFT_1_13) {
            if (protocolVersion >= ProtocolConstants.MINECRAFT_1_9) {
                assumeCommand = buf.readBoolean();
            }

            if (hasPositon = buf.readBoolean()) {
                position = buf.readLong();
            }
        }
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        if (protocolVersion >= ProtocolConstants.MINECRAFT_1_13) {
            writeVarInt(transactionId, buf);
        }
        writeString(cursor, buf);

        if (protocolVersion < ProtocolConstants.MINECRAFT_1_13) {
            if (protocolVersion >= ProtocolConstants.MINECRAFT_1_9) {
                buf.writeBoolean(assumeCommand);
            }

            buf.writeBoolean(hasPositon);
            if (hasPositon) {
                buf.writeLong(position);
            }
        }
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle(this);
    }

    @Override
    public int getId() {
        return ProtocolIds.ServerBound.Play.TAB_COMPLETE;
    }
}
