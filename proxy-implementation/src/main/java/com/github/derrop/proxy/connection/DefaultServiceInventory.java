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
package com.github.derrop.proxy.connection;

import com.github.derrop.proxy.api.connection.ServiceInventory;
import com.github.derrop.proxy.api.item.ItemStack;
import com.github.derrop.proxy.connection.cache.handler.HeldItemSlotCache;
import com.github.derrop.proxy.connection.cache.handler.PlayerInventoryCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class DefaultServiceInventory implements ServiceInventory {

    private final BasicServiceConnection connection;

    public DefaultServiceInventory(BasicServiceConnection connection) {
        this.connection = connection;
    }

    private PlayerInventoryCache cache() {
        return (PlayerInventoryCache) this.connection.getClient().getPacketCache().getHandler(handler -> handler instanceof PlayerInventoryCache);
    }

    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        return this.cache().getItemsBySlot();
    }

    @Override
    public @Nullable ItemStack getItemInHand() {
        HeldItemSlotCache cache = (HeldItemSlotCache) this.connection.getClient().getPacketCache().getHandler(handler -> handler instanceof HeldItemSlotCache);
        return this.getItem(cache.getSlot());
    }

    @Override
    public @Nullable ItemStack getItem(int slot) {
        return this.getContent().get(slot);
    }
}