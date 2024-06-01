package net.libz.network;

import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.ConfigManager;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Jankson;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.api.SyntaxError;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.libz.access.MouseAccessor;
import net.libz.api.ConfigSync;
import net.libz.mixin.config.AutoConfigAccess;
import net.libz.network.packet.ConfigPacket;
import net.libz.network.packet.MousePacket;
import net.libz.util.ConfigHelper;

@SuppressWarnings("unchecked")
@Environment(EnvType.CLIENT)
public class LibzClientPacket {

    @SuppressWarnings("resource")
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(ConfigPacket.PACKET_ID, (payload, context) -> {
            String configName = payload.configName();
            boolean gson = payload.gson();
            ObjectMapper objectMapper = new ObjectMapper();
            byte[] jsonBytes = payload.bytes();
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
        });

        ClientPlayNetworking.registerGlobalReceiver(MousePacket.PACKET_ID, (payload, context) -> {
            int mouseX = payload.mouseX();
            int mouseY = payload.mouseY();
            context.client().execute(() -> {
                ((MouseAccessor) context.client().mouse).setMousePosition(mouseX, mouseY);
            });
        });
    }
}
