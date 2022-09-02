package net.danh.MIR.api;

import net.danh.SkyShine;
import net.danh.MIR.MMOHelper;
import java.util.Map;
import java.util.Optional;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MIROptions implements MIRObject {
   private final boolean keepEnchants;

   private final boolean keepDisplayName;

   private final MMOItemTemplate mmo;

   public MIROptions(MMOItemTemplate mmo, boolean enchants, boolean displayName) {
      this.mmo = mmo;
      this.keepEnchants = enchants;
      this.keepDisplayName = displayName;
   }

   public ItemStack generate(RPGPlayer player, ItemStack old) {
      ItemStack stack = this.mmo.newBuilder(player).build().newBuilder().build();
      ItemMeta meta = stack.getItemMeta();
      if (this.keepDisplayName && old.getItemMeta().hasDisplayName())
         meta.setDisplayName(old.getItemMeta().getDisplayName());
      if (this.keepEnchants)
         for (Map.Entry<Enchantment, Integer> entry : old.getEnchantments().entrySet())
            meta.addEnchant(entry.getKey(), entry.getValue(), true);
      stack.setItemMeta(meta);
      return stack;
   }

   public static MIROptions parse(String s) {
      return parseOptions(s, (SkyShine.getPlugin().getGlobals()).global_keepEnchants,
              (SkyShine.getPlugin().getGlobals()).global_keepDisplayName);
   }

   public static MIROptions parse(ConfigurationSection cfg) {
      return parseOptions(cfg.getString("item"),
              cfg.contains("keepEnchantments") ? cfg.getBoolean("keepEnchantments") :
                      (SkyShine.getPlugin().getGlobals()).global_keepEnchants,
              cfg.contains("keepCustomNames") ? cfg.getBoolean("keepCustomNames") :
                      (SkyShine.getPlugin().getGlobals()).global_keepDisplayName);
   }

   private static MIROptions parseOptions(String s, boolean enchants, boolean display) {
      Optional<MMOItemTemplate> mmo = MMOHelper.parseMMOItem(s);
      return mmo.map(mmoItemTemplate -> new MIROptions(mmoItemTemplate, enchants, display)).orElse(null);
   }
}
