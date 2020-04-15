package com.github.derrop.proxy.protocol.play.shared;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.protocol.ProtocolIds;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PacketPlayKeepAlive extends DefinedPacket {

    private long randomId;

    @Override
    public void read(ByteBuf buf, ProtocolDirection direction, int protocolVersion) {
        randomId = (protocolVersion >= ProtocolConstants.MINECRAFT_1_12_2) ? buf.readLong() : readVarInt(buf);
    }

    @Override
    public void write(ByteBuf buf, ProtocolDirection direction, int protocolVersion) {
        if (protocolVersion >= ProtocolConstants.MINECRAFT_1_12_2) {
            buf.writeLong(randomId);
        } else {
            writeVarInt((int) randomId, buf);
        }
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle(this);
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.KEEP_ALIVE;
    }
}
