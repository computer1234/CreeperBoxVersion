package helper.creeperbox.sdk.block;

import helper.creeperbox.sdk.PointerHolder;

public class Material extends PointerHolder {
    public Material(long pointer) {
        super(pointer);
    }
    public boolean isSolid(){
        return a();
    }

    public native boolean a();
    public boolean isReplaceable(){
        return b();
    }
    public native boolean b();
    public boolean isDestroyable(){
        return c();
    }
    public native boolean c();
    public boolean isLiquid(){
        return d();
    }
    public native boolean d();
    public boolean isSuperHot(){
        return e();
    }
    public native boolean e();
    public boolean isBlockPrecipitation(){
        return f();
    }
    public native boolean f();
    public boolean isBlockMotion(){
        return g();
    }
    public native boolean g();
    public float getTranslucency(){
        return h();
    }

    public native float h();

    public boolean isAir(){
        return i();
    }

    public native boolean i();
}
