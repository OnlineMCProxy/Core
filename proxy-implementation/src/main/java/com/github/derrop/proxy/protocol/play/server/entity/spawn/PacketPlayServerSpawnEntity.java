package com.github.derrop.proxy.protocol.play.server.entity.spawn;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.util.PositionedPacket;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerSpawnEntity implements PositionedPacket {

    private int entityId;
    private int x;
    private int y;
    private int z;
    private int speedX;
    private int speedY;
    private int speedZ;
    private byte pitch;
    private byte yaw;
    private int type;
    private int trackedEntityId;

    public PacketPlayServerSpawnEntity(int entityId, int x, int y, int z, int speedX, int speedY, int speedZ, byte pitch, byte yaw, int type, int trackedEntityId) {
        this.entityId = entityId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.speedX = speedX;
        this.speedY = speedY;
        this.speedZ = speedZ;
        this.pitch = pitch;
        this.yaw = yaw;
        this.type = type;
        this.trackedEntityId = trackedEntityId;
    }

    public PacketPlayServerSpawnEntity() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.SPAWN_ENTITY;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public int getSpeedX() {
        return this.speedX;
    }

    public int getSpeedY() {
        return this.speedY;
    }

    public int getSpeedZ() {
        return this.speedZ;
    }

    public byte getPitch() {
        return this.pitch;
    }

    public byte getYaw() {
        return this.yaw;
    }

    public int getType() {
        return this.type;
    }

    public int getTrackedEntityId() {
        return this.trackedEntityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public void setSpeedX(int speedX) {
        this.speedX = speedX;
    }

    public void setSpeedY(int speedY) {
        this.speedY = speedY;
    }

    public void setSpeedZ(int speedZ) {
        this.speedZ = speedZ;
    }

    public void setPitch(byte pitch) {
        this.pitch = pitch;
    }

    public void setYaw(byte yaw) {
        this.yaw = yaw;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setTrackedEntityId(int trackedEntityId) {
        this.trackedEntityId = trackedEntityId;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.entityId = protoBuf.readVarInt();
        this.type = protoBuf.readByte();
        this.x = protoBuf.readInt();
        this.y = protoBuf.readInt();
        this.z = protoBuf.readInt();
        this.pitch = protoBuf.readByte();
        this.yaw = protoBuf.readByte();
        this.trackedEntityId = protoBuf.readInt();

        if (this.trackedEntityId > 0) {
            this.speedX = protoBuf.readShort();
            this.speedY = protoBuf.readShort();
            this.speedZ = protoBuf.readShort();
        }
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(this.entityId);
        protoBuf.writeByte(this.type);
        protoBuf.writeInt(this.x);
        protoBuf.writeInt(this.y);
        protoBuf.writeInt(this.z);
        protoBuf.writeByte(this.pitch);
        protoBuf.writeByte(this.yaw);
        protoBuf.writeInt(this.trackedEntityId);

        if (this.trackedEntityId > 0) {
            protoBuf.writeShort(this.speedX);
            protoBuf.writeShort(this.speedY);
            protoBuf.writeShort(this.speedZ);
        }
    }

    public String toString() {
        return "PacketPlayServerSpawnEntity(entityId=" + this.getEntityId() + ", x=" + this.getX() + ", y=" + this.getY() + ", z=" + this.getZ() + ", speedX=" + this.getSpeedX() + ", speedY=" + this.getSpeedY() + ", speedZ=" + this.getSpeedZ() + ", pitch=" + this.getPitch() + ", yaw=" + this.getYaw() + ", type=" + this.getType() + ", field_149020_k=" + this.getTrackedEntityId() + ")";
    }
}
