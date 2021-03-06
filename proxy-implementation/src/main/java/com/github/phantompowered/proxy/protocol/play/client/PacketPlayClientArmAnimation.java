package com.github.phantompowered.proxy.protocol.play.client;

import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.wrapper.ProtoBuf;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayClientArmAnimation implements Packet {

    public PacketPlayClientArmAnimation() {
    }

    @Override
    public void read(@NotNull ProtoBuf buf, @NotNull ProtocolDirection direction, int protocolVersion) {
    }

    @Override
    public void write(@NotNull ProtoBuf buf, @NotNull ProtocolDirection direction, int protocolVersion) {
    }

    @Override
    public int getId() {
        return ProtocolIds.FromClient.Play.ARM_ANIMATION;
    }

    @Override
    public String toString() {
        return "PacketPlayClientArmAnimation{}";
    }
}
