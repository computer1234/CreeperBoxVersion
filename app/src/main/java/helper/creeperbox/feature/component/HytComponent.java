package helper.creeperbox.feature.component;

import android.util.Log;

import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityDataType;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityFlag;
import org.cloudburstmc.protocol.bedrock.packet.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.event.EventPriority;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.PacketReceiveEvent;
import helper.creeperbox.feature.event.events.PacketSendEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.modules.movement.Fly;
import helper.creeperbox.feature.module.modules.movement.GodMode;
import helper.creeperbox.sdk.block.Block;
import helper.creeperbox.sdk.entity.type.EntityActor;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.level.Level;
import helper.creeperbox.sdk.math.Vec3f;
import helper.creeperbox.sdk.math.Vec3i;
import helper.creeperbox.sdk.network.packet.modify.NeteasePlayerAuthInputPacket;
import helper.creeperbox.utils.mc.PlayerUtil;

public class HytComponent {

    private static NeteasePlayerAuthInputPacket lastAuth;
    public static HashMap<Long,Integer> deadMap = new HashMap<>();


    public static Vector3f lastOnGround;
    @SubscribeEvent
    public void calcGround(TickEvent event){
        EntityLocalPlayer player = event.getPlayer();
        Level level = player.getLevel();
        Vec3f pos = player.getPos();
        Vector3i blockPos = Vector3i.from(Math.floor(pos.x),pos.y-EYE_HEIGHT-0.01f,pos.z);
        if(!level.getMaterial(new Vec3i(blockPos.getX(),blockPos.getY(),blockPos.getZ())).isAir()){
            lastOnGround = Vector3f.from(pos.x,pos.y,pos.z);
        }
    }





    public static float distanceTo(Vector3f thiz,Vector3f other) {
        return (float) Math.sqrt(Math.pow(thiz.getX() - other.getX(), 2) + Math.pow(thiz.getZ() - other.getZ(), 2));
    }
    @SubscribeEvent
    public void updateLevel(PacketReceiveEvent event){
        if(event.getPacket() instanceof SetDifficultyPacket || event.getPacket() instanceof AdventureSettingsPacket){
            lastOnGround = null;
        }
    }


    @SubscribeEvent
    public void handlePlayerFlag(PacketReceiveEvent event) {
        if(event.getPacket() instanceof MovePlayerPacket){
            EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
            if(player == null) return;
            MovePlayerPacket packet = (MovePlayerPacket) event.getPacket();
            Vector3f pos = packet.getPosition();
            if(pos.getY()>800 && player.getRuntimeID() == packet.getRuntimeEntityId()){
                HytComponent.moveTo(player,pos,null);
                packet.setPosition(Vector3f.from(pos.getX(),player.getPos().y,pos.getZ()));
            }
        }
    }

    @SubscribeEvent
    public void handleYComponent(PacketSendEvent event){
        if(event.getPacket() instanceof InventoryTransactionPacket) {
            if((Fly.INSTANCE !=null && Fly.INSTANCE.isEnable()) || (GodMode.INSTANCE !=null && GodMode.INSTANCE.isEnable())){
                EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
                if(player!=null && lastAuth !=null && lastAuth.getPosition().getY()>800){
                    Vector3f lastPos = lastAuth.getPosition();
                    Vec3f pos = player.getPos();
                    moveTo(player,Vector3f.from(lastPos.getX(),pos.y,lastPos.getZ()),null);
                    event.setCancelled();
                    player.sendPacketNoEvent(event.getPacket());
                    moveTo(player,lastPos,null);
                }
            }
        }
    }



    @SubscribeEvent
    public void onDeathCheck(TickEvent event){
        EntityLocalPlayer player = event.getPlayer();
        Iterator<Map.Entry<Long, Integer>> iterator = deadMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, Integer> entry = iterator.next();
            Long key = entry.getKey();
            int tick = entry.getValue();
            if (tick > 20*8) {
                iterator.remove();
            } else {
                deadMap.put(key, tick + 1);
            }
        }
    }

    @SubscribeEvent(EventPriority.VERY_HIGH)
    public void handleAuth(PacketSendEvent event){
        if(event.getPacket() instanceof NeteasePlayerAuthInputPacket){
            lastAuth = (NeteasePlayerAuthInputPacket) event.getPacket();
        }
    }








    @SubscribeEvent
    public void onDeathPacketCheck(PacketReceiveEvent event){
        if(event.getPacket() instanceof SetEntityDataPacket){
            SetEntityDataPacket packet = (SetEntityDataPacket) event.getPacket();
            if(packet.getMetadata()!=null && packet.getMetadata().getFlags()!= null && packet.getMetadata().getFlags().containsKey(EntityFlag.INVISIBLE)){
                deadMap.put(packet.getRuntimeEntityId(),0);
            }
        }
    }




    public static final float fallHeight = 0.25f;

    public static void updateTick(EntityLocalPlayer player) {
        long currentTick = player.getLevel().getTick();
        lastAuth.setTick(currentTick);
        currentTick++;
        player.getLevel().setTick(currentTick);
    }


    public static void moveTo(EntityLocalPlayer player, Vector3f to, Vector2f rot) {
        updateTick(player);
        Vector3f lastPos = lastAuth.getPosition();
        lastAuth.setDelta(Vector3f.from(to.getX() - lastPos.getX(), to.getY() - lastPos.getY(), to.getY() - lastPos.getY()));
        lastAuth.setPosition(to);
        if(rot!=null){
            lastAuth.setRotation(Vector3f.from(rot.getX(),rot.getY(),rot.getY()));
        }
        player.sendPacketNoEvent(lastAuth);
    }



    public static void moveXZ(EntityLocalPlayer player, Vector2f motion) {
        updateTick(player);
        Vector3f lastPos = lastAuth.getPosition();
        lastAuth.setDelta(Vector3f.from(motion.getX(), -fallHeight, motion.getY()));
        lastAuth.setPosition(Vector3f.from(lastPos.getX() + motion.getX(), lastPos.getY() - fallHeight, lastPos.getZ() + motion.getY()));
        player.sendPacketNoEvent(lastAuth);
    }

    public static void moveToY(EntityLocalPlayer player, float to) {
        updateTick(player);
        Vector3f lastPos = lastAuth.getPosition();
        lastAuth.setDelta(Vector3f.from(0.0f, to - lastPos.getY(), 0.0f));
        lastAuth.setPosition(Vector3f.from(lastPos.getX(), to, lastPos.getZ()));
        player.sendPacketNoEvent(lastAuth);
    }

    public static final float stepLength = 0.34f;
    public static final float minY = 299f;
    public static final float maxY = 9999f;

    private static final float EYE_HEIGHT = 1.62f;
    public static final double maxAttackDistance = 5d;



    //传送到指定位置
    public static boolean teleportToPosRot(EntityLocalPlayer player,Vector3f to,Vector2f rot){

        // 1. 判断auth存在
        if(lastAuth == null) return false;


        Vector3f from = lastAuth.getPosition();


        // 3. 部分参数计算
        Vector2f fromXZ = Vector2f.from(from.getX(), from.getZ());
        Vector2f toXZ = Vector2f.from(to.getX(), to.getZ());
        if(fromXZ.equals(toXZ)) {
            moveTo(player,to,rot);
            return true;
        }

        float distance = fromXZ.distance(toXZ);
        Vector2f direction = toXZ.sub(fromXZ).normalize();
        Vector2f motion = Vector2f.from(direction.getX() * stepLength, direction.getY() * stepLength);

        // 4. 判断能否到达to
        if(distance > (maxY - Math.max(minY, to.getY())) / fallHeight * stepLength) {
            return false;
        }

        // 5. 起飞
        moveToY(player, maxY);

        // 6.跑过去
        for(int i = 0; i < (distance / stepLength); i++) {
            moveXZ(player, motion);
        }

        // 7. 抵达终点
        moveTo(player, to, null);

        player.buildBlock(new Vec3i((int) to.getX(), (int) to.getY(), (int) to.getZ()),1,false);
        return true;
    }




    //传送路径
    public static boolean teleportPath(EntityLocalPlayer player,Vector3f to){

        // 1. 判断auth存在
        if(lastAuth == null) return false;

        Vector3f from = lastAuth.getPosition();

        // 3. 部分参数计算
        Vector2f fromXZ = Vector2f.from(from.getX(), from.getZ());
        Vector2f toXZ = Vector2f.from(to.getX(), to.getZ());
        if(fromXZ.equals(toXZ)) {
            return true;
        }

        float distance = fromXZ.distance(toXZ);
        Vector2f direction = toXZ.sub(fromXZ).normalize();
        Vector2f motion = Vector2f.from(direction.getX() * stepLength, direction.getY() * stepLength);

        // 4. 判断能否到达to
        if(distance > (from.getY() - Math.max(minY, to.getY())) / fallHeight * stepLength) {
            return false;
        }


        // 6.跑过去
        for(int i = 0; i < (distance / stepLength); i++) {
            moveXZ(player, motion);
        }

        return true;
    }

    public static float getCurrentY(){
        return lastAuth.getPosition().getY();
    }


    public static Vector3f getLastPos(){
        return lastAuth.getPosition();
    }

    private static boolean moveBelow(EntityLocalPlayer player){
        Vector3f from = null;
        Vector3f fromPos = lastAuth.getPosition();
        Vector3i belowBlock = Vector3i.from(fromPos.getFloorX(), (int)(fromPos.getY()-EYE_HEIGHT), fromPos.getFloorZ());

        for(int y = belowBlock.getY(); y > 0; y--){
            if(!player.getLevel().getMaterial(new Vec3i(belowBlock.getX(), y, belowBlock.getZ())).isAir()){
                from = Vector3f.from(fromPos.getX(), player.getLevel().getBlock(new Vec3i(belowBlock.getX(), y, belowBlock.getZ())).getCollisionShape().maxY+EYE_HEIGHT,fromPos.getZ());
                break;
            }
        }

        if(from == null) return false;
        moveTo(player, from, null);
        return true;
    }


    private static Vector3f tryGetBelowA(EntityLocalPlayer player, EntityActor actor){
        Vector3f from = null;
        Vec3f actorPos = actor.getPos();
        Vector3i belowBlock = Vector3i.from(Math.floor(actorPos.x), actorPos.y-EYE_HEIGHT, Math.floor(actorPos.z));
        for(int i = 0;i>-3;i--){
            if(!player.getLevel().getMaterial(new Vec3i(belowBlock.getX(), belowBlock.getY()+i, belowBlock.getZ())).isAir()){
                from = Vector3f.from(actorPos.x, player.getLevel().getBlock(new Vec3i(belowBlock.getX(), belowBlock.getY()+i, belowBlock.getZ())).getCollisionShape().maxY+EYE_HEIGHT,actorPos.z);
                break;
            }
        }
        return from;
    }

    private static Vector3f tryGetBelowB(EntityLocalPlayer player,EntityActor actor){
        float bestY = 0f;
        Vec3i bestBlockPos = null;
        Vec3f actorPos = actor.getPos();
        double minDistance = Double.MAX_VALUE;
        List<Block> collide = PlayerUtil.getCollidingBoundingBoxes(player, actor.getAABB().expand(4, 4, 4));

        Level level = player.getLevel();

        for (Block block : collide) {
            Vec3i blockPos = block.getPos();
            double distance = blockTopToPos(blockPos, actorPos);

            if (distance < minDistance && level.getBlock(new Vec3i(blockPos.x,blockPos.y+1,blockPos.z)).getNameSpace().equals("minecraft:air") && level.getBlock(new Vec3i(blockPos.x,blockPos.y+2,blockPos.z)).getNameSpace().equals("minecraft:air")) {
                bestY = (float) block.getCollisionShape().maxY + EYE_HEIGHT;
                bestY = roundUpToMatchCondition(bestY);
                minDistance = distance;
                bestBlockPos = block.getPos();
            }
        }

        if (bestBlockPos == null){
            return null;
        }
        if (minDistance >= maxAttackDistance){
            return null;
        }

        return Vector3f.from(bestBlockPos.x + 0.5f,bestY,bestBlockPos.z + 0.5f);
    }


    public static boolean teleportToActor(EntityLocalPlayer player, EntityActor actor) { // to为玩家附近(r=4)最近落脚点

        // 1. 判断auth存在
        if (lastAuth == null) return false;

        // 2. 求落脚点
        Vector3f pos = tryGetBelowA(player,actor);
        if(pos==null) {
            pos = tryGetBelowB(player,actor);
            if(pos == null) return false;
        }

        return teleportToPosRot(player,pos, calcRotation(new Vec3f(pos.getX(),pos.getY(),pos.getZ()),actor.getPos()));
    }

    public static boolean teleportBelow(EntityLocalPlayer player){
        Vector3f from = null;
        Vector3f fromPos = lastAuth.getPosition();
        Vector3i belowBlock = Vector3i.from(fromPos.getFloorX(), (int)(fromPos.getY()-EYE_HEIGHT), fromPos.getFloorZ());

        for(int y = belowBlock.getY(); y > 0; y--){
            if(!player.getLevel().getMaterial(new Vec3i(belowBlock.getX(), y, belowBlock.getZ())).isAir()){
                from = Vector3f.from(fromPos.getX(), player.getLevel().getBlock(new Vec3i(belowBlock.getX(), y, belowBlock.getZ())).getCollisionShape().maxY+EYE_HEIGHT,fromPos.getZ());
                break;
            }
        }

        if(from == null) return false;
        moveTo(player, from, null);
        return true;
    }


    public static Vector2f calcRotation(Vec3f from,Vec3f to){
        final double xSize = to.x - from.x;
        final double ySize = to.y - from.y;
        final double zSize = to.z - from.z;
        final double theta = Math.sqrt(xSize * xSize + zSize * zSize);
        final float yaw = (float) (Math.atan2(zSize, xSize) * 180 / Math.PI) - 90;
        final float pitch = (float) (-(Math.atan2(ySize, theta) * 180 / Math.PI));
        return Vector2f.from(pitch,yaw);
    }
    public static boolean teleportToPos(EntityLocalPlayer player, Vector3f to){
        return teleportToPosRot(player,to,null);
    }

    private static double blockTopToPos(Vec3i blockPos, Vec3f pos){
        double dx = blockPos.x - pos.x + 0.5d;
        double dy = blockPos.y - pos.y + EYE_HEIGHT + 0.0001d;
        double dz = blockPos.z - pos.z + 0.5d;
        return Math.sqrt(dx*dx+dy*dy+dz*dz);
    }
    public static float roundUpToMatchCondition(float y) {
        float modValue = y % 0.015625f;
        float targetValue = 0.010627747f;
        float leniency = 0.00001f;

        // 计算目标范围
        float lowerBound = targetValue - leniency; // 0.010617747f
        float upperBound = targetValue + leniency; // 0.010637747f

        // 如果当前余数小于下限，向上调整y
        if (modValue < lowerBound) {
            y += (lowerBound - modValue);
        }
        // 如果当前余数大于上限，向上调整y
        else if (modValue > upperBound) {
            y += (upperBound - modValue);
        }

        return y;
    }


}
