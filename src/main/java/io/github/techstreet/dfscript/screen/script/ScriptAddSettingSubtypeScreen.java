package io.github.techstreet.dfscript.screen.script;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.widget.CItem;
import io.github.techstreet.dfscript.screen.widget.CTexturedButton;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.options.ScriptNamedOption;
import io.github.techstreet.dfscript.script.options.ScriptOption;
import io.github.techstreet.dfscript.script.options.ScriptOptionEnum;
import io.github.techstreet.dfscript.util.chat.ChatUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ScriptAddSettingSubtypeScreen extends CScreen {
    private int width;
    private int height;
    private static final List<ScriptOptionEnum> primitiveTypes;

    private final Script script;
    private final ScriptOptionEnum option;
    private final int pos;

    private int optionPos;

    private List<ScriptOptionEnum> subtypes;

    static {
        primitiveTypes = Arrays.stream(ScriptOptionEnum.values()).filter(c -> c.getExtraTypes() == 0).toList();
    }

    public ScriptAddSettingSubtypeScreen(Script script, ScriptOptionEnum option, int pos) {
        super(calculateWidth(option.getExtraTypes()),calculateHeight(option.getExtraTypes()));
        width = calculateWidth(option.getExtraTypes());
        height = calculateHeight(option.getExtraTypes());

        this.script = script;
        this.option = option;
        this.pos = pos;

        optionPos = 0;

        subtypes = new ArrayList<>(option.getExtraTypes());
        for(int i = 0; i < option.getExtraTypes(); i++) subtypes.add(null);

        reloadMenu();
    }

    private static int calculateWidth(int extraTypes) {
        return (int) Math.max((Math.ceil(Math.sqrt(primitiveTypes.size())) * 10)+4, 2+8+4+extraTypes*10+2+8+2);
    }

    private static int calculateHeight(int extraTypes) {
        return (int) Math.ceil(primitiveTypes.size()/((calculateWidth(extraTypes)-2)/10f))*10+4+12;
    }

    public void reloadMenu()
    {
        widgets.clear();

        int x = 3;
        int y = 3;

        CItem citem = new CItem(x, y, option.getIcon());

        widgets.add(citem);

        x += 12;

        int i = 0;

        boolean noNull = true;

        for(ScriptOptionEnum o : subtypes) {
            ItemStack icon;

            if(o == null) {
                noNull = false;
                icon = new ItemStack(Items.BARRIER).setCustomName(Text.of("None"));
            }
            else {
                icon = o.getIcon();
            }

            if(optionPos == i) citem = new CItem(x, y, icon) {
                @Override
                public void render(DrawContext context, int mouseX, int mouseY, float tickDelta) {
                    super.render(context, mouseX, mouseY, tickDelta);
                    Rectangle b = getBounds();
                    context.fill(b.x, b.y, b.x + b.width, b.y + b.height, 0x3300ff00);
                }
            };
            else citem = new CItem(x, y, icon);

            int finalI = i;
            citem.setClickListener((a) -> {
                optionPos = finalI;

                reloadMenu();
            });

            widgets.add(citem);

            x += 10;
            i++;
        }

        if(noNull) {
            CTexturedButton button = new CTexturedButton(width - 10, 3, 8, 8, DFScript.MOD_ID + ":on_button.png", DFScript.MOD_ID + ":on_button_highlight.png", () -> {
                try {
                    script.addOption(pos, new ScriptNamedOption(ScriptOption.instantiate(option, subtypes), script.getUnnamedOption()));
                } catch (Exception e) {
                    ChatUtil.error(String.valueOf(e.getCause()));
                }

                DFScript.MC.setScreen(new ScriptSettingsScreen(script, true));
            });

            widgets.add(button);
        }

        x = 3;
        y = 3+12;

        for(ScriptOptionEnum o : primitiveTypes) {
            citem = new CItem(x, y, o.getIcon());

            citem.setClickListener((a) -> {
                if(optionPos < subtypes.size()-1 && subtypes.get(optionPos) == null) {
                    subtypes.set(optionPos, o);
                    optionPos++;
                }
                else
                {
                    subtypes.set(optionPos, o);
                }

                reloadMenu();
            });

            widgets.add(citem);

            x += 10;
            if (x >= width - 10) {
                x = 3;
                y += 10;
            }
        }
    }

    @Override
    public void close() {
        DFScript.MC.setScreen(new ScriptAddSettingScreen(script, pos));
    }
}
