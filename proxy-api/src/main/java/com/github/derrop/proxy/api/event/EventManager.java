package com.github.derrop.proxy.api.event;

import org.jetbrains.annotations.NotNull;

import java.util.List;

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

    /**
     * @return All registered listeners
     */
    @NotNull
    List<List<LoadedListener>> getListeners();
}
