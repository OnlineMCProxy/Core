package com.github.phantompowered.proxy.protocol.play.client;

import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.wrapper.ProtoBuf;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PacketPlayClientSpectate implements Packet {

    private UUID targetId;

    public PacketPlayClientSpectate(UUID targetId) {
        this.targetId = targetId;
    }

    public PacketPlayClientSpectate() {
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.targetId = protoBuf.readUniqueId();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeUniqueId(this.targetId);
    }

    @Override
    public int getId() {
        return ProtocolIds.FromClient.Play.SPECTATE;
    }
}
