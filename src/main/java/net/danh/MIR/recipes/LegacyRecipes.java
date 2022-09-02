package net.danh.MIR.recipes;

import net.danh.MIR.Database;
import net.danh.SkyShine;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.material.MaterialData;

public class LegacyRecipes extends RecipeManager {
   int recipeCount = 0;

   public void loadRecipes() {
      SkyShine.getPlugin().getLogger().info("Loading furnace recipes...");
      if (this.recipeCount > 0) {
         SkyShine.error("ERROR! Recipes are already loaded!");
         return;
      }
      Iterator<Recipe> recipes = Bukkit.recipeIterator();
      while (recipes.hasNext()) {
         Recipe recipe = recipes.next();
         if (recipe instanceof FurnaceRecipe) {
            FurnaceRecipe frecipe = (FurnaceRecipe)recipe;
            Material m = frecipe.getResult().getType();
            if (!Database.i().contains(m))
               continue;
            ItemStack barrier = createPlaceholderStack(m);
            if (Database.i().isCache() && !Database.i().isWeighted(m)) {
               addRecipe(frecipe, SkyShine.replace(null, recipe.getResult()));
               continue;
            }
            addRecipe(frecipe, barrier);
         }
      }
   }

   private void addRecipe(FurnaceRecipe recipe, ItemStack stack) {
      SkyShine.getPlugin().getLogger().info(ChatColor.BLUE + "Added Furnace Recipe for " + recipe.getInput().getType());
      FurnaceRecipe newrecipe = new FurnaceRecipe(stack, new MaterialData(recipe.getInput().getType()), recipe.getExperience());
      this.recipeCount++;
      Bukkit.addRecipe(newrecipe);
   }
}
