package net.libz.network.packet;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.ConfigManager;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Jankson;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.api.SyntaxError;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.libz.api.ConfigSync;
import net.libz.mixin.config.AutoConfigAccess;
import net.libz.util.ConfigHelper;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.Iterator;

public record ConfigPacket(String configName, boolean gson, byte[] bytes) implements CustomPayload {

    public static final CustomPayload.Id<ConfigPacket> IDENTIFIER = new CustomPayload.Id<>(Identifier.of("libz", "config_packet"));

    public static final PacketCodec<RegistryByteBuf, ConfigPacket> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, ConfigPacket::configName,
            PacketCodecs.BOOL, ConfigPacket::gson,
            PacketCodecs.BYTE_ARRAY, ConfigPacket::bytes,
            ConfigPacket::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return IDENTIFIER;
    }


    public void handlePacket(ClientPlayNetworking.Context context) {
        String configName = this.configName();
        boolean gson = this.gson();
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] jsonBytes = this.bytes();
        context.client().execute(() -> {
            JsonNode oldJsonNode = ConfigHelper.getConfigNode(configName, gson, false);
            JsonNode jsonNode = ConfigHelper.readJsonTree(objectMapper, jsonBytes);

            if (oldJsonNode.isObject() && jsonNode.isObject()) {
                ObjectNode existingObjectNode = (ObjectNode) oldJsonNode;
                ObjectNode newObjectNode = (ObjectNode) jsonNode;

                newObjectNode.fields().forEachRemaining(entry -> {
                    String fieldName = entry.getKey();
                    JsonNode newValue = entry.getValue();
                    existingObjectNode.set(fieldName, newValue);
                });

                Jankson jankson = Jankson.builder().build();

                // Could get improved here by just get holder not iterating over
                Iterator<ConfigHolder<?>> iterator = AutoConfigAccess.getHolders().values().iterator();
                while (iterator.hasNext()) {
                    ConfigHolder<?> holder = iterator.next();
                    if (((ConfigManager<?>) holder).getDefinition().name().equals(configName)) {
                        try {
                            ConfigData data = (ConfigData) jankson.fromJson(jankson.load(existingObjectNode.toString()), holder.getConfigClass());
                            ((ConfigManager<ConfigData>) holder).setConfig(data);

                            if (holder.getConfig() instanceof ConfigSync) {
                                ((ConfigSync) holder.getConfig()).updateConfig(data);
                            }

                        } catch (SyntaxError e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }
        });
    }
}
