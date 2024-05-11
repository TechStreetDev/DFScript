package io.github.techstreet.dfscript.screen;

public abstract class CReloadableScreen extends CScreen {

    public abstract void reload();

    protected CReloadableScreen(int width, int height) {
        super(width, height);
    }
}
