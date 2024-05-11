package io.github.techstreet.dfscript.util.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.util.Identifier;

public class BlendableTexturedButtonWidget extends TexturedButtonWidget {

    public BlendableTexturedButtonWidget(int x, int y, int width, int height, Identifier unfocused, Identifier focused, PressAction pressAction) {
        super(x, y, width, height, new ButtonTextures(unfocused, focused), pressAction);
    }

    /*public BlendableTexturedButtonWidget(int x, int y, int width, int height, Identifier unfocused, Identifier focused, int textureWidth, int textureHeight, PressAction pressAction) {
        super(x, y, width, height, u, v, hoveredVOffset, texture, textureWidth, textureHeight, pressAction, Text.empty());
    }*/

//    public BlendableTexturedButtonWidget(int i, int j, int k, int l, int m, int n, int o, Identifier identifier, int p, int q, PressAction pressAction, Text text) {
//        super(i, j, k, l, m, n, o, identifier, p, q, pressAction, EMPTY, text);
//    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        RenderSystem.clearColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        super.renderWidget(context, mouseX, mouseY, delta);
    }
}