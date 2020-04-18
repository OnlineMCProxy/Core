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
package com.github.derrop.proxy.api.event;

import org.jetbrains.annotations.NotNull;

public interface EventManager {

    /**
     * Calls an event
     *
     * @param event The class of the event which get instantiated and then called
     * @see #callEvent(Event)
     */
    void callEvent(Class<? extends Event> event);

    /**
     * Calls an event
     *
     * @param event The event which should be called
     * @return The same event after the call
     */
    @NotNull <T extends Event> T callEvent(T event);

    /**
     * Calls an event async
     *
     * @param event The class of the event which get instantiated and then called
     * @see #callEventAsync(Event)
     */
    void callEventAsync(Class<? extends Event> event);

    /**
     * Calls an event async
     *
     * @param event The event which should be called
     */
    void callEventAsync(Event event);

    /**
     * Registers a event listener
     *
     * @param listener The listener which should get registered
     */
    void registerListener(Object listener);

    /**
     * Registers a listener
     *
     * @param listener The listener class which will get instantiated and then registered
     * @see #registerListener(Object)
     */
    void registerListener(Class<?> listener);

    /**
     * Registers a listener async
     *
     * @param listener The listener which should get registered
     */
    void registerListenerAsync(Object listener);

    /**
     * Registers a listener async
     *
     * @param listener The listener class which will get instantiated and then registered
     * @see #registerListenerAsync(Object)
     */
    void registerListenerAsync(Class<?> listener);

    /**
     * Unregisters a specific listener
     *
     * @param listener The listener which should get unregistered
     */
    void unregisterListener(Object listener);

    /**
     * Unregisters all listeners
     */
    void unregisterAll();
}
