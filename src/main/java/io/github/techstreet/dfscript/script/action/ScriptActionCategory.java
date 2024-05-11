package io.github.techstreet.dfscript.script.action;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.script.ScriptConditionCategoryScreen;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptComment;
import io.github.techstreet.dfscript.script.conditions.ScriptBooleanSet;
import io.github.techstreet.dfscript.script.event.ScriptFunction;
import io.github.techstreet.dfscript.script.repetitions.ScriptWhile;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public enum ScriptActionCategory {

    VISUALS("Visuals", Items.ENDER_EYE),
    ACTIONS("Actions", Items.PLAYER_HEAD),
    MISC("Misc", Items.COMPASS, List.of(
            new ScriptActionCategoryExtraPartCreator(new ItemStack(Items.MAP).setCustomName(Text.literal("Comment").setStyle(Style.EMPTY.withItalic(false))), () -> new ScriptComment(""))
    )),
    VARIABLES("Variables", Items.IRON_INGOT),
    CONDITIONS("Conditions", Items.LEVER, List.of(
            new ScriptActionCategoryExtra(ScriptBooleanSet.booleanSetIcon, (sc, sn, ii) -> DFScript.MC.setScreen(new ScriptConditionCategoryScreen(sc, sn, ii, (con) -> new ScriptBooleanSet(new ArrayList<>(), con)))),
            new ScriptActionCategoryExtra(ScriptWhile.whileIcon, (sc, sn, ii) -> DFScript.MC.setScreen(new ScriptConditionCategoryScreen(sc, sn, ii, (con) -> new ScriptWhile(new ArrayList<>(), con))))
    )),
    NUMBERS("Numbers", Items.SLIME_BALL),
    LISTS("Lists", Items.BOOKSHELF),
    TEXTS("Texts", Items.BOOK),
    DICTIONARIES("Dictionaries", Items.ENDER_CHEST),

    MENUS("Menus", Items.PAINTING),

    CONTROL("Control", Items.COAL),

    FUNCTIONS("Functions", Items.LAPIS_LAZULI, (script) -> {
        List<ScriptActionCategoryExtra> extras = new ArrayList<>();

        for (ScriptFunction function : script.getFunctions()) {
            extras.add(new ScriptActionCategoryExtraPartCreator(
                    function.getIcon(),
                    () -> new ScriptFunctionCall(script, function.getName(), new ArrayList<>())
            ));
        }

        return extras;
    });

    private final ItemStack icon;

    private Function<Script, List<ScriptActionCategoryExtra>> extras;

    ScriptActionCategory(String name, Item icon) {
        this.icon = new ItemStack(icon);
        this.icon.setCustomName(Text.literal(name).fillStyle(Style.EMPTY.withItalic(false)));
    }

    ScriptActionCategory(String name, Item icon, List<ScriptActionCategoryExtra> extras) {
        this.icon = new ItemStack(icon);
        this.icon.setCustomName(Text.literal(name).fillStyle(Style.EMPTY.withItalic(false)));
        this.extras = (script) -> extras;
    }

    ScriptActionCategory(String name, Item icon, Function<Script, List<ScriptActionCategoryExtra>> extras) {
        this.icon = new ItemStack(icon);
        this.icon.setCustomName(Text.literal(name).fillStyle(Style.EMPTY.withItalic(false)));
        this.extras = extras;
    }

    public ItemStack getIcon() {
        return icon;
    }
    public List<ScriptActionCategoryExtra> getExtras(Script script)
    {
        if(extras == null) return List.of();

        return extras.apply(script);
    }
}
