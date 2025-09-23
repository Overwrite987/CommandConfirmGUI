package ru.overwrite.ccgui;

import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.InventoryHolder;
import ru.overwrite.ccgui.configuration.Config;
import ru.overwrite.ccgui.configuration.data.MainSettings;
import ru.overwrite.ccgui.utils.Action;
import ru.overwrite.ccgui.utils.CCGUIHolder;
import ru.overwrite.ccgui.utils.Utils;

import java.util.List;
import java.util.UUID;

public class CCGUIListener implements Listener {

    private final Config pluginConfig;
    private final List<UUID> confirmationCache = new ReferenceArrayList<>();

    public CCGUIListener(CommandConfirmGUI commandConfirmGUI) {
        this.pluginConfig = commandConfirmGUI.getPluginConfig();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        final String originalCommand = e.getMessage();
        String commandToCheck = originalCommand;
        MainSettings mainSettings = pluginConfig.getMainSettings();
        if (!mainSettings.exactEquality()) {
            commandToCheck = Utils.cutCommand(originalCommand);
        }
        Player p = e.getPlayer();
        if (mainSettings.commands().contains(commandToCheck)) {
            if (!confirmationCache.isEmpty() && confirmationCache.contains(p.getUniqueId())) {
                p.closeInventory();
                return;
            }
            InventoryHolder inventoryHolder = new CCGUIHolder(originalCommand, pluginConfig.getGUITemplate());
            p.openInventory(inventoryHolder.getInventory());
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) {
            return;
        }
        if (!(e.getWhoClicked() instanceof Player p)) {
            return;
        }
        InventoryHolder inventoryHolder = e.getClickedInventory().getHolder();
        if (!(inventoryHolder instanceof CCGUIHolder ccguiHolder)) {
            return;
        }
        e.setCancelled(true);
        int clickedSlot = e.getSlot();
        Action action = ccguiHolder.getActions().get(clickedSlot);
        switch (action) {
            case CONFIRM:
                confirmationCache.add(p.getUniqueId());
                p.chat(ccguiHolder.getCommand());
            case CANCEL:
                p.closeInventory();
                break;
            case NAN:
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent e) {
        if (!(e.getPlayer() instanceof Player p)) {
            return;
        }
        InventoryHolder inventoryHolder = e.getInventory().getHolder();
        if (!(inventoryHolder instanceof CCGUIHolder)) {
            return;
        }
        if (!confirmationCache.remove(p.getUniqueId())) {
            p.sendMessage(pluginConfig.getMessages().cancelled());
        }
    }
}
