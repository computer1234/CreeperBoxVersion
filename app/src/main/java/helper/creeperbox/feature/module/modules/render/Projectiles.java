package helper.creeperbox.feature.module.modules.render;

import android.graphics.Color;
import android.opengl.GLES20;

import java.util.ArrayList;
import java.util.List;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.component.RenderHelperComponent;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.PostRender3DEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.sdk.block.Block;
import helper.creeperbox.sdk.block.EnumFacing;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.math.AxisAlignedBB;
import helper.creeperbox.sdk.math.Vec2f;
import helper.creeperbox.sdk.math.Vec3f;
import helper.creeperbox.utils.mc.PlayerUtil;

@ModuleInfo(name = "抛物线", category = Category.Render)
public class Projectiles extends Module {
    @SubscribeEvent
    public void onRender3D(PostRender3DEvent event) {


        if(RenderHelperComponent.render3DData==null) {
            return;
        }

        EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
        if(player == null || !player.getItemInHand().getNameSpace().equals("item.ender_pearl")) {
            return;
        }

        boolean usingDepth = GLES20.glIsEnabled(GLES20.GL_DEPTH_TEST);
        boolean usingCull = GLES20.glIsEnabled(GLES20.GL_CULL_FACE);

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glDisable(GLES20.GL_CULL_FACE);

        float[] clone = new float[16];
        System.arraycopy(RenderHelperComponent.render3DData.mvpMatrix.m,0,clone,0,16);
        float partialTicks = RenderHelperComponent.render3DData.partialTicks;
        Vec3f cameraPos = RenderHelperComponent.render3DData.cameraPos;
        Vec2f rot = player.getRotation();
        Vec3f pos = player.getPos();
        Vec3f prev = player.getPosPrev();
        float x = prev.x + (pos.x - prev.x) * partialTicks;
        float y = prev.y + (pos.y - prev.y) * partialTicks;
        float z = prev.z + (pos.z - prev.z) * partialTicks;

        float gravity = 0.03f;
        float size = 0.25f;
        float motionSlowdown = 0.99f;
        float motionFactor = 1.5f;

        float yawRadians = (float) Math.toRadians(rot.y);
        float pitchRadians = (float) Math.toRadians(rot.x);

        float posX = (float) (x - Math.cos(yawRadians)*0.16f);
        float posY = y - 0.10000000149011612f;
        float posZ = (float) (z - Math.sin(yawRadians)*0.16f);

        float motionX = (float) (-Math.sin(yawRadians) * Math.cos(pitchRadians) * 0.4f);
        float motionY = (float) (-Math.sin(pitchRadians) * 0.4f);
        float motionZ = (float) (Math.cos(yawRadians) * Math.cos(pitchRadians) * 0.4f);
        float distance = (float) Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);

        motionX /= distance;
        motionY /= distance;
        motionZ /= distance;
        motionX += 0.007499999832361937f * 0.5f;
        motionY += 0.007499999832361937f * 0.5f;
        motionZ += 0.007499999832361937f * 0.5f;
        motionX *= motionFactor;
        motionY *= motionFactor;
        motionZ *= motionFactor;

        boolean hasLanded = false;
        List<Vec3f> list = new ArrayList<>();
        while (!hasLanded && posY > -64f) {
            posX += motionX;
            posY += motionY;
            posZ += motionZ;
            motionX *= motionSlowdown;
            motionY *= motionSlowdown;
            motionZ *= motionSlowdown;
            motionY -= gravity;
            list.add(new Vec3f(posX-cameraPos.x, posY-cameraPos.y, posZ-cameraPos.z));

            //block
            EnumFacing facing = getNearestCollisionDirection(player,new AxisAlignedBB(posX,posY,posZ,posX+size,posY+size,posZ+size),new Vec3f(motionX,motionY,motionZ));
            if(facing!=null){
                hasLanded = true;

                float xOff = 0.0f;
                float yOff = 0.0f;
                float zOff = 0.0f;

                if(facing == EnumFacing.UP || facing == EnumFacing.DOWN) {
                    xOff = 0.5f;
                    zOff = 0.5f;
                    yOff = -1f;
                }

                if(facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH) {
                    xOff = 0.5f;
                    zOff = 0.5f;
                    yOff = -0.5f;
                }

                if(facing == EnumFacing.WEST || facing == EnumFacing.EAST) {
                    xOff = 0.5f;
                    zOff = 0.5f;
                    yOff = -0.5f;
                }

                RenderHelperComponent.rectangle(posX-cameraPos.x-xOff,posY-cameraPos.y-yOff,posZ-cameraPos.z-zOff,facing,0xff00bf00,clone);
            }
        }

        RenderHelperComponent.drawLine(list, Color.WHITE,clone);


        if(!usingDepth){
            GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        }
        if(usingCull){
            GLES20.glEnable(GLES20.GL_CULL_FACE);
        }

    }


    public static EnumFacing getNearestCollisionDirection(EntityLocalPlayer player, AxisAlignedBB aabb ,Vec3f motion) {

        ArrayList<Block> collidingBlocks = PlayerUtil.getCollidingBoundingBoxes(player, aabb.offset(motion.x,motion.y,motion.z));

        if (collidingBlocks.isEmpty()) {
            return null;
        }

        if(!PlayerUtil.getCollidingBoundingBoxes(player,aabb.offset(motion.x,0,0)).isEmpty()){
            if(motion.x>0) return EnumFacing.EAST;
            else return EnumFacing.WEST;
        }

        if(!PlayerUtil.getCollidingBoundingBoxes(player,aabb.offset(0,0,motion.z)).isEmpty()){
            if(motion.z>0) return EnumFacing.SOUTH;
            else return EnumFacing.NORTH;
        }

        if(motion.y>0) return EnumFacing.UP;
        return EnumFacing.DOWN;
    }

}
