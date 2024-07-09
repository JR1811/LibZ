package net.libz.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.libz.network.packet.ConfigPacket;
import net.libz.network.packet.MousePacket;

@Environment(EnvType.CLIENT)
public class LibzClientPacket {
    static {
        ClientPlayNetworking.registerGlobalReceiver(MousePacket.IDENTIFIER, MousePacket::handlePacket);
        ClientPlayNetworking.registerGlobalReceiver(ConfigPacket.IDENTIFIER, ConfigPacket::handlePacket);
    }

    public static void init() {
        // static initialisation
    }
}
