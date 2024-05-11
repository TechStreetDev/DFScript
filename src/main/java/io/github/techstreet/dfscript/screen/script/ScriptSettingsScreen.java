package io.github.techstreet.dfscript.screen.script;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.dfscript.ScriptListScreen;
import io.github.techstreet.dfscript.screen.widget.*;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptManager;
import io.github.techstreet.dfscript.script.options.ScriptNamedOption;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ScriptSettingsScreen extends CScreen {
    private final Script script;

    private static int scroll = 0;

    private boolean owned;

    private final CScrollPanel panel;

    private final List<CWidget> contextMenu = new ArrayList<>();

    public ScriptSettingsScreen(Script script, boolean owned) {
        super(125, 100);
        this.script = script;
        this.owned = owned;
        panel = new CScrollPanel(0, 3, 120, 94);

        widgets.add(panel);

        reloadMenu();

        panel.setScroll(scroll);
    }

    public void reloadMenu() {
        clearContextMenu();
        panel.clear();

        int y = 3;
        int index = 0;

        String descriptionString = owned ? "Description:" : script.getDescription();

        Text descriptionText = Text.of(descriptionString);

        CWrappedText descriptionCText = new CWrappedText(5, y, 110*2, descriptionText);
        panel.add(descriptionCText);

        y += DFScript.MC.textRenderer.getWrappedLinesHeight(descriptionString, 110*2) / 2;
        y++;

        if(owned) {
            CTextField description = new CTextField(script.getDescription(), 5, y, 110, 20, true);
            description.setChangedListener(() -> script.setDescription(description.getText()));
            panel.add(description);

            y += 22;
        }

        for(ScriptNamedOption option : script.getOptions())
        {
            String name = option.getFullName() + ":";

            Text text = Text.of(name);

            CWrappedText ctext = new CWrappedText(5, y, 110*2, text);

            panel.add(ctext);

            int height = DFScript.MC.textRenderer.getWrappedLinesHeight(name, 110*2) / 2;
            int finalIndex = index;
            if(owned) panel.add(new CButton(5, y, 115, height, "",() -> {}) {
                @Override
                public void render(DrawContext context, int mouseX, int mouseY, float tickDelta) {
                    Rectangle b = getBounds();
                    if (b.contains(mouseX, mouseY)) {
                        context.fill(b.x, b.y, b.x + b.width, b.y + b.height, 0x33000000);
                    }
                }

                @Override
                public boolean mouseClicked(double x, double y, int button) {
                    if (getBounds().contains(x, y)) {
                        DFScript.MC.getSoundManager().play(PositionedSoundInstance.ambient(SoundEvents.UI_BUTTON_CLICK.value(), 1f,1f));

                        if (button != 0) {
                            CButton insertBefore = new CButton((int) x, (int) y, 40, 8, "Insert Before", () -> {
                                DFScript.MC.setScreen(new ScriptAddSettingScreen(script, finalIndex));
                            });
                            CButton insertAfter = new CButton((int) x, (int) y+8, 40, 8, "Insert After", () -> {
                                DFScript.MC.setScreen(new ScriptAddSettingScreen(script, finalIndex + 1));
                            });
                            CButton delete = new CButton((int) x, (int) y+16, 40, 8, "Delete", () -> {
                                script.removeOption(script.getOptions().get(finalIndex).getName());
                                script.getOptions().remove(finalIndex);
                                scroll = panel.getScroll();
                                DFScript.MC.setScreen(new ScriptSettingsScreen(script, true));
                            });
                            newContextMenu(new CButton[]{insertBefore, insertAfter, delete});
                        }
                        else
                        {
                            DFScript.MC.setScreen(new ScriptEditSettingScreen(script, option));
                        }
                        return true;
                    }
                    return false;
                }
            });

            y += height;
            y++;

            y = option.create(panel, 5, y);

            index++;
        }

        if(owned)
        {
            CButton add = new CButton(37, y, 48, 9, "Add Option", () -> {
                DFScript.MC.setScreen(new ScriptAddSettingScreen(script, script.getOptions().size()));
            });
            panel.add(add);
        }
    }

    @Override
    public void close() {
        ScriptManager.getInstance().saveScript(script);
        DFScript.MC.setScreen(new ScriptListScreen(true));
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean b = super.mouseClicked(mouseX, mouseY, button);
        clearContextMenu();
        return b;
    }

    public void newContextMenu(CButton[] buttons) {
        clearContextMenu();

        DFScript.MC.send(() -> {
            for(CButton button : buttons) {
                panel.add(button);
                contextMenu.add(button);
            }
        });
    }
    private void clearContextMenu() {
        for (CWidget w : contextMenu) {
            panel.remove(w);
        }
        contextMenu.clear();
    }
}