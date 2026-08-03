package helper.creeperbox.feature.module.modules.movement;

import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.packet.MovePlayerPacket;

import java.io.IOException;
import java.util.List;

import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.PacketSendEvent;
import helper.creeperbox.feature.event.events.PostMoveEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.sdk.block.Block;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.math.AxisAlignedBB;
import helper.creeperbox.sdk.math.Vec3f;
import helper.creeperbox.utils.mc.PlayerUtil;


@ModuleInfo(name = "爬墙", category = Category.Movement)
public class Spider extends Module {

    private final NumberValue speed = new NumberValue("速度", this,0.7,0.1, 0.4, 0.05);

    private boolean lastSpider = false;


    public static float getValidY(float y) {
        float multiplied = y * 32.0f;
        if (multiplied % 1.0 == 0.0) {
            return y;
        } else {
            int nearestMultiple = (int) Math.ceil(multiplied);
            return nearestMultiple / 32.0f;
        }

    }

    @SubscribeEvent
    public void onPacket(PacketSendEvent event) throws IOException {
        if(event.getPacket() instanceof MovePlayerPacket){
            MovePlayerPacket packet = (MovePlayerPacket) event.getPacket();
            if(lastSpider){
                float y = getValidY(packet.getPosition().getY()-1.7f)+1.62001f;
                packet.setPosition(Vector3f.from(packet.getPosition().getX(),y,packet.getPosition().getZ()));
            }
        }
    }

    @Override
    public void onEnable() {
        lastSpider = false;
    }

    @SubscribeEvent
    public void onMove(PostMoveEvent event){
        EntityLocalPlayer player = event.getPlayer();

        AxisAlignedBB aabb = player.getAABB();
        Vec3f motion = player.getMotion();

        float motionX = motion.x;
        float motionZ = motion.z;

        List<Block> mayCollide = PlayerUtil.getCollidingBoundingBoxes(player,aabb.offset(motionX,0,motionZ));
        if(mayCollide.isEmpty()){
            if(lastSpider){
                player.setMotion(new Vec3f(motionX,0f,motionZ));
            }
            lastSpider = false;
            return;
        }

        lastSpider = true;
        float motionY = speed.getCurrentValue().floatValue();
        if(PlayerUtil.getCollidingBoundingBoxes(player,aabb.offset(motionX,motionY,motionZ)).isEmpty()){
            Vec3f pos = player.getPos();
            float maxY = -100;
            for(Block b : mayCollide){
                double bY = b.getCollisionShape().maxY;
                if(bY > maxY){
                    maxY = (float) bY;
                }
            }

            player.setPos(new Vec3f(pos.x,maxY + 1.62001f,pos.z));
            player.setMotion(new Vec3f(motionX,0.0f,motionZ));
            lastSpider = false;
            return;
        }
        player.setMotion(new Vec3f(motionX,motionY,motionZ));
    }


}
