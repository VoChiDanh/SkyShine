package net.danh.MIR;

import net.danh.MIR.api.MIRObject;
import net.danh.MIR.api.MIRWeighted;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import net.danh.SkyShine;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class EventListener implements Listener {
    @EventHandler
    public void itemPickup(EntityPickupItemEvent e) {
        if (e.isCancelled() || !(SkyShine.getPlugin().getGlobals()).convertOnPickup ||
                !e.getEntity().getType().equals(EntityType.PLAYER) || worldCheck(e.getEntity().getWorld().getName()))
            return;
        ItemStack eitem = e.getItem().getItemStack();
        if (!SkyShine.isValid(eitem))
            return;
        e.setCancelled(true);
        RPGPlayer rpg = PlayerData.get((OfflinePlayer) e.getEntity()).getRPG();
        e.getEntity().getWorld().dropItem(e.getItem().getLocation(), SkyShine.replace(rpg, eitem)).setVelocity(new Vector(0, 0, 0));
        e.getItem().remove();
    }

    @EventHandler
    public void itemPrepareCraft(PrepareItemCraftEvent e) {
        ItemStack stack;
        if (!SkyShine.getPlugin().getGlobals().convertOnCraft || e.getInventory()
                .getResult() == null || worldCheck(e.getView().getPlayer().getWorld().getName()))
            return;
        if (!SkyShine.isValid(e.getInventory().getResult()))
            return;
        MIRObject mir = Database.i().get(e.getInventory().getResult()).orElse(null);
        if (mir instanceof MIRWeighted) {
            stack = ((MIRWeighted) mir).generatePreview(e.getInventory().getResult().getType());
        } else {
            RPGPlayer rpg = PlayerData.get(e.getView().getPlayer().getUniqueId()).getRPG();
            stack = SkyShine.replace(rpg, e.getInventory().getResult());
            ItemMeta meta = stack.getItemMeta();
            List<String> lore = SkyShine.getPlugin().getGlobals().getConstructedLore();
            if (!lore.isEmpty()) {
                assert meta != null;
                meta.setLore(SkyShine.getPlugin().getGlobals().getConstructedLore());
            }
            stack.setItemMeta(meta);
            stack = NBTItem.get(stack).addTag(new ItemTag("MIReplacer_OldMat", e.getInventory().getResult().getType().name())).toItem();
        }
        e.getInventory().setResult(stack);
    }

    @EventHandler
    public void itemCraft(CraftItemEvent e) {
        if (!(SkyShine.getPlugin().getGlobals()).convertOnCraft || e.getCurrentItem() == null || worldCheck(e.getWhoClicked().getWorld().getName()))
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

    @EventHandler
    public void itemSmelt(FurnaceSmeltEvent e) {
        if (e.isCancelled() || worldCheck(e.getBlock().getWorld().getName()))
            return;
        if (e.getResult().getType() == Material.BARRIER && (SkyShine.getPlugin().getGlobals()).convertOnSmelt) {
            List<Entity> entities = new ArrayList<>(e.getBlock().getWorld().getNearbyEntities(e.getBlock().getLocation(), 10.0D, 10.0D, 10.0D, ent -> (ent.getType() == EntityType.PLAYER)));
            RPGPlayer rpg = entities.isEmpty() ? null : PlayerData.get(entities.get(0).getUniqueId()).getRPG();
            ItemStack stack = SkyShine.replace(rpg, new ItemStack(
                    Material.valueOf(NBTItem.get(e.getResult()).getString("MIREPLACER_PLACEHOLDER"))));
            e.setResult(stack);
        }
    }

    @EventHandler
    public void inventoryMove(InventoryClickEvent e) {
        if (e.isCancelled() || e.getCurrentItem() == null || !(SkyShine.getPlugin().getGlobals()).convertOnInventory || worldCheck(e
                .getWhoClicked().getWorld().getName()))
            return;
        if (SkyShine.isValid(e.getCurrentItem())) {
            RPGPlayer rpg = PlayerData.get(e.getWhoClicked().getUniqueId()).getRPG();
            e.setCurrentItem(SkyShine.replace(rpg, e.getCurrentItem()));
        }
    }

    @EventHandler
    public void openChest(InventoryOpenEvent e) {
        if (e.isCancelled() || e.getInventory().getHolder() == null || !(SkyShine.getPlugin().getGlobals()).convertOnChest || worldCheck(e
                .getPlayer().getWorld().getName()))
            return;
        InventoryHolder holder = e.getInventory().getHolder();
        if (holder instanceof DoubleChest) {
            if ((SkyShine.getPlugin()).chestManager.isChest(((DoubleChest) holder).getLeftSide())) {
                Block block = (SkyShine.getPlugin()).chestManager.get(((DoubleChest) holder).getLeftSide());
                if ((SkyShine.getPlugin()).chestManager.has(block.getLocation()))
                    return;
                convertInventory(e.getPlayer().getUniqueId(), Objects.requireNonNull(((DoubleChest) holder).getLeftSide()).getInventory());
                (SkyShine.getPlugin()).chestManager.add(block.getLocation());
            }
            holder = ((DoubleChest) e.getInventory().getHolder()).getRightSide();
        }
        if ((SkyShine.getPlugin()).chestManager.isChest(holder)) {
            Block block = (SkyShine.getPlugin()).chestManager.get(holder);
            if ((SkyShine.getPlugin()).chestManager.has(block.getLocation()))
                return;
            convertInventory(e.getPlayer().getUniqueId(), e.getInventory());
            (SkyShine.getPlugin()).chestManager.add(block.getLocation());
        } else if (e.getInventory().getHolder() instanceof StorageMinecart) {
            StorageMinecart minecart = (StorageMinecart) e.getInventory().getHolder();
            if (minecart.hasMetadata("mireplacer_checked"))
                return;
            convertInventory(e.getPlayer().getUniqueId(), e.getInventory());
            minecart.setMetadata("mireplacer_checked", new FixedMetadataValue(SkyShine.getPlugin(), Boolean.TRUE));
        }
    }

    private void convertInventory(UUID opener, Inventory inv) {
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack != null &&
                    SkyShine.isValid(stack)) {
                RPGPlayer rpg = PlayerData.get(opener).getRPG();
                inv.setItem(i, SkyShine.replace(rpg, stack));
            }
        }
    }

    private boolean worldCheck(String worldName) {
        return !SkyShine.getPlugin().getGlobals().checkWorld(worldName);
    }
}
