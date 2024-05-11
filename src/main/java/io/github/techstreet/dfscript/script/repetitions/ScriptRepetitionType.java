package io.github.techstreet.dfscript.script.repetitions;

import io.github.techstreet.dfscript.script.action.ScriptActionArgument;
import io.github.techstreet.dfscript.script.action.ScriptActionArgument.ScriptActionArgumentType;
import io.github.techstreet.dfscript.script.action.ScriptActionArgumentList;
import io.github.techstreet.dfscript.script.action.ScriptActionCategory;
import io.github.techstreet.dfscript.script.execution.ScriptActionContext;
import io.github.techstreet.dfscript.script.values.ScriptNumberValue;
import io.github.techstreet.dfscript.script.values.ScriptTextValue;
import io.github.techstreet.dfscript.script.values.ScriptValue;
import io.github.techstreet.dfscript.util.chat.ChatUtil;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public enum ScriptRepetitionType {

    REPEAT_MULTIPLE(builder -> builder.name("RepeatMultiple")
            .description("Repeats a specified amount of times.")
            .icon(Items.REDSTONE)
            .category(ScriptActionCategory.NUMBERS)
            .arg("Times", ScriptActionArgumentType.NUMBER)
            .arg("Current", ScriptActionArgumentType.VARIABLE, b -> b.optional(true))
            .action(ctx -> {
                if(!ctx.hasScopeVariable("Counter")) {
                    ctx.setScopeVariable("Counter", 0);
                }

                int counter = (Integer)ctx.getScopeVariable("Counter")+1;

                if(counter <= ctx.value("Times").asNumber()) {
                    ctx.setScopeVariable("Counter", counter);
                    if (ctx.argMap().containsKey("Current")) {
                        ctx.setVariable("Current", new ScriptNumberValue(counter));
                    }
                    return true;
                }
                return false;
            })),

    FOR_EACH_IN_LIST(builder -> builder.name("For Each In List")
            .description("Iterates over a list.")
            .icon(Items.BOOKSHELF)
            .category(ScriptActionCategory.LISTS)
            .arg("Variable", ScriptActionArgumentType.VARIABLE)
            .arg("List", ScriptActionArgumentType.LIST)
            .action(ctx -> {
                if(!ctx.hasScopeVariable("Counter")) {
                    ctx.setScopeVariable("Counter", 0);
                }

                int counter = (Integer)ctx.getScopeVariable("Counter")+1;
                List<ScriptValue> list = ctx.value("List").asList();

                if(counter <= list.size()) {
                    ctx.setScopeVariable("Counter", counter);
                    ctx.setVariable("Variable", list.get(counter-1));
                    return true;
                }
                return false;
            })),

    DICT_FOR_EACH(builder -> builder.name("For Each In Dictionary")
            .description("Iterates over a dictionary.")
            .icon(Items.BOOKSHELF)
            .category(ScriptActionCategory.DICTIONARIES)
            .arg("Key", ScriptActionArgumentType.VARIABLE)
            .arg("Value", ScriptActionArgumentType.VARIABLE)
            .arg("Dictionary", ScriptActionArgumentType.DICTIONARY)
            .action(ctx -> {
                HashMap<String, ScriptValue> dict = ctx.value("Dictionary").asDictionary();

                if(!ctx.hasScopeVariable("Iterator")) {
                    ctx.setScopeVariable("Iterator", dict.entrySet().iterator());
                }

                Iterator<Map.Entry<String, ScriptValue>> iterator = (Iterator<Map.Entry<String, ScriptValue>>) ctx.getScopeVariable("Iterator");

                if(iterator.hasNext()) {
                    Map.Entry<String, ScriptValue> entry = iterator.next();
                    ctx.setScopeVariable("Iterator", iterator);
                    ctx.setVariable("Key", new ScriptTextValue(entry.getKey()));
                    ctx.setVariable("Value", entry.getValue());
                    return true;
                }
            return false;
            })),

    REPEAT_FOREVER(builder -> builder.name("RepeatForever")
            .description("Repeats for eternity.\nMake sure to have a Stop Repetition, Stop Codeline or Wait somewhere in the code!\nThere's a lagslayer for the repetition actions.\nIt activates after 100000 iterations with no Wait.")
            .icon(Items.GOLD_INGOT)
            .category(ScriptActionCategory.MISC)
            .action(ctx -> true));

    private Function<ScriptActionContext, Boolean> action = (ctx) -> false;

    private boolean glow = false;
    private Item icon = Items.STONE;
    private String name = "Unnamed Action";
    private boolean hasChildren = false;
    private ScriptActionCategory category = ScriptActionCategory.MISC;
    private List<String> description = new ArrayList();

    private ScriptRepetitionType deprecated = null; //if deprecated == null, the action is not deprecated
    private final ScriptActionArgumentList arguments = new ScriptActionArgumentList();
    ScriptRepetitionType(Consumer<ScriptRepetitionType> builder) {
        description.add("No description provided.");
        builder.accept(this);
    }
    public ItemStack getIcon() {
        ItemStack item = new ItemStack(icon);

        item.setCustomName(Text.literal(name)
            .fillStyle(Style.EMPTY
                .withColor(Formatting.WHITE)
                .withItalic(false)));

        NbtList lore = new NbtList();

        if(isDeprecated())
        {
            lore.add(NbtString.of(Text.Serialization.toJsonString(Text.literal("This action is deprecated!")
                    .fillStyle(Style.EMPTY
                            .withColor(Formatting.RED)
                            .withItalic(false)))));
            lore.add(NbtString.of(Text.Serialization.toJsonString(Text.literal("Use '" + deprecated.getName() + "'")
                    .fillStyle(Style.EMPTY
                            .withColor(Formatting.RED)
                            .withItalic(false)))));
        }

        for (String descriptionLine: description) {
            lore.add(NbtString.of(Text.Serialization.toJsonString(Text.literal(descriptionLine)
                .fillStyle(Style.EMPTY
                      .withColor(Formatting.GRAY)
                      .withItalic(false)))));
        }

        lore.add(NbtString.of(Text.Serialization.toJsonString(Text.literal(""))));

        for (ScriptActionArgument arg : arguments) {
            for (Text txt : arg.text()) {
                lore.add(NbtString.of(Text.Serialization.toJsonString(txt)));
            }
        }

        item.getSubNbt("display")
            .put("Lore", lore);

        if(glow)
        {
            item.addEnchantment(Enchantments.UNBREAKING, 1);
            item.addHideFlag(ItemStack.TooltipSection.ENCHANTMENTS);
        }

        return item;
    }
    public String getName() {
        return name;
    }

    public boolean isDeprecated() {
        return deprecated != null;
    }

    public boolean hasChildren() {
        return hasChildren;
    }

    public ScriptActionCategory getCategory() {
        return category;
    }

    private ScriptRepetitionType action(Function<ScriptActionContext, Boolean> action) {
        this.action = action;
        return this;
    }

    private ScriptRepetitionType icon(Item icon, boolean glow) {
        this.icon = icon;
        this.glow = glow;
        return this;
    }

    private ScriptRepetitionType icon(Item icon) {
        icon(icon, false);
        return this;
    }

    private ScriptRepetitionType name(String name) {
        this.name = name;
        return this;
    }

    private ScriptRepetitionType hasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
        return this;
    }

    private ScriptRepetitionType category(ScriptActionCategory category) {
        this.category = category;
        return this;
    }

    private ScriptRepetitionType description(String description) {
        this.description.clear();
        this.description.addAll(Arrays.asList(description.split("\n", -1)));
        return this;
    }

    public ScriptRepetitionType arg(String name, ScriptActionArgumentType type, Consumer<ScriptActionArgument> builder) {
        ScriptActionArgument arg = new ScriptActionArgument(name, type);
        builder.accept(arg);
        arguments.add(arg);
        return this;
    }

    public ScriptRepetitionType arg(String name, ScriptActionArgumentType type) {
        return arg(name, type, (arg) -> {
        });
    }

    public ScriptRepetitionType deprecate(ScriptRepetitionType newScriptRepetitionType) {
        deprecated = newScriptRepetitionType;

        return this;
    }

    public boolean run(ScriptActionContext ctx) {
        try
        {
            arguments.getArgMap(ctx);
            return action.apply(ctx);
        }
        catch(IllegalArgumentException e)
        {
            ChatUtil.error("Invalid arguments for " + name + ".");
            return false;
        }
    }
}
