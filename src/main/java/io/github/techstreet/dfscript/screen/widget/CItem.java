package io.github.techstreet.dfscript.screen.widget;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.function.Consumer;

public class CItem implements CWidget {

    private final int x;
    private final int y;
    private ItemStack item;

    private Text countText;
    private Consumer<Integer> onClick;

    public CItem(int x, int y, ItemStack item) {
        this.x = x;
        this.y = y;
        this.item = item;
    }

    public CItem(int x, int y, ItemStack item, Text countText) {
        this.x = x;
        this.y = y;
        this.item = item;
        this.countText = countText;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float tickDelta) {
        MatrixStack stack = context.getMatrices();
        stack.push();
        stack.translate(x, y, 0);
        RenderUtil.renderGuiItem(context, item);
        if (countText != null) {
            stack.translate(4, 4, 0);
            stack.scale(0.5f, 0.5f, 0.5f);
            context.drawText(DFScript.MC.textRenderer, countText, 0, 0, 0xFFFFFFFF, true);
        }
        stack.pop();
    }

    @Override
    public void renderOverlay(DrawContext context, int mouseX, int mouseY, float tickDelta) {
        MatrixStack stack = context.getMatrices();
        Rectangle rect = new Rectangle(x, y,8, 8);

        if (rect.contains(mouseX, mouseY)) {
            stack.push();
            stack.translate(mouseX, mouseY, 0);
            stack.scale(0.5f, 0.5f, 1f);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
//            stack.peek().getPositionMatrix().addToLastColumn(new Vec3f(0, 0, 600));
            context.drawTooltip(DFScript.MC.textRenderer, item.getTooltip(DFScript.MC.player, TooltipContext.BASIC), 0, 0);
//            DFScript.MC.currentScreen.renderTooltip(stack, item.getTooltip(
//                DFScript.MC.player, TooltipContext.BASIC
//            ), Optional.empty(), 0, 0);
            stack.pop();
        }
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, 8, 8);
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        Rectangle rect = new Rectangle(this.x, this.y, 8, 8);

        if (rect.contains(x, y)) {
            if (onClick != null) {
                DFScript.MC.getSoundManager().play(PositionedSoundInstance.ambient(SoundEvents.UI_BUTTON_CLICK.value(), 1f, 1f));
                onClick.accept(button);
                return true;
            }
        }

        return false;
    }

    public void setClickListener(Consumer<Integer> onClick) {
        this.onClick = onClick;
    }

    public void setItemStack(ItemStack newItem) {
        item = newItem;
    }
}
