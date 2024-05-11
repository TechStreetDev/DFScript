package io.github.techstreet.dfscript.features;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.exceptions.AuthenticationException;
import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.loader.Loadable;
import io.github.techstreet.dfscript.script.ScriptManager;
import io.github.techstreet.dfscript.script.util.AuthcodeResponse;
import io.github.techstreet.dfscript.script.util.ServercodeResponse;
import net.minecraft.client.session.Session;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class AuthHandler implements Loadable {
    private static String authCode = null;

    @Override
    public void load() {
        regen();
    }

    public static void updateScripts() {
        try {
            URL url = new URL("https://api.techstreet.tech/dfscript/scripts/");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("authorization", AuthHandler.getAuthCode());
            con.setDoOutput(true);

            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;

                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                ArrayList<String> expected = new ArrayList<>();
                JsonObject scripts = JsonParser.parseString(response.toString()).getAsJsonObject();
                scripts.getAsJsonArray("scripts").forEach(script -> expected.add(script.getAsJsonObject().get("id").getAsLong() + ".json"));

                File file = new File("DFScript/Scripts");
                for (File f : Objects.requireNonNull(file.listFiles())) {
                    if (!expected.contains(f.getName())) {
                        f.delete();
                    }
                }

                scripts.getAsJsonArray("scripts").forEach(script -> {
                    try {
                        File f = new File("DFScript/Scripts/" + script.getAsJsonObject().get("id").getAsLong() + ".json");
                        if (!f.exists()) {
                            f.createNewFile();

                            JsonObject obj = script.getAsJsonObject();
                            obj.addProperty("disabled", false);
                            obj.add("config", new JsonArray());

                            Files.write(f.toPath(), DFScript.GSON.toJson(obj).getBytes(StandardCharsets.UTF_8));
                        } else {
                            JsonObject obj = JsonParser.parseString(new String(Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8)).getAsJsonObject();
                            obj.add("headers", script.getAsJsonObject().get("headers").getAsJsonArray());

                            Files.write(f.toPath(), DFScript.GSON.toJson(obj).getBytes(StandardCharsets.UTF_8));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                ScriptManager.getInstance().reload();
            }
        } catch (Exception e) {
            regen();
            e.printStackTrace();
        }
    }

    public static void checkAuth() {
        try {
            URL url = new URL("https://api.techstreet.tech/dfscript/auth/refresh/");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("authorization", AuthHandler.getAuthCode());
            con.setDoOutput(true);

            con.getInputStream();
            if (con.getResponseCode() != 200) {
                regen();
            }
        } catch (Exception e) {
            regen();
            e.printStackTrace();
        }
    }

    public static void deauth() {
        try {
            URL url = new URL("https://api.techstreet.tech/dfscript/auth/deauth/");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("authorization", AuthHandler.getAuthCode());
            con.setDoOutput(true);

            con.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void regen() {
        URL url;
        HttpURLConnection con;
        String commonSecret;
        JsonObject obj;

        try {
            // Authorization step one - Create a random clientcode
            url = new URL("https://api.techstreet.tech/dfscript/auth/clientcode/");
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            con.setReadTimeout(5000);
            con.setConnectTimeout(5000);

            String clientCode = UUID.randomUUID().toString();

            obj = new JsonObject();
            obj.addProperty("uuid", DFScript.PLAYER_UUID);
            obj.addProperty("clientcode", clientCode);

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = obj.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;

                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                ServercodeResponse servercodeResponse = DFScript.GSON.fromJson(response.toString(), ServercodeResponse.class);
                commonSecret = DigestUtils.sha256Hex(servercodeResponse.getServercode() + clientCode);
                commonSecret = commonSecret.substring(0, 30);
            }

            // Authorization step two - Fake server connect
            try {
                Session session = DFScript.MC.getSession();
                DFScript.MC.getSessionService().joinServer(session.getUuidOrNull(), session.getAccessToken(), commonSecret);
            } catch (AuthenticationException e) {
//                DFScript.LOGGER.error(e.getMessage());
//                e.printStackTrace();
            }

            // Authorization step two - Generate the authcode
            url = new URL("https://api.techstreet.tech/dfscript/auth/");
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            con.setReadTimeout(5000);
            con.setConnectTimeout(5000);

            obj = new JsonObject();
            obj.addProperty("secret", commonSecret);
            obj.addProperty("uuid", DFScript.PLAYER_UUID);

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = obj.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;

                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                AuthcodeResponse authcodeResponse = DFScript.GSON.fromJson(response.toString(), AuthcodeResponse.class);
                authCode = authcodeResponse.getAuthcode();
                DFScript.LOGGER.info("Server authorization code successfully generated!");
            }

            updateScripts();
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    public static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;

        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }

        return sb.toString();
    }

    public static String getAuthCode() {
        return authCode;
    }
}
