package net.danh;

import com.google.gson.JsonObject;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import net.danh.MIR.*;
import net.danh.MIR.api.MIRObject;
import net.danh.MIR.api.event.MIRReplaceEvent;
import net.danh.MIR.chests.ChestManager;
import net.danh.MIR.chests.DefaultChestManager;
import net.danh.MIR.chests.LegacyChestManager;
import net.danh.MIR.command.MIRCommand;
import net.danh.MIR.recipes.DefaultRecipes;
import net.danh.MIR.recipes.LegacyRecipes;
import net.danh.MMOStats.CMD.CMD;
import net.danh.MMOStats.Manager.PStats;
import net.danh.MMOStats.PlaceholderAPI.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;

public class SkyShine extends JavaPlugin {
    public static final HashMap<String, Integer> stats = new HashMap<>();
    private static SkyShine plugin;

    public static SkyShine getPlugin() {
        return plugin;
    }

    private final GlobalConfig globals = new GlobalConfig();

    public GlobalConfig getGlobals() {
        return this.globals;
    }

    private final JsonUtils json = new JsonUtils();

    public JsonUtils getJson() {
        return this.json;
    }

    private final Map<String, PluginFile> files = new HashMap<>();

    public Database database;

    public ChestManager chestManager;

    public void onEnable() {
        plugin = this;
        MIR();
        MMOStats();
        new BukkitRunnable() {
            @Override
            public void run() {
                getServer().getOnlinePlayers().forEach(PStats::updateStats);
            }
        }.runTaskTimer(this, 20L, 20L);
    }

    public void newFile(String fileName, boolean hasDefault) {
        if (this.files.containsKey(fileName))
            return;
        this.files.put(fileName, new PluginFile(fileName, hasDefault));
    }

    public Optional<File> getFile(String fileName) {
        return Optional.ofNullable(this.files.getOrDefault(fileName, new PluginFile()).getFile());
    }

    private void MIR() {
        plugin.getLogger().log(Level.INFO, "Loading MIR Features....");
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        if (MythicLib.inst().getVersion().isStrictlyHigher(1, 15)) {
            getServer().getPluginManager().registerEvents(new NewFangledEventListener(), this);
        }
        MIRCommand command = new MIRCommand();
        Objects.requireNonNull(getCommand("skyshine")).setExecutor(command);
        if (command.hasTCC()) {
            Objects.requireNonNull(getCommand("skyshine")).setTabCompleter(command);
        }
        newFile("chests.json", false);
        ChestCollection cc = new ChestCollection(getFile("chests.json").orElse(null));
        this.chestManager = MythicLib.inst().getVersion().isStrictlyHigher(1, 13) ? new DefaultChestManager(cc) : new LegacyChestManager(cc);
        this.chestManager.load();
        reload();
        (MythicLib.inst().getVersion().isStrictlyHigher(1, 12) ? new DefaultRecipes() : new LegacyRecipes()).loadRecipes();
        plugin.getLogger().log(Level.INFO, "Loaded complete features of MIR!");
    }

    private void MMOStats() {
        plugin.getLogger().log(Level.INFO, "Loading MMOStats Features....");
        net.danh.MMOStats.Resource.Files.getConfig().load();
        new CMD();
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Placeholder().register();
        }
        plugin.getLogger().log(Level.INFO, "Loaded complete features of MMOStats!");
    }

    public void reload() {
        reloadConfig();
        this.globals.global_keepEnchants = getConfig().getBoolean("Options.keepEnchantments");
        this.globals.global_keepDisplayName = getConfig().getBoolean("Options.keepCustomNames");
        this.globals.previewDisplayName = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(getConfig().getString("Options.previewItemName")));
        this.globals.previewPreLore = getConfig().getString("Options.previewItemIndex");
        this.globals.setPreviewCraftLore(getConfig().getStringList("Options.craftingLorePreview"));
        this.globals.worldList = getConfig().getStringList("Options.worldList");
        this.globals.exemptNBTTags = getConfig().getStringList("Options.exemptNBTTags");
        this.globals.loreMatcher = getConfig().getString("Options.loreMatcher", "([\\s\\S])+");
        this.globals.nameMatcher = getConfig().getString("Options.nameMatcher", "([\\s\\S])+");
        this.globals.convertOnPickup = getConfig().getBoolean("Convert-On.pickup");
        this.globals.convertOnCraft = getConfig().getBoolean("Convert-On.craft");
        this.globals.convertOnSmelt = getConfig().getBoolean("Convert-On.smelt");
        this.globals.convertOnChest = getConfig().getBoolean("Convert-On.chest");
        this.globals.convertOnInventory = getConfig().getBoolean("Convert-On.inventory-change");
        this.globals.convertOnSmithing = getConfig().getBoolean("Convert-On.smithing");
        this.globals.slimefun = getConfig().getBoolean("Options.exemptSlimefun");
        if (this.database != null)
            this.database.dispose();
        this.database = new Database(getConfig());
        this.chestManager.reload();
    }

    public void onDisable() {
        this.chestManager.save();
    }

    public static void error(String msg) {
        plugin.getLogger().warning(msg);
    }

    public static boolean isValid(ItemStack stack) {
        if (!Database.i().contains(stack.getType()))
            return false;
        return (!getPlugin().getGlobals().nbtCheck(NBTItem.get(stack)) && getPlugin().getGlobals().metaCheck(Objects.requireNonNull(stack.getItemMeta())));
    }

    public static ItemStack replace(RPGPlayer player, ItemStack stack) {
        Optional<MIRObject> mir = plugin.database.get(stack);
        if (mir.isPresent()) {
            ItemStack generated = mir.get().generate(player, stack);
            generated.setAmount(stack.getAmount());
            MIRReplaceEvent event = new MIRReplaceEvent(player, stack, generated);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled())
                return stack;
            return generated;
        }
        return stack;
    }

    protected class PluginFile {
        private final File file;

        public File getFile() {
            return this.file;
        }

        protected PluginFile(String path, boolean hasDefault) {
            this.file = new File(SkyShine.plugin.getDataFolder(), path);
            if (!this.file.exists()) {
                if (path.contains("/")) {
                    this.file.mkdirs();
                    this.file.delete();
                }
                if (hasDefault) {
                    try {
                        Files.copy(Objects.requireNonNull(SkyShine.plugin.getClass().getResourceAsStream("/default/" + path)), Paths.get(this.file.getAbsolutePath()));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                } else if (this.file.getName().contains(".json")) {
                    SkyShine.this.json.writeToFile(this.file, new JsonObject());
                }
            }
        }

        private PluginFile() {
            this.file = null;
        }
    }
}
