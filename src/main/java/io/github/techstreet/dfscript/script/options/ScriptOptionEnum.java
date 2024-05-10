package io.github.techstreet.dfscript.script.options;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.security.InvalidParameterException;

public enum ScriptOptionEnum {
    TEXT("Text", "A single option, no checks.", Items.BOOK, ScriptTextOption.class),
    INT("Integer", "A single option, must be an int.", Items.SLIME_BALL, ScriptIntOption.class),
    FLOAT("Floating-Point", "A single option, must be an int or a float.", Items.SLIME_BLOCK, ScriptFloatOption.class),
    KEY("Key", "A single option, acts as a key bind.", Items.STONE_BUTTON, ScriptKeyOption.class),
    BOOL("Boolean", "A true/false option.", Items.LEVER, ScriptBoolOption.class),
    OLD_BOOL("Boolean (Legacy)", "A true/false option. Returns either \"true\" or \"false\".", Items.LEVER, ScriptLegacyBoolOption.class, 0, BOOL),
    DUAL("Dual", "Two values, two types", Items.HOPPER, ScriptDualOption.class, 2),
    LIST("List", "A list of values, can contain a single type", Items.CHEST, ScriptListOption.class, 1),
    DICTIONARY("Dictionary", "A dictionary of values, two types", Items.CHEST_MINECART, ScriptDictionaryOption.class, 2);

    final String name;
    final String description;
    final Item icon;
    final Class<? extends ScriptOption> optionType;
    final int extraTypes;

    final ScriptOptionEnum deprecate;

    ScriptOptionEnum(String name, String description, Item icon, Class<? extends ScriptOption> optionType) {
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.optionType = optionType;
        this.extraTypes = 0;
        this.deprecate = null;
    }

    ScriptOptionEnum(String name, String description, Item icon, Class<? extends ScriptOption> optionType, int extraTypes) {
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.optionType = optionType;
        this.extraTypes = extraTypes;
        this.deprecate = null;
    }

    ScriptOptionEnum(String name, String description, Item icon, Class<? extends ScriptOption> optionType, int extraTypes, ScriptOptionEnum deprecate) {
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.optionType = optionType;
        this.extraTypes = extraTypes;
        this.deprecate = deprecate;
    }

    public ItemStack getIcon()
    {
        ItemStack item = new ItemStack(icon);

        item.setCustomName(Text.literal(name + " Option").fillStyle(Style.EMPTY.withColor(Formatting.WHITE).withItalic(false)));

        NbtList lore = new NbtList();

        lore.add(NbtString.of(Text.Serialization.toJsonString(Text.literal(description)
                .fillStyle(Style.EMPTY.withColor(Formatting.GRAY).withItalic(false))
        )));

        item.getSubNbt("display")
                .put("Lore", lore);

        return item;
    }

    public String getName() {
        return name;
    }

    public int getExtraTypes()
    {
        return extraTypes;
    }

    public Class<? extends ScriptOption> getOptionType() {
        return optionType;
    }

    public static ScriptOptionEnum fromName(String name) {
        for (ScriptOptionEnum t : ScriptOptionEnum.values()) {
            if(t.name().equals(name)) {
                return t;
            }
        }

        throw new InvalidParameterException("Unknown option name: " + name);
    }

    public static ScriptOptionEnum fromClass(Class<? extends ScriptOption> name) {
        for (ScriptOptionEnum t : ScriptOptionEnum.values()) {
            if(t.getOptionType().equals(name)) {
                return t;
            }
        }

        throw new InvalidParameterException("Unknown option class: " + name.getName());
    }

    public boolean isDeprecated() {
        return deprecate != null;
    }
}
