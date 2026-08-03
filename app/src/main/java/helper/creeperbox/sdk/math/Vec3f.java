package helper.creeperbox.sdk.math;

public class Vec3f {

    public final float x;

    public final float y;

    public final float z;
    public Vec3f(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3f add(float x,float y,float z){
        return new Vec3f(this.x + x, this.y + y, this.z + z);
    }

    public Vec3f sub(float x,float y,float z){
        return new Vec3f(this.x - x, this.y - y, this.z - z);
    }

    @Override
    public String toString() {
        return "Vec3f{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
