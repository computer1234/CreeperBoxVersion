package helper.creeperbox.sdk.entity.type;

import helper.creeperbox.sdk.item.ItemStack;
import helper.creeperbox.sdk.PointerHolder;
import helper.creeperbox.sdk.level.Level;
import helper.creeperbox.sdk.math.AxisAlignedBB;
import helper.creeperbox.sdk.math.Vec2f;
import helper.creeperbox.sdk.math.Vec3f;
import helper.creeperbox.sdk.math.Vec3i;

public class EntityActor extends PointerHolder {


    public EntityActor(long pointer) {
        super(pointer);
    }
    public int getEntityID(){
        return a();
    }
    public native int a();

    public boolean isAlive(){
        return b();
    }
    public native boolean b();

    public void swing(){
        c();
    }
    public native void c();
    public void setSprinting(boolean isSprinting){
        d(isSprinting);
    }
    public native void d(boolean isSprinting);
    public boolean isClientSide(){
        return e();
    }
    public native boolean e();
    public boolean isInvisible(){
        return f();
    }
    public native boolean f();
    public boolean canSeeActor(EntityActor other){
        return g(other);
    }
    public native boolean g(EntityActor other);

    public String getNameTag(){
        return h();
    }
    public native String h();
    public String getNameSpace(){
        return i();
    }
    public native String i();
    public boolean isOnGround(){
        return j();
    }
    public native boolean j();
    public void setOnGround(boolean onGround){
        k(onGround);
    }
    public native void k(boolean onGround);

    public float getFallDistance(){
        return l();
    }
    public native float l();

    public void setFallDistance(float value){
        m(value);
    }
    public native void m(float value);

    public long getRuntimeID(){
        return n();
    }
    public native long n();

    public void setRuntimeID(long value){
        o(value);
    }
    public native void o(long value);

    public int getGameType(){
        return p();
    }
    public native int p();

    public void setGameType(int value){
        q(value);
    }
    public native void q(int value);
    public Vec3f getBlockMovementSlowdownMultiplier(){
        return r();
    }
    public native Vec3f r();

    public void setBlockMovementSlowdownMultiplier(Vec3f value){
        s(value);
    }
    public native void s(Vec3f value);

    public float getMaxAutoStep(){
        return t();
    }
    public native float t();
    public void setMaxAutoStep(float value){
        u(value);
    }
    public native void u(float value);

    public long getUniqueID(){
        return v();
    }
    public native long v();

    public void setUniqueID(long value){
        w(value);
    }
    public native void w(long value);

    public boolean getAbilityBool(int index){
        return x(index);
    }
    public native boolean x(int index);
    public float getAbilityFloat(int index){
        return y(index);
    }
    public native float y(int index);

    public void setAbilityBool(int index,boolean value){
        z(index, value);
    }
    public native void z(int index,boolean value);

    public void setAbilityFloat(int index,float value){
        a1(index, value);
    }
    public native void a1(int index,float value);

    public float getActorHeadRotationYaw(){
        return b1();
    }
    public native float b1();

    public float getActorHeadRotationYawPrev(){
        return c1();
    }
    public native float c1();

    public void setActorHeadRotationYaw(float value){
        d1(value);
    }
    public native void d1(float value);
    public void setActorHeadRotationYawPrev(float value){
        e1(value);
    }
    public native void e1(float value);

    public float getMobBodyRotationYaw(){
        return f1();
    }
    public native float f1();
    public float getMobBodyRotationYawPrev(){
        return g1();
    }
    public native float g1();
    public void setMobBodyRotationYaw(float value){
        h1(value);
    }
    public native void h1(float value);
    public void setMobBodyRotationYawPrev(float value){
        i1(value);
    }
    public native void i1(float value);

    public Vec2f getRotation(){
        return j1();
    }
    public native Vec2f j1();
    public Vec2f getRotationPrev(){
        return k1();
    }
    public native Vec2f k1();

    public void setRotation(Vec2f value){
        l1(value);
    }
    public native void l1(Vec2f value);

    public void setRotationPrev(Vec2f value){
        m1(value);
    }
    public native void m1(Vec2f value);

    public Vec3f getPos(){
        return n1();
    }
    public native Vec3f n1();
    public void setPos(Vec3f value){
        o1(value);
    }
    public native void o1(Vec3f value);

    public Vec3f getPosPrev(){
        return p1();
    }
    public native Vec3f p1();

    public void setPosPrev(Vec3f value){
        q1(value);
    }
    public native void q1(Vec3f value);

    public Vec3f getMotion(){
        return r1();
    }
    public native Vec3f r1();
    public void setMotion(Vec3f value){
        s1(value);
    }
    public native void s1(Vec3f value);
    public void setAABB(AxisAlignedBB aabb){
        t1(aabb);
    }
    public native void t1(AxisAlignedBB aabb);
    public AxisAlignedBB getAABB(){
        return u1();
    }
    public native AxisAlignedBB u1();
    public void setHitBoxSize(Vec2f size){
        v1(size);
    }
    public native void v1(Vec2f size);
    public Vec2f getHitBoxSize(){
        return w1();
    }
    public native Vec2f w1();
    public boolean getStatusFlag(int index){
        return x1(index);
    }
    public native boolean x1(int index);
    public void setStatusFlag(int index,boolean flag){
        y1(index, flag);
    }
    public native void y1(int index,boolean flag);

    public float getAttributeCurrentValue(int index){
        return z1(index);
    }
    public native float z1(int index);

    public float getAttributeMinValue(int index){
        return a2(index);
    }
    public native float a2(int index);

    public float getAttributeMaxValue(int index){
        return b2(index);
    }
    public native float b2(int index);

    public void setAttributeCurrentValue(int index,float value){
        c2(index, value);
    }
    public native void c2(int index,float value);

    public void setAttributeMinValue(int index,float value){
        d2(index, value);
    }
    public native void d2(int index,float value);

    public void setAttributeMaxValue(int index,float value){
        e2(index, value);
    }
    public native void e2(int index,float value);
    public boolean isSprinting(){
        return f2();
    }
    public native boolean f2();
    public boolean isSneaking(){
        return g2();
    }
    public native boolean g2();
    public void setSneaking(boolean sneaking){
        h2(sneaking);
    }
    public native void h2(boolean sneaking);

    public boolean isPrimaryLocalPlayer(){
        return i2();
    }
    public native boolean i2();

    public EntityActor getVehicle(){
        return k2();
    }
    public native EntityActor k2();

    public Level getLevel(){
        return l2();
    }
    public native Level l2();
    public ItemStack getItemInHand(){
        return m2();
    }
    public native ItemStack m2();

    public ItemStack getItemOffHand(){
        return n2();
    }
    public native ItemStack n2();
    public ItemStack getArmor(int slot){
        return o2(slot);
    }
    public native ItemStack o2(int slot);

    public native Vec3i[] p2();
    public Vec3i[] getEffectList(){
        return p2();
    }


}
