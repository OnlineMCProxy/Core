package com.github.derrop.proxy.protocol.play.server.entity;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerEntityAnimation implements Packet, EntityPacket {

    private int entityId;
    private int type;

    public PacketPlayServerEntityAnimation(int entityId, int type) {
        this.entityId = entityId;
        this.type = type;
    }

    public PacketPlayServerEntityAnimation() {
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.entityId = protoBuf.readVarInt();
        this.type = protoBuf.readUnsignedByte();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(this.entityId);
        protoBuf.writeByte(this.type);
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.ANIMATION;
    }
}
