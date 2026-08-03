package helper.creeperbox.sdk.render;

import helper.creeperbox.sdk.PointerHolder;
import helper.creeperbox.sdk.math.Matrix;

public class Camera extends PointerHolder {

    public Camera(long pointer) {
        super(pointer);
    }


    public Matrix getViewMatrix(){
        return a();
    }

    public native Matrix a();

    public Matrix getModelMatrix(){
        return b();
    }

    public native Matrix b();

    public Matrix getProjectionMatrix(){
        return c();
    }

    public native Matrix c();

}
