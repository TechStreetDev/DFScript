package io.github.techstreet.dfscript.screen.dfscript;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.features.AuthHandler;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.widget.CImage;
import io.github.techstreet.dfscript.screen.widget.CPlainPanel;
import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.screen.widget.CText;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ContributorsScreen extends CScreen {
    private final List<Contributor> contributors = new ArrayList<>();

    public ContributorsScreen() {
        super(165, 110);
        CPlainPanel root = new CPlainPanel(0, 0, 165, 110);


        root.add(new CText(5, 3, Text.literal("Contributors"), 0x333333, 1.25f, false, false));

        CPlainPanel panel = new CPlainPanel(0, 10, 165, 95);

        CScrollPanel scrollPanel = new CScrollPanel(0, 0, 165, 95);



        int y = 0;
        int x = 5;

        try {
            URL url = new URL("https://api.github.com/repos/DFScripting/DFScript/contributors");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);

            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                for (JsonElement element : JsonParser.parseReader(br).getAsJsonArray()) {
                    JsonObject object = element.getAsJsonObject();
                    this.contributors.add(new Contributor(object.get("login").getAsString(), object.get("id").getAsInt(), object.get("contributions").getAsInt(), object.get("avatar_url").getAsString()));
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Contributor contributor : contributors) {

            if (contributor.getAvatar() == null) {
                try {
                    URL url = new URL(contributor.getAvatarUrl());
                    Identifier identifier = DFScript.MC.getTextureManager().registerDynamicTexture("contributor_" + contributor.getName().toLowerCase(), new NativeImageBackedTexture(NativeImage.read(url.openStream())));
                    contributor.setAvatar(identifier);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            CImage image = new CImage(x, y, 16, 16, contributor.getAvatar().toString());

            scrollPanel.add(image);
            scrollPanel.add(new CText(x + 20, y + 6, Text.of(contributor.getName())));

            DFScript.LOGGER.log(Level.WARN, contributor.getName() + ": (" + x + ", " + y + ")");

            if (x == 75) {
                x = 5;
                y += 20;
            } else {
                x = 75;
            }
        }

        panel.add(scrollPanel);

        root.add(panel);

        widgets.add(root);

    }

    @Override
    public void close() {
        DFScript.MC.setScreen(new DFScriptScreen());
    }
}