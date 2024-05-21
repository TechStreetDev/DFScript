package io.github.techstreet.dfscript.screen.script;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.widget.CItem;
import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptParametrizedPart;
import io.github.techstreet.dfscript.script.argument.ScriptConfigArgument;
import io.github.techstreet.dfscript.script.event.ScriptHeader;
import io.github.techstreet.dfscript.script.options.ScriptNamedOption;

public class ScriptAddConfigValueScreen extends CScreen {
    private final Script script;

    private final ScriptHeader header;
    private final ScriptParametrizedPart action;
    private final int insertIndex;

    private static final int WIDTH = 200;
    private static final int HEIGHT = 94;

    public ScriptAddConfigValueScreen(ScriptParametrizedPart action, Script script, int insertIndex, ScriptHeader header, String overwrite) {
        super(WIDTH, HEIGHT);
        this.script = script;
        this.action = action;
        this.header = header;
        this.insertIndex = insertIndex;

        CScrollPanel panel = new CScrollPanel(0, 0, WIDTH, HEIGHT);

        int x = 5;
        int y = 5;
        for (ScriptNamedOption arg : script.getOptions()) {
            CItem item = new CItem(x, y, arg.getIcon());
            item.setClickListener((btn) -> {
                if(overwrite != null) action.getArguments().remove(insertIndex);
                this.action.getArguments().add(insertIndex, new ScriptConfigArgument(arg.getName(), this.script));
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

    @Override
    public void close() {
        DFScript.MC.setScreen(new ScriptAddArgumentScreen(script, action, insertIndex, header));
    }
}
