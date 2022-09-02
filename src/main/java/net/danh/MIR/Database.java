package net.danh.MIR;

import net.danh.MIR.api.MIRObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.danh.MIR.api.MIRWeighted;
import net.danh.SkyShine;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

public class Database {
   private final boolean cache;

   public boolean isCache() {
      return this.cache;
   }

   private final Map<Material, MIRObject> cachedDB = new HashMap<>();

   private final List<Material> runtimeDB = new ArrayList<>();

   public Database(FileConfiguration config) {
      this.cache = config.getBoolean("Options.cacheConfig");
      ConfigurationSection items = config.getConfigurationSection("ConversionItems");
      if (items == null) {
         SkyShine.getPlugin().getLogger().warning("[ERROR] Could not load 'ConversionItems' from config.yml");
         return;
      }
      for (String key : items.getKeys(false)) {
         Optional<Material> mat = Optional.ofNullable(Material.getMaterial(key));
         if (mat.isPresent()) {
            if (this.cache) {
               Optional<MIRObject> mir = MIRObject.getObjectFrom(key);
               mir.ifPresent(mirObject -> this.cachedDB.put(mat.get(), mirObject));
               continue;
            }
            this.runtimeDB.add(mat.get());
         }
      }
   }

   public boolean contains(Material mat) {
      return (this.runtimeDB.contains(mat) || (this.cache && this.cachedDB.containsKey(mat)));
   }

   public Optional<MIRObject> get(ItemStack stack) {
      if (!contains(stack.getType()))
         return Optional.empty();
      if (this.cache)
         return Optional.of(this.cachedDB.get(stack.getType()));
      return MIRObject.getFromStack(stack);
   }

   public void dispose() {
      this.cachedDB.clear();
      this.runtimeDB.clear();
   }

   public static Database i() {
      return (SkyShine.getPlugin()).database;
   }

   public boolean isWeighted(Material m) {
      if (this.cache && this.cachedDB.containsKey(m))
         return this.cachedDB.get(m) instanceof MIRWeighted;
      return false;
   }
}
