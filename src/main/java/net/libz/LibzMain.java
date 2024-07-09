package net.libz;

import net.fabricmc.api.ModInitializer;
import net.libz.init.*;
import net.libz.network.LibzServerPacket;
import net.minecraft.util.Identifier;

public class LibzMain implements ModInitializer {
    public static final String MODID = "libz";

    @Override
    public void onInitialize() {
        ConfigInit.init();
        LibzServerPacket.init();
        EventInit.init();
    }

    public static Identifier identifierOf(String name) {
        return Identifier.of(MODID, name);
    }
}
