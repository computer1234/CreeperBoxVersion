package helper.creeperbox.feature.event.events;

import helper.creeperbox.feature.event.Event;
import helper.creeperbox.sdk.render.ScreenView;
import helper.creeperbox.sdk.render.UIRenderContext;

public class PostRender2DEvent extends Event {
    private ScreenView screenView;

    private UIRenderContext ctx;

    public PostRender2DEvent(ScreenView screenView, UIRenderContext ctx){
        this.screenView = screenView;
        this.ctx = ctx;
    }

    public UIRenderContext getCtx() {
        return ctx;
    }

    public ScreenView getScreenView() {
        return screenView;
    }
}
