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
package com.github.phantompowered.proxy.protocol.play.server.message;

import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.network.Packet;
import com.github.phantompowered.proxy.api.network.wrapper.ProtoBuf;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;

public class PacketPlayServerTitle implements Packet {

    private Action action;
    private String text;
    private int fadeIn;
    private int stay;
    private int fadeOut;

    private PacketPlayServerTitle(Action action, String text, int fadeIn, int stay, int fadeOut) {
        this.action = action;
        this.text = text;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
    }

    public PacketPlayServerTitle() {
    }

    public static PacketPlayServerTitle title(Component title) {
        return new PacketPlayServerTitle(Action.TITLE, GsonComponentSerializer.gson().serialize(title), 0, 0, 0);
    }

    public static PacketPlayServerTitle subTitle(Component subTitle) {
        return new PacketPlayServerTitle(Action.SUBTITLE, GsonComponentSerializer.gson().serialize(subTitle), 0, 0, 0);
    }

    public static PacketPlayServerTitle reset() {
        return new PacketPlayServerTitle(Action.RESET, null, 0, 0, 0);
    }

    public static PacketPlayServerTitle clear() {
        return new PacketPlayServerTitle(Action.CLEAR, null, 0, 0, 0);
    }

    public static PacketPlayServerTitle times(int fadeIn, int stay, int fadeOut) {
        return new PacketPlayServerTitle(Action.TIMES, null, fadeIn, stay, fadeOut);
    }

    @Override
    public int getId() {
        return ProtocolIds.ToClient.Play.TITLE;
    }

    public Action getAction() {
        return this.action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getFadeIn() {
        return this.fadeIn;
    }

    public void setFadeIn(int fadeIn) {
        this.fadeIn = fadeIn;
    }

    public int getStay() {
        return this.stay;
    }

    public void setStay(int stay) {
        this.stay = stay;
    }

    public int getFadeOut() {
        return this.fadeOut;
    }

    public void setFadeOut(int fadeOut) {
        this.fadeOut = fadeOut;
    }

    @Override
    public void read(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        int index = protoBuf.readVarInt();
        if (index >= 2) {
            index++;
        }

        this.action = Action.values()[index];
        switch (action) {
            case TITLE:
            case SUBTITLE:
            case PLACEHOLDER:
                this.text = protoBuf.readString();
                break;

            case TIMES:
                this.fadeIn = protoBuf.readInt();
                this.stay = protoBuf.readInt();
                this.fadeOut = protoBuf.readInt();
                break;

            default:
                break;
        }
    }

    @Override
    public void write(@NotNull ProtoBuf protoBuf, @NotNull ProtocolDirection direction, int protocolVersion) {
        int index = this.action.ordinal(); // TODO: somehow got an NPE here on the connect command
        if (index >= 2) {
            index--;
        }

        protoBuf.writeVarInt(index);
        switch (action) {
            case TITLE:
            case SUBTITLE:
            case PLACEHOLDER:
                protoBuf.writeString(this.text);
                break;

            case TIMES:
                protoBuf.writeInt(this.fadeIn);
                protoBuf.writeInt(this.stay);
                protoBuf.writeInt(this.fadeOut);
                break;

            default:
                break;
        }
    }

    public String toString() {
        return "PacketPlayServerTitle(action=" + this.getAction() + ", text=" + this.getText() + ", fadeIn=" + this.getFadeIn() + ", stay=" + this.getStay() + ", fadeOut=" + this.getFadeOut() + ")";
    }

    public enum Action {

        TITLE,
        SUBTITLE,
        PLACEHOLDER,
        TIMES,
        CLEAR,
        RESET
    }
}
