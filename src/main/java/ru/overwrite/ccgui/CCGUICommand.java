package ru.overwrite.ccgui;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import ru.overwrite.ccgui.configuration.Config;
import ru.overwrite.ccgui.configuration.data.Messages;

import java.util.ArrayList;
import java.util.List;

public class CCGUICommand implements TabExecutor {

    private final Config pluginConfig;
    private final CommandConfirmGUI plugin;

    public CCGUICommand(CommandConfirmGUI plugin) {
        this.plugin = plugin;
        this.pluginConfig = plugin.getPluginConfig();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        Messages messages = pluginConfig.getMessages();

        if (!sender.hasPermission("ccgui.admin")) {
            sender.sendMessage(messages.noPerms());
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sender.sendMessage(messages.help());
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> {
                plugin.reloadConfig();
                plugin.setPluginConfig(plugin.getConfig());
                sender.sendMessage(messages.reloaded());
            }

            case "add" -> {
                if (args.length < 2) {
                    sender.sendMessage(messages.addOrRemoveHelp());
                    return true;
                }
                String command = args[1].startsWith("/") ? args[1] : "/" + args[1];
                List<String> commands = new ArrayList<>(pluginConfig.getMainSettings().commands());
                commands.add(command);
                updateCommands(commands);
                sender.sendMessage(messages.addCommand().replace("%command%", command));
            }

            case "remove" -> {
                if (args.length < 2) {
                    sender.sendMessage(messages.addOrRemoveHelp());
                    return true;
                }
                String command = args[1].startsWith("/") ? args[1] : "/" + args[1];
                List<String> commands = new ArrayList<>(pluginConfig.getMainSettings().commands());
                commands.remove(command);
                updateCommands(commands);
                sender.sendMessage(messages.removeCommand().replace("%command%", command));
            }

            default -> sender.sendMessage(messages.help());
        }
        return true;
    }

    private void updateCommands(List<String> commands) {
        plugin.getConfig().set("main_settings.commands", commands);
        plugin.saveConfig();
        plugin.reloadConfig();
        plugin.setPluginConfig(plugin.getConfig());
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, String[] args) {
        if (!sender.hasPermission("ccgui.admin")) {
            return List.of();
        }
        if (args.length == 1) {
            return getResult(args, List.of("help", "reload", "add", "remove"));
        }
        if (args.length == 2) {
            return switch (args[0].toLowerCase()) {
                case "remove" -> getResult(args, List.copyOf(pluginConfig.getMainSettings().commands()));
                case "add" -> getResult(args, List.copyOf(Bukkit.getCommandMap().getKnownCommands().keySet()));
                default -> List.of();
            };
        }
        return List.of();
    }

    private List<String> getResult(String[] args, List<String> completions) {
        if (completions.isEmpty()) {
            return completions;
        }
        final List<String> result = new ArrayList<>();
        for (String c : completions) {
            if (StringUtil.startsWithIgnoreCase(c, args[args.length - 1])) {
                result.add(c);
            }
        }
        return result;
    }
}
