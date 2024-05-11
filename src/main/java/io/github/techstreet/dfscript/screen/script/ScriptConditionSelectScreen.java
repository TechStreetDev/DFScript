package io.github.techstreet.dfscript.screen.script;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.widget.CItem;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptPart;
import io.github.techstreet.dfscript.script.ScriptSnippet;
import io.github.techstreet.dfscript.script.action.*;
import io.github.techstreet.dfscript.script.conditions.*;
import io.github.techstreet.dfscript.script.repetitions.ScriptBuiltinRepetition;
import io.github.techstreet.dfscript.script.repetitions.ScriptRepetitionType;

import java.util.ArrayList;
import java.util.function.Function;

public class ScriptConditionSelectScreen extends CScreen {
    private final Script script;

    private final ScriptSnippet snippet;

    private final int insertIndex;

    private final Function<ScriptCondition, ScriptPart> partCreator;

    public ScriptConditionSelectScreen(Script script, ScriptSnippet snippet, int insertIndex, Function<ScriptCondition, ScriptPart> partCreator, ScriptActionCategory category) {
        super(size(category), size(category));
        int size = size(category);
        this.script = script;
        this.partCreator = partCreator;
        this.insertIndex = insertIndex;
        this.snippet = snippet;

        int x = 3;
        int y = 3;

        for (ScriptConditionType type : ScriptConditionType.values()) {
            if (type.getCategory() != category) continue;
            if (type.isDeprecated()) continue;

            CItem item = new CItem(x, y, type.getIcon(""));
            item.setClickListener((btn) -> {
                snippet.add(insertIndex, partCreator.apply(new ScriptBuiltinCondition(type)));
            });
            widgets.add(item);
            x += 10;
            if (x >= size-10) {
                x = 3;
                y += 10;
            }
        }
    }

    private static int size(ScriptActionCategory category) {
        int amount = 0;
        for (ScriptConditionType type : ScriptConditionType.values()) {
            if (type.getCategory() == category) {
                amount++;
            }
        }
        return (int) (Math.ceil(Math.sqrt(amount))*10)+4;
    }

    @Override
    public void close() {
        DFScript.MC.setScreen(new ScriptConditionCategoryScreen(script, snippet, insertIndex, partCreator));
    }
}
