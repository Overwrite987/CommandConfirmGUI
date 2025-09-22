package ru.overwrite.ccgui;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ru.overwrite.ccgui.configuration.Config;
import ru.overwrite.ccgui.utils.Utils;

@Getter
public final class CommandConfirmGUI extends JavaPlugin {

    private final Config pluginConfig = new Config();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        setPluginConfig(getConfig());
        getServer().getPluginManager().registerEvents(new CCGUIListener(this), this);
        getCommand("commandconfirmgui").setExecutor(new CCGUICommand(this));
    }

    public void setPluginConfig(FileConfiguration config) {
        ConfigurationSection mainSettings = config.getConfigurationSection("main_settings");
        Utils.setupColorizer(mainSettings);
        pluginConfig.setupMainSettings(mainSettings);
        pluginConfig.setupMessages(config.getConfigurationSection("messages"));
        pluginConfig.setupGUITemplate(config.getConfigurationSection("gui_settings"));
    }
}
