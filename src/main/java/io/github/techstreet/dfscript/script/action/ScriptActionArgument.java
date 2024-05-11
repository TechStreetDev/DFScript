package io.github.techstreet.dfscript.script.action;

import com.google.gson.*;
import io.github.techstreet.dfscript.script.values.*;
import io.github.techstreet.dfscript.util.chat.ChatUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.MutableText;
import net.minecraft.text.NbtTextContent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ScriptActionArgument {

    private String name;
    private final ScriptActionArgumentType type;
    private boolean optional = false;
    private ScriptValue defaultValue;
    private boolean plural = false;

    // Replicates the behaviour of optional arguments before the Function update
    private boolean rightOptional = false;

    public ScriptActionArgument(String name, ScriptActionArgumentType type) {
        this.name = name;
        this.type = type;
        defaultValue = new ScriptUnknownValue();
    }

    public String getName() {
        return name;
    }

    public boolean isOptional() {
        return optional;
    }

    public boolean isPlural() {
        return plural;
    }

    public ScriptActionArgumentType getType() {
        return type;
    }

    public ScriptActionArgument optional(boolean optional) {
        this.optional = optional;
        this.rightOptional = false;
        return this;
    }

    public ScriptActionArgument rightOptional(boolean optional) {
        this.rightOptional = optional;
        this.optional = false;
        return this;
    }

    public ScriptActionArgument plural(boolean plural) {
        this.plural = plural;
        return this;
    }

    public ScriptActionArgument defaultValue(ScriptValue value) {
        if(value instanceof ScriptVariable) return this;

        this.defaultValue = value;
        if(!optional && !rightOptional) {
            optional = true;
        }
        plural = false;
        return this;
    }

    public ScriptActionArgument defaultValue(double i) {
        return defaultValue(new ScriptNumberValue(i));
    }

    public ScriptActionArgument defaultValue(String i) {
        return defaultValue(new ScriptTextValue(i));
    }

    public String name() {
        return name;
    }

    public boolean optional() {
        return optional;
    }

    public boolean rightOptional() {
        return rightOptional;
    }

    public boolean plural() {
        return plural;
    }

    public ScriptValue defaultValue() {
        if(optional() && !plural()) {
            return defaultValue;
        }
        else {
            return new ScriptUnknownValue();
        }
    }

    public ScriptActionArgumentType type() {
        return type;
    }

    public List<Text> text() {
        MutableText t = type.text();
        if (plural) {
            t.append(Text.literal("(s)").fillStyle(Style.EMPTY.withItalic(false).withColor(Formatting.WHITE)));
        }
        if (optional) {
            t.append(Text.literal("*").fillStyle(Style.EMPTY.withItalic(true)));
        }
        if (rightOptional) {
            t.append(Text.literal("*").fillStyle(Style.EMPTY.withColor(Formatting.RED).withItalic(true)));
        }

        t.append(Text.literal(" - ").fillStyle(Style.EMPTY.withItalic(false).withColor(Formatting.GRAY)))
                .append(Text.literal(name).fillStyle(Style.EMPTY.withItalic(false).withColor(Formatting.WHITE)));

        List<Text> argText = new ArrayList<>();
        argText.add(t);

        if(!(defaultValue() instanceof ScriptUnknownValue)) {
            argText.add(Text.literal("  Default: "+defaultValue().formatAsText())
                    .fillStyle(Style.EMPTY.withItalic(false).withColor(Formatting.GRAY)));
        }

        return argText;
    }

    public void setName(String text) {
        name = text;
    }

    private ItemStack convertIcon(ItemStack icon) {
        NbtList lore = new NbtList();

        NbtCompound comp = icon.getSubNbt("display");
        if(comp != null) {
            if (comp.getList("Lore", NbtElement.STRING_TYPE) != null) {
                lore = comp.getList("Lore", NbtElement.STRING_TYPE);
            }
        }

        if(optional() || plural()) {
            lore.add(NbtString.of(Text.Serialization.toJsonString(
                    Text.literal((optional() ? (plural() ? "Optional & " : "Optional") : "")
                                    + (plural() ? "Plural" : ""))
                            .fillStyle(Style.EMPTY.withItalic(false).withColor(Formatting.GRAY))
            )));
        }

        if(!(defaultValue() instanceof ScriptUnknownValue)) {
            lore.add(NbtString.of(Text.Serialization.toJsonString(
                    Text.literal("Default: "+defaultValue().formatAsText())
                            .fillStyle(Style.EMPTY.withItalic(false).withColor(Formatting.GRAY))
            )));
        }

        if(comp != null) {
            comp.put("Lore", lore);
        }

        return icon;
    }
    public ItemStack getIcon() {
        ItemStack icon = type().icon(name);

        return convertIcon(icon);
    }

    public ItemStack getUnnamedIcon() {
        ItemStack icon = type().icon();

        return convertIcon(icon);
    }

    public enum ScriptActionArgumentType {
        VARIABLE("Variable", Items.MAGMA_CREAM, null, false),
        NUMBER("Number", Items.SLIME_BALL, ScriptNumberValue.class),
        TEXT("Text", Items.BOOK, ScriptTextValue.class),
        BOOL("Boolean", Items.LEVER, ScriptBoolValue.class),
        LIST("List", Items.CHEST, null),
        DICTIONARY("Dictionary", Items.CHEST_MINECART, null),
        ANY("Any", Items.ENDER_EYE, ScriptValue.class);

        private final String name;
        private final Item icon;

        private final Class<? extends ScriptValue> defaultValueType;

        private final boolean allowOptional;

        ScriptActionArgumentType(String name, Item icon, Class<? extends ScriptValue> defaultValueType) {
            this.name = name;
            this.icon = icon;
            this.defaultValueType = defaultValueType;
            this.allowOptional = true;
        }

        ScriptActionArgumentType(String name, Item icon, Class<? extends ScriptValue> defaultValueType, boolean allowOptional) {
            this.name = name;
            this.icon = icon;
            this.defaultValueType = defaultValueType;
            this.allowOptional = allowOptional;
        }

        public String getName() {
            return name;
        }

        public Item getIcon() {
            return icon;
        }

        public MutableText text() {
            MutableText val = Text.literal(name);
            return val.fillStyle(Style.EMPTY.withItalic(false).withColor(Formatting.WHITE));
        }
        public boolean convertableTo(ScriptActionArgumentType to) {
            return to == ANY
                || to == TEXT
                || this == VARIABLE
                || this == to;
        }

        public ItemStack icon() {
            ItemStack itemStack = new ItemStack(icon);

            itemStack.setCustomName(text());

            return itemStack;
        }

        public ItemStack icon(String name) {
            ItemStack itemStack = new ItemStack(icon);

            itemStack.setCustomName(Text.literal(name).setStyle(Style.EMPTY.withColor(Formatting.WHITE).withItalic(false)));

            NbtList lore = new NbtList();
            lore.add(NbtString.of(Text.Serialization.toJsonString(text())));
            itemStack.getSubNbt("display").put("Lore", lore);

            return itemStack;
        }

        public boolean allowOptional() {
            return allowOptional;
        }

        public Class<? extends ScriptValue> getDefaultValueType() {
            return defaultValueType;
        }
    }

    public static class Serializer implements JsonSerializer<ScriptActionArgument>, JsonDeserializer<ScriptActionArgument> {

        @Override
        public JsonElement serialize(ScriptActionArgument src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();

            obj.addProperty("name", src.name());
            obj.addProperty("type", src.type().name());

            if(src.type().allowOptional()) {
                obj.addProperty("optional", src.optional());
                obj.addProperty("plural", src.plural());

                if(!(src.defaultValue() instanceof ScriptUnknownValue)) {
                    obj.add("default", context.serialize(src.defaultValue()));
                }
            }

            return obj;
        }

        @Override
        public ScriptActionArgument deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();

            String name = obj.get("name").getAsString();
            ScriptActionArgumentType argType = ScriptActionArgumentType.valueOf(obj.get("type").getAsString());

            ScriptActionArgument arg = new ScriptActionArgument(name, argType);

            if(argType.allowOptional()) {
                boolean optional = obj.get("optional").getAsBoolean();
                boolean plural = obj.get("plural").getAsBoolean();

                arg.optional(optional).plural(plural);

                if(obj.has("default")) {
                    arg.defaultValue((ScriptValue) context.deserialize(obj.get("default"), ScriptValue.class));
                }
            }

            return arg;
        }
    }
}