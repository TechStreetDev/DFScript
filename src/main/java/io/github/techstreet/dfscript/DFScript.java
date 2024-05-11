package io.github.techstreet.dfscript;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.techstreet.dfscript.commands.CommandManager;
import io.github.techstreet.dfscript.commands.arguments.StringFuncArgumentType;
import io.github.techstreet.dfscript.commands.arguments.serializers.StringFuncArgumentSerializer;
import io.github.techstreet.dfscript.features.AuthHandler;
import io.github.techstreet.dfscript.features.UpdateAlerts;
import io.github.techstreet.dfscript.loader.Loader;
import io.github.techstreet.dfscript.screen.overlay.OverlayManager;
import io.github.techstreet.dfscript.script.ScriptManager;
import io.github.techstreet.dfscript.util.Scheduler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DFScript implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
    public static final MinecraftClient MC = MinecraftClient.getInstance();

    public static final String MOD_NAME = "DFScript";
    public static final String MOD_ID = "dfscript";
    public static String MOD_VERSION;

    public static String PLAYER_UUID = null;
    public static String PLAYER_NAME = null;

    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing");
        Runtime.getRuntime().addShutdownHook(new Thread(this::onClose));

        // allows FileDialog class to open without a HeadlessException
        System.setProperty("java.awt.headless", "false");

        PLAYER_UUID = MC.getSession().getUuidOrNull().toString();
        PLAYER_NAME = MC.getSession().getUsername();

        MOD_VERSION = FabricLoader.getInstance().getModContainer(MOD_ID).get().getMetadata().getVersion().getFriendlyString();

        ArgumentTypeRegistry.registerArgumentType(new Identifier("tutorial", "uuid"), StringFuncArgumentType.class, new StringFuncArgumentSerializer());

        Loader loader = Loader.getInstance();
        loader.load(new CommandManager());
        loader.load(new AuthHandler());
        loader.load(new ScriptManager());
        loader.load(new Scheduler());
        loader.load(new UpdateAlerts());
        loader.load(new OverlayManager());
        loader.load(new ActiondumpHandler());

        LOGGER.info("Initialized");
    }

    public void onClose() {
        LOGGER.info("Closing...");
        AuthHandler.deauth();
        LOGGER.info("Closed.");
    }
}
