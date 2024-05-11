package io.github.techstreet.dfscript.util;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.techstreet.dfscript.DFScript;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

// TODO: Convert to using DrawContext
public class RenderUtil {

    private static final List<Scissor> scissorStack = new ArrayList<>();

    public static void renderImage(DrawContext context, int x, int y, int width, int height, float ux, float uy, float uw, float uh, String image) {
        MatrixStack stack = context.getMatrices();
        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, new Identifier(image));
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.disableCull();

        Tessellator tessellator = Tessellator.getInstance();

        BufferBuilder bb = tessellator.getBuffer();
        bb.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bb.vertex(stack.peek().getPositionMatrix(), x, y, 0).texture(ux, uy).next();
        bb.vertex(stack.peek().getPositionMatrix(), x + width, y, 0).texture(ux+uw, uy).next();
        bb.vertex(stack.peek().getPositionMatrix(), x + width, y + height, 0).texture(ux+uw, uy+uh).next();
        bb.vertex(stack.peek().getPositionMatrix(), x, y + height, 0).texture(ux, uy+uh).next();
        tessellator.draw();

        RenderSystem.enableCull();
    }

    public static void renderButton(DrawContext context, int x, int y, int width, int height, boolean hovered, boolean disabled) {
        String image = "textures/gui/sprites/widget/button.png";
        if (disabled) {
            image = "textures/gui/sprites/widget/button_disabled.png";
        }
        else if (hovered) {
            image = "textures/gui/sprites/widget/button_highlighted.png";
        }
        final int textureWidth = 200;
        final int textureHeight = 20;
        final int padding = 3;
        int x1 = 0;
        int y1 = 0;
        int x2 = 200;
        int y2 = y1 + 20;
        renderContinuousTexture(context, x, y, width, height, image, x1, y1, x2, y2, textureWidth, textureHeight, padding,1);
    }

    public static void renderCustomButton(DrawContext context, int x, int y, int width, int height, boolean hovered, boolean disabled, String image, String disabledImage, String highlightedImage) {
        final int textureWidth = 200;
        final int textureHeight = 20;
        final int padding = 3;
        String imageUsed = image;
        if (disabled) {
            imageUsed = disabledImage;
        }
        else if (hovered) {
            imageUsed = highlightedImage;
        }
        int x1 = 0;
        int y1 = 0;
        int x2 = 200;
        int y2 = y1 + 20;
        renderContinuousTexture(context, x, y, width, height, image, x1, y1, x2, y2, textureWidth, textureHeight, padding,1);
    }

    public static void renderContinuousTexture(DrawContext context, int x, int y, int width, int height, String image, int tx1, int ty1, int tx2, int ty2, int textureWidth, int textureHeight, int padding,double scale) {
        int scaledPadding = (int) (padding*scale);
        //top left corner
        renderContinuousTexture(context, x, y, scaledPadding, scaledPadding, image, tx1, ty1, tx1 + padding, ty1 + padding, textureWidth, textureHeight,scale);
        //top right corner
        renderContinuousTexture(context, x + width - scaledPadding, y, scaledPadding, scaledPadding, image, tx2 - padding, ty1, tx2, ty1 + padding, textureWidth, textureHeight,scale);
        //bottom left corner
        renderContinuousTexture(context, x, y + height - scaledPadding, scaledPadding, scaledPadding, image, tx1, ty2 - padding, tx1 + padding, ty2, textureWidth, textureHeight,scale);
        //bottom right corner
        renderContinuousTexture(context, x + width - scaledPadding, y + height - scaledPadding, scaledPadding, scaledPadding, image, tx2 - padding, ty2 - padding, tx2, ty2, textureWidth, textureHeight,scale);

        //top
        renderContinuousTexture(context, x + scaledPadding, y, width - scaledPadding * 2, scaledPadding, image, tx1 + padding, ty1, tx2 - padding, ty1 + padding, textureWidth, textureHeight,scale);
        //bottom
        renderContinuousTexture(context, x + scaledPadding, y + height - scaledPadding, width - scaledPadding * 2, scaledPadding, image, tx1 + padding, ty2 - padding, tx2 - padding, ty2, textureWidth, textureHeight,scale);
        //left
        renderContinuousTexture(context, x, y + scaledPadding, scaledPadding, height - scaledPadding * 2, image, tx1, ty1 + scaledPadding, tx1 + padding, ty2 - padding, textureWidth, textureHeight,scale);
        //right
        renderContinuousTexture(context, x + width - scaledPadding, y + scaledPadding, scaledPadding, height - scaledPadding * 2, image, tx2 - padding, ty1 + padding, tx2, ty2 - padding, textureWidth, textureHeight,scale);

        //center
        renderContinuousTexture(context, x + scaledPadding, y + scaledPadding, width - scaledPadding * 2, height - scaledPadding * 2, image, tx1 + padding, ty1 + padding, tx2 - padding, ty2 - padding, textureWidth, textureHeight,scale);
    }

    public static void renderContinuousTexture(DrawContext context, int x, int y, int width, int height, String image, int tx1, int ty1, int tx2, int ty2, int textureWidth, int textureHeight, double scale) {
        int tw = (tx2 - tx1);
        int th = (ty2 - ty1);

        float ux = (float) tx1 / textureWidth;
        float uy = (float) ty1 / textureHeight;
        float uw = (float) tw / textureWidth;
        float uh = (float) th / textureHeight;

        tw*=scale;
        th*=scale;

        float xrepeations = (float) width / (tw);
        float yrepeations = (float) height / (th);
        for (int xi = 0; xi <= xrepeations - 1; xi++) {
            for (int yi = 0; yi <= yrepeations - 1; yi++) {
                renderImage(context,x + xi * tw, y + yi * th, tw, th, ux, uy, uw, uh, image);
            }
        }
        float minH = Math.min(th, yrepeations * th + 1);

        for (int xi = 0; xi < xrepeations - 1; xi++) {
            renderImage(context, x + xi * tw, Math.max((int) (y + yrepeations * th) - th, y), tw, (int) minH, ux, uy, uw, uh, image);
        }
        float minW = Math.min(tw, xrepeations * tw + 1);
        for (int yi = 0; yi < yrepeations - 1; yi++) {
            renderImage(context, Math.max((int) (x + xrepeations * tw) - tw, x), y + yi * th, (int) minW, th, ux, uy, uw, uh, image);
        }
        renderImage(context, Math.max((int) (x + xrepeations * tw) - tw, x), Math.max((int) (y + yrepeations * th) - th, y), (int) minW, (int) minH, ux, uy, uw, uh, image);
    }

    public static void renderGui(DrawContext context, int x, int y, int width, int height) {
        renderContinuousTexture(context, x, y, width, height, "textures/gui/demo_background.png", 0, 0, 248, 166, 256, 256, 10,0.66);
    }

    public static void pushScissor(int x, int y, int width, int height) {
        if (scissorStack.size() != 0) {
            Scissor state = scissorStack.get(scissorStack.size() - 1);
            x = Math.max(x, state.x);
            y = Math.max(y, state.y);
            width = Math.min(width, state.x + state.width - x);
            height = Math.min(height, state.y + state.height - y);
        }

        Scissor s = new Scissor(x, y, width, height);
        scissorStack.add(s);
        GL11.glScissor(x, DFScript.MC.getWindow().getHeight() - y - height, width, height);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
    }

    public static void popScissor() {
        scissorStack.remove(scissorStack.size() - 1);
        if (scissorStack.size() > 0) {
            Scissor s = scissorStack.get(scissorStack.size() - 1);
            GL11.glScissor(s.x, DFScript.MC.getWindow().getHeight() - s.y - s.height, s.width, s.height);
        } else {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }
    }

    public static void renderGuiItem(DrawContext context, ItemStack item) {
        MatrixStack stack = context.getMatrices();
        stack.push();
        stack.scale(0.5F,0.5F,1F);
        context.drawItem(item, 0, 0);
        stack.pop();
    }

    private record Scissor(int x, int y, int width, int height) {

    }

    public static void sendToaster(String title, String description, SystemToast.Type type) {
        sendToaster(Text.literal(title), Text.literal(description), type);
    }

    public static void sendToaster(MutableText title, MutableText description, SystemToast.Type type) {
        MinecraftClient.getInstance().getToastManager().add(new SystemToast(type, title, description));
    }

    static int renderSteps = 100;

    public static void renderLine(MatrixStack stack, float x1, float y1, float x2, float y2, int color, float size) {
        float stepX = (x2-x1)/renderSteps;
        float stepY = (y2-y1)/renderSteps;

        for(int i = 0; i <= renderSteps; i++) {
            fill(stack, (x1-(size/2)), (y1-(size/2)), (x1+(size/2)), (y1+(size/2)), color);

            x1 += stepX;
            y1 += stepY;
        }
    }

    // these functions exist because fabric api dumb and used an integer for positions in the DrawableHelper class...
    public static void fill(DrawContext context, float x1, float y1, float x2, float y2, int color) {
        fill(context.getMatrices(), x1, y1, x2, y2, color);
    }

    public static void fill(MatrixStack stack, float x1, float y1, float x2, float y2, int color) {
        fill(stack.peek().getPositionMatrix(), x1, y1, x2, y2, color);
    }
    public static void fill(Matrix4f matrix, float x1, float y1, float x2, float y2, int color) {
        float i;
        if (x1 < x2) {
            i = x1;
            x1 = x2;
            x2 = i;
        }
        if (y1 < y2) {
            i = y1;
            y1 = y2;
            y2 = i;
        }
        float f = (float)(color >> 24 & 0xFF) / 255.0f;
        float g = (float)(color >> 16 & 0xFF) / 255.0f;
        float h = (float)(color >> 8 & 0xFF) / 255.0f;
        float j = (float)(color & 0xFF) / 255.0f;
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
//        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorTexProgram);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix, x1, y2, 0.0f).color(g, h, j, f).next();
        bufferBuilder.vertex(matrix, x2, y2, 0.0f).color(g, h, j, f).next();
        bufferBuilder.vertex(matrix, x2, y1, 0.0f).color(g, h, j, f).next();
        bufferBuilder.vertex(matrix, x1, y1, 0.0f).color(g, h, j, f).next();
//        BufferRenderer.drawWithShader(bufferBuilder.end());
        BufferRenderer.draw(bufferBuilder.end());
//        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }
}