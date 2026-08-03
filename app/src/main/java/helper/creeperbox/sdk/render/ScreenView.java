package helper.creeperbox.sdk.render;

import helper.creeperbox.sdk.PointerHolder;

public class ScreenView extends PointerHolder {
    public ScreenView(long pointer) {
        super(pointer);
    }

    public void tryCloseScreen(){
        a();
    }
    public native void a();

    public String getScreenName(){
        return b();
    }
    public native String b();

}
