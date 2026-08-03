package helper.creeperbox.feature.event.events;

import helper.creeperbox.feature.event.Event;
import helper.creeperbox.sdk.render.LevelRenderer;
import helper.creeperbox.sdk.render.ScreenContext;

public class Render3DEvent extends Event {

    private LevelRenderer levelRenderer;
    private ScreenContext ctx;


    public Render3DEvent(LevelRenderer levelRenderer, ScreenContext ctx) {
        this.levelRenderer = levelRenderer;
        this.ctx = ctx;
    }

    public LevelRenderer getLevelRenderer() {
        return levelRenderer;
    }

    public void setLevelRenderer(LevelRenderer levelRenderer) {
        this.levelRenderer = levelRenderer;
    }

    public ScreenContext getCtx() {
        return ctx;
    }

    public void setCtx(ScreenContext ctx) {
        this.ctx = ctx;
    }
}
