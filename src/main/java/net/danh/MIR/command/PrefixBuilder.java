package net.danh.MIR.command;

import net.md_5.bungee.api.ChatColor;

public class PrefixBuilder {
   private final String prefix;

   private ChatColor pColor = ChatColor.LIGHT_PURPLE;

   private ChatColor bColor = ChatColor.DARK_PURPLE;

   private ChatColor mColor = ChatColor.BLUE;

   public PrefixBuilder(String prefix) {
      this.prefix = prefix;
   }

   public PrefixBuilder setPrefixColor(ChatColor color) {
      this.pColor = color;
      return this;
   }

   public PrefixBuilder setBracketColor(ChatColor color) {
      this.bColor = color;
      return this;
   }

   public PrefixBuilder setMessageColor(ChatColor color) {
      this.mColor = color;
      return this;
   }

   public Prefix build() {
      return new Prefix(this.prefix, this.pColor, this.bColor, this.mColor);
   }
}
