package io.github.techstreet.dfscript.screen.script;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.widget.CItem;
import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptParametrizedPart;
import io.github.techstreet.dfscript.script.action.ScriptActionArgument;
import io.github.techstreet.dfscript.script.argument.ScriptFunctionArgument;
import io.github.techstreet.dfscript.script.event.ScriptFunction;
import io.github.techstreet.dfscript.script.event.ScriptHeader;

public class ScriptAddFunctionArgValueScreen extends CScreen {
    private final Script script;

    private final ScriptHeader header;
    private final ScriptParametrizedPart action;
    private final int insertIndex;

    private static final int WIDTH = 200;
    private static final int HEIGHT = 94;

    public ScriptAddFunctionArgValueScreen(ScriptParametrizedPart action, Script script, int insertIndex, ScriptHeader header, String overwrite) {
        super(WIDTH, HEIGHT);
        this.script = script;
        this.action = action;
        this.header = header;
        this.insertIndex = insertIndex;

        CScrollPanel panel = new CScrollPanel(0, 0, WIDTH, HEIGHT);

        if(header instanceof ScriptFunction f)
        {
            int x = 5;
            int y = 5;
            for (ScriptActionArgument arg : f.argList()) {
                CItem item = new CItem(x, y, arg.getIcon());
                item.setClickListener((btn) -> {
                    if(overwrite != null) action.getArguments().remove(insertIndex);
                    this.action.getArguments().add(insertIndex, new ScriptFunctionArgument(arg.name(), header));
                    DFScript.MC.setScreen(new ScriptEditPartScreen(this.action, this.script, this.header));
                });
                panel.add(item);
                x += 10;
                if (x > WIDTH-10) {
                    x = 5;
                    y += 10;
                }
            }

            widgets.add(panel);
        }
    }

    @Override
    public void close() {
        DFScript.MC.setScreen(new ScriptAddArgumentScreen(script, action, insertIndex, header));
    }
}
