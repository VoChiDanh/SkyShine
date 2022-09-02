package net.danh.MIR;

import net.danh.MIR.api.MIRObject;
import net.danh.MIR.api.MIRWeighted;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.NBTItem;
import java.util.List;
import java.util.Objects;

import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import net.danh.SkyShine;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class NewFangledEventListener implements Listener {
   @EventHandler
   public void prepareSmith(PrepareSmithingEvent e) {
      ItemStack stack;
      if (!(SkyShine.getPlugin().getGlobals()).convertOnSmithing || e.getResult() == null || worldCheck(e
              .getView().getPlayer().getWorld().getName()))
         return;
      if (!SkyShine.isValid(e.getResult()))
         return;
      MIRObject mir = Database.i().get(e.getResult()).orElse(null);
      if (mir instanceof MIRWeighted) {
         stack = ((MIRWeighted)mir).generatePreview(e.getResult().getType());
      } else {
         RPGPlayer rpg = PlayerData.get(e.getView().getPlayer().getUniqueId()).getRPG();
         stack = SkyShine.replace(rpg, e.getResult());
         ItemMeta meta = stack.getItemMeta();
         List<String> lore = SkyShine.getPlugin().getGlobals().getConstructedLore();
         if (!lore.isEmpty())
            Objects.requireNonNull(meta).setLore(SkyShine.getPlugin().getGlobals().getConstructedLore());
         stack.setItemMeta(meta);
         stack = NBTItem.get(stack).addTag(new ItemTag("MIReplacer_OldMat", e.getResult().getType().name())).toItem();
      }
      e.setResult(stack);
   }

   @EventHandler
   public void actuallySmith(InventoryClickEvent e) {
      if (e.getInventory()
              .getType() != InventoryType.SMITHING || e.getSlotType() != InventoryType.SlotType.RESULT || e.isCancelled() || e.getCurrentItem() == null ||
              !(SkyShine.getPlugin().getGlobals()).convertOnSmithing || worldCheck(e.getWhoClicked().getWorld().getName()))
         return;
      if (e.getAction() == InventoryAction.NOTHING || e.getAction() == InventoryAction.CLONE_STACK) {
         e.setCancelled(true);
         return;
      }
      NBTItem nbt = NBTItem.get(e.getCurrentItem());
      if (nbt.hasTag("MIReplacer_OldMat")) {
         if (e.isShiftClick()) {
            e.setCancelled(true);
            return;
         }
         ItemStack stack = new ItemStack(Objects.requireNonNull(Material.getMaterial(nbt.getString("MIReplacer_OldMat").toUpperCase())));
         stack.setAmount(e.getCurrentItem().getAmount());
         RPGPlayer rpg = PlayerData.get(e.getView().getPlayer().getUniqueId()).getRPG();
         e.setCurrentItem(SkyShine.replace(rpg, stack));
      }
   }

   private boolean worldCheck(String worldName) {
      return !SkyShine.getPlugin().getGlobals().checkWorld(worldName);
   }
}
