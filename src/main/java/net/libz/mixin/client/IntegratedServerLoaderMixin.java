package net.libz.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.integrated.IntegratedServerLoader;

@Environment(EnvType.CLIENT)
@Mixin(value = IntegratedServerLoader.class, priority = 999)
public class IntegratedServerLoaderMixin {

    @ModifyVariable(method = "tryLoad", at = @At("HEAD"), ordinal = 0)
    private static boolean startMixin(boolean original) {
        return false;
    }

}
