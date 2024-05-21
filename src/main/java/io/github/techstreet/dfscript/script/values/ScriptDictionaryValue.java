package io.github.techstreet.dfscript.script.values;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ScriptDictionaryValue extends ScriptValue {

    private final HashMap<String, ScriptValue> value;

    public ScriptDictionaryValue(HashMap<String, ScriptValue> value) {
        this.value = value;
    }

    @Override
    String typeName() {
        return "Dictionary";
    }

    @Override
    public HashMap<String, ScriptValue> asDictionary() {
        return new HashMap<>(value);
    }

    @Override
    public boolean valueEquals(ScriptValue other) {
        if (!(other.get() instanceof ScriptDictionaryValue)
            && !(other.get() instanceof ScriptUnknownValue)) {
            return false;
        }
        HashMap<String, ScriptValue> otherValue = other.asDictionary();
        if (otherValue.size() != value.size()) {
            return false;
        }
        for (String key : value.keySet()) {
            if (!otherValue.containsKey(key)) {
                return false;
            }
            if (!value.get(key).valueEquals(otherValue.get(key))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String asText() {
        return value.toString();
    }

    @Override
    public boolean asBoolean() {
        return !value.isEmpty();
    }

    @Override
    public ScriptValue getCompareValue() {
        HashMap<String, ScriptValue> dict = asDictionary();

        return dict.get(dict.keySet().toArray()[0]).getCompareValue();
    }

    public static class Serializer implements JsonSerializer<ScriptDictionaryValue>, JsonDeserializer<ScriptDictionaryValue> {
        @Override
        public ScriptDictionaryValue deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            if(jsonElement.isJsonObject()) {
                JsonObject obj = jsonElement.getAsJsonObject();
                HashMap<String, ScriptValue> dict = new HashMap<>();

                for (Map.Entry<String, JsonElement> element : obj.entrySet()) {
                    dict.put(element.getKey(), context.deserialize(element.getValue(), ScriptValue.class));
                }

                return new ScriptDictionaryValue(dict);
            }

            throw new JsonParseException("Unable to convert the json into a script list value!");
        }

        @Override
        public JsonElement serialize(ScriptDictionaryValue scriptValue, Type type, JsonSerializationContext context) {
            JsonObject dictObj = new JsonObject();
            HashMap<String, ScriptValue> dict = scriptValue.asDictionary();
            for (String key : dict.keySet()) {
                dictObj.add(key, context.serialize(dict.get(key)));
            }
            JsonObject obj = new JsonObject();
            obj.add("dict", dictObj);
            obj.addProperty("___objectType", "dict");
            return obj;
        }
    }
}
