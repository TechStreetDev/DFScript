package io.github.techstreet.dfscript.screen.dfscript;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.widget.CButton;
import io.github.techstreet.dfscript.screen.widget.CImage;
import io.github.techstreet.dfscript.screen.widget.CPlainPanel;
import io.github.techstreet.dfscript.screen.widget.CText;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

public class DFScriptScreen extends CScreen {
    private static final String DFSCRIPT_LOGO = "dfscript:icon_transparent.png";

    public DFScriptScreen() {
        super(110, 100);
        CPlainPanel root = new CPlainPanel(0, 0, 110, 110);

        CImage cImage = new CImage(23, 0, 64, 64, DFSCRIPT_LOGO);
        root.add(cImage);
        root.add(new CText(55, 63, Text.literal("DFScript"), 0x333333, 1.5f, true, false));
        root.add(new CText(55, 68, Text.literal("v" + DFScript.MOD_VERSION), 0x333333, 1f, true, false));

        addButtons(root);
        widgets.add(root);
    }

    private void addButtons(CPlainPanel panel) {
        // ------------------------ Features Button ------------------------
        CButton featuresButton = new CButton(5, 75, 50, 10, 0.8f, "Installed Scripts", () -> {
            DFScript.MC.send(() -> DFScript.MC.setScreen(new ScriptListScreen(true)));
        });
        panel.add(featuresButton);

        // ------------------------ Contributors Button ------------------------
        CButton contributorsButton = new CButton(5, 85, 50, 10, 0.8f, "Contributors", () -> {
            DFScript.MC.send(() -> DFScript.MC.setScreen(new ContributorsScreen()));
        });
        panel.add(contributorsButton);

        // ------------------------ Bug Report Button ------------------------
        CButton bugReport = new CButton(55, 75, 50, 10, 0.8f, "Bug Report", () -> {
            Util.getOperatingSystem().open("https://github.com/DFScripting/DFScript/issues");
        });
        panel.add(bugReport);

        // ------------------------ Dashboard Button ------------------------
        CButton options = new CButton(55, 85, 50, 10, 0.8f, "Dashboard", () -> {
            Util.getOperatingSystem().open("https://dfscript.techstreet.tech/");
        });
        panel.add(options);
    }
}