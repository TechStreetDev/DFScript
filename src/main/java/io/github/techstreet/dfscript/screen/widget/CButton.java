package io.github.techstreet.dfscript.screen.widget;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.util.RenderUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.awt.*;

public class CButton implements CWidget {

    public final int x;
    public final int y;
    public final int width;
    public final int height;
    private final float textScale;
    private Text text;
    private Runnable onClick;
    private boolean disabled;

    public CButton(int x, int y, int width, int height, float textScale, String text, Runnable onClick) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.textScale = textScale;
        this.text = Text.literal(text);
        this.onClick = onClick;
        this.disabled = false;
    }

    public CButton(int x, int y, int width, int height, String text, Runnable onClick) {
        this(x, y, width, height, 1f, text, onClick);
    }



    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float tickDelta) {
        MatrixStack stack = context.getMatrices();
        stack.push();
        stack.translate(x, y, 0);

        stack.push();
        stack.scale(0.5f, 0.5f, 0.5f);

        Rectangle rect = new Rectangle(x, y, width, height);

        RenderUtil.renderButton(context, 0, 0, width * 2, height * 2, rect.contains(mouseX, mouseY), disabled);
        stack.pop();

        TextRenderer f = DFScript.MC.textRenderer;

        stack.translate(rect.width / 2f, rect.height / 2f, 0);
        stack.scale(0.5f * textScale, 0.5f * textScale, 0.5f * textScale);
        stack.translate(-f.getWidth(text) / 2f, -f.fontHeight / 2f, 0);

        context.drawText(f, text, 0, 0, 0xFFFFFF, true);

        stack.pop();
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        Rectangle rect = new Rectangle(this.x, this.y, width, height);

        if (rect.contains(x, y)) {
            DFScript.MC.getSoundManager().play(PositionedSoundInstance.ambient(SoundEvents.UI_BUTTON_CLICK.value(), 1f,1f));
            onClick.run();
            return true;
        }

        return false;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void setOnClick(Runnable onClick) {
        this.onClick = onClick;
    }

    public void setText(String text) {
        this.text = Text.literal(text);
    }

    public void setText(Text text) {
        this.text = text;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}