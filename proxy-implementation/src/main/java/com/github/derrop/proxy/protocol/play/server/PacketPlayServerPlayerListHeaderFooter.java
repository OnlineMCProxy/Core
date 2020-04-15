package com.github.derrop.proxy.protocol.play.server;

import com.github.derrop.proxy.protocol.ProtocolIds;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PacketPlayServerPlayerListHeaderFooter extends DefinedPacket {

    private String header;
    private String footer;

    @Override
    public void read(ByteBuf buf) {
        header = readString(buf);
        footer = readString(buf);
    }

    @Override
    public void write(ByteBuf buf) {
        writeString(header, buf);
        writeString(footer, buf);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle(this);
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.PLAYER_LIST_HEADER_FOOTER;
    }
}
