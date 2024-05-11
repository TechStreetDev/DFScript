package io.github.techstreet.dfscript.script.argument;

import com.google.gson.*;
import io.github.techstreet.dfscript.screen.ContextMenuButton;
import io.github.techstreet.dfscript.script.action.ScriptActionArgument.ScriptActionArgumentType;
import io.github.techstreet.dfscript.script.execution.ScriptTask;
import io.github.techstreet.dfscript.script.values.ScriptValue;
import io.github.techstreet.dfscript.script.values.ScriptVariable;
import io.github.techstreet.dfscript.util.ComponentUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.NbtTextContent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ScriptVariableArgument implements ScriptArgument {
    private final String name;
    private ScriptVariableScope scope;

    public ScriptVariableArgument(String name, ScriptVariableScope scope) {
        this.name = name;
        this.scope = scope;
    }

    @Override
    public ScriptValue getValue(ScriptTask task) {
        return getVariable(task);
    }

    public ScriptVariable getVariable(ScriptTask task) {
        return scope.getMap(task).getReference(name);
    }

    public boolean exists(ScriptTask task) {
        return scope.getMap(task).has(name);
    }

    @Override
    public boolean convertableTo(ScriptActionArgumentType type) {
        return ScriptActionArgumentType.VARIABLE.convertableTo(type);
    }

    public String name() {
        return name;
    }

    @Override
    public ItemStack getArgIcon() {
        ItemStack icon = new ItemStack(Items.MAGMA_CREAM).setCustomName(Text.literal("Variable").setStyle(Style.EMPTY.withColor(Formatting.WHITE).withItalic(false)));

        NbtList lore = new NbtList();

        lore.add(NbtString.of(Text.Serialization.toJsonString(scope.getName())));

        icon.getSubNbt("display").put("Lore", lore);

        return icon;
    }

    @Override
    public String getArgText() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ScriptVariableArgument) obj;
        return Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "ScriptVariableArgument[" +
                "name=" + name + ']';
    }

    @Override
    public List<ContextMenuButton> getContextMenu() {
        List<ContextMenuButton> contextMenuButtons = new ArrayList<>();

        for (ScriptVariableScope scope : ScriptVariableScope.values()) {
            contextMenuButtons.add(new ContextMenuButton(
                scope.getName().getString(),
                () -> this.scope = scope
            ));
        }

        return contextMenuButtons;
    }

    @Override
    public Text getArgIconText() {
        return scope.getShortName();
    }

    public static class Serializer implements JsonSerializer<ScriptVariableArgument>, JsonDeserializer<ScriptVariableArgument> {
        @Override
        public ScriptVariableArgument deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();

            String name = obj.get("value").getAsString();
            String scope = "SCRIPT";

            if(obj.has("scope")) scope = obj.get("scope").getAsString();



            return new ScriptVariableArgument(name, ScriptVariableScope.valueOf(scope));
        }

        @Override
        public JsonElement serialize(ScriptVariableArgument src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();

            obj.addProperty("type", "VARIABLE");
            obj.addProperty("value", src.name);
            obj.addProperty("scope", src.scope.name());

            return obj;
        }
    }
}
