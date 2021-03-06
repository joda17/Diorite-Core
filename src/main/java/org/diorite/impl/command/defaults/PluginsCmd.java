package org.diorite.impl.command.defaults;

import java.util.Collection;
import java.util.StringJoiner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.diorite.impl.command.SystemCommandImpl;
import org.diorite.impl.plugin.FakePluginLoader;
import org.diorite.Diorite;
import org.diorite.chat.ChatColor;
import org.diorite.command.CommandPriority;
import org.diorite.plugin.BasePlugin;
import org.diorite.plugin.DioritePlugin;
import org.diorite.plugin.FakeDioritePlugin;

public class PluginsCmd extends SystemCommandImpl
{
    public PluginsCmd()
    {
        super("plugins", Pattern.compile("(pl(ugin(s|)|))", Pattern.CASE_INSENSITIVE), CommandPriority.LOW);
        this.setCommandExecutor((sender, command, label, matchedPattern, args) -> {
            // TODO check permissions
            if (args.length() == 0)
            {
                final Collection<BasePlugin> plugins = Diorite.getServer().getPluginManager().getPlugins();
                String msg = "&7Plugins (&3" + plugins.size() + "&7): ";

                msg += plugins.stream().map(pl -> pl.isEnabled() ? (ChatColor.GREEN + pl.getName()) : (ChatColor.RED + pl.getName())).collect(Collectors.joining("&7, "));

                sender.sendSimpleColoredMessage(msg);
            }
            else if (args.length() == 1)
            {
                final BasePlugin pl = Diorite.getPluginManager().getPlugin(args.asString(0));
                if (pl == null)
                {
                    sender.sendMessage(ChatColor.RED + "Plugin not found");
                    return;
                }

                sender.sendSimpleColoredMessage("&7Plugin overview:");
                sender.sendSimpleColoredMessage("  &7Name: &3" + pl.getName());
                sender.sendSimpleColoredMessage("  &7Author: &3" + pl.getAuthor());
                sender.sendSimpleColoredMessage("  &7Version: &3" + pl.getVersion());
                if (pl.getDescription() != null)
                {
                    sender.sendSimpleColoredMessage("  &7Description: &3" + pl.getDescription());
                }
                if (pl.getWebsite() != null)
                {
                    sender.sendSimpleColoredMessage("  &7Support website: &3" + pl.getWebsite());
                }
                sender.sendSimpleColoredMessage("&7Stats for nerds:");
                if (pl.getPluginLoader().getFileExtension().equals(FakePluginLoader.FAKE_PLUGIN_EXTENSION))
                {
                    sender.sendSimpleColoredMessage("  &7Parent plugin: &3" + ((FakeDioritePlugin)pl).getParent().getName());
                }
                else
                {
                    sender.sendSimpleColoredMessage("  &7Loaded classes: &3" + ((DioritePlugin)pl).getClassLoader().loadedClasses());
                }
                sender.sendSimpleColoredMessage("  &7Plugin loader: &3" + pl.getPluginLoader().getClass().getSimpleName());
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "Usage: /plugins [plugin]");
            }
        });
    }
}
