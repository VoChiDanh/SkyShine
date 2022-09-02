package net.danh.MMOStats.Manager;

import net.danh.MMOStats.Resource.Files;
import net.danh.dcore.DCore;

public class Debug {

    public static void debug(String msg) {
        if (Files.getConfig().getConfig().getBoolean("debug")) {
            DCore.dCoreLog(msg);
        }
    }
}
