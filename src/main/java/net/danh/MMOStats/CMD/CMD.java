package net.danh.MMOStats.CMD;

import net.danh.MMOStats.Resource.Files;
import net.danh.SkyShine;
import net.danh.dcore.Commands.CMDBase;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CMD extends CMDBase {

    public CMD() {
        super(SkyShine.getPlugin(), "mmostats");
    }

    @Override
    public void playerexecute(Player p, String[] args) {
        if (p.hasPermission("mmostats.admin")) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    Files.getConfig().save();
                    Files.getConfig().load();
                    net.danh.dcore.Utils.Player.sendPlayerMessage(p, "&aReloaded");
                }
            }
        }
    }

    @Override
    public void consoleexecute(ConsoleCommandSender c, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                Files.getConfig().save();
                Files.getConfig().load();
                net.danh.dcore.Utils.Player.sendConsoleMessage(c, "&aReloaded");
            }
        }
    }

    @Override
    public List<String> TabComplete(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();
        if (sender.hasPermission("mmostats.admin")) {
            if (args.length == 1) {
                commands.add("reload");
                StringUtil.copyPartialMatches(args[0], commands, completions);
            }
        }
        Collections.sort(completions);
        return completions;
    }

}