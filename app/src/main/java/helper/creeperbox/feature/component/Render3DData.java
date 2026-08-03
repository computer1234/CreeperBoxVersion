package helper.creeperbox.feature.component;

import helper.creeperbox.sdk.math.Matrix;
import helper.creeperbox.sdk.math.Vec3f;

public class Render3DData {

    public Matrix viewMatrix;

    public Matrix modelMatrix;

    public Matrix proMatrix;
    public Matrix mvpMatrix;

    public Vec3f cameraPos;

    public float partialTicks;
}
