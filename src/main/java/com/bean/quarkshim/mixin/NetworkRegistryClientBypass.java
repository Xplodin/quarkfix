package com.bean.quarkshim.mixin;

import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.Map;

/**
 * Client-side: relax channel version validation by returning an empty mismatch map
 * for BOTH client and server validation paths.
 */
@Mixin(targets = "net.minecraftforge.network.NetworkRegistry")
public abstract class NetworkRegistryClientBypass {

    @Inject(
            method = "validateClientChannels(Ljava/util/Map;)Ljava/util/Map;",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private static void shim$emptyClient(
            Map<ResourceLocation, String> channels,
            CallbackInfoReturnable<Map<ResourceLocation, String>> cir
    ) {
        cir.setReturnValue(Collections.emptyMap());
    }

    @Inject(
            method = "validateServerChannels(Ljava/util/Map;)Ljava/util/Map;",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private static void shim$emptyServer(
            Map<ResourceLocation, String> channels,
            CallbackInfoReturnable<Map<ResourceLocation, String>> cir
    ) {
        cir.setReturnValue(Collections.emptyMap());
    }
}
