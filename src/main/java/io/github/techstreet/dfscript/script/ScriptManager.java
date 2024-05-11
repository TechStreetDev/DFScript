package io.github.techstreet.dfscript.script;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.event.*;
import io.github.techstreet.dfscript.event.system.Event;
import io.github.techstreet.dfscript.event.system.EventManager;
import io.github.techstreet.dfscript.loader.Loadable;
import io.github.techstreet.dfscript.script.action.ScriptActionArgument;
import io.github.techstreet.dfscript.script.action.ScriptActionArgumentList;
import io.github.techstreet.dfscript.script.action.ScriptBuiltinAction;
import io.github.techstreet.dfscript.script.action.ScriptFunctionCall;
import io.github.techstreet.dfscript.script.argument.*;
import io.github.techstreet.dfscript.script.conditions.ScriptBooleanSet;
import io.github.techstreet.dfscript.script.conditions.ScriptBranch;
import io.github.techstreet.dfscript.script.conditions.ScriptBuiltinCondition;
import io.github.techstreet.dfscript.script.conditions.ScriptCondition;
import io.github.techstreet.dfscript.script.event.*;
import io.github.techstreet.dfscript.script.options.ScriptNamedOption;
import io.github.techstreet.dfscript.script.repetitions.ScriptBuiltinRepetition;
import io.github.techstreet.dfscript.script.repetitions.ScriptWhile;
import io.github.techstreet.dfscript.script.values.*;
import io.github.techstreet.dfscript.util.FileUtil;
import io.github.techstreet.dfscript.util.chat.ChatType;
import io.github.techstreet.dfscript.util.chat.ChatUtil;
import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScriptManager implements Loadable {
    public static final Logger LOGGER = LogManager.getLogger("Scripts");
    public static long lastServerUpdate = 0;
    private static ScriptManager instance;
    private final List<Script> scripts = new ArrayList<>();
    private final Gson GSON = new GsonBuilder()
        .registerTypeAdapter(Script.class, new Script.Serializer())
        .registerTypeAdapter(ScriptPart.class, new ScriptPart.Serializer())
        .registerTypeAdapter(ScriptArgument.class, new ScriptArgument.Serializer())
        .registerTypeAdapter(ScriptTextArgument.class, new ScriptTextArgument.Serializer())
        .registerTypeAdapter(ScriptNumberArgument.class, new ScriptNumberArgument.Serializer())
        .registerTypeAdapter(ScriptBoolArgument.class, new ScriptBoolArgument.Serializer())
        .registerTypeAdapter(ScriptVariableArgument.class, new ScriptVariableArgument.Serializer())
        .registerTypeAdapter(ScriptClientValueArgument.class, new ScriptClientValueArgument.Serializer())
        .registerTypeAdapter(ScriptConfigArgument.class, new ScriptConfigArgument.Serializer())
        .registerTypeAdapter(ScriptFunctionArgument.class, new ScriptFunctionArgument.Serializer())
        .registerTypeAdapter(ScriptNamedOption.class, new ScriptNamedOption.Serializer())
        .registerTypeAdapter(ScriptBuiltinAction.class, new ScriptBuiltinAction.Serializer())
        .registerTypeAdapter(ScriptFunctionCall.class, new ScriptFunctionCall.Serializer())
        .registerTypeAdapter(ScriptBuiltinCondition.class, new ScriptBuiltinCondition.Serializer())
        .registerTypeAdapter(ScriptCondition.class, new ScriptCondition.Serializer())
        .registerTypeAdapter(ScriptBuiltinRepetition.class, new ScriptBuiltinRepetition.Serializer())
        .registerTypeAdapter(ScriptWhile.class, new ScriptWhile.Serializer())
        .registerTypeAdapter(ScriptBranch.class, new ScriptBranch.Serializer())
        .registerTypeAdapter(ScriptBooleanSet.class, new ScriptBooleanSet.Serializer())
        .registerTypeAdapter(ScriptEvent.class, new ScriptEvent.Serializer())
        .registerTypeAdapter(ScriptFunction.class, new ScriptFunction.Serializer())
        .registerTypeAdapter(ScriptComment.class, new ScriptComment.Serializer())
        .registerTypeAdapter(ScriptSnippet.class, new ScriptSnippet.Serializer())
        .registerTypeAdapter(ScriptHeader.class, new ScriptHeader.Serializer())
        .registerTypeAdapter(ScriptEmptyHeader.class, new ScriptEmptyHeader.Serializer())
        .registerTypeAdapter(ScriptActionArgument.class, new ScriptActionArgument.Serializer())
        .registerTypeAdapter(ScriptActionArgumentList.class, new ScriptActionArgumentList.Serializer())
        .registerTypeAdapter(ScriptValue.class, new ScriptValue.Serializer())
        .registerTypeAdapter(ScriptNumberValue.class, new ScriptNumberValue.Serializer())
        .registerTypeAdapter(ScriptTextValue.class, new ScriptTextValue.Serializer())
        .registerTypeAdapter(ScriptListValue.class, new ScriptListValue.Serializer())
        .registerTypeAdapter(ScriptDictionaryValue.class, new ScriptDictionaryValue.Serializer())
        .registerTypeAdapter(ScriptBoolValue.class, new ScriptDictionaryValue.Serializer())
        .create();

    public ScriptManager() {
        instance = this;
    }

    public static ScriptManager getInstance() {
        if (instance == null) {
            instance = new ScriptManager();
        }

        return instance;
    }

    public Gson getGSON() {
        return GSON;
    }

    @Override
    public void load() {
        loadScripts();
        loadEvents();

        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();

            FileUtil.folder("Scripts").register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);

            new Thread(() -> {
                try {
                    while (true) {
                        WatchKey key = watchService.take();
                        for (WatchEvent<?> event : key.pollEvents()) {
                            if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                                Path p = (Path) event.context();
                                Path absolute = FileUtil.folder("Scripts").resolve(p);
                                if (absolute.getParent().equals(FileUtil.folder("Scripts")) && !absolute.toFile().isDirectory()) {
                                    loadScript(absolute.toFile());
                                    ChatUtil.sendMessage("Script loaded: " + p.getFileName(), ChatType.INFO_BLUE);
                                }
                            } else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                                Path p = (Path) event.context();
                                Path absolute = FileUtil.folder("Scripts").resolve(p);
                                if (absolute.getParent().equals(FileUtil.folder("Scripts"))) {
                                    unloadScript(absolute.toFile());
                                    ChatUtil.sendMessage("Script unloaded: " + p.getFileName(), ChatType.INFO_BLUE);
                                }
                            }
                        }
                        key.reset();
                    }
                } catch (Exception err) {
                        err.printStackTrace();
                }
            }).start();
        } catch (Exception err) {
            LOGGER.error("Unable to listen for new scripts", err);
        }
    }

    private void unloadScript(File file) {
        for (Script s : scripts) {
            if (s.getFile().getAbsoluteFile().equals(file.getAbsoluteFile())) {
                s.setDisabled(true);
                scripts.remove(s);
                return;
            }
        }
    }

    private void loadScripts() {
        LOGGER.info("Loading script...");

        File dir = FileUtil.folder("Scripts").toFile();

        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                LOGGER.error("Failed to create script directory!");
                return;
            }
        }

        for (File file : dir.listFiles()) {
            loadScript(file);
        }

        LOGGER.info("Loaded " + scripts.size() + " script!");

        handleEvent(new ScriptStartUpEvent());
    }

    private void loadScript(File file) {
        if (scripts.stream().anyMatch(s -> s.getFile().getAbsoluteFile().equals(file.getAbsoluteFile()))) {
            return;
        }

        try {
            if (file.isDirectory()) {
                LOGGER.info("Skipped directory: " + file.getName());
                return;
            }

            String content = FileUtil.readFile(file.toPath());
            Script s = GSON.fromJson(content, Script.class);
            s.setFile(file);

            ScriptMigrator.migrate(s);

            if (s.getVersion() != Script.scriptVersion) throw new RuntimeException("this script uses version " + s.getVersion() + " when this version of DFScript uses version " + Script.scriptVersion + "!");

            scripts.add(s);
            LOGGER.info("Loaded script: " + file.getName());
        } catch (Exception e) {
            LOGGER.error("Failed to load script: " + file.getName());
//            e.printStackTrace();
        }
    }

    public void saveScript(Script script) {
        try {
            FileUtil.writeFile(script.getFile().toPath(), GSON.toJson(script));
        } catch (Exception e) {
            LOGGER.error("Failed to save script: " + script.getFile().getName());
//            e.printStackTrace();
        }
    }

    private void loadEvents() {
        EventManager manager = EventManager.getInstance();

        manager.register(SendChatEvent.class, this::handleEvent);
        manager.register(KeyPressEvent.class, this::handleEvent);
        manager.register(ReceiveChatEvent.class, this::handleEvent);
        manager.register(TickEvent.class, this::handleEvent);
        manager.register(PlayModeEvent.class, this::handleEvent);
        manager.register(BuildModeEvent.class, this::handleEvent);
        manager.register(DevModeEvent.class, this::handleEvent);
        manager.register(HudRenderEvent.class, this::handleEvent);
        manager.register(RecieveSoundEvent.class, this::handleEvent);
    }

    public void handleEvent(Event event) {
        for (Script script : scripts) {
            script.invoke(event);
        }
    }

    public List<Script> getScripts() {
        return scripts;
    }

    public void deleteScript(Script script) {
        if (scripts.contains(script)) {
            scripts.remove(script);
            if (!script.getFile().delete()) {
                LOGGER.error("Failed to delete script: " + script.getFile().getName());
            }
        }
    }

    public void createScript(String name) {
        String ownerId = null;
        if (DFScript.MC.player != null) {
            ownerId = DFScript.MC.player.getUuid().toString();
        }

        Script script = new Script(name, ownerId, "None", new ArrayList<>(),false, Script.scriptVersion);
        scripts.add(script);

        File file = null;
        try {
            file = FileUtil.folder("Scripts").resolve(name + ".json").toFile();
        } catch (InvalidPathException e) {
            LOGGER.error("Failed to save script: " + script.getFile().getName());
//            e.printStackTrace();
        }

        script.setFile(file);
        script.setOwner(DFScript.PLAYER_UUID);
        saveScript(script);
    }

    public void reload() {
        for (Script script : scripts.stream().toList()) {
            unloadScript(script.getFile());
        }
        loadScripts();
    }
}
