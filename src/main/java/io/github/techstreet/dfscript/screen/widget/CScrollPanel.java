package io.github.techstreet.dfscript.screen.widget;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Vector4f;

public class CScrollPanel extends CPanel {
    private int scroll = 0;

    public CScrollPanel(int x, int y, int w, int h) {
        super(x,y,w,h);
    }

    @Override
    public double getOffsetCenterX() {
        return 0;
    }

    @Override
    public double getOffsetCenterY() {
        return scroll;
    }

    private int getMaxScroll() {
        int max = 0;
        for (CWidget child : getChildren()) {
            max = Math.max(max, child.getBounds().y + child.getBounds().height);
        }
        return max - getBounds().height;
    }

    public int getScroll() {
        return scroll;
    }

    public void setScroll(int s) {
        scroll = s;

        if (scroll < -getMaxScroll()) {
            scroll = -getMaxScroll();
        }

        if (scroll > 0) {
            scroll = 0;
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double vertical, double horizontal) {
        if(!getBounds().contains(mouseX, mouseY)) {
            return false;
        }

        if(super.mouseScrolled(mouseX, mouseY, vertical, horizontal)) {
            return true;
        }

        scroll += vertical * 5;

        if (scroll < -getMaxScroll()) {
            scroll = -getMaxScroll();
        }

        if (scroll > 0) {
            scroll = 0;
        }

        return true;
    }
}
