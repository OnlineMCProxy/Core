package com.github.phantompowered.proxy.protocol.play.server.entity.position;

import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.wrapper.ProtoBuf;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import com.github.phantompowered.proxy.protocol.play.server.entity.EntityPacket;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerEntity implements Packet, EntityPacket {

    protected int entityId;
    protected byte posX;
    protected byte posY;
    protected byte posZ;
    protected byte yaw;
    protected byte pitch;
    protected boolean onGround;
    protected boolean hasRotation;

    public PacketPlayServerEntity(int entityId) {
        this.entityId = entityId;
    }

    public PacketPlayServerEntity() {
    }

    @Override
    public int getEntityId() {
        return entityId;
    }

    @Override
    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public byte getPosX() {
        return posX;
    }

    public void setPosX(byte posX) {
        this.posX = posX;
    }

    public byte getPosY() {
        return posY;
    }

    public void setPosY(byte posY) {
        this.posY = posY;
    }

    public byte getPosZ() {
        return posZ;
    }

    public void setPosZ(byte posZ) {
        this.posZ = posZ;
    }

    public byte getYaw() {
        return yaw;
    }

    public void setYaw(byte yaw) {
        this.yaw = yaw;
    }

    public byte getPitch() {
        return pitch;
    }

    public void setPitch(byte pitch) {
        this.pitch = pitch;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public boolean isHasRotation() {
        return hasRotation;
    }

    public void setHasRotation(boolean hasRotation) {
        this.hasRotation = hasRotation;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.entityId = protoBuf.readVarInt();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(this.entityId);
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.ENTITY;
    }

    @Override
    public String toString() {
        return "PacketPlayServerEntity{"
                + "entityId=" + entityId
                + ", posX=" + posX
                + ", posY=" + posY
                + ", posZ=" + posZ
                + ", yaw=" + yaw
                + ", pitch=" + pitch
                + ", onGround=" + onGround
                + ", hasRotation=" + hasRotation
                + '}';
    }
}
