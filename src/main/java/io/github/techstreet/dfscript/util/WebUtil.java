package io.github.techstreet.dfscript.util;

import io.github.techstreet.dfscript.DFScript;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class WebUtil {

    public static void getAsync(String url, Consumer<String> callback) {
        HttpClient client = HttpClient.newHttpClient();
        client.sendAsync(HttpRequest.newBuilder(URI.create(url)).GET().build(), HttpResponse.BodyHandlers.ofString())
            .thenApply(HttpResponse::body)
            .thenAccept(s -> {
                DFScript.MC.submit(() -> {
                    callback.accept(s);
                });
            });
    }

    public static String getString(String urlToRead, Charset charset) throws IOException {
        URL url = new URL(urlToRead);
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), charset));
        StringBuilder builder = new StringBuilder();
        String line;

        while ((line = in.readLine()) != null) {
            builder.append("\n").append(line);
        }
        in.close();
        return builder.toString();
    }

    public static String getString(String urlToRead) throws IOException {
        return getString(urlToRead, StandardCharsets.UTF_8);
    }
}
