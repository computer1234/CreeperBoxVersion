package helper.creeperbox.sdk.level;

import android.util.Pair;

import helper.creeperbox.sdk.PointerHolder;
import helper.creeperbox.sdk.math.Vec3i;

import helper.creeperbox.sdk.block.Block;
import helper.creeperbox.sdk.block.Material;
import helper.creeperbox.sdk.entity.type.EntityActor;

public class Level extends PointerHolder {
    public Level(long pointer) {
        super(pointer);
    }
    public Block getBlock(Vec3i pos){
        return a(pos);
    }
    public native Block a(Vec3i pos);

    public Material getMaterial(Vec3i pos){
        return b(pos);
    }
    public native Material b(Vec3i pos);
    public EntityActor[] getRuntimeActorList(){
        return c();
    }
    public native EntityActor[] c();

    public long getTick(){
        return d();
    }
    public native long d();

    public void setTick(long tick){
        e(tick);
    }
    public native void e(long tick);

    public EntityActor[] getPlayers(){
        return f();
    }

    public native EntityActor[] f();

    public Pair<Long,String>[] getPlayerList(){
        return g();
    }

    public native Pair<Long,String>[] g();
}
