package io.github.techstreet.dfscript.screen.widget;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.util.RenderUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Vector4f;

import java.awt.*;
import java.util.List;

public class CTextField implements CWidget {

    final int x, y, width, height;

    boolean selected;
    boolean editable;

    boolean multiline = true;

    boolean xScrolling = false;
    public int textColor = 0xFFFFFFFF;
    String text;
    Runnable changedListener;
    int cursorPos = 0;
    boolean hasSelection = false;
    int selectionPos = 0;
    int scroll = 0;
    int xScroll = 0;

    public CTextField(String text, int x, int y, int width, int height, boolean editable) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.editable = editable;
        this.selected = false;
    }

    public void setMultiline(boolean multiline) {
        this.multiline = multiline;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float tickDelta) {
        MatrixStack stack = context.getMatrices();
        stack.push();
        stack.translate(x, y, 0);

        context.fill(0, 0, width, height, 0xFF888888);
        context.fill(1, 1, width - 1, height - 1, 0xFF000000);

        float xPos = stack.peek().getPositionMatrix().m30();
        float yPos = stack.peek().getPositionMatrix().m31();

        Vector4f begin = new Vector4f(xPos - 2, yPos + 2, 1, 1);
        Vector4f end = new Vector4f((xPos + (width * 2)) - 7, (yPos + (height * 2)), 1, 1);

        int guiScale = (int) DFScript.MC.getWindow().getScaleFactor();
        RenderUtil.pushScissor(
                (int) begin.x()*guiScale,
                (int) begin.y()*guiScale,
                (int) (end.x() - begin.x())*guiScale,
                (int) (end.y() - begin.y())*guiScale
        );

        stack.translate(2 + xScroll, 2 + scroll, 0);
        stack.scale(0.5f, 0.5f, 0);

        TextRenderer f = DFScript.MC.textRenderer;
        String[] lines = text.split("\n");

        stack.push();
        int selectionStart = Math.min(selectionPos, cursorPos);
        int selectionEnd = Math.max(selectionPos, cursorPos);

        for (String line : lines) {
            if (hasSelection) {
                int lineSelectionStart = Math.max(0, Math.min(selectionStart, line.length()));
                int lineSelectionEnd = Math.max(0, Math.min(selectionEnd, line.length()));

                stack.push();

                stack.translate(f.getWidth(line.substring(0, lineSelectionStart)), 0, 0);
                context.fill(0, 0, f.getWidth(line.substring(lineSelectionStart, lineSelectionEnd)), f.fontHeight, 0xFF5555FF);

                stack.pop();
            }
            context.drawText(f, line, 0, 0, textColor, false);

            selectionStart -= line.length() + 1;
            selectionEnd -= line.length() + 1;

            stack.translate(0, f.fontHeight, 0);
        }
        stack.pop();

        if (editable && selected) {
            int cursorLine = getCursorLineIndex();
            int cursorLinePos = getIndexInCursorLine();

            int cursorX = f.getWidth(getCursorLine().substring(0, cursorLinePos));
            int cursorY = f.fontHeight * cursorLine;

            context.drawText(f, "|", cursorX, cursorY,0x99FFFFFF, false);
        }

        stack.pop();
        RenderUtil.popScissor();
    }

    @Override
    public void charTyped(char ch, int keyCode) {
        if (ch == '§') {
            return;
        }

        if (editable && selected) {
            if (hasSelection) {
                int selectionStart = Math.min(selectionPos, cursorPos);
                int selectionEnd = Math.max(selectionPos, cursorPos);
                deleteSelection(selectionStart, selectionEnd);
                cursorPos = selectionStart;
            }
            text = text.substring(0, cursorPos) + ch + text.substring(cursorPos);
            cursorPos++;
            if (changedListener != null) {
                changedListener.run();
            }
        }
    }

    public void keyReleased(int keyCode, int scanCode, int modifiers)
    {
        if(keyCode == 341) xScrolling = false; // left control
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode == 341) xScrolling = true; // left control

        if (editable && selected) {
            String lastText = text;
            TextRenderer f = DFScript.MC.textRenderer;
            boolean createSelection = modifiers != 0;
            if (createSelection && !hasSelection) {
                hasSelection = true;
                selectionPos = cursorPos;
            }
            int selectionStart = Math.min(selectionPos, cursorPos);
            int selectionEnd = Math.max(selectionPos, cursorPos);

            switch (keyCode) {
                case 259 -> { //backspace
                    if (hasSelection) {
                        deleteSelection(selectionStart, selectionEnd);
                    } else if (cursorPos > 0) {
                        text = text.substring(0, cursorPos - 1) + text.substring(cursorPos);
                        cursorPos--;
                    }
                }
                case 257 -> { //enter
                    if (!multiline) {
                        return;
                    }
                    if (hasSelection) {
                        deleteSelection(selectionStart, selectionEnd);
                    }
                    text = text.substring(0, cursorPos) + "\n" + text.substring(cursorPos);
                    cursorPos++;
                }
                case 263 -> { //left
                    if (cursorPos > 0) {
                        cursorPos--;
                    }
                }
                case 262 -> { //right
                    if (cursorPos < text.length()) {
                        cursorPos++;
                    }
                }
                case 265 -> { //up
                    if (getCursorLineIndex() > 0) {
                        int x = f.getWidth(getCursorLine().substring(0, getIndexInCursorLine()));
                        int charPos = f.trimToWidth(getLine(getCursorLineIndex() - 1), x, true).length();
                        setCursor(getCursorLineIndex() - 1, charPos);
                    } else {
                        cursorPos = 0;
                    }
                }
                case 264 -> { //down
                    if (getCursorLineIndex() < getLines().length - 1) {
                        int x = f.getWidth(getCursorLine().substring(0, getIndexInCursorLine()));
                        int charPos = f.trimToWidth(getLine(getCursorLineIndex() + 1), x, true).length();
                        setCursor(getCursorLineIndex() + 1, charPos);
                    } else {
                        cursorPos = text.length();
                    }
                }
                case 261 -> { //delete
                    if (hasSelection) {
                        deleteSelection(selectionStart, selectionEnd);
                    } else if (cursorPos < text.length()) {
                        text = text.substring(0, cursorPos) + text.substring(cursorPos + 1);
                    }
                }
                case 65 -> { //a
                    if (modifiers == 2) {
                        selectionPos = 0;
                        cursorPos = text.length();
                        hasSelection = true;
                    }
                }
                case 67 -> { //c
                    if (modifiers == 2) {
                        if (hasSelection) {
                            DFScript.MC.keyboard.setClipboard(text.substring(selectionStart, selectionEnd));
                        }
                    }
                }
                case 86 -> { //v
                    if (modifiers == 2) {
                        if (hasSelection) {
                            deleteSelection(selectionStart, selectionEnd);
                        }
                        String clipboard = DFScript.MC.keyboard.getClipboard();
                        text = text.substring(0, cursorPos) + clipboard + text.substring(cursorPos);
                        cursorPos += clipboard.length();
                    }
                }
                case 88 -> { //x
                    if (modifiers == 2) {
                        if (hasSelection) {
                            DFScript.MC.keyboard.setClipboard(text.substring(selectionStart, selectionEnd));
                            deleteSelection(selectionStart, selectionEnd);
                        }
                    }
                }
            }

            if (selectionPos == cursorPos) {
                hasSelection = false;
            }

            if (!lastText.equals(text)) {
                if (changedListener != null) {
                    changedListener.run();
                }
            }
        }
    }

    public void deleteSelection(int selectionStart, int selectionEnd) {
        text = text.substring(0, selectionStart) + text.substring(selectionEnd);
        cursorPos = selectionStart;
        hasSelection = false;
    }

    private void setCursor(int line, int pos) {
        List<String> lines = List.of(getLines());
        for (int i = 0; i < lines.size(); i++) {
            if (i == line) {
                break;
            }
            pos += lines.get(i).length() + 1;
        }
        cursorPos = Math.max(0, Math.min(pos, text.length()));
    }


    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (editable) {
            if (button == 0) {
                if (x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.height) {
                    this.selected = true;
                    TextRenderer f = DFScript.MC.textRenderer;

                    x -= 1 + this.x;
                    y -= 1 + this.y + scroll;

                    x *= 2;
                    y *= 2;

                    int line = (int) (y / f.fontHeight);
                    int pixelX = (int) (x);
                    line = Math.max(0, Math.min(line, getLines().length - 1));
                    int lineIndex = f.trimToWidth(getLine(line), pixelX, true).length();
                    setCursor(line, lineIndex);

                    if (hasSelection) {
                        hasSelection = false;
                    }
                    //return true;
                }
                else
                {
                    this.selected = false;
                }
            }
        }
        else
        {
            this.selected = false;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double vertical, double horizontal) {
        if (!editable || !selected) {
            return false;
        }

        TextRenderer f = DFScript.MC.textRenderer;

        int maxScroll = 0;

        for (String line : getLines()) {
            int lineWidth = f.getWidth(line);

            if(maxScroll < lineWidth) {
                maxScroll = lineWidth;
            }
        }

        maxScroll /= 2;
        maxScroll -= width - 2;

        if(xScrolling)
        {
            double temp = vertical;
            vertical = horizontal;
            horizontal = temp;
        }

        scroll += vertical * 5;
        scroll = Math.min(0, Math.max(scroll, -(getLines().length + 1) * f.fontHeight / 2 + height - 2));
        xScroll += horizontal * 5;
        xScroll = Math.min(0, Math.max(xScroll, -maxScroll));

        return true;
    }

    public int getCursorLineIndex() {
        return text.substring(0, cursorPos).split("\n", -1).length - 1;
    }

    public String getCursorLine() {
        return getLines()[getCursorLineIndex()];
    }

    public int getIndexInCursorLine() {
        String[] lines = text.substring(0, cursorPos).split("\n", -1);
        return lines[lines.length - 1].length();
    }

    private String[] getLines() {
        return text.split("\n", -1);
    }

    public void setLine(int line, String text) {
        String[] lines = getLines();
        lines[line] = text;
        this.text = String.join("\n", lines);
    }

    private String getLine(int line) {
        return getLines()[line];
    }

    public void setChangedListener(Runnable r) {
        changedListener = r;
    }

    public String getText() {
        return text;
    }

    public void setText(String value) {
        text = value;
    }
}