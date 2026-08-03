package helper.creeperbox.sdk.math;

public class Vec4f {
    public final float x;
    public final float y;
    public final float z;
    public final float w;

    public Vec4f(float x, float y, float z,float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Vec4f{");
        sb.append("x=").append(x);
        sb.append(", y=").append(y);
        sb.append(", z=").append(z);
        sb.append(", w=").append(w);
        sb.append('}');
        return sb.toString();
    }
}
