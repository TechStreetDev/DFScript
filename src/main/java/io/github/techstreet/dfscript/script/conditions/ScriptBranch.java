package io.github.techstreet.dfscript.script.conditions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.techstreet.dfscript.screen.ContextMenuButton;
import io.github.techstreet.dfscript.script.*;
import io.github.techstreet.dfscript.script.argument.ScriptArgument;
import io.github.techstreet.dfscript.script.execution.ScriptActionContext;
import io.github.techstreet.dfscript.script.execution.ScriptTask;
import io.github.techstreet.dfscript.script.render.ScriptPartRender;
import io.github.techstreet.dfscript.script.render.ScriptPartRenderIconElement;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ScriptBranch extends ScriptParametrizedPart implements ScriptScopeParent {

    public static String closeBracketName = "Close Bracket";
    public static ItemStack closeBracketIcon = new ItemStack(Items.PISTON);
    static String elseName = "Else";
    static ItemStack elseIcon = new ItemStack(Items.END_STONE);

    static {
        closeBracketIcon.setCustomName(Text.literal(closeBracketName)
                .fillStyle(Style.EMPTY
                        .withColor(Formatting.WHITE)
                        .withItalic(false)));

        NbtList lore = new NbtList();

        lore.add(NbtString.of(Text.Serialization.toJsonString(Text.literal("Closes the current code block.").setStyle(Style.EMPTY.withColor(Formatting.GRAY).withItalic(false)))));

        closeBracketIcon.getSubNbt("display")
                .put("Lore", lore);

        elseIcon.setCustomName(Text.literal(elseName)
                .fillStyle(Style.EMPTY
                        .withColor(Formatting.WHITE)
                        .withItalic(false)));

        lore = new NbtList();

        lore.add(NbtString.of(Text.Serialization.toJsonString(Text.literal("Executes if the last IF condition failed.").setStyle(Style.EMPTY.withColor(Formatting.GRAY).withItalic(false)))));

        elseIcon.getSubNbt("display")
                .put("Lore", lore);
    }
    boolean hasElse = false;

    ScriptCondition condition;
    ScriptContainer container;

    public ScriptBranch(List<ScriptArgument> arguments, ScriptCondition condition) {
        super(arguments);
        this.condition = condition;
        container = new ScriptContainer(2);
    }

    @Override
    public void create(ScriptPartRender render, Script script) {
        condition.create(render, script, "If", "Unless");

        render.addElement(container.createSnippet(0));

        render.addElement(new ScriptPartRenderIconElement(closeBracketName, closeBracketIcon));

        if(hasElse)
        {
            render.addElement(new ScriptPartRenderIconElement(elseName, elseIcon));

            render.addElement(container.createSnippet(1));

            render.addElement(new ScriptPartRenderIconElement(closeBracketName, closeBracketIcon));
        }
    }

    public ScriptBranch setHasElse() {
        hasElse = !hasElse;
        return this;
    }

    public boolean hasElse() {
        return hasElse;
    }

    @Override
    public void run(ScriptTask task) {
        ScriptActionContext actionCtx = new ScriptActionContext(task, getArguments());
        boolean result = condition.run(actionCtx);

        if(!result && !hasElse) return;

        container.runSnippet(task, result ? 0 : 1, this);
    }

    public ScriptCondition getCondition() {
        return condition;
    }

    @Override
    public void forEach(Consumer<ScriptSnippet> consumer) {
        container.forEach(consumer);
    }

    @Override
    public ScriptContainer container() {
        return container;
    }

    @Override
    public List<ContextMenuButton> getContextMenu() {
        List<ContextMenuButton> extra = new ArrayList<>();
        extra.add(new ContextMenuButton("Invert", () -> condition.invert()));
        extra.add(new ContextMenuButton(!hasElse ? "Add Else Statement" : "Remove Else Statement", this::setHasElse));
        return extra;
    }

    @Override
    public ItemStack getIcon() {
        return condition.getIcon("If", "Unless");
    }

    @Override
    public String getName() {
        return condition.getName("If", "Unless");
    }

    public static class Serializer implements JsonSerializer<ScriptBranch> {

        @Override
        public JsonElement serialize(ScriptBranch src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "branch");
            obj.add("condition", context.serialize(src.condition));
            obj.add("arguments", context.serialize(src.getArguments()));
            obj.addProperty("hasElse", src.hasElse);
            obj.add("true", context.serialize(src.container().getSnippet(0)));
            obj.add("false", context.serialize(src.container().getSnippet(1)));
            return obj;
        }
    }
}
