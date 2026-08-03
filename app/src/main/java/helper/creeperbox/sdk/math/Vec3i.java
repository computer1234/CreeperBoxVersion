package helper.creeperbox.sdk.math;

public class Vec3i {

    public final int x;

    public final int y;

    public final int z;

    public Vec3i(final int xIn, final int yIn, final int zIn) {
        this.x = xIn;
        this.y = yIn;
        this.z = zIn;
    }

    @Override
    public String toString() {
        return "Vec3i{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }


}
