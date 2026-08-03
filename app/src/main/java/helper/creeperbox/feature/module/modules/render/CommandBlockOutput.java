package helper.creeperbox.feature.module.modules.render;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;

import android.graphics.Color;

import org.cloudburstmc.protocol.bedrock.packet.BlockEntityDataPacket;
import org.cloudburstmc.protocol.bedrock.packet.NetworkSettingsPacket;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.component.CommandBlockComponent;
import helper.creeperbox.feature.component.RenderHelperComponent;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.PacketReceiveEvent;
import helper.creeperbox.feature.event.events.Render2DEvent;
import helper.creeperbox.feature.event.events.Render3DEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.math.AxisAlignedBB;
import helper.creeperbox.sdk.math.Vec3f;
import helper.creeperbox.sdk.math.Vec3i;
import helper.creeperbox.sdk.math.Vec4f;
import helper.creeperbox.utils.render.Render2DUtil;
import helper.creeperbox.utils.render.animation.Animation;
import helper.creeperbox.utils.render.animation.Easing;

@ModuleInfo(name = "获取命令内容", category = Category.Render)
public class CommandBlockOutput extends Module {

    private Vec4f projData = new Vec4f(0,0,0,0);
    private boolean success;
    private Vec3i lastPos;
    private Animation animation = new Animation(Easing.Decelerate,200);
    private String command = "";
    private String commandBlockName = "";
    private String condition = "";
    private String needRedStone = "";
    private int delay = 0;

    @SubscribeEvent
    public void preRender3D(Render3DEvent event) {
        EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
        if(player == null) return;
        int type = player.getHitType();

        success = false;
        Vec3i pos = player.getHitBlock();
        String name = player.getLevel().getBlock(pos).getNameSpace();

        if(type == 0 && name.equals("minecraft:command_block")){
            commandBlockName = "脉冲";
            success = true;
        }

        if(type == 0 && name.equals("minecraft:chain_command_block")){
            commandBlockName = "锁链";
            success = true;
        }

        if(type == 0 && name.equals("minecraft:repeating_command_block")){
            commandBlockName = "循环";
            success = true;
        }

        CommandBlockData data = CommandBlockComponent.commandBlockMap.get(new BlockPos(pos.x,pos.y,pos.z));
        if(data==null){
            success = false;
        }else{
            command = data.command;
            condition = data.condition?"有条件":"无条件";
            needRedStone = data.auto?"保持开启":"红石控制";
            delay = data.tickDelay;
        }

        animation.run(success?1f:0f);

        if(success){
            lastPos = pos;
        }

        if(animation.isFinished() && !success){
            return;
        }

        AxisAlignedBB aabb = new AxisAlignedBB(lastPos.x,lastPos.y,lastPos.z,lastPos.x+1f,lastPos.y+1f,lastPos.z+1f);
        final List<Vec3f> vectors = Arrays.asList(new Vec3f((float) aabb.minX, (float) aabb.minY, (float) aabb.minZ),
                new Vec3f((float) aabb.minX, (float) aabb.maxY, (float) aabb.minZ),
                new Vec3f((float) aabb.maxX, (float) aabb.minY, (float) aabb.minZ),
                new Vec3f((float) aabb.maxX, (float) aabb.maxY, (float) aabb.minZ),
                new Vec3f((float) aabb.minX, (float) aabb.minY, (float) aabb.maxZ),
                new Vec3f((float) aabb.minX, (float) aabb.maxY, (float) aabb.maxZ),
                new Vec3f((float) aabb.maxX, (float) aabb.minY, (float) aabb.maxZ),
                new Vec3f((float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ));

        Vec4f position = null;
        for (Vec3f vector : vectors) {
            vector = RenderHelperComponent.worldToScreen(RenderHelperComponent.render3DData,vector);
            if (vector.z > 0 && vector.z < 1) {
                if (position == null) position = new Vec4f(vector.x, vector.y, vector.z, 0);
                position = new Vec4f(Math.min(vector.x, position.x),Math.min(vector.y, position.y),Math.max(vector.x, position.z),Math.max(vector.y, position.w));
            }
        }

        if(position!=null){
            success = true;
            projData = position;
        }else {
            success = false;
        }

    }

    @SubscribeEvent
    public void renderHud(Render2DEvent event){
        EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
        if(player == null) return;

        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);



        if(projData!=null){
            float startX = projData.x;
            float startY = projData.y;
            float endPosX = projData.z;
            float endPosY = projData.w;
            float centerX = startX + (endPosX-startX)*0.5f;
            float centerY = startY + (endPosY-startY)*0.5f;

            float width = 500f;
            float height = 240f;
            float value = animation.getValue();
            float upMove = animation.getValue()*150f;

            Render2DUtil.startScale(centerX,centerY-upMove,value);
            float x = centerX-width/2f;
            float y = centerY-height/2f-upMove;
            RenderHelperComponent.drawGradientRound(x,y,width,height,40f,0x80000000,0x80000000,true);
            RenderHelperComponent.drawGradientRound(x+15f,y+15f,width-30f,110f,20f,0x1AFFFFFF,0x1AFFFFFF,true);
            RenderHelperComponent.pingfang18.drawFixWidthString(command,x+25f,y+25f,width-50f,4, Color.WHITE);
            RenderHelperComponent.pingfang18.drawString(String.format("方块类型:%s",commandBlockName),x+25f,y+130f,Color.WHITE);
            RenderHelperComponent.pingfang18.drawString(String.format("条件:%s",condition),x+25f,y+160f,Color.WHITE);
            RenderHelperComponent.pingfang18.drawString(String.format("红石:%s",needRedStone),x+25f,y+185f,Color.WHITE);
            RenderHelperComponent.pingfang18.drawString(String.format("延迟:%d",delay),x+25f,y+210f,Color.WHITE);
            Render2DUtil.endScale();

            RenderHelperComponent.bloomUtil.addTask(new Runnable() {
                 @Override
                public void run() {
                     Render2DUtil.startScale(centerX,centerY-upMove,value);
                     RenderHelperComponent.drawGradientRound(centerX-width/2f,centerY-height/2f-upMove,width,height,40f,RenderInterface.applyColor(270),RenderInterface.applyColor(0),RenderInterface.applyColor(180),RenderInterface.applyColor(90));
                     Render2DUtil.endScale();
                 }
            });

        }




    }

    public static class CommandBlockData {
        public boolean condition;
        public boolean auto;
        public int tickDelay;
        public String command;

        public CommandBlockData(boolean condition, boolean auto, int tickDelay,String command) {
            this.condition = condition;
            this.auto = auto;
            this.tickDelay = tickDelay;
            this.command = command;
        }
    }

    public static class BlockPos {
        public int x;
        public int y;
        public int z;
        public BlockPos(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof BlockPos blockPos)) return false;
            return x == blockPos.x && y == blockPos.y && z == blockPos.z;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, z);
        }
    }
}
