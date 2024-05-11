package io.github.techstreet.dfscript.screen.widget;

import io.github.techstreet.dfscript.DFScript;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.awt.*;

public class CText implements CWidget {

    int x;
    int y;
    Text text;
    int color;
    float scale;
    boolean centered;
    boolean shadow;

    public CText(int x, int y, Text text, int color, float scale, boolean centered, boolean shadow) {
        this.x = x;
        this.y = y;
        this.text = text;
        this.color = color;
        this.scale = scale;
        this.centered = centered;
        this.shadow = shadow;
    }

    public CText(int x, int y, Text text) {
        this(x, y, text, 0x333333, 1, false, false);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float tickDelta) {
        MatrixStack stack = context.getMatrices();
        stack.push();
        stack.translate(x, y, 0);

        //stack.scale(0.5f, 0.5f, 0.5f);

        // maintain backwards compat with the old 0.5f
        stack.scale(0.5f * scale, 0.5f * scale, 0.5f * scale);

        TextRenderer f = DFScript.MC.textRenderer;

        if (centered) {
            stack.translate(-f.getWidth(text) / 2f, -f.fontHeight / 2f, 0);
        }

        context.drawText(f, text, 0, 0, color, shadow);
        stack.pop();
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, 0, 0);
    }

    public void setText(Text t) {
        text = t;
    }
}
