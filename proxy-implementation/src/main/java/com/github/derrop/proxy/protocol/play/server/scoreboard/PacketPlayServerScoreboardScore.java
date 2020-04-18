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
package com.github.derrop.proxy.protocol.play.server.scoreboard;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.protocol.ProtocolIds;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerScoreboardScore implements Packet {

    private String itemName;
    private byte action;
    private String objectiveName;
    private int value;

    public PacketPlayServerScoreboardScore(String itemName, String objectiveName) {
        this(itemName, (byte) 1, objectiveName, -1);
    }

    public PacketPlayServerScoreboardScore(String itemName, byte action, String objectiveName, int value) {
        this.itemName = itemName;
        this.action = action;
        this.objectiveName = objectiveName;
        this.value = value;
    }

    public PacketPlayServerScoreboardScore() {
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.SCOREBOARD_SCORE;
    }

    public String getItemName() {
        return this.itemName;
    }

    public byte getAction() {
        return this.action;
    }

    public String getObjectiveName() {
        return this.objectiveName;
    }

    public int getValue() {
        return this.value;
    }

    public void setAction(byte action) {
        this.action = action;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        this.itemName = protoBuf.readString();
        this.action = protoBuf.readByte();
        this.objectiveName = protoBuf.readString();

        if (action != 1) {
            this.value = protoBuf.readVarInt();
        }
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        protoBuf.writeString(this.itemName);
        protoBuf.writeByte(this.action);
        protoBuf.writeString(this.objectiveName);

        if (action != 1) {
            protoBuf.writeVarInt(this.value);
        }
    }

    public String toString() {
        return "PacketPlayServerScoreboardScore(itemName=" + this.getItemName() + ", action=" + this.getAction() + ", objectiveName=" + this.getObjectiveName() + ", value=" + this.getValue() + ")";
    }
}
