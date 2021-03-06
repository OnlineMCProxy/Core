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
package com.github.phantompowered.proxy.http;

import com.github.phantompowered.proxy.api.APIUtil;
import com.github.phantompowered.proxy.api.concurrent.Callback;
import com.github.phantompowered.proxy.io.IOUtils;
import com.github.phantompowered.proxy.util.LeftRightHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public final class HttpUtil {

    private HttpUtil() {
        throw new UnsupportedOperationException();
    }

    public static void get(String url, Callback<String> callback) {
        APIUtil.EXECUTOR_SERVICE.execute(() -> {
            LeftRightHolder<String, IOException> result = getSync(url);
            callback.done(result.getLeft(), result.getRight());
        });
    }

    public static @NotNull LeftRightHolder<String, IOException> getSync(@NotNull String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoOutput(false);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11"
            );
            connection.connect();

            return LeftRightHolder.left(readResponse(connection));
        } catch (IOException exception) {
            return LeftRightHolder.right(exception);
        }
    }

    public static void post(String url, Consumer<OutputStream> consumer, Callback<String> callback) {
        APIUtil.EXECUTOR_SERVICE.execute(() -> {
            LeftRightHolder<String, IOException> result = postSync(url, consumer);
            callback.done(result.getLeft(), result.getRight());
        });
    }

    public static @NotNull LeftRightHolder<String, IOException> postSync(@NotNull String url, Consumer<OutputStream> consumer) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11"
            );
            connection.setRequestMethod("POST");
            connection.connect();

            try (OutputStream outputStream = connection.getOutputStream()) {
                consumer.accept(outputStream);
            }

            return LeftRightHolder.left(readResponse(connection));
        } catch (IOException exception) {
            return LeftRightHolder.right(exception);
        }
    }

    private static String readResponse(HttpURLConnection connection) throws IOException {
        try (InputStream inputStream = getCorrectInputStream(connection)) {
            if (inputStream == null) {
                connection.disconnect();
                return null;
            }

            return new String(IOUtils.readAllBytes(inputStream), StandardCharsets.UTF_8);
        } finally {
            connection.disconnect();
        }
    }

    private static @Nullable InputStream getCorrectInputStream(@NotNull HttpURLConnection connection) throws IOException {
        return connection.getResponseCode() >= 200 || connection.getResponseCode() < 300 ? connection.getInputStream() : connection.getErrorStream();
    }
}
