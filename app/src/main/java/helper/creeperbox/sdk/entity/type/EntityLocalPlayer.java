package helper.creeperbox.sdk.entity.type;

import helper.creeperbox.sdk.InstanceGenerator;
import helper.creeperbox.sdk.block.Block;
import helper.creeperbox.sdk.component.MoveInputComponent;
import helper.creeperbox.sdk.inventory.PlayerInventory;
import helper.creeperbox.sdk.math.Vec2f;
import helper.creeperbox.sdk.math.Vec3f;
import helper.creeperbox.sdk.math.Vec3i;

import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

public class EntityLocalPlayer extends EntityPlayer{
    public EntityLocalPlayer(long pointer) {
        super(pointer);
    }
    public void attack(EntityActor other){
        a(other);
    }
    public native void a(EntityActor other);
    public void destroyBlock(Vec3i pos,int enumFacingIndex){
        b(pos,enumFacingIndex);
    }
    public native void b(Vec3i pos,int enumFacingIndex);
    public boolean startDestroyBlock(Vec3i pos,int enumFacingIndex){
        return c(pos, enumFacingIndex);
    }
    public native boolean c(Vec3i pos,int enumFacingIndex);
    public boolean continueDestroyBlock(Vec3i pos,int enumFacingIndex){
        return d(pos, enumFacingIndex);
    }
    public native boolean d(Vec3i pos,int enumFacingIndex);
    public void stopDestroyBlock(Vec3i pos){
        e2(pos);
    }

    public native void e2(Vec3i pos);
    public void startBuildBlock(Vec3i pos,int enumFacingIndex){
        f(pos,enumFacingIndex);
    }
    public native void f(Vec3i pos,int enumFacingIndex);
    public void buildBlock(Vec3i pos,int enumFacingIndex,boolean checkItem){
        g(pos, enumFacingIndex, checkItem);
    }
    public native void g(Vec3i pos,int enumFacingIndex,boolean checkItem);
    public void continueBuildBlock(Vec3i pos,int enumFacingIndex){
        h(pos,enumFacingIndex);
    }
    public native void h(Vec3i pos,int enumFacingIndex);
    public void stopBuildBlock(){
        i1();
    }
    public native void i1();
    public MoveInputComponent getMoveInput(){
        return j2();
    }
    public native MoveInputComponent j2();
    public void applyTurnDelta(Vec2f delta){
        k(delta);
    }
    public native void k(Vec2f delta);
    public Vec3f getHitStartPos(){
        return l3();
    }
    public native Vec3f l3();
    public Vec3f getHitEndPos(){
        return m();
    }
    public native Vec3f m();

    public int getHitType(){
        return n3();
    }
    public native int n3();

    public byte getHitFace(){
        return o();
    }
    public native byte o();
    public Vec3i getHitBlock(){
        return p3();
    }
    public native Vec3i p3();
    public Vec3f getHitPos(){
        return q();
    }
    public native Vec3f q();
    public void setHitStartPos(Vec3f vec){
        r(vec);
    }
    public native void r(Vec3f vec);

    public void setHitEndPos(Vec3f vec){
        s(vec);
    }
    public native void s(Vec3f vec);

    public void setHitType(int type){
        t(type);
    }
    public native void t(int type);

    public void setHitFace(byte hitFace){
        u(hitFace);
    }
    public native void u(byte hitFace);

    public void setHitBlock(Vec3i pos){
        v(pos);
    }
    public native void v(Vec3i pos);

    public void setHitPos(Vec3f pos){
        w(pos);
    }
    public native void w(Vec3f pos);
    public void sendPacket(BedrockPacket packet){
        x(InstanceGenerator.decodePacket(packet));
    }
    private native void x(byte[] data);

    public void receivePacketNoEvent(BedrockPacket packet){
        y(InstanceGenerator.decodePacket(packet));
    }
    private native void y(byte[] data);
    public void receivePacket(BedrockPacket packet){
        z(InstanceGenerator.decodePacket(packet));
    }
    public native void z(byte[] data);

    public PlayerInventory getInventory(){
        return g3();
    }
    public native PlayerInventory g3();
    public void sendPacketNoEvent(BedrockPacket packet){
        a1(InstanceGenerator.decodePacket(packet));
    }

    private native void a1(byte[] data);

    public void displayClientMessage(String msg){
        b1(msg);
    }
    public native void b1(String msg);

    public static void setNoSlow(boolean noSlow){
        c1(noSlow);
    }
    public static native void c1(boolean noSlow);

    public static void setInvMove(boolean invMove){
        d1(invMove);
    }
    public static native void d1(boolean invMove);

    public float getDestroyRate(Block block){
        return e1(block);
    }
    public native float e1(Block block);

    public static void setTimer(float timer){
        f1(timer);
    }
    public static native void f1(float timer);

    public void setSwingTick(int tick){
        g1(tick);
    }
    public native void g1(int tick);
    public int getSwingTick(){
        return h1();
    }

    public native int h1();

    public static void setItemNoRot(boolean itemNoRot){
        i2(itemNoRot);
    }

    public static native void i2(boolean itemNoRot);

    public static void setNoWeb(boolean noweb){
        j4(noweb);
    }

    public static native void j4(boolean noweb);

    public void setNoJumpDelay(int delay){
        j5(delay);
    }

    public native void j5(int delay);


    public static void setAttackRange(float range){
        f4(range);
    }
    public static native void f4(float range);

    public static void setBuildRange(float range){
        f5(range);
    }
    public static native void f5(float range);

    public static void setForcePos(boolean force){
        f6(force);
    }

    public static native void f6(boolean force);


    public static int getView(){
        return f7();
    }

    public static native int f7();
}
