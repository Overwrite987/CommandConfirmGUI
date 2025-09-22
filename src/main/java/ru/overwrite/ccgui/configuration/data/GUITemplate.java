package ru.overwrite.ccgui.configuration.data;

import it.unimi.dsi.fastutil.ints.IntList;
import org.bukkit.inventory.ItemStack;
import ru.overwrite.ccgui.utils.Action;

import java.util.List;

public record GUITemplate(
        String title,
        int size,
        List<GUIItem> items) {

    public record GUIItem(
            ItemStack itemStack,
            IntList slots,
            Action action
    ) {
    }
}
