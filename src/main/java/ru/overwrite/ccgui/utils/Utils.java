package ru.overwrite.ccgui.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.configuration.ConfigurationSection;
import ru.overwrite.ccgui.color.Colorizer;
import ru.overwrite.ccgui.color.impl.LegacyAdvancedColorizer;
import ru.overwrite.ccgui.color.impl.LegacyColorizer;
import ru.overwrite.ccgui.color.impl.MiniMessageColorizer;

@UtilityClass
public class Utils {

    public Colorizer COLORIZER;

    public void setupColorizer(ConfigurationSection mainSettings) {
        COLORIZER = switch (mainSettings.getString("serializer", "LEGACY").toUpperCase()) {
            case "MINIMESSAGE" -> new MiniMessageColorizer();
            case "LEGACY_ADVANCED" -> new LegacyAdvancedColorizer();
            default -> new LegacyColorizer();
        };
    }

    public final char COLOR_CHAR = '§';

    public String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
        final char[] chars = textToTranslate.toCharArray();

        for (int i = 0, length = chars.length - 1; i < length; i++) {
            if (chars[i] == altColorChar && isValidColorCharacter(chars[i + 1])) {
                chars[i++] = COLOR_CHAR;
                chars[i] |= 0x20;
            }
        }

        return new String(chars);
    }

    private boolean isValidColorCharacter(char c) {
        return switch (c) {
            case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B', 'C', 'D',
                 'E', 'F', 'r', 'R', 'k', 'K', 'l', 'L', 'm', 'M', 'n', 'N', 'o', 'O', 'x', 'X' -> true;
            default -> false;
        };
    }

    public String cutCommand(String str) {
        int index = str.indexOf(' ');
        return index == -1 ? str : str.substring(0, index);
    }
}
