package net.danh.MMOStats.Resource;


import net.danh.SkyShine;

public class Files {

    public static net.danh.dcore.Resource.Files getConfig() {
        return new net.danh.dcore.Resource.Files(SkyShine.getPlugin(), "mmostats-config");
    }
}
