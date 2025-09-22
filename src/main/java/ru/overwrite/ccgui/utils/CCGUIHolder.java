package ru.overwrite.ccgui.utils;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import ru.overwrite.ccgui.configuration.data.GUITemplate;

@Getter
public class CCGUIHolder implements InventoryHolder {

    private final String command;
    private final Inventory inventory;
    private final Int2ObjectMap<Action> actions = new Int2ObjectOpenHashMap<>();

    public CCGUIHolder(String command, GUITemplate guiTemplate) {
        this.command = command;
        this.inventory = createInventory(guiTemplate);
    }

    private Inventory createInventory(GUITemplate guiTemplate) {
        Inventory inv = Bukkit.createInventory(this, guiTemplate.size(), guiTemplate.title());
        for (GUITemplate.GUIItem guiItem : guiTemplate.items()) {
            for (int slot : guiItem.slots()) {
                inv.setItem(slot, guiItem.itemStack());
                actions.put(slot, guiItem.action());
            }
        }
        return inv;
    }
}
