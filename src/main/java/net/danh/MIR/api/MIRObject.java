package net.danh.MIR.api;

import net.danh.SkyShine;
import io.lumine.mythic.lib.exception.EmptyWeightListException;

import java.util.Objects;
import java.util.Optional;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public interface MIRObject {
   ItemStack generate(RPGPlayer paramRPGPlayer, ItemStack paramItemStack);

   static Optional<MIRObject> getObjectFrom(String key) {
      MIRObject mir;
      ConfigurationSection config = SkyShine.getPlugin().getConfig().getConfigurationSection("ConversionItems");
      if (Objects.requireNonNull(config).isConfigurationSection(key)) {
         ConfigurationSection cfg = config.getConfigurationSection(key);
         if (Objects.requireNonNull(cfg).contains("item") && cfg.isString("item")) {
            mir = MIROptions.parse(cfg);
         } else {
            try {
               mir = MIRWeighted.parse(cfg);
            } catch (EmptyWeightListException e) {
               SkyShine.error("Weighted List was empty, ignoring entry: " + key);
               mir = null;
            }
         }
      } else {
         mir = MIROptions.parse(config.getString(key));
      }
      return Optional.ofNullable(mir);
   }

   static Optional<MIRObject> getFromStack(ItemStack stack) {
      return getObjectFrom(stack.getType().name());
   }
}
