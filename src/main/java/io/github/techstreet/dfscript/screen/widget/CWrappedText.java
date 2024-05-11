package io.github.techstreet.dfscript.screen.widget;

import io.github.techstreet.dfscript.DFScript;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class CWrappedText extends CText {

    private int width;
    private List<OrderedText> wrapped = new ArrayList<OrderedText>();

    public CWrappedText(int x, int y, int width, Text text, int color, float scale, boolean centered, boolean shadow) {
        super(x, y, text, color, scale, centered, shadow);
        this.width = width;
        wrapText();
    }

    public CWrappedText(int x, int y, int width, Text text) {
        this(x, y, width, text,0x333333, 1, false, false);
    }

    private void wrapText() {
        TextRenderer renderer = DFScript.MC.textRenderer;
        wrapped = renderer.wrapLines(text, width);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float tickDelta) {
        MatrixStack stack = context.getMatrices();
        stack.push();
        stack.translate(x, y, 0);

        stack.scale(0.5f * scale, 0.5f * scale, 0.5f * scale);

        TextRenderer f = DFScript.MC.textRenderer;

        if (centered) {
            stack.translate(-f.getWidth(text) / 2f, -f.fontHeight / 2f, 0);
        }

        int y_offset = 0;
        for (OrderedText line : wrapped) {
            context.drawText(f, line, 0, y_offset, color, shadow);
            y_offset += 8;
        }

        stack.pop();
    }

    @Override
    public void setText(Text t) {
        super.setText(t);
        wrapText();
    }

}
