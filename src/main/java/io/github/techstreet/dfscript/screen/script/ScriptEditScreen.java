package io.github.techstreet.dfscript.screen.script;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CReloadableScreen;
import io.github.techstreet.dfscript.screen.ContextMenuButton;
import io.github.techstreet.dfscript.screen.widget.*;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptManager;

import io.github.techstreet.dfscript.script.event.ScriptFunction;
import io.github.techstreet.dfscript.script.event.ScriptHeader;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
public class ScriptEditScreen extends CReloadableScreen {
    private final Identifier identifier_main = new Identifier(DFScript.MOD_ID + ":wrench.png");

    private final Script script;
    private static int scroll = 0;

    public final static int width = 125;
    private CScrollPanel panel;
    private final List<CWidget> contextMenu = new ArrayList<>();

    public ScriptEditScreen(Script script) {
        super(width, 100);
        this.script = script;

        reload();
    }

    public void reload()
    {
        clearContextMenu();
        widgets.clear();

        if(panel != null)
        {
            scroll = panel.getScroll();
        }

        panel = new CScrollPanel(0, 3, 120, 94);
        widgets.add(panel);

        int y = 0;
        int index = 0;

        CText name = new CText(5,y+2,Text.literal(script.getName()),0,1,false,false);
        panel.add(name);

        CButton settings = new CTexturedButton(120-8, y, 8, 8, DFScript.MOD_ID + ":settings.png", DFScript.MOD_ID + ":settings_highlight.png", () -> {
            changeScreen(new ScriptSettingsScreen(this.script, true));
        });
        panel.add(settings);

        y += 10;

        for(ScriptHeader header : script.getHeaders()) {
            int origY = y;
            y = header.create(panel, y, index, script);
            int currentIndex = index;
            panel.add(new CButton(5, origY-1, 115, 10, "",() -> {}) {
                @Override
                public void render(DrawContext context, int mouseX, int mouseY, float tickDelta) {
                    Rectangle b = getBounds();

                    if (b.contains(mouseX, mouseY)) {
                        int color = 0x33000000;

                        context.fill(b.x, b.y, b.x + b.width, b.y + b.height, color);
                    }
                }

                @Override
                public boolean mouseClicked(double x, double y, int button) {
                    if (getBounds().contains(x, y)) {
                        DFScript.MC.getSoundManager().play(PositionedSoundInstance.ambient(SoundEvents.UI_BUTTON_CLICK.value(), 1f,1f));

                        if (button != 0) {
                            CButton insertBefore = new CButton((int) x, (int) y, 40, 8, "Insert Before", () -> {
                                changeScreen(new ScriptHeaderCategoryScreen(script, currentIndex));
                            });
                            CButton insertAfter = new CButton((int) x, (int) y+8, 40, 8, "Insert After", () -> {
                                changeScreen(new ScriptHeaderCategoryScreen(script, currentIndex + 1));
                            });
                            CButton delete = new CButton((int) x, (int) y+16, 40, 8, "Delete", () -> {
                                script.getHeaders().remove(currentIndex);
                                if(header instanceof ScriptFunction f) {
                                    script.removeFunction(f.getName());
                                }
                                reload();
                            });
                            DFScript.MC.send(() -> {
                                panel.add(insertBefore);
                                panel.add(insertAfter);
                                panel.add(delete);
                                contextMenu.add(insertBefore);
                                contextMenu.add(insertAfter);
                                contextMenu.add(delete);
                            });
                        }
                        else {
                            if(header instanceof ScriptFunction f) {
                                changeScreen(new ScriptEditFunctionScreen(f, script));
                            }
                        }
                        return true;
                    }
                    return false;
                }
            });
            index++;
        }

        CButton add = new CButton(37, y, 46, 8, "Add Header", () -> {
            changeScreen(new ScriptHeaderCategoryScreen(script, script.getHeaders().size()));
        });

        panel.add(add);

        panel.setScroll(scroll);
    }

    @Override
    public void changeScreen(Screen screen) {
        scroll = panel.getScroll();
        DFScript.MC.setScreen(screen);
    }

    public void createIndent(int indent, int y)
    {
        for (int i = 0; i < indent; i += 5) {
            int xpos = 8 + i;
            int ypos = y;
            panel.add(new CWidget() {
                @Override
                public void render(DrawContext context, int mouseX, int mouseY, float tickDelta) {
                    context.fill(xpos, ypos, xpos + 1, ypos + 8, 0xFF333333);
                }

                @Override
                public Rectangle getBounds() {
                    return new Rectangle(0, 0, 0, 0);
                }
            });
        }
    }

    @Override
    public void close() {
        scroll = panel.getScroll();
        ScriptManager.getInstance().saveScript(script);
        DFScript.MC.setScreen(new ScriptListScreen(true));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean b = super.mouseClicked(mouseX, mouseY, button);
        clearContextMenu();
        return b;
    }

    private void clearContextMenu() {
        for (CWidget w : contextMenu) {
            panel.remove(w);
        }
        contextMenu.clear();
    }

    public void contextMenu(int x, int y, List<ContextMenuButton> contextMenuButtons) {
        clearContextMenu();

        int maxWidth = 0;

        for(ContextMenuButton w : contextMenuButtons)
        {
            TextRenderer t = DFScript.MC.textRenderer;
            int width = t.getWidth(w.getName())/2 + 4;

            if(width > maxWidth) maxWidth = width;
        }

        for(ContextMenuButton w : contextMenuButtons)
        {
            CButton button = new CButton(x, y, maxWidth, 8, w.getName(), w.getOnClick());
            y += 8;

            panel.add(button);
            contextMenu.add(button);
        }
    }
}
