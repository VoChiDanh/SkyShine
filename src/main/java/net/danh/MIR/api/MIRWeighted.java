package net.danh.MIR.api;

import net.danh.SkyShine;
import net.danh.MIR.MMOHelper;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.weight.WeightedContainer;
import io.lumine.mythic.lib.api.weight.WeightedObject;
import io.lumine.mythic.lib.exception.EmptyWeightListException;
import io.lumine.mythic.lib.exception.InvalidWeightException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MIRWeighted implements MIRObject {
   private final WeightedContainer<MMOItemTemplate> choices;

   public MIRWeighted(WeightedContainer<MMOItemTemplate> choices) {
      this.choices = choices;
   }

   public ItemStack generate(RPGPlayer player, ItemStack old) {
      try {
         return this.choices.select().newBuilder(player).build().newBuilder().build();
      } catch (EmptyWeightListException e) {
         return old;
      }
   }

   public ItemStack generatePreview(Material mat) {
      ItemStack preview = new ItemStack(Material.ITEM_FRAME);
      ItemMeta meta = preview.getItemMeta();
      meta.setDisplayName((SkyShine.getPlugin().getGlobals()).previewDisplayName);
      List<String> lore = new ArrayList<>();
      for (WeightedObject<MMOItemTemplate> choice : this.choices)
         lore.add(ChatColor.translateAlternateColorCodes('&', (SkyShine.getPlugin().getGlobals()).previewPreLore
                 .replace("{itemname}", MMOHelper.getNameFromMMOItem(choice.getObject()))));
      meta.setLore(lore);
      preview.setItemMeta(meta);
      NBTItem nbt = NBTItem.get(preview);
      nbt.addTag(new ItemTag("MIReplacer_OldMat", mat.name()));
      return nbt.toItem();
   }

   public static MIRWeighted parse(ConfigurationSection items) throws EmptyWeightListException {
      WeightedContainer<MMOItemTemplate> weights = new WeightedContainer<>();
      for (String keys : items.getKeys(false)) {
         if (!items.isConfigurationSection(keys))
            break;
         ConfigurationSection cfg = items.getConfigurationSection(keys);
         if (!Objects.requireNonNull(cfg).contains("item") || !cfg.contains("weight"))
            break;
         Optional<MMOItemTemplate> mmo = MMOHelper.parseMMOItem(Objects.requireNonNull(cfg.getString("item")));
         if (!mmo.isPresent())
            continue;
         try {
            weights.add(cfg.getInt("weight"), mmo.get());
         } catch (InvalidWeightException e) {
            SkyShine.error("Invalid weight! Weights must be between 1 and 2147483647");
         }
      }
      if (!weights.isValid())
         throw new EmptyWeightListException();
      return new MIRWeighted(weights);
   }
}
