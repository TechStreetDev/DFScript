package io.github.techstreet.dfscript;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.techstreet.dfscript.loader.Loadable;
import io.github.techstreet.dfscript.script.action.ScriptActionArgument;
import io.github.techstreet.dfscript.script.action.ScriptActionArgumentList;
import io.github.techstreet.dfscript.script.action.ScriptActionType;
import io.github.techstreet.dfscript.script.argument.ScriptClientValueArgument;
import io.github.techstreet.dfscript.script.event.ScriptEventType;

import java.io.File;
import java.nio.file.Files;

public class ActiondumpHandler implements Loadable {
    @Override
    public void load() {
        JsonObject obj = new JsonObject();
        JsonArray array = new JsonArray();

        for (ScriptEventType type : ScriptEventType.values()) {
            JsonObject event = new JsonObject();
            event.addProperty("identifier", type.name());
            event.addProperty("name", type.getName());
            event.addProperty("description", type.getDescription());
            event.addProperty("icon", type.getItem().toString());
            array.add(event);
        }

        obj.add("events", array);
        array = new JsonArray();

        for (ScriptActionType type : ScriptActionType.values()) {
            JsonObject event = new JsonObject();
            JsonArray array2 = new JsonArray();
            event.addProperty("identifier", type.name());
            event.addProperty("name", type.getName());
            event.addProperty("category", type.getCategory().name());
            event.addProperty("description", String.join("\n", type.getDescription()));
            event.addProperty("icon", type.getItem().toString());

            for (ScriptActionArgument arg : type.getArguments()) {
                JsonObject arg2 = new JsonObject();
                arg2.addProperty("type", arg.getType().name());
                arg2.addProperty("type_icon", arg.getType().getIcon().toString());
                arg2.addProperty("type_name", arg.getType().getName());
                arg2.addProperty("name", arg.getName());
                arg2.addProperty("optional", arg.isOptional());
                arg2.addProperty("plural", arg.isPlural());

                array2.add(arg2);
            }


            //event.addProperty("icon", type.get().toString());
            event.add("arguments", array2);
            array.add(event);
        }

        obj.add("actions", array);
        array = new JsonArray();

        for (ScriptClientValueArgument type : ScriptClientValueArgument.values()) {
            JsonObject event = new JsonObject();
            event.addProperty("identifier", type.name());
            event.addProperty("name", type.getName());
            event.addProperty("description", String.join("\n", type.getDescription()));
            event.addProperty("icon", type.getItem().toString());
            event.addProperty("type", type.getType().name());
            event.addProperty("type_icon", type.getType().getIcon().toString());
            event.addProperty("type_name", type.getType().getName());

            array.add(event);
        }

        obj.add("client_values", array);

        File file = new File("DFScript/actiondump.json");
        DFScript.LOGGER.info("Dumping action data to " + file.getAbsolutePath());

        try {
            if (!file.exists()) file.createNewFile();
            Files.write(file.toPath(), DFScript.GSON.toJson(obj).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
