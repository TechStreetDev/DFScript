package io.github.techstreet.dfscript.screen;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.widget.CWidget;
import io.github.techstreet.dfscript.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CScreen extends Screen {

    private final int width, height;
    public final List<CWidget> widgets = new ArrayList<>();

    protected CScreen(int width, int height) {
        super(Text.literal("DFScript Screen"));
        this.width = width;
        this.height = height;
//        DFScript.MC.keyboard.setRepeatEvents(true);
    }

    @Override
    public void render(@NotNull DrawContext context, int mouseX, int mouseY, float tickDelta) {
        //renderBackground(context, mouseX, mouseY, tickDelta);
        super.render(context, mouseX, mouseY, tickDelta);
        context.getMatrices().push();
        MinecraftClient mc = DFScript.MC;

        MatrixStack stack = context.getMatrices();

        assert mc.currentScreen != null;
        stack.translate(mc.currentScreen.width/2f, mc.currentScreen.height/2f, 0);

//        float scaleFactor = (float) mc.getWindow().getScaleFactor();
        float scaleFactor = 2;
        stack.scale(scaleFactor,scaleFactor,1F);

        stack.translate(-width/2f, -height/2f, 0);

        RenderUtil.renderGui(context,0,0,width,height);

        mouseX += -mc.currentScreen.width/2;
        mouseY += -mc.currentScreen.height/2;

        mouseX /= scaleFactor;
        mouseY /= scaleFactor;

        mouseX += width/2;
        mouseY += height/2;

        for (CWidget cWidget : widgets) {
            cWidget.render(context, mouseX, mouseY, tickDelta);
        }
        for (CWidget cWidget : widgets) {
            cWidget.renderOverlay(context, mouseX, mouseY, tickDelta);
        }
        stack.pop();
    }

    @Override
    public boolean charTyped(char ch, int keyCode) {
        for (CWidget cWidget : widgets) {
            cWidget.charTyped(ch, keyCode);
        }
        return super.charTyped(ch, keyCode);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (CWidget cWidget : widgets) {
            cWidget.keyPressed(keyCode, scanCode, modifiers);
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        for (CWidget cWidget : widgets) {
            cWidget.keyReleased(keyCode, scanCode, modifiers);
        }

        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        mouseX = translateMouseX(mouseX);
        mouseY = translateMouseY(mouseY);

        for (int i = widgets.size() - 1; i >= 0; i--) {
            if (widgets.get(i).mouseClicked(mouseX, mouseY, button)) {
                break;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        mouseX = translateMouseX(mouseX);
        mouseY = translateMouseY(mouseY);

        for (CWidget cWidget : widgets) {
            cWidget.mouseScrolled(mouseX, mouseY, vertical, horizontal);
        }
        return super.mouseScrolled(mouseX, mouseY, horizontal, vertical);
    }

    public double translateMouseX(double mouseX) {
        MinecraftClient mc = DFScript.MC;
//        float s = (float) mc.getWindow().getScaleFactor();
        float scaleFactor = 2;
        assert mc.currentScreen != null;
        mouseX += -mc.currentScreen.width/2f;
        mouseX /= scaleFactor;
        mouseX += width/2f;
        return mouseX;
    }

    public double translateMouseY(double mouseY) {
        MinecraftClient mc = DFScript.MC;
//        float scaleFactor = (float) mc.getWindow().getScaleFactor();
        float scaleFactor = 2;
        assert mc.currentScreen != null;
        mouseY += -mc.currentScreen.height/2f;
        mouseY /= scaleFactor;
        mouseY += height/2f;
        return mouseY;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        for(CWidget widget : widgets) {
            if(!widget.enableClosingOnEsc()) {
                return false;
            }
        }

        return super.shouldCloseOnEsc();
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        mouseX = translateMouseX(mouseX);
        mouseY = translateMouseY(mouseY);

        for (int i = widgets.size() - 1; i >= 0; i--) {
            if (widgets.get(i).mouseReleased(mouseX, mouseY, button)) {
                break;
            }
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        mouseX = translateMouseX(mouseX);
        mouseY = translateMouseY(mouseY);
        deltaX = translateMouseX(deltaX);
        deltaY = translateMouseY(deltaY);

        for (int i = widgets.size() - 1; i >= 0; i--) {
            if (widgets.get(i).mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
                break;
            }
        }

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
}
