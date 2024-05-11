package io.github.techstreet.dfscript.screen.widget;

import io.github.techstreet.dfscript.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

public class CTexturedButton extends CButton {

    private String texture;
    private String highlightedTexture;

    public CTexturedButton(int x, int y, int width, int height, String texture, String highlightedTexture, Runnable onClick) {
        super(x, y, width, height, 0, "", onClick);
        this.texture = texture;
        this.highlightedTexture = highlightedTexture;
    }

    public void setTexture(String newTexture, String newHighlightedTexture) {
        texture = newTexture;
        highlightedTexture = newHighlightedTexture;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float tickDelta) {
        MatrixStack stack = context.getMatrices();
        stack.push();
        stack.translate(x, y, 0);
        stack.scale(0.5f, 0.5f, 0.5f);

        Rectangle rect = new Rectangle(x, y, width, height);

        String usedTexture = texture;
        if (rect.contains(mouseX, mouseY)) {
            usedTexture = highlightedTexture;
        }

        RenderUtil.renderImage(context, 0, 0, width * 2, height * 2, 0, 0, 1, 1, usedTexture);
        stack.pop();
    }
}
