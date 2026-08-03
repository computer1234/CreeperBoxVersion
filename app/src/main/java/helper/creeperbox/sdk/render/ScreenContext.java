package helper.creeperbox.sdk.render;

import helper.creeperbox.sdk.PointerHolder;

public class ScreenContext extends PointerHolder {
    public ScreenContext(long pointer) {
        super(pointer);
    }

    public float getPartialTicks(){
        return a();
    }

    public native float a();
    public Camera getCamera(){
        return b();
    }

    public native Camera b();

}
