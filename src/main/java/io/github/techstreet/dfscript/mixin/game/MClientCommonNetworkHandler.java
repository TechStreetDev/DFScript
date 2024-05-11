package io.github.techstreet.dfscript.mixin.game;

import io.github.techstreet.dfscript.event.ServerLeaveEvent;
import io.github.techstreet.dfscript.event.system.EventManager;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientCommonNetworkHandler.class)
public class MClientCommonNetworkHandler {
    @Inject(method = "onDisconnect", at = @At("RETURN"), cancellable = true)
    private void onDisconnect(DisconnectS2CPacket packet, CallbackInfo ci) {
        ServerLeaveEvent event = new ServerLeaveEvent(packet);
        EventManager.getInstance().dispatch(event);
    }
}
