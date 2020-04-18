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
package com.github.derrop.proxy.util.io;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class IOUtils {

    private IOUtils() {
        throw new UnsupportedOperationException();
    }

    public static void createDirectories(@NotNull Path path) {
        if (Files.exists(path)) {
            if (Files.isDirectory(path)) {
                return;
            }

            try {
                Files.delete(path);
            } catch (final IOException ignored) {
            }
        }

        Path parent = path.getParent();
        if (parent != null) {
            createDirectories(parent);
        }

        try {
            Files.createDirectory(path);
        } catch (final IOException ignored) {
        }
    }

    @NotNull
    public static String replaceLast(@NotNull String in, @NotNull String regex, @NotNull String replacement) {
        return in.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
    }
}
