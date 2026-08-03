package helper.creeperbox.feature.module.modules.render;

import android.opengl.GLES20;

import org.cloudburstmc.protocol.bedrock.data.entity.EntityEventType;
import org.cloudburstmc.protocol.bedrock.packet.EntityEventPacket;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.component.RenderHelperComponent;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.PacketReceiveEvent;
import helper.creeperbox.feature.event.events.PostRender3DEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.BooleanValue;
import helper.creeperbox.feature.settings.ListValue;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.sdk.entity.type.EntityActor;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.entity.type.EntityServerPlayer;
import helper.creeperbox.sdk.math.Vec2f;
import helper.creeperbox.sdk.math.Vec3f;
import helper.creeperbox.utils.math.RandomUtil;

@ModuleInfo(name = "打击粒子", category = Category.Render)
public class HitParticle extends Module {
    public static HashMap<Long,Integer> hurtMap = new HashMap<>();

    private final CopyOnWriteArrayList<PhysicsParticles> hitParticles = new CopyOnWriteArrayList<>();
    public NumberValue fadeTime = new NumberValue("消失时间",this, 300, 100, 5000, 1);
    public BooleanValue self = new BooleanValue("自己",this,true);
    public BooleanValue other = new BooleanValue("其他人",this,true);

    public NumberValue particlesAmount = new NumberValue("粒子数量",this, 5, 1, 5, 1);

    public final ListValue particleMode = new ListValue("粒子模式",this,"下落")
            .addSubList("飞行")
            .addSubList("下落");
    public final ListValue particleType = new ListValue("粒子种类",this,"雪花")
            .addSubList("雪花")
            .addSubList("心形")
            .addSubList("五角星");

    @SubscribeEvent
    public void handleHurt(PacketReceiveEvent event){
        if(event.getPacket() instanceof EntityEventPacket){
            EntityEventPacket packet = (EntityEventPacket) event.getPacket();
            if(packet.getType() == EntityEventType.HURT) hurtMap.put(packet.getRuntimeEntityId(),10);
        }
    }

    @SubscribeEvent
    public void onHurtCheck(TickEvent event){
        Iterator<Map.Entry<Long, Integer>> iterator = hurtMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, Integer> entry = iterator.next();
            Long key = entry.getKey();
            int hurtTime = entry.getValue();
            if (hurtTime <= 0) {
                iterator.remove();
            } else {
                hurtMap.put(key, hurtTime - 1);
            }
        }


        hitParticles.removeIf(new Predicate<PhysicsParticles>() {
            @Override
            public boolean test(PhysicsParticles particleBase) {
                return particleBase.update(event.getPlayer(),particleMode.getCurrentValue().equals("飞行"),fadeTime.getCurrentValue().intValue());
            }
        });





    }


    @SubscribeEvent
    public void onRender3D(PostRender3DEvent event) {
        if(RenderHelperComponent.render3DData==null) return;

        boolean usingDepth = GLES20.glIsEnabled(GLES20.GL_DEPTH_TEST);
        boolean usingCull = GLES20.glIsEnabled(GLES20.GL_CULL_FACE);

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);

        EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
        if(player == null) return;
        Vec3f cameraPos = RenderHelperComponent.render3DData.cameraPos;
        Vec2f rot = player.getRotation();
        float size = 0.2f;
        float partialTicks = RenderHelperComponent.render3DData.partialTicks;

        for(PhysicsParticles p : hitParticles){

            float[] clone = new float[16];
            System.arraycopy(RenderHelperComponent.render3DData.mvpMatrix.m,0,clone,0,16);

            float x = p.px + (p.x - p.px) * partialTicks - cameraPos.x;
            float y = p.py + (p.y - p.py) * partialTicks - cameraPos.y;
            float z = p.pz + (p.z - p.pz) * partialTicks - cameraPos.z;

            p.rotationAngle+= p.rotationSpeed*partialTicks;

            android.opengl.Matrix.translateM(clone, 0, x,y,z);

            android.opengl.Matrix.rotateM(clone, 0, -rot.y, 0, 1, 0);
            android.opengl.Matrix.rotateM(clone, 0, rot.x, 1, 0, 0);

            android.opengl.Matrix.translateM(clone, 0, size/2f,size/2f,size/2f);
            android.opengl.Matrix.rotateM(clone, 0,p.rotationAngle, 0, 0, 1);
            android.opengl.Matrix.translateM(clone, 0, -size/2f,-size/2f,-size/2f);

            android.opengl.Matrix.translateM(clone, 0, -x,-y,-z);

            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,GLES20.GL_ONE);
            RenderHelperComponent.imageXY(RenderHelperComponent.getTexture(particleType.getCurrentValue()),x,y,z,size,size,p.color,clone);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,GLES20.GL_ONE_MINUS_SRC_ALPHA);
        }

        if(usingDepth){
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        }
        if(usingCull){
            GLES20.glEnable(GLES20.GL_CULL_FACE);
        }

    }


    @SubscribeEvent
    public void spawnHitParticle(TickEvent event){
        EntityLocalPlayer player = event.getPlayer();
        for(EntityActor actor : player.getLevel().getPlayers()){
            if(!self.getCurrentValue() && actor instanceof EntityLocalPlayer) continue;
            if(!other.getCurrentValue() && actor instanceof EntityServerPlayer) continue;

            if(hurtMap.containsKey(actor.getRuntimeID())){
                Vec3f pos = actor.getPos();
                int color = RenderInterface.applyColor((int) RandomUtil.getRandom(1,228));
                for (int i = 0; i < particlesAmount.getCurrentValue().intValue(); i++) {
                    hitParticles.add(new PhysicsParticles(pos.x,(float) RandomUtil.getRandom(pos.y+1.6f,pos.y),pos.z, color, (float) RandomUtil.getRandom(0, 180),(float) RandomUtil.getRandom(10f, 60f), 0));
                }
            }
        }
    }



}
