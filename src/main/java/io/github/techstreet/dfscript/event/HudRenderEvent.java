package io.github.techstreet.dfscript.event;

import io.github.techstreet.dfscript.event.system.Event;
import net.minecraft.client.gui.DrawContext;

public record HudRenderEvent(DrawContext context) implements Event {

}
