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
package com.github.derrop.proxy.paste;

import com.github.derrop.proxy.api.paste.PasteServerUploadResult;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/* package */ class DefaultPasteServerUploadResult implements PasteServerUploadResult {

    protected DefaultPasteServerUploadResult(JsonObject result, String url, String content) {
        this.wasSuccessful = !result.has("message");
        this.content = content;

        if (this.wasSuccessful) {
            // key and deleteSecret
            this.url = (url.endsWith("/") ? url : url + "/") + result.get("key").getAsString();
            this.deleteSecret = result.has("deleteSecret") ? result.get("deleteSecret").getAsString() : null;
        } else {
            this.url = null;
            this.deleteSecret = null;
        }
    }

    private final boolean wasSuccessful;
    private final String url;
    private final String deleteSecret;
    private final String content;

    @Override
    public boolean wasSuccessful() {
        return this.wasSuccessful;
    }

    @Override
    public @NotNull Optional<String> getPasteUrl() {
        return Optional.ofNullable(this.url);
    }

    @Override
    public @NotNull Optional<String> getDeleteSecret() {
        return Optional.ofNullable(this.deleteSecret);
    }

    @Override
    public @NotNull String getContent() {
        return this.content;
    }
}