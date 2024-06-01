package net.libz.network.packet;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record MousePacket(int mouseX, int mouseY) implements CustomPayload {

    public static final CustomPayload.Id<MousePacket> PACKET_ID = new CustomPayload.Id<>(new Identifier("libz", "mouse_packet"));

    public static final PacketCodec<RegistryByteBuf, MousePacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeInt(value.mouseX);
        buf.writeInt(value.mouseY);
    }, buf -> new MousePacket(buf.readInt(), buf.readInt()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

}
