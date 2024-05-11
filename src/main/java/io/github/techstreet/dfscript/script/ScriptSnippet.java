package io.github.techstreet.dfscript.script;

import com.google.gson.*;
import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.ContextMenuButton;
import io.github.techstreet.dfscript.screen.widget.CButton;
import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.screen.widget.CText;
import io.github.techstreet.dfscript.script.action.ScriptActionType;
import io.github.techstreet.dfscript.script.action.ScriptBuiltinAction;
import io.github.techstreet.dfscript.script.action.ScriptFunctionCall;
import io.github.techstreet.dfscript.script.conditions.ScriptBranch;
import io.github.techstreet.dfscript.script.conditions.ScriptBuiltinCondition;
import io.github.techstreet.dfscript.script.conditions.ScriptConditionType;
import io.github.techstreet.dfscript.script.event.ScriptHeader;
import io.github.techstreet.dfscript.script.execution.ScriptActionContext;
import io.github.techstreet.dfscript.script.execution.ScriptTask;
import io.github.techstreet.dfscript.script.render.ScriptPartRender;
import io.github.techstreet.dfscript.script.repetitions.ScriptBuiltinRepetition;
import io.github.techstreet.dfscript.script.repetitions.ScriptRepetitionType;
import io.github.techstreet.dfscript.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.awt.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ScriptSnippet extends ArrayList<ScriptPart> {
    boolean hidden = false;
    ScriptSnippet() {

    }

    public void run(ScriptTask task, ScriptScopeParent parent, ScriptActionContext context)
    {
        task.stack().push(this, parent, context);
    }

    public int create(CScrollPanel panel, int y, int indent, Script script, ScriptHeader header) {
        ScriptSnippet thisSnippet = this;
        panel.add(new CButton(3, y, 2, 8, "", () -> {
            thisSnippet.hidden = !thisSnippet.hidden;
        }) {
            @Override
            public void render(DrawContext context, int mouseX, int mouseY, float tickDelta) {
                MatrixStack stack = context.getMatrices();
                Rectangle b = getBounds();

                int color = 0xFF323232;

                if(b.contains(mouseX, mouseY)) {
                    color = 0xFF707070;
                }

                if(thisSnippet.hidden) {
                    RenderUtil.renderLine(stack, b.x, b.y, b.x+b.width, b.y+(b.height/2f), color, 0.5f);
                    RenderUtil.renderLine(stack, b.x, b.y+b.height, b.x+b.width, b.y+(b.height/2f), color, 0.5f);
                }
                else {
                    RenderUtil.renderLine(stack, b.x, b.y, b.x+(b.width/2f), b.y+b.height, color, 0.5f);
                    RenderUtil.renderLine(stack, b.x+b.width, b.y, b.x+(b.width/2f), b.y+b.height, color, 0.5f);
                }
            }
        });

        if(hidden) {
            ScriptPartRender.createIndent(panel, indent, y, 8);

            panel.add(new CText(15 + indent * 5, y + 2, Text.literal("...")));

            return y+10;
        }

        int index = 0;

        for(ScriptPart part : this) {
            ScriptPartRender render = new ScriptPartRender();
            part.create(render, script);
            y = render.create(panel, y, indent, script, header);
            int currentIndex = index;
            for (var buttonPos : render.getButtonPositions()) {
                panel.add(new CButton(5, buttonPos.getY() - 1, 115, buttonPos.height(), "", () -> {
                }) {
                    @Override
                    public void render(DrawContext context, int mouseX, int mouseY, float tickDelta) {
                        Rectangle b = getBounds();
                        int color = 0;
                        boolean drawFill = false;
                        if (b.contains(mouseX, mouseY)) {
                            drawFill = true;
                            color = 0x33000000;

                            if (part.isDeprecated()) {
                                color = 0x80FF0000;
                            }


                        } else {
                            if (part.isDeprecated()) {
                                drawFill = true;
                                color = 0x33FF0000;
                            }
                        }

                        if(drawFill) {
                            for(var renderButtonPos : render.getButtonPositions()) {
                                context.fill(b.x, renderButtonPos.y()-1, b.x + b.width, renderButtonPos.y()-1 + renderButtonPos.height(), color);
                            }
                        }
                    }

                    @Override
                    public boolean mouseClicked(double x, double y, int button) {
                        if (getBounds().contains(x, y)) {
                            DFScript.MC.getSoundManager().play(PositionedSoundInstance.ambient(SoundEvents.UI_BUTTON_CLICK.value(), 1f, 1f));

                            return true;
                        }
                        return false;
                    }
                });
            }
            index++;
        }

        return y+10;
    }

    public void replaceAction(ScriptActionType oldAction, ScriptActionType newAction) {
        for(ScriptPart part : this) {
            if(part instanceof ScriptBuiltinAction a) {
                if(a.getType() == oldAction) {
                    a.setType(newAction);
                }
            }
            if(part instanceof ScriptScopeParent p) {
                p.forEach((snippet) -> snippet.replaceAction(oldAction, newAction));
            }
        }
    }

    public void replaceCondition(ScriptConditionType oldCondition, ScriptConditionType newCondition) {
        for(ScriptPart part : this) {
            if(part instanceof ScriptBranch b) {
                if(b.getCondition() instanceof ScriptBuiltinCondition c) {
                    if(c.getType() == oldCondition) {
                        c.setType(newCondition);
                    }
                }
            }
            if(part instanceof ScriptScopeParent p) {
                p.forEach((snippet) -> snippet.replaceCondition(oldCondition, newCondition));
            }
        }
    }

    public void replaceRepetition(ScriptRepetitionType oldRepetition, ScriptRepetitionType newRepetition) {
        for(ScriptPart part : this) {
            if(part instanceof ScriptBuiltinRepetition r) {
                if(r.getType() == oldRepetition) {
                    r.setType(newRepetition);
                }
            }
            if(part instanceof ScriptScopeParent p) {
                p.forEach((snippet) -> snippet.replaceRepetition(oldRepetition, newRepetition));
            }
        }
    }

    public void updateScriptReferences(Script script, ScriptHeader header) {
        for(ScriptPart part : this) {
            if(part instanceof ScriptParametrizedPart p) {
                p.updateScriptReferences(script, header);
            }
            if(part instanceof ScriptScopeParent p) {
                p.forEach((snippet) -> snippet.updateScriptReferences(script, header));
            }
        }
    }

    public void replaceOption(String oldOption, String newOption) {
        for(ScriptPart part : this) {
            if(part instanceof ScriptParametrizedPart p) {
                p.updateConfigArguments(oldOption, newOption);
            }
            if(part instanceof ScriptScopeParent p) {
                p.forEach((snippet) -> snippet.replaceOption(oldOption, newOption));
            }
        }
    }

    public void removeOption(String option) {
        for(ScriptPart part : this) {
            if(part instanceof ScriptParametrizedPart p) {
                p.removeConfigArguments(option);
            }
            if(part instanceof ScriptScopeParent p) {
                p.forEach((snippet) -> snippet.removeOption(option));
            }
        }
    }

    public void replaceFunction(String oldFunction, String newFunction) {
        for(ScriptPart part : this) {
            if(part instanceof ScriptFunctionCall fc) {
                if(fc.getFunctionName() == oldFunction) {
                    fc.setFunction(newFunction);
                }
            }
            if(part instanceof ScriptScopeParent p) {
                p.forEach((snippet) -> snippet.replaceFunction(oldFunction, newFunction));
            }
        }
    }

    public void removeFunction(String function) {
        int index = 0;

        while(index < this.size()) {
            ScriptPart part = this.get(index);
            if(part instanceof ScriptFunctionCall fc) {
                if(fc.getFunctionName() == function) {
                    this.remove(index);
                    continue;
                }
            }
            if(part instanceof ScriptScopeParent p) {
                p.forEach((snippet) -> snippet.removeFunction(function));
            }
            index++;
        }
    }

    public void replaceFunctionArgument(String oldArg, String newArg) {
        for(ScriptPart part : this) {
            if(part instanceof ScriptParametrizedPart p) {
                p.replaceFunctionArgument(oldArg, newArg);
            }
            if(part instanceof ScriptScopeParent p) {
                p.forEach((snippet) -> snippet.replaceFunctionArgument(oldArg, newArg));
            }
        }
    }

    public void removeFunctionArgument(String arg) {
        for(ScriptPart part : this) {
            if(part instanceof ScriptParametrizedPart p) {
                p.removeFunctionArgument(arg);
            }
            if(part instanceof ScriptScopeParent p) {
                p.forEach((snippet) -> snippet.removeFunctionArgument(arg));
            }
        }
    }

    public static class Serializer implements JsonSerializer<ScriptSnippet>, JsonDeserializer<ScriptSnippet> {

        @Override
        public ScriptSnippet deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            ScriptSnippet snippet = new ScriptSnippet();

            for(JsonElement element : obj.getAsJsonArray("parts")) {
                snippet.add(context.deserialize(element, ScriptPart.class));
            }

            if(obj.has("hidden")) {
                snippet.hidden = obj.get("hidden").getAsBoolean();
            }

            return snippet;
        }

        @Override
        public JsonElement serialize(ScriptSnippet src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            JsonArray parts = new JsonArray();

            for (ScriptPart part : src) {
                parts.add(context.serialize(part));
            }

            obj.add("parts", parts);
            obj.addProperty("hidden", src.hidden);

            return obj;
        }
    }
}
