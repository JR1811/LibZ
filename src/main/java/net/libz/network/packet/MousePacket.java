package net.libz.network.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.libz.LibzMain;
import net.libz.access.MouseAccessor;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record MousePacket(int mouseX, int mouseY) implements CustomPayload {

    public static final CustomPayload.Id<MousePacket> IDENTIFIER = new CustomPayload.Id<>(LibzMain.identifierOf("mouse_packet"));

    public static final PacketCodec<RegistryByteBuf, MousePacket> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, MousePacket::mouseX,
            PacketCodecs.INTEGER, MousePacket::mouseY,
            MousePacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return IDENTIFIER;
    }


    public void handlePacket(ClientPlayNetworking.Context context) {
        int mouseX = this.mouseX();
        int mouseY = this.mouseY();
        ((MouseAccessor) context.client().mouse).setMousePosition(mouseX, mouseY);
    }
}
