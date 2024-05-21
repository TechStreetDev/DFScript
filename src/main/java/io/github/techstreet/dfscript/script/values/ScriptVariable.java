package io.github.techstreet.dfscript.script.values;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

public class ScriptVariable extends ScriptValue {
    private ScriptValue value;

    public ScriptVariable() {
        this.value = new ScriptUnknownValue();
    }

    public ScriptVariable(ScriptValue value) {
        this.value = value;
    }

    @Override
    public ScriptValue get() {
        return value;
    }

    @Override
    public String toString() {
        return asText();
    }

    public void set(ScriptValue value) {
        this.value = value;
    }

    @Override
    public String asText() {
        return get().asText();
    }

    @Override
    public double asNumber() {
        return get().asNumber();
    }

    @Override
    public List<ScriptValue> asList() {
        return get().asList();
    }

    @Override
    public HashMap<String,ScriptValue> asDictionary() {
        return get().asDictionary();
    }

    @Override
    public boolean asBoolean() {
        return get().asBoolean();
    }

    @Override
    String typeName() {
        return "Variable";
    }

    @Override
    public boolean valueEquals(ScriptValue other) {
        return get().valueEquals(other);
    }

    @Override
    public ScriptValue getCompareValue() {
        return get().getCompareValue();
    }

    @Override
    public int compare(ScriptValue other) {
        return get().compare(other);
    }

    @Override
    public String formatAsText() {
        return get().formatAsText();
    }

    public static class Serializer implements JsonSerializer<ScriptVariable> {
        @Override
        public JsonElement serialize(ScriptVariable scriptValue, Type type, JsonSerializationContext context) {
            return context.serialize(scriptValue.get());
        }
    }
}
