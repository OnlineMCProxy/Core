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
package com.github.phantompowered.proxy.protocol.play.server.world;

import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.wrapper.ProtoBuf;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import com.github.phantompowered.proxy.protocol.play.server.world.util.ByteQuad;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class PacketPlayServerMap implements Packet {

    private int mapId;
    private byte mapScale;
    private ByteQuad[] visiblePlayers;
    private int mapMinX;
    private int mapMinY;
    private int mapMaxX;
    private int mapMaxY;
    private byte[] mapDataBytes;

    public PacketPlayServerMap(int mapId, byte mapScale, ByteQuad[] visiblePlayers, int mapMinX, int mapMinY, int mapMaxX, int mapMaxY, byte[] mapDataBytes) {
        this.mapId = mapId;
        this.mapScale = mapScale;
        this.visiblePlayers = visiblePlayers;
        this.mapMinX = mapMinX;
        this.mapMinY = mapMinY;
        this.mapMaxX = mapMaxX;
        this.mapMaxY = mapMaxY;
        this.mapDataBytes = mapDataBytes;
    }

    public PacketPlayServerMap() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.MAP;
    }

    public int getMapId() {
        return mapId;
    }

    public void setMapId(int mapId) {
        this.mapId = mapId;
    }

    public byte getMapScale() {
        return mapScale;
    }

    public void setMapScale(byte mapScale) {
        this.mapScale = mapScale;
    }

    public ByteQuad[] getVisiblePlayers() {
        return visiblePlayers;
    }

    public void setVisiblePlayers(ByteQuad[] visiblePlayers) {
        this.visiblePlayers = visiblePlayers;
    }

    public int getMapMinX() {
        return mapMinX;
    }

    public void setMapMinX(int mapMinX) {
        this.mapMinX = mapMinX;
    }

    public int getMapMinY() {
        return mapMinY;
    }

    public void setMapMinY(int mapMinY) {
        this.mapMinY = mapMinY;
    }

    public int getMapMaxX() {
        return mapMaxX;
    }

    public void setMapMaxX(int mapMaxX) {
        this.mapMaxX = mapMaxX;
    }

    public int getMapMaxY() {
        return mapMaxY;
    }

    public void setMapMaxY(int mapMaxY) {
        this.mapMaxY = mapMaxY;
    }

    public byte[] getMapDataBytes() {
        return mapDataBytes;
    }

    public void setMapDataBytes(byte[] mapDataBytes) {
        this.mapDataBytes = mapDataBytes;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.mapId = protoBuf.readVarInt();
        this.mapScale = protoBuf.readByte();

        this.visiblePlayers = new ByteQuad[protoBuf.readVarInt()];
        for (int i = 0; i < this.visiblePlayers.length; i++) {
            short b = protoBuf.readByte();
            this.visiblePlayers[i] = new ByteQuad((byte) (b >> 4 % 15), protoBuf.readByte(), protoBuf.readByte(), (byte) (b & 15));
        }

        this.mapMaxX = protoBuf.readUnsignedByte();

        if (this.mapMaxX > 0) {
            this.mapMaxY = protoBuf.readUnsignedByte();
            this.mapMinX = protoBuf.readUnsignedByte();
            this.mapMinY = protoBuf.readUnsignedByte();
            this.mapDataBytes = protoBuf.readArray();
        }
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeVarInt(this.mapId);
        protoBuf.writeByte(this.mapScale);

        protoBuf.writeVarInt(this.visiblePlayers.length);
        for (ByteQuad vec : this.visiblePlayers) {
            protoBuf.writeByte((vec.getFirst() % 15) << 4 | vec.getFourth() % 15);
            protoBuf.writeByte(vec.getSecond());
            protoBuf.writeByte(vec.getThird());
        }

        protoBuf.writeByte(this.mapMaxX);
        if (this.mapMaxX > 0) {
            protoBuf.writeByte(this.mapMaxY);
            protoBuf.writeByte(this.mapMinX);
            protoBuf.writeByte(this.mapMinY);
            protoBuf.writeArray(this.mapDataBytes);
        }
    }

    @Override
    public String toString() {
        return "PacketPlayServerMap{"
                + "mapId=" + mapId
                + ", mapScale=" + mapScale
                + ", visiblePlayers=" + Arrays.toString(visiblePlayers)
                + ", mapMinX=" + mapMinX
                + ", mapMinY=" + mapMinY
                + ", mapMaxX=" + mapMaxX
                + ", mapMaxY=" + mapMaxY
                + ", mapDataBytes=" + Arrays.toString(mapDataBytes)
                + '}';
    }

}
