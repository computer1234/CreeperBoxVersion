package helper.creeperbox.sdk.block;

import helper.creeperbox.sdk.PointerHolder;
import helper.creeperbox.sdk.math.AxisAlignedBB;
import helper.creeperbox.sdk.math.Vec3i;

public class Block extends PointerHolder {
    private Vec3i pos;
    public Block(long pointer,Vec3i pos) {
        super(pointer);
        this.pos = pos;
    }
    public String getNameSpace(){
        return a();
    }


    public AxisAlignedBB getCollisionShape(){
        return b();
    }


    public native String a();

    public native AxisAlignedBB b();


    public Vec3i getPos() { return pos; }

    public int getRuntimeID(){
        return c();
    }

    public native int c();

    public int getData(){
        return d();
    }
    public native int d();

    

}
