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
package com.github.phantompowered.proxy.connection.login;

import com.github.phantompowered.proxy.api.connection.ProtocolDirection;
import com.github.phantompowered.proxy.api.connection.ProtocolState;
import com.github.phantompowered.proxy.api.event.EventPriority;
import com.github.phantompowered.proxy.api.network.PacketHandler;
import com.github.phantompowered.proxy.api.network.channel.NetworkChannel;
import com.github.phantompowered.proxy.api.network.exception.CancelProceedException;
import com.github.phantompowered.proxy.connection.ConnectedProxyClient;
import com.github.phantompowered.proxy.connection.handler.ServerChannelListener;
import com.github.phantompowered.proxy.network.NetworkUtils;
import com.github.phantompowered.proxy.network.pipeline.cipher.PacketCipherDecoder;
import com.github.phantompowered.proxy.network.pipeline.cipher.PacketCipherEncoder;
import com.github.phantompowered.proxy.network.pipeline.encryption.ClientEncryptionUtils;
import com.github.phantompowered.proxy.network.pipeline.handler.HandlerEndpoint;
import com.github.phantompowered.proxy.protocol.ProtocolIds;
import com.github.phantompowered.proxy.protocol.login.client.PacketLoginInEncryptionRequest;
import com.github.phantompowered.proxy.protocol.login.server.PacketLoginOutEncryptionResponse;
import com.github.phantompowered.proxy.protocol.login.server.PacketLoginOutLoginSuccess;
import com.github.phantompowered.proxy.protocol.login.server.PacketLoginOutSetCompression;
import com.github.phantompowered.proxy.protocol.play.server.message.PacketPlayServerKickPlayer;
import com.mojang.authlib.exceptions.AuthenticationException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.security.PublicKey;

public class ProxyClientLoginHandler {

    @PacketHandler(packetIds = {ProtocolIds.ToClient.Play.KICK_DISCONNECT}, directions = ProtocolDirection.TO_CLIENT, protocolState = ProtocolState.PLAY)
    private void handle(ConnectedProxyClient proxyClient, PacketPlayServerKickPlayer kick) throws Exception {
        Component reason = GsonComponentSerializer.gson().deserialize(kick.getMessage());

        proxyClient.setLastKickReason(reason);
        proxyClient.connectionFailed();
    }

    @PacketHandler(packetIds = {ProtocolIds.ToClient.Login.ENCRYPTION_BEGIN}, directions = ProtocolDirection.TO_CLIENT, protocolState = ProtocolState.LOGIN)
    private void handle(ConnectedProxyClient proxyClient, PacketLoginInEncryptionRequest request) throws Exception {
        if (proxyClient.getCredentials().isOffline()) {
            proxyClient.close();
            throw new IllegalStateException("Joined with an offline account on an online mode server");
        }

        final SecretKey secretKey = ClientEncryptionUtils.createNewSharedKey();
        String s = request.getServerId();
        PublicKey publicKey = ClientEncryptionUtils.decodePublicKey(request.getPublicKey());
        String s1 = (new BigInteger(ClientEncryptionUtils.getServerIdHash(s, publicKey, secretKey))).toString(16);

        try {
            proxyClient.getSessionService().joinServer(proxyClient.getAuthentication().getSelectedProfile(), proxyClient.getAuthentication().getAuthenticatedToken(), s1);
        } catch (AuthenticationException exception) {
            throw new Error("Failed to join server on auth servers!", exception);
        }

        byte[] secretKeyEncrypted = ClientEncryptionUtils.cipherOperation(publicKey, secretKey.getEncoded());
        byte[] verifyTokenEncrypted = ClientEncryptionUtils.cipherOperation(publicKey, request.getVerifyToken());
        proxyClient.write(new PacketLoginOutEncryptionResponse(secretKeyEncrypted, verifyTokenEncrypted));

        proxyClient.getWrappedChannel().pipeline().addBefore(
                NetworkUtils.LENGTH_DECODER,
                NetworkUtils.DECRYPT,
                new PacketCipherDecoder(secretKey)
        );
        proxyClient.getWrappedChannel().pipeline().addBefore(
                NetworkUtils.LENGTH_ENCODER,
                NetworkUtils.ENCRYPT,
                new PacketCipherEncoder(secretKey)
        );
    }

    @PacketHandler(packetIds = {ProtocolIds.ToClient.Login.SUCCESS}, directions = ProtocolDirection.TO_CLIENT, protocolState = ProtocolState.LOGIN, priority = EventPriority.LAST)
    private void handle(ConnectedProxyClient client, PacketLoginOutLoginSuccess loginSuccess) throws Exception {
        client.setProtocolState(ProtocolState.PLAY);
        client.getWrappedChannel().pipeline().get(HandlerEndpoint.class).setChannelListener(new ServerChannelListener(client));
        throw CancelProceedException.INSTANCE; // without this, the LoginSuccess would be recorded by ConnectedProxyClient#redirectPacket
    }

    @PacketHandler(packetIds = {ProtocolIds.ToClient.Login.SET_COMPRESSION}, directions = ProtocolDirection.TO_CLIENT, protocolState = ProtocolState.LOGIN)
    private void handle(NetworkChannel channel, PacketLoginOutSetCompression setCompression) {
        channel.setCompression(setCompression.getThreshold());
    }
}
