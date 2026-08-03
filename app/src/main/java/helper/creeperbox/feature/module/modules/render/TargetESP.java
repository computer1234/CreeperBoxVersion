package helper.creeperbox.feature.module.modules.render;

import android.opengl.GLES20;

import org.cloudburstmc.protocol.bedrock.data.PlayerActionType;
import org.cloudburstmc.protocol.bedrock.data.inventory.transaction.InventoryTransactionType;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.InventoryTransactionPacket;
import org.cloudburstmc.protocol.bedrock.packet.PlayerActionPacket;
import org.cloudburstmc.protocol.bedrock.packet.RemoveEntityPacket;

import java.util.HashMap;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.component.RenderHelperComponent;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.PacketReceiveEvent;
import helper.creeperbox.feature.event.events.PacketSendEvent;
import helper.creeperbox.feature.event.events.PostRender3DEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.sdk.entity.type.EntityActor;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.math.Vec2f;
import helper.creeperbox.sdk.math.Vec3f;
import helper.creeperbox.utils.mc.StopWatch;
import helper.creeperbox.utils.render.ColorUtil;

@ModuleInfo(name = "目标绘制", category = Category.Render)
public class TargetESP extends Module {

    private NumberValue espLength = new NumberValue("绘制长度",this, 14, 1, 40, 1);
    private NumberValue espFactor = new NumberValue("绘制因子",this, 8, 1, 20, 1);
    private NumberValue espShaking = new NumberValue("绘制强度",this, 1.8f, 1.5f, 10f, 1);
    private NumberValue espAmplitude = new NumberValue("绘制幅度",this, 3f, 0.1f, 8f, 1);

    public TargetESP(){
    }

    private double distance(EntityActor a1, EntityActor a2) {
        Vec3f pos1 = a1.getPos();
        Vec3f pos2 = a2.getPos();
        float dx = pos1.x - pos2.x;
        float dy = pos1.y - pos2.y;
        float dz = pos1.z - pos2.z;
        return Math.sqrt((double)(dx * dx + dy * dy + dz * dz));
    }

    public static HashMap<Long,Integer> ageMap = new HashMap<>();
    public long attackUid;

    @SubscribeEvent
    public void handleAge(TickEvent event){
        for(EntityActor player : event.getPlayer().getLevel().getPlayers()){
            long uid = player.getRuntimeID();
            if(ageMap.containsKey(uid)){
                ageMap.put(uid,ageMap.get(uid)+1);
            }else{
                ageMap.put(uid,0);
            }
        }

    }

    @SubscribeEvent
    public void handleAge(PacketReceiveEvent event){
        if (event.getPacket() instanceof PlayerActionPacket) {
            if(((PlayerActionPacket) event.getPacket()).getAction() == PlayerActionType.DIMENSION_CHANGE_SUCCESS){
                ageMap.clear();
            }
        }
    }

    private boolean isAttackPacket(BedrockPacket packet){
        if(packet instanceof InventoryTransactionPacket){
            InventoryTransactionPacket inventoryTransactionPacket = (InventoryTransactionPacket) packet;
            if(inventoryTransactionPacket.getTransactionType() == InventoryTransactionType.ITEM_USE_ON_ENTITY && inventoryTransactionPacket.getActionType() == 1){
                return true;
            }
        }
        return false;
    }


    @SubscribeEvent
    public void onAttack(PacketSendEvent event){
        if(isAttackPacket(event.getPacket())) {
            attack = true;
            attackWatch.reset();
            attackUid = ((InventoryTransactionPacket)event.getPacket()).getRuntimeEntityId();
        }
    }



    @SubscribeEvent
    public void handleRemove(PacketReceiveEvent event){
        if(event.getPacket() instanceof RemoveEntityPacket){
            ageMap.remove(((RemoveEntityPacket) event.getPacket()).getUniqueEntityId());
        }
    }

    public StopWatch attackWatch = new StopWatch();
    public boolean attack;

    @Override
    public void onEnable() {
        attack = false;
        attackWatch.reset();
    }

    @SubscribeEvent
    public void renderTargetESP(PostRender3DEvent event) {

        if(RenderHelperComponent.render3DData == null) return;

        EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
        if(player == null) return;


        if(attackWatch.finished(1000) || !attack) return;

        EntityActor target = null;
        for(EntityActor actor : player.getLevel().getPlayers()){
            if(actor.getRuntimeID() == attackUid){
                target = actor;
            }
        }

        if(target!=null){
            Vec2f rot = player.getRotation();

            Vec3f pos = target.getPos();
            Vec3f prev = target.getPosPrev();
            Vec2f size = target.getHitBoxSize();
            float partialTicks = RenderHelperComponent.render3DData.partialTicks;
            Vec3f cameraPos = RenderHelperComponent.render3DData.cameraPos;

            float x = prev.x + (pos.x - prev.x) * partialTicks - cameraPos.x;
            float y = prev.y + (pos.y - prev.y) * partialTicks - cameraPos.y - size.y;
            float z = prev.z + (pos.z - prev.z) * partialTicks - cameraPos.z;

            float age = ageMap.getOrDefault(player.getRuntimeID(),0) + partialTicks;

            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            GLES20.glDepthMask(false);

            float espLength = this.espLength.getCurrentValue().floatValue();
            float shaking = this.espShaking.getCurrentValue().floatValue();
            float amplitude = this.espAmplitude.getCurrentValue().floatValue();
            int factor = this.espFactor.getCurrentValue().intValue();
            for (int j = 0; j < 3; j++) {
                for (int i = 0; i <= espLength; i++) {
                    double radians = Math.toRadians((((float) i / 1.5f + age) * factor + (j * 120)) % (factor * 360));
                    double sinQuad = Math.sin(Math.toRadians(age * 2.5f + i * (j + 1)) * amplitude) / shaking;
                    float offset = ((float) i / espLength);

                    float toX = (float) (x + Math.cos(radians) * size.x);
                    float toY = (float) (y + sinQuad + 1);
                    float toZ = (float) (z + Math.sin(radians) * size.x);

                    float[] clone = new float[16];
                    System.arraycopy(RenderHelperComponent.render3DData.mvpMatrix.m,0,clone,0,16);

                    android.opengl.Matrix.translateM(clone, 0, toX,toY,toZ);

                    android.opengl.Matrix.rotateM(clone, 0, -rot.y, 0, 1, 0);
                    android.opengl.Matrix.rotateM(clone, 0, rot.x, 1, 0, 0);

                    android.opengl.Matrix.translateM(clone, 0, -toX,-toY,-toZ);

                    int color = ColorUtil.applyOpacity(RenderInterface.applyColor((int) (180 * offset)), offset);
                    float scale = Math.max(0.24f * (offset), 0.2f) * 2f;

                    GLES20.glEnable(GLES20.GL_BLEND);
                    GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,GLES20.GL_ONE);
                    RenderHelperComponent.imageXY(RenderHelperComponent.fireFlyTexture,toX,toY,toZ,scale,scale,color,clone);
                    GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,GLES20.GL_ONE_MINUS_SRC_ALPHA);
                }
            }

            GLES20.glDepthMask(true);
        }
    }




}
