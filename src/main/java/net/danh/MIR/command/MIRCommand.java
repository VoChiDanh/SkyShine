package net.danh.MIR.command;

import net.danh.SkyShine;
import net.danh.MIR.command.tcc.StringListTCComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MIRCommand extends BaseCommand {
   final String pluginPrefix = ChatColor.translateAlternateColorCodes('&', "&6[&aSkyShine&6] ");

   public void runNoArgs(CommandSender sender) {
      sender.sendMessage(this.pluginPrefix + ChatColor.GREEN + "Version " + SkyShine.getPlugin().getDescription().getVersion());
   }

   public void runArgs(CommandSender sender, String[] args) {
      if (args[0].equalsIgnoreCase("reload")) {
         SkyShine.getPlugin().reload();
         sender.sendMessage(this.pluginPrefix + ChatColor.GREEN + "Successfully reloaded.");
         return;
      }
      sender.sendMessage(this.pluginPrefix + ChatColor.RED + "Unknown command.");
   }

   public boolean hasArgs() {
      return true;
   }

   public TabCompleteContainer getTCC() {
      TabCompleteContainer container = new TabCompleteContainer();
      container.add(new StringListTCComponent("reload"));
      return container;
   }
}
