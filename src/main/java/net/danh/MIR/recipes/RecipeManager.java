package net.danh.MIR.recipes;

import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.NBTItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public abstract class RecipeManager {
   public abstract void loadRecipes();

   public static ItemStack createPlaceholderStack(Material m) {
      return NBTItem.get(new ItemStack(Material.BARRIER)).addTag(new ItemTag("MIREPLACER_PLACEHOLDER", m.name())).toItem();
   }
}
