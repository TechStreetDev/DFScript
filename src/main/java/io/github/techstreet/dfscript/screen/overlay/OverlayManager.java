package io.github.techstreet.dfscript.screen.overlay;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.loader.Loadable;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Date;

public class OverlayManager implements Loadable {
    private static OverlayManager instance;

    private ArrayList<Overlay> overlayText;

    @Override
    public void load() {
        instance = this;
        overlayText = new ArrayList<>();
    }

    private static class Overlay {
        public Text text;
        public Date time;
        public Date by;

        public Overlay(Text text) {
            this.text = text;
            this.time = new Date();
            this.by = new Date(time.getTime() + 5000);
        }

        public boolean past() {
            return new Date().after(by);
        }
    }

    public void add(String text) {
        add(Text.literal(text));
    }
    public void add(Text text) {
        overlayText.add(new Overlay(text));
    }

    public void render(DrawContext context) {
        while (overlayText.size() > 10) {
            overlayText.remove(0);
        }
        int y = 10;
        for (Overlay text: this.overlayText) {
            if(text.past()) continue;
            long now = new Date().getTime();
            long fadeout = text.by.getTime() - 980;
            int opacity = 0;
            if(fadeout < now) {
                opacity = (int) ((fadeout - now + 1000) * 255 / 1000);
            }
            context.drawText(DFScript.MC.textRenderer, text.text, 10, y, 0xFF5555 + ((opacity) << 24), true);
            y += 9;
        }
    }

    public static OverlayManager getInstance() {
        return instance;
    }
}
