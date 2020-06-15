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
package com.github.derrop.proxy.api.entity;

import com.github.derrop.proxy.api.location.Location;
import com.github.derrop.proxy.api.network.Packet;
import org.jetbrains.annotations.NotNull;

public interface Entity {

    boolean isBurning();

    boolean isSneaking();

    boolean isRiding();

    boolean isSprinting();

    boolean isBlocking();

    boolean isInvisible();

    short getAirTicks();

    boolean isCustomNameVisible();

    boolean isSilent();

    boolean hasCustomName();

    String getCustomName();

    int getType();

    @NotNull
    Location getLocation();

    void setLocation(@NotNull Location location);

    boolean isOnGround();

    int getEntityId();

    int getDimension();

    @NotNull
    Unsafe unsafe();

    @NotNull
    Callable getCallable();

    // todo: unfortunately i forgot to set this for the different entity types
    // todo: and we should add information about length, width and the head height
    double getEyeHeight();

    interface Unsafe {

        void setLocationUnchecked(@NotNull Location locationUnchecked);

    }

    interface Callable {

        void handleEntityPacket(@NotNull Packet packet);
    }
}
