package io.github.techstreet.dfscript.screen.script;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.widget.CItem;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptPart;
import io.github.techstreet.dfscript.script.ScriptSnippet;
import io.github.techstreet.dfscript.script.action.ScriptActionCategory;
import io.github.techstreet.dfscript.script.conditions.ScriptBuiltinCondition;
import io.github.techstreet.dfscript.script.conditions.ScriptCondition;
import io.github.techstreet.dfscript.script.conditions.ScriptConditionType;

import java.util.function.Consumer;
import java.util.function.Function;

public class ScriptConditionCategoryScreen extends CScreen {
    private static final int size;

    static {
        size = (int) (Math.ceil(Math.sqrt(ScriptActionCategory.values().length)) * 10)+4;
    }

    private final Script script;

    private final ScriptSnippet snippet;

    private final int insertIndex;

    private final Function<ScriptCondition, ScriptPart> partCreator;

    public ScriptConditionCategoryScreen(Script script, ScriptSnippet snippet, int insertIndex, Function<ScriptCondition, ScriptPart> partCreator) {
        super(size, size);
        this.script = script;
        this.snippet = snippet;
        this.insertIndex = insertIndex;
        this.partCreator = partCreator;

        int x = 3;
        int y = 3;

        for (ScriptActionCategory category : ScriptActionCategory.values()) {
            CItem actionItem = new CItem(x, y, category.getIcon());
            widgets.add(actionItem);

            actionItem.setClickListener(btn -> DFScript.MC.setScreen(new ScriptConditionSelectScreen(script, snippet, insertIndex, partCreator, category)));

            x += 10;
            if (x >= size - 10) {
                x = 3;
                y += 10;
            }
        }
    }

    @Override
    public void close() {

    }
}
