package net.danh.MIR;

import java.util.Optional;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.danh.SkyShine;
import org.bukkit.ChatColor;

public class MMOHelper {
    public static Optional<MMOItemTemplate> parseMMOItem(String toParse) {
        String[] split = toParse.split("\\.");
        if (split.length != 2)
            return parseError("Couldn't split string... Is the config formatted correct? (" + toParse + ")");
        if (!Type.isValid(split[0]))
            return parseError("Couldn't find the type specified... Does it exist? (" + split[0] + ")");
        Type type = Type.get(split[0]);
        if (!MMOItems.plugin.getTemplates().hasTemplate(type, split[1]))
            return parseError("Couldn't find the MMOItem... Does it exist? (" + split[0] + " | " + split[1] + ")");
        MMOItemTemplate mmo = MMOItems.plugin.getTemplates().getTemplate(type, split[1]);
        if (mmo == null)
            SkyShine.error("Couldn't find the MMOItem... Does it exist? (" + split[0] + " | " + split[1] + ")");
        return Optional.ofNullable(mmo);
    }

    private static Optional<MMOItemTemplate> parseError(String message) {
        SkyShine.error(message);
        return Optional.empty();
    }

    public static String getNameFromMMOItem(MMOItemTemplate template) {
        MMOItem mmo = template.newBuilder(1, null).build();
        ItemStackBuilder builder = mmo.newBuilder();
        return mmo.hasData(ItemStats.NAME) ? mmo.getData(ItemStats.NAME).toString() : (
                builder.getMeta().hasDisplayName() ? builder.getMeta().getDisplayName() : (ChatColor.RED + "NameError"));
    }
}
