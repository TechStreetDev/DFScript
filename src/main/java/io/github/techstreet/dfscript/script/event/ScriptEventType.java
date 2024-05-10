package io.github.techstreet.dfscript.script.event;

import io.github.techstreet.dfscript.event.*;
import io.github.techstreet.dfscript.event.system.Event;
import io.github.techstreet.dfscript.script.menu.ScriptMenuClickButtonEvent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public enum ScriptEventType {

    SEND_CHAT(SendChatEvent.class,"OnSendChat", "Executed when a player sends a chat message or command.", Items.BOOK),

    KEY_PRESS(KeyPressEvent.class, "OnKeyPress", "Executed when a player presses a key.", Items.STONE_BUTTON),

    RECEIVE_CHAT(ReceiveChatEvent.class, "OnReceiveChat", "Executed when a player receives a chat message.", Items.BOOK),

    TICK_EVENT(TickEvent.class, "OnTick", "Executed every tick.", Items.CLOCK),

    PLAY_MODE_EVENT(PlayModeEvent.class, "OnPlayMode", "Executed when a player enters play mode.", Items.DIAMOND),

    BUILD_MODE(BuildModeEvent.class, "OnBuildMode", "Executed when a player enters build mode.", Items.DIAMOND_PICKAXE),

    DEV_MODE(DevModeEvent.class, "OnDevMode", "Executed when a player enters dev mode.", Items.DIAMOND_SWORD),

    START_UP(ScriptStartUpEvent.class, "OnStartUp", "Executed when all scripts are being loaded.", Items.FIREWORK_ROCKET),

    OVERLAY_EVENT(HudRenderEvent.class, "OnOverlay", "Executed when the HUD is being rendered.", Items.GREEN_STAINED_GLASS_PANE),

    MENU_BUTTON_EVENT(ScriptMenuClickButtonEvent.class, "OnMenuButtonClick", "Executed when a player clicks a button inside a custom menu.", Items.CHISELED_STONE_BRICKS),

    RECEIVE_SOUND(RecieveSoundEvent.class, "OnReceiveSound", "Executed when a player receives a sound", Items.NAUTILUS_SHELL);

    private final String name;
    private final String description;
    private final Item item;
    private final ItemStack icon;
    private final Class<? extends Event> codeutilitiesEvent;

    ScriptEventType(Class<? extends Event> codeutilitiesEvent, String name, String description, Item item) {
        this.codeutilitiesEvent = codeutilitiesEvent;
        this.name = name;
        this.description = description;
        this.item = item;
        icon = new ItemStack(item);
        icon.setCustomName(Text.literal(name)
            .setStyle(Style.EMPTY
                .withColor(Formatting.WHITE)
                .withItalic(false)));
        NbtList lore = new NbtList();
        lore.add(NbtString.of(Text.Serialization.toJsonString(Text.literal(description)
            .fillStyle(Style.EMPTY
                .withColor(Formatting.GRAY)
                .withItalic(false)))));
        icon.getSubNbt("display")
            .put("Lore", lore);
    }

    public ItemStack getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Item getItem() {
        return item;
    }

    public Class<? extends Event> getCodeutilitiesEvent() {
        return codeutilitiesEvent;
    }
}
