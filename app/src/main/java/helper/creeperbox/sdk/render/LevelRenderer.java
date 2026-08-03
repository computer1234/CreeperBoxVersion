package helper.creeperbox.sdk.render;

import helper.creeperbox.sdk.PointerHolder;
import helper.creeperbox.sdk.math.Vec3f;

public class LevelRenderer extends PointerHolder {

    public LevelRenderer(long pointer) {
        super(pointer);
    }

    public Vec3f getCameraPos(){
        return a();
    }

    public native Vec3f a();

    public Vec3f getTargetCameraPos(){
        return b();
    }

    public native Vec3f b();

    public native void c();

    public void refreshWorld(){
        c();
    }
}
