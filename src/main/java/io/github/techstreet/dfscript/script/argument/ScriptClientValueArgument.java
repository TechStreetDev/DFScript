package io.github.techstreet.dfscript.script.argument;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.event.KeyPressEvent;
import io.github.techstreet.dfscript.event.ReceiveChatEvent;
import io.github.techstreet.dfscript.event.RecieveSoundEvent;
import io.github.techstreet.dfscript.event.SendChatEvent;
import io.github.techstreet.dfscript.event.system.Event;
import io.github.techstreet.dfscript.script.action.ScriptActionArgument.ScriptActionArgumentType;
import io.github.techstreet.dfscript.script.execution.ScriptContext;
import io.github.techstreet.dfscript.script.execution.ScriptTask;
import io.github.techstreet.dfscript.script.menu.ScriptMenuClickButtonEvent;
import io.github.techstreet.dfscript.script.util.ScriptValueItem;
import io.github.techstreet.dfscript.script.values.ScriptListValue;
import io.github.techstreet.dfscript.script.values.ScriptNumberValue;
import io.github.techstreet.dfscript.script.values.ScriptTextValue;
import io.github.techstreet.dfscript.script.values.ScriptValue;
import io.github.techstreet.dfscript.util.ComponentUtil;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public enum ScriptClientValueArgument implements ScriptArgument {

    EVENT_KEY("KeyPressed","The key code of the key pressed. (KeyPressEvent)", Items.STONE_BUTTON, ScriptActionArgumentType.NUMBER, (task) -> {
        if (task.event() instanceof KeyPressEvent e) {
            return new ScriptNumberValue(e.getKey().getCode());
        } else {
            throw new IllegalStateException("Event is not a KeyPressEvent");
        }
    }),

    EVENT_KEY_ACTION("KeyAction","The code of the key action performed. (KeyPressEvent)", Items.OAK_BUTTON, ScriptActionArgumentType.NUMBER, (task) -> {
        if (task.event() instanceof KeyPressEvent e) {
            return new ScriptNumberValue(e.getAction());
        } else {
            throw new IllegalStateException("Event is not a KeyPressEvent");
        }
    }),

    EVENT_MESSAGE("ReceivedMessage","The message received. (ReceiveChatEvent)", Items.WRITTEN_BOOK, ScriptActionArgumentType.TEXT, (task) -> {
        if (task.event() instanceof ReceiveChatEvent e) {
            return new ScriptTextValue(ComponentUtil.sectionSignsToAnds(ComponentUtil.toFormattedString(e.getMessage())));
        } else {
            throw new IllegalStateException("Event is not a ReceiveChatEvent");
        }
    }),

    ENTERED_MESSAGE("EnteredMessage","The message entered. (SendChatEvent)", Items.WRITABLE_BOOK, ScriptActionArgumentType.TEXT, (task) -> {
        if (task.event() instanceof SendChatEvent e) {
            return new ScriptTextValue(e.getMessage());
        } else {
            throw new IllegalStateException("Event is not a SendChatEvent");
        }
    }),

    TIMESTAMP("Timestamp","The current timestamp in milliseconds.", Items.CLOCK, ScriptActionArgumentType.NUMBER, (task) -> new ScriptNumberValue(System.currentTimeMillis())),

    CLIPBOARD("Clipboard", "The current text on the clipboard", Items.PAPER, ScriptActionArgumentType.TEXT, (task) -> new ScriptTextValue(DFScript.MC.keyboard.getClipboard())),

    MAIN_HAND_ITEM("MainHandItem","The item in the players main hand.", Items.STONE_BUTTON, ScriptActionArgumentType.DICTIONARY,
            (task) -> ScriptValueItem.valueFromItem(DFScript.MC.player.getMainHandStack())
    ),

    OFF_HAND_ITEM("OffHandItem","The item in the players off hand.", Items.OAK_BUTTON, ScriptActionArgumentType.DICTIONARY,
            (task) -> ScriptValueItem.valueFromItem(DFScript.MC.player.getOffHandStack())
    ),

    FULL_INVENTORY("FullInventory","The entire inventory items of the player.", Items.OAK_PLANKS, ScriptActionArgumentType.LIST, (task) -> {
        List<ScriptValue> list = new ArrayList<>();
        for (int i = 0; i < DFScript.MC.player.getInventory().size(); i++) {
            list.add(ScriptValueItem.valueFromItem(DFScript.MC.player.getInventory().getStack(i)));
        }
        return new ScriptListValue(list);
    }),

    MAIN_INVENTORY("MainInventory", "The main inventory items of the player.", Items.BIRCH_PLANKS, ScriptActionArgumentType.LIST, (task) -> {
        List<ScriptValue> list = new ArrayList<>();
        for (ItemStack item : DFScript.MC.player.getInventory().main) {
            list.add(ScriptValueItem.valueFromItem(item));
        }
        return new ScriptListValue(list);
    }),

    ARMOR("Armor", "The armor items of the player.", Items.IRON_CHESTPLATE, ScriptActionArgumentType.LIST, (task) -> {
        List<ScriptValue> list = new ArrayList<>();
        for (ItemStack item : DFScript.MC.player.getInventory().armor) {
            list.add(ScriptValueItem.valueFromItem(item));
        }
        return new ScriptListValue(list);
    }),

    HOTBAR_ITEMS("Hotbar Items", "The hotbar items of the player.", Items.IRON_AXE, ScriptActionArgumentType.LIST, (task) -> {
        List<ScriptValue> list = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            list.add(ScriptValueItem.valueFromItem(DFScript.MC.player.getInventory().getStack(i)));
        }
        return new ScriptListValue(list);
    }),

    SELECTED_SLOT("Selected Slot", "The selected hotbar slot.", Items.LIME_DYE, ScriptActionArgumentType.NUMBER,
            (task) -> new ScriptNumberValue(DFScript.MC.player.getInventory().selectedSlot)
    ),

    GAME_MODE("Game Mode", "The gamemode the player is in.", Items.BEDROCK, ScriptActionArgumentType.TEXT,
            (task) -> new ScriptTextValue(DFScript.MC.interactionManager.getCurrentGameMode().getName())
    ),

    WINDOW_WIDTH("Window Width", "The width of the current window.", Items.STICK, ScriptActionArgumentType.NUMBER,
            (task) -> new ScriptNumberValue(DFScript.MC.getWindow().getScaledWidth())
    ),

    WINDOW_HEIGHT("Window Height", "The height of the current window.", Items.STICK, ScriptActionArgumentType.NUMBER,
            (task) -> new ScriptNumberValue(DFScript.MC.getWindow().getScaledHeight())
    ),

    MENU_ELEMENT_IDENTIFIER("Menu Element Identifier", "The identifier of the menu element that triggered the event.", Items.NAME_TAG, ScriptActionArgumentType.TEXT,(task) -> {
        if (task.event() instanceof ScriptMenuClickButtonEvent e) {
            return new ScriptTextValue(e.identifier());
        } else {
            throw new IllegalStateException("The event is not a menu click event.");
        }
    }),
    
    PLAYER_UUID("Player UUID", "The UUID of the player.", Items.PLAYER_HEAD, ScriptActionArgumentType.TEXT,
            (task) -> new ScriptTextValue(DFScript.PLAYER_UUID)),

    PLAYER_NAME("Player Name", "The name of the player.", Items.PLAYER_HEAD, ScriptActionArgumentType.TEXT,
            (task) -> new ScriptTextValue(DFScript.PLAYER_NAME)),

    EVENT_SOUND("ReceivedSound", "The ID of the sound. (OnReceiveSound)", Items.NAUTILUS_SHELL, ScriptActionArgumentType.TEXT, (task) -> {
        if(task.event() instanceof RecieveSoundEvent e) {
            return new ScriptTextValue(e.getSoundId().toString().replaceAll("^minecraft:", ""));
        } else {
            throw new IllegalStateException("The event is not a receive sound event.");
        }
    }),

    EVENT_VOLUME("ReceivedSoundVolume", "The volume of the sound received. (OnReceiveSound)", Items.NOTE_BLOCK, ScriptActionArgumentType.NUMBER, (task) -> {
        if(task.event() instanceof RecieveSoundEvent e) {
            return new ScriptNumberValue(e.getVolume());
        } else {
            throw new IllegalStateException("The event is not a receive sound event.");
        }
    }),

    EVENT_PITCH("ReceivedSoundPitch", "The pitch of the sound received. (OnReceiveSound)", Items.JUKEBOX, ScriptActionArgumentType.NUMBER, (task) -> {
        if(task.event() instanceof RecieveSoundEvent e) {
            return new ScriptNumberValue(e.getPitch());
        } else {
            throw new IllegalStateException("The event is not a receive sound event.");
        }
    });

    private final String name;
    private final String description;
    private final Item item;
    private final ItemStack icon;
    private final Function<ScriptTask, ScriptValue> consumer;
    private final ScriptActionArgumentType type;

    ScriptClientValueArgument(String name, String description, Item type, ScriptActionArgumentType varType, Function<ScriptTask, ScriptValue> consumer) {
        this.name = name;
        this.description = description;
        this.item = type;
        this.icon = new ItemStack(type);
        icon.setCustomName(Text.literal(name)
            .fillStyle(Style.EMPTY
                .withItalic(false)));
        NbtList lore = new NbtList();
        lore.add(NbtString.of(Text.Serialization.toJsonString(Text.literal(description)
            .fillStyle(Style.EMPTY
                .withColor(Formatting.GRAY)
                .withItalic(false)))));
        icon.getSubNbt("display")
            .put("Lore", lore);
        this.consumer = consumer;
        this.type = varType;
    }

    public Item getItem() {
        return item;
    }

    public String getDescription() {
        return description;
    }

    public ScriptActionArgumentType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public ItemStack getIcon() {
        return icon;
    }

    @Override
    public ScriptValue getValue(ScriptTask task) {
        return consumer.apply(task);
    }

    @Override
    public boolean convertableTo(ScriptActionArgumentType type) {
        return this.type.convertableTo(type);
    }

    public static class Serializer implements JsonSerializer<ScriptClientValueArgument> {

        @Override
        public JsonElement serialize(ScriptClientValueArgument src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty("type","CLIENT_VALUE");
            object.addProperty("value",src.name());
            return object;
        }
    }

    @Override
    public ItemStack getArgIcon() {
        return new ItemStack(Items.NAME_TAG).setCustomName(Text.literal("Client Value").setStyle(Style.EMPTY.withColor(Formatting.WHITE).withItalic(false)));
    }

    @Override
    public String getArgText() {
        return name;
    }
}
