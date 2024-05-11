package io.github.techstreet.dfscript.commands.misc;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.commands.Command;
import io.github.techstreet.dfscript.commands.arguments.StringFuncArgumentType;
import io.github.techstreet.dfscript.event.system.EventManager;
import io.github.techstreet.dfscript.features.AuthHandler;
import io.github.techstreet.dfscript.screen.dfscript.DFScriptScreen;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptManager;
import io.github.techstreet.dfscript.script.values.ScriptVariable;
import io.github.techstreet.dfscript.util.chat.ChatUtil;
import java.util.List;
import java.util.Map.Entry;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import static io.github.techstreet.dfscript.commands.arguments.StringFuncArgumentFunctions.SCRIPTS;

public class DFScriptCommand implements Command {

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> cd) {
        cd.register(
            literal("dfscript")
                .executes(ctx -> {
                    DFScript.MC.send(() -> DFScript.MC.setScreen(new DFScriptScreen()));
                    return 0;
                })
                .then(literal("reload")
                        .executes(ctx -> {
                            AuthHandler.updateScripts();
                            ChatUtil.info("Scripts updated and reloaded!");
                            return 0;
                        })
                )
                .then(literal("vars")
                    .then(argument("script", new StringFuncArgumentType(SCRIPTS, false)
                        )
                        .executes(ctx -> {
                            listVars(ctx.getArgument("script", String.class), "");
                            return 0;
                        })
                        .then(argument("filter", StringArgumentType.greedyString())
                            .executes(ctx -> {
                                listVars(ctx.getArgument("script", String.class), ctx.getArgument("filter", String.class));
                                return 0;
                            })
                        )
                    )
                )
                .then(literal("recursion")
                    .then(argument("limit", IntegerArgumentType.integer(-1)).executes(ctx -> {
                        int limit = IntegerArgumentType.getInteger(ctx,"limit");
                        EventManager.getInstance().setEventLimit(limit);
                        ChatUtil.info("Set events per tick limit to " + limit);
                        return 0;
                    }))
                )
        );
    }

    private void listVars(String script, String filter) {
        for (Script s : ScriptManager.getInstance().getScripts()) {
            if (s.getName().replaceAll(" ", "_").equals(script)) {
                List<Entry<String, ScriptVariable>> vars = s.getContext().variables().list(filter);

                int showing = Math.min(vars.size(), 50);
                int filtered = vars.size();
                int total = s.getContext().variables().count();

                ChatUtil.info("Script " + s.getName() + " has a total of " + total + " variables.");

                if (filter.isEmpty()) {
                    ChatUtil.info("Showing " + showing + " variables.");
                } else {
                    ChatUtil.info("Showing " + showing + " of " + filtered + " filtered variables.");
                }

                for (int i = 0; i < showing; i++) {
                    Entry<String, ScriptVariable> e = vars.get(i);
                    ChatUtil.info(e.getKey() + ": " + e.getValue().asText());
                }
                return;
            }
        }
        ChatUtil.error("Unknown script!");
    }

    @Override
    public String getDescription() {
        return """
                [blue]/dfscript[reset]

                Opens a GUI to edit custom DFScript scripts.
                """;
    }

    @Override
    public String getName() {
        return "/dfscript";
    }
}
