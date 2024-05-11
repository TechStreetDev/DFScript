package io.github.techstreet.dfscript.screen.dfscript;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.features.AuthHandler;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.widget.*;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptManager;
import io.github.techstreet.dfscript.util.chat.ChatUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ScriptListScreen extends CScreen {
    private final List<CWidget> contextMenu = new ArrayList<>();


    public ScriptListScreen(boolean allowEditAndUpload) {
        super(160, 100);
        CScrollPanel panel = new CScrollPanel(0, 5, 160, 94);
        widgets.add(panel);

        int y = 0;
        for (Script s : ScriptManager.getInstance().getScripts()) {
            MutableText text = Text.literal(s.getName());

            if (s.disabled()) {
                text = text.formatted(Formatting.STRIKETHROUGH);
            }

            panel.add(new CText(6, y + 2, text));

            panel.add(new CButton(4, y-1, 153, 10, "",() -> {}) {
                @Override
                public void render(DrawContext context, int mouseX, int mouseY, float tickDelta) {
                    Rectangle b = getBounds();
                    context.fill(b.x, b.y, b.x + b.width, b.y + b.height, 0x33000000);
                }

                @Override
                public boolean mouseClicked(double x, double y, int button) {
                    return false;
                }
            });

            int addedY = 0;
            int addedX = 118;

            // Enable or Disable Button
            CButton enableDisable;
            if (s.disabled()) {
                enableDisable = new CTexturedButton(30 + addedX, y + addedY, 8, 8, DFScript.MOD_ID + ":enable.png", DFScript.MOD_ID + ":enable_highlight.png", () -> {
                    s.setDisabled(false);
                    ScriptManager.getInstance().saveScript(s);
                    DFScript.MC.setScreen(new ScriptListScreen(allowEditAndUpload));
                });
            } else {
                enableDisable = new CTexturedButton(30 + addedX, y + addedY, 8, 8, DFScript.MOD_ID + ":disable.png", DFScript.MOD_ID + ":disable_highlight.png", () -> {
                    s.setDisabled(true);
                    ScriptManager.getInstance().saveScript(s);
                    DFScript.MC.setScreen(new ScriptListScreen(allowEditAndUpload));
                });
            }

            panel.add(enableDisable);
            y += 12;
        }

        CButton refresh = new CButton(60, y + 1, 40, 8, "Refresh", () -> {
            AuthHandler.checkAuth();
            AuthHandler.updateScripts();
            ChatUtil.info("Scripts updated and reloaded!");
            DFScript.MC.setScreen(new ScriptListScreen(allowEditAndUpload));
        });

        panel.add(refresh);
    }

    @Override
    public void close() {
        DFScript.MC.setScreen(new DFScriptScreen());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean b = super.mouseClicked(mouseX, mouseY, button);
        clearContextMenu();
        return b;
    }

    private void clearContextMenu() {
        for (CWidget w : contextMenu) {
            widgets.remove(w);
        }
        contextMenu.clear();
    }
}
