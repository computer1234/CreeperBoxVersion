package helper.creeperbox.sdk.math;

public class Vec2f {
    public final float x;
    public final float y;
    public Vec2f(float x, float y)
    {
        this.x = x;
        this.y = y;
    }


    @Override
    public String toString() {
        return "Vec2f{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
