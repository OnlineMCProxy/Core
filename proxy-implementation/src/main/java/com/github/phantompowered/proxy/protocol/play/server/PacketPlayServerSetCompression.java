package com.github.phantompowered.proxy.protocol.play.server;

import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.wrapper.ProtoBuf;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerSetCompression implements Packet {

    private int value;

    public PacketPlayServerSetCompression(int value) {
        this.value = value;
    }

    public PacketPlayServerSetCompression() {
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.value = protoBuf.readVarInt();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(this.value);
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.SET_COMPRESSION;
    }
}
