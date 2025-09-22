package ru.overwrite.ccgui.configuration;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.overwrite.ccgui.configuration.data.GUITemplate;
import ru.overwrite.ccgui.configuration.data.MainSettings;
import ru.overwrite.ccgui.configuration.data.Messages;
import ru.overwrite.ccgui.utils.Action;
import ru.overwrite.ccgui.utils.Utils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
public class Config {

    private MainSettings mainSettings;

    private Messages messages;

    private GUITemplate GUITemplate;

    public void setupMainSettings(ConfigurationSection mainSettings) {

        boolean exactEquality = mainSettings.getBoolean("exact_equality", true);
        Set<String> commands = new LinkedHashSet<>(mainSettings.getStringList("commands"));

        this.mainSettings = new MainSettings(exactEquality, commands);
    }

    public void setupMessages(ConfigurationSection messagesSection) {

        String help = Utils.COLORIZER.colorize(messagesSection.getString("help"));
        String noPerms = Utils.COLORIZER.colorize(messagesSection.getString("no_perms"));
        String reloaded = Utils.COLORIZER.colorize(messagesSection.getString("reloaded"));
        String addCommand = Utils.COLORIZER.colorize(messagesSection.getString("add_command"));
        String removeCommand = Utils.COLORIZER.colorize(messagesSection.getString("remove_command"));
        String addOrRemoveHelp = Utils.COLORIZER.colorize(messagesSection.getString("add_or_remove_help"));
        String cancelled = Utils.COLORIZER.colorize(messagesSection.getString("cancelled"));

        messages = new Messages(help, noPerms, reloaded, addCommand, removeCommand, addOrRemoveHelp, cancelled);
    }

    public void setupGUITemplate(ConfigurationSection guiSettings) {

        String title = Utils.COLORIZER.colorize(guiSettings.getString("menu_title"));
        int rawSize = guiSettings.getInt("size", 36);

        int size = Math.min(((rawSize + 8) / 9) * 9, 54);

        List<GUITemplate.GUIItem> guiItems = new ArrayList<>(size);

        ConfigurationSection itemsSection = guiSettings.getConfigurationSection("items");

        for (String key : itemsSection.getKeys(false)) {
            ConfigurationSection itemSection = itemsSection.getConfigurationSection(key);
            String displayName = Utils.COLORIZER.colorize(itemSection.getString("display_name", ""));
            Material material = Material.matchMaterial(itemSection.getString("material", "STONE"));
            if (material == null) {
                continue;
            }
            Action action = Action.valueOf(itemSection.getString("action", "NAN").toUpperCase());
            ItemStack itemStack = new ItemStack(material);
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName(displayName);
            itemStack.setItemMeta(meta);
            List<String> slotList = itemSection.getStringList("slots");
            IntList slots = parseSlots(slotList, size);
            guiItems.add(new GUITemplate.GUIItem(itemStack, slots, action));
        }

        GUITemplate = new GUITemplate(title, size, guiItems);
    }

    private IntList parseSlots(List<String> slotList, int size) {
        IntList slots = new IntArrayList();
        for (String slot : slotList) {
            int dashIndex = slot.indexOf('-');
            if (dashIndex != -1) {
                int start = Integer.parseInt(slot.substring(0, dashIndex));
                int end = Integer.parseInt(slot.substring(dashIndex + 1));
                for (int i = start; i <= end; i++) {
                    if (i < 0 || i >= size) {
                        continue;
                    }
                    slots.add(i);
                }
            } else {
                int slotIndex = Integer.parseInt(slot);
                if (slotIndex < 0 || slotIndex >= size) {
                    continue;
                }
                slots.add(slotIndex);
            }
        }
        return slots;
    }
}
