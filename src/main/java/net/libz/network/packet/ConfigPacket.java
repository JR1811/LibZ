package net.libz.network.packet;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ConfigPacket(String configName, boolean gson, byte[] bytes) implements CustomPayload {

    public static final CustomPayload.Id<ConfigPacket> PACKET_ID = new CustomPayload.Id<>(Identifier.of("libz", "config_packet"));

    public static final PacketCodec<RegistryByteBuf, ConfigPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeString(value.configName);
        buf.writeBoolean(value.gson);
        buf.writeBytes(value.bytes);
    }, buf -> {
        String configName = buf.readString();
        boolean gson = buf.readBoolean();
        byte[] jsonBytes = new byte[buf.readableBytes()];
        buf.readBytes(jsonBytes);
        return new ConfigPacket(configName, gson, jsonBytes);
    });

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

}
