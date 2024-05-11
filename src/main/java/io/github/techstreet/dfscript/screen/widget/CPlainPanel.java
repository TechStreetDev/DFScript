package io.github.techstreet.dfscript.screen.widget;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Vector4f;

public class CPlainPanel extends CPanel {
    public CPlainPanel(int x, int y, int w, int h) {
        super(x, y, w, h);
    }

    @Override
    public double getOffsetCenterX() {
        return 0;
    }

    @Override
    public double getOffsetCenterY() {
        return 0;
    }
}
