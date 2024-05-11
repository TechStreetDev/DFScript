package io.github.techstreet.dfscript.script;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.techstreet.dfscript.screen.widget.CItem;
import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.script.execution.ScriptTask;
import io.github.techstreet.dfscript.script.render.ScriptPartRender;
import io.github.techstreet.dfscript.script.render.ScriptPartRenderDynamicElement;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.lang.reflect.Type;

public class ScriptComment extends ScriptPart {

    private String comment;

    public ScriptComment(String comment) {
        this.comment = comment;
    }

    public ScriptComment setComment(String comment) {
        this.comment = comment;

        return this;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public void run(ScriptTask task) {

    }

    @Override
    public void create(ScriptPartRender render, Script script) {
        render.addElement(new ScriptPartRenderDynamicElement((args) -> {
            int y = args.y();
            int indent = args.indent();
            CScrollPanel panel = args.panel();

            panel.add(new CItem(5+indent*5, y, new ItemStack(Items.MAP).setCustomName(Text.literal("Comment").setStyle(Style.EMPTY.withItalic(false)))));

            return y+10;
        }));
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Items.MAP).setCustomName(Text.literal("Comment").setStyle(Style.EMPTY.withItalic(false)));
    }

    @Override
    public String getName() {
        return comment;
    }

    public static class Serializer implements JsonSerializer<ScriptComment> {

        @Override
        public JsonElement serialize(ScriptComment src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "comment");
            obj.addProperty("comment", src.getComment());
            return obj;
        }
    }
}