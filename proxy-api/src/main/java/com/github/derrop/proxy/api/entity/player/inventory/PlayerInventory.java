package com.github.derrop.proxy.api.entity.player.inventory;

import com.github.derrop.proxy.api.entity.player.Player;
import com.github.derrop.proxy.api.util.ItemStack;
import net.kyori.text.Component;
import org.jetbrains.annotations.NotNull;

public interface PlayerInventory {

    void open();

    void close();

    Player getPlayer();

    Component getTitle();

    byte getWindowId();

    void setWindowId(byte windowId);

    void setTitle(Component component);

    InventoryType getType();

    void setType(InventoryType type);

    void setContent(@NotNull ItemStack[] items);

    @NotNull
    ItemStack[] getContent();

    void setItem(int slot, ItemStack item);

    @NotNull
    ItemStack getItem(int slot);

}
