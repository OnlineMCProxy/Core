/*
 * MIT License
 *
 * Copyright (c) derrop and derklaro
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.derrop.proxy.protocol.play.server.entity.spawn;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.util.PositionedPacket;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerSpawnEntityWeather implements PositionedPacket {

    private int entityId;
    private int x;
    private int y;
    private int z;
    private byte type;

    public PacketPlayServerSpawnEntityWeather(int entityId, int x, int y, int z, byte type) {
        this.entityId = entityId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
    }

    public PacketPlayServerSpawnEntityWeather() {
    }

    @Override
    public void setYaw(byte yaw) {
    }

    @Override
    public void setPitch(byte pitch) {
    }

    @Override
    public byte getYaw() {
        return 0;
    }

    @Override
    public byte getPitch() {
        return 0;
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.SPAWN_ENTITY_WEATHER;
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

    public byte getType() {
        return this.type;
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

    public void setType(byte type) {
        this.type = type;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.entityId = protoBuf.readVarInt();
        this.type = protoBuf.readByte();
        this.x = protoBuf.readInt();
        this.y = protoBuf.readInt();
        this.z = protoBuf.readInt();
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(this.entityId);
        protoBuf.writeByte(this.type);
        protoBuf.writeInt(this.x);
        protoBuf.writeInt(this.y);
        protoBuf.writeInt(this.z);
    }

    public String toString() {
        return "PacketPlayServerSpawnEntityWeather(entityId=" + this.getEntityId() + ", x=" + this.getX() + ", y=" + this.getY() + ", z=" + this.getZ() + ", type=" + this.getType() + ")";
    }
}
