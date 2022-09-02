package net.danh.MIR.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public abstract class BaseCommand implements CommandExecutor, TabCompleter {
   private final Prefix prefix;

   public BaseCommand() {
      this(null);
   }

   public BaseCommand(Prefix prefix) {
      this.prefix = prefix;
   }

   public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
      if (args == null || args.length == 0 || !hasArgs()) {
         runNoArgs(sender);
      } else {
         runArgs(sender, args);
      }
      return true;
   }

   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      Optional<TabCompleteContainer> tcc = Optional.ofNullable(getTCC());
      if (tcc.isPresent())
         return tcc.get().generate(Math.max(0, args.length - 1), args[Math.max(0, args.length - 1)], args[Math.max(0, args.length - 2)]);
      return new ArrayList<>();
   }

   public void sendMessage(CommandSender sender, String msg) {
      if (this.prefix != null) {
         sender.sendMessage(this.prefix.get() + msg);
      } else {
         sender.sendMessage(ChatColor.GREEN + msg);
      }
   }

   public boolean hasTCC() {
      return (getTCC() != null);
   }

   public abstract void runNoArgs(CommandSender paramCommandSender);

   public abstract void runArgs(CommandSender paramCommandSender, String[] paramArrayOfString);

   public abstract boolean hasArgs();

   public abstract TabCompleteContainer getTCC();
}
