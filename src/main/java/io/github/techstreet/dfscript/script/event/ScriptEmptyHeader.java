package io.github.techstreet.dfscript.script.event;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.techstreet.dfscript.screen.widget.CItem;
import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.screen.widget.CText;
import io.github.techstreet.dfscript.script.Script;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.awt.*;
import java.lang.reflect.Type;

public class ScriptEmptyHeader extends ScriptHeader {

    static String emptyName;
    static ItemStack emptyIcon;

    static {
        emptyName = "Empty...";

        emptyIcon = new ItemStack(Items.LIGHT_GRAY_DYE);
        emptyIcon.setCustomName(Text.literal(emptyName).setStyle(Style.EMPTY.withItalic(false).withColor(Formatting.WHITE)));

        NbtList lore = new NbtList();

        lore.add(NbtString.of(Text.Serialization.toJsonString(Text.literal("Literally can never be triggered...").setStyle(Style.EMPTY.withColor(Formatting.GRAY).withItalic(false)))));

        emptyIcon.getSubNbt("display")
                .put("Lore", lore);
    }

    public static class Serializer implements JsonSerializer<ScriptEmptyHeader> {

        @Override
        public JsonElement serialize(ScriptEmptyHeader src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "empty");
            obj.add("snippet", context.serialize(src.container().getSnippet(0)));
            return obj;
        }
    }

    public int create(CScrollPanel panel, int y, int index, Script script) {
        panel.add(new CItem(5, y, emptyIcon));
        panel.add(new CText(15, y + 2, Text.literal(emptyName)));

        return super.create(panel, y, index, script);
    }
}
