package io.github.techstreet.dfscript.mixin.render;

import io.github.techstreet.dfscript.event.HudRenderEvent;
import io.github.techstreet.dfscript.event.system.EventManager;
import io.github.techstreet.dfscript.screen.overlay.OverlayManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MInGameHUD {
    @Inject(at = @At("HEAD"), method = "render")
    private void render(DrawContext context, float tickDelta, CallbackInfo ci) {
        OverlayManager.getInstance().render(context);
        EventManager em = EventManager.getInstance();
        em.dispatch(new HudRenderEvent(context));
        em.resetEvents();
    }
}