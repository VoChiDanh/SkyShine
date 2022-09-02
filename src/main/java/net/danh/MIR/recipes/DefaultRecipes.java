package net.danh.MIR.recipes;

import net.danh.MIR.Database;
import net.danh.SkyShine;
import io.lumine.mythic.lib.MythicLib;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DefaultRecipes extends RecipeManager {
   int recipeCount = 0;

   final List<Recipe> MIRRecipes = new ArrayList<>();

   public void loadRecipes() {
      SkyShine.getPlugin().getLogger().info("Loading furnace recipes...");
      if (this.recipeCount > 0) {
         SkyShine.error("ERROR! Recipes are already loaded!");
         SkyShine.error("Please restart your server completely.");
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
            } else {
               addRecipe(frecipe, barrier);
            }
         }
         if (MythicLib.plugin.getVersion().isStrictlyHigher(1, 13) && (recipe instanceof BlastingRecipe || recipe instanceof SmokingRecipe)) {
            Material m = recipe.getResult().getType();
            if (!Database.i().contains(m))
               continue;
            ItemStack barrier = createPlaceholderStack(m);
            if (recipe instanceof BlastingRecipe) {
               if (Database.i().isCache() && !Database.i().isWeighted(m)) {
                  addRecipe((BlastingRecipe)recipe, SkyShine.replace(null, recipe.getResult()));
                  continue;
               }
               addRecipe((BlastingRecipe)recipe, barrier);
               continue;
            }
            if (Database.i().isCache() && !Database.i().isWeighted(m)) {
               addRecipe((SmokingRecipe)recipe, SkyShine.replace(null, recipe.getResult()));
               continue;
            }
            addRecipe((SmokingRecipe)recipe, barrier);
         }
      }
      for (Recipe recipe : this.MIRRecipes)
         Bukkit.addRecipe(recipe);
   }

   private void addRecipe(FurnaceRecipe recipe, ItemStack stack) {
      SkyShine.getPlugin().getLogger().info(ChatColor.BLUE + "Added Furnace Recipe for " + recipe.getInput().getType());
      FurnaceRecipe newrecipe = new FurnaceRecipe(new NamespacedKey(SkyShine.getPlugin(), "mireplacer_recipe_" + this.recipeCount), stack, recipe.getInput().getType(), recipe.getExperience(), recipe.getCookingTime());
      this.recipeCount++;
      this.MIRRecipes.add(newrecipe);
   }

   private void addRecipe(BlastingRecipe recipe, ItemStack stack) {
      SkyShine.getPlugin().getLogger().info(ChatColor.BLUE + "Added Blast F. Recipe for " + recipe.getInput().getType());
      BlastingRecipe newrecipe = new BlastingRecipe(new NamespacedKey(SkyShine.getPlugin(), "mireplacer_recipe_" + this.recipeCount), stack, recipe.getInput().getType(), recipe.getExperience(), recipe.getCookingTime());
      this.recipeCount++;
      this.MIRRecipes.add(newrecipe);
   }

   private void addRecipe(SmokingRecipe recipe, ItemStack stack) {
      SkyShine.getPlugin().getLogger().info(ChatColor.BLUE + "Added Smoker Recipe for " + recipe.getInput().getType());
      SmokingRecipe newrecipe = new SmokingRecipe(new NamespacedKey(SkyShine.getPlugin(), "mireplacer_recipe_" + this.recipeCount), stack, recipe.getInput().getType(), recipe.getExperience(), recipe.getCookingTime());
      this.recipeCount++;
      this.MIRRecipes.add(newrecipe);
   }
}
