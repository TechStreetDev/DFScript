package io.github.techstreet.dfscript.util;

import io.github.techstreet.dfscript.DFScript;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtil {

    public static Path folder() {
        return DFScript.MC.runDirectory.toPath().resolve("DFScript");
    }

    public static Path folder(String path) {
        return folder().resolve(path);
    }

    public static String readFile(Path path) throws IOException {
        return String.join("\n", Files.readAllLines(path));
    }

    public static void writeFile(Path path, String content) throws IOException {
        Files.writeString(path, content);
    }

}
