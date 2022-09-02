package net.danh.MIR.command;

import net.md_5.bungee.api.ChatColor;

public class Prefix {
   private final String prefix;

   private final ChatColor pColor;

   private final ChatColor bColor;

   private final ChatColor mColor;

   public Prefix(String p, ChatColor pC, ChatColor bC, ChatColor mC) {
      this.prefix = p;
      this.pColor = pC;
      this.bColor = bC;
      this.mColor = mC;
   }

   public String get() {
      return this.bColor + "[" + this.pColor + this.prefix + this.bColor + "] " + this.mColor;
   }
}
