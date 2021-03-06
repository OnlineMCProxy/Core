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
package com.github.phantompowered.proxy.logging;

import com.github.phantompowered.proxy.api.service.ServiceRegistry;
import org.jetbrains.annotations.NotNull;
import org.jline.reader.LineReader;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class ProxyLogger extends Logger implements AutoCloseable {

    private final RecordDispatcher recordDispatcher = new RecordDispatcher(this);

    public ProxyLogger(@NotNull LineReader lineReader, @NotNull ServiceRegistry registry) {
        super("com.github.phantompowered.proxy", null);
        super.setLevel(Level.INFO);

        try {
            if (!Files.exists(Paths.get("logs"))) {
                Files.createDirectory(Paths.get("logs"));
            }

            FileHandler fileHandler = new FileHandler("logs/proxy.log", 1 << 24, 8, true);
            fileHandler.setFormatter(new LogFormatter(registry, true));
            fileHandler.setLevel(Level.ALL);
            fileHandler.setEncoding(StandardCharsets.UTF_8.name());
            super.addHandler(fileHandler);

            LogHandler logHandler = new LogHandler(lineReader);
            logHandler.setLevel(Level.ALL);
            logHandler.setFormatter(new LogFormatter(registry, false));
            logHandler.setEncoding(StandardCharsets.UTF_8.name());
            super.addHandler(logHandler);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        System.setOut(new PrintStream(new LoggingOutputStream(this, Level.INFO), true));
        System.setErr(new PrintStream(new LoggingOutputStream(this, Level.SEVERE), true));

        this.recordDispatcher.start();
    }

    @Override
    public void log(LogRecord record) {
        this.recordDispatcher.queue(record);
    }

    void doLog(@NotNull LogRecord record) {
        super.log(record);
    }

    @Override
    public void close() {
        this.recordDispatcher.interrupt();
    }
}
