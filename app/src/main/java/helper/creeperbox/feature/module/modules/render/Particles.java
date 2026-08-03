package helper.creeperbox.feature.module.modules.render;

import android.opengl.GLES20;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.component.RenderHelperComponent;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.PostRender3DEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.ListValue;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.math.Vec2f;
import helper.creeperbox.sdk.math.Vec3f;
import helper.creeperbox.utils.math.RandomUtil;
import helper.creeperbox.utils.render.ColorUtil;
@ModuleInfo(name = "漂浮粒子", category = Category.Render)
public class Particles extends Module {

    public final NumberValue particlesAmount = new NumberValue( "粒子数量",this, 100, 0, 300, 1);

    public final NumberValue fireAmount = new NumberValue("火焰粒子数量",this, 40, 0, 100, 1);

    public final ListValue particleMode = new ListValue("粒子模式",this,"飞行")
            .addSubList("飞行")
            .addSubList("下落");

    public final ListValue particleType = new ListValue("粒子种类",this,"雪花")
            .addSubList("雪花")
            .addSubList("心形")
            .addSubList("五角星")
            .addSubList("美元")
            .addSubList("光晕");

    public final ArrayList<ParticleBase> particles = new ArrayList<>();
    private final ArrayList<FireFly> fireParticles = new ArrayList<>();
    public final NumberValue particlesSize = new NumberValue("粒子大小",this, 0.5f, 0.2f, 8f, 0.1);
    public final NumberValue fireSize = new NumberValue("火焰粒子大小",this, 0.2f, 0.1f, 2f, 0.1);


    @SubscribeEvent
    public void onUpdateParticle(TickEvent event) {

        boolean fly = particleMode.getCurrentValue().equals("飞行");


        particles.removeIf(new Predicate<ParticleBase>() {
            @Override
            public boolean test(ParticleBase particleBase) {
                return particleBase.tick(event,fly);
            }
        });

        fireParticles.removeIf(new Predicate<ParticleBase>() {
            @Override
            public boolean test(ParticleBase particleBase) {
                return particleBase.tick(event,fly);
            }
        });



        int particlesCount = particlesAmount.getCurrentValue().intValue();

        int fireCount = fireAmount.getCurrentValue().intValue();

        Vec3f pos = event.getPlayer().getPos();

        for (int i = fireParticles.size(); i < fireCount; i++) {
            fireParticles.add(new FireFly(
                    (float) (pos.x + RandomUtil.getRandom(-25f, 25f)),
                    (float) (pos.y + RandomUtil.getRandom(2f, 15f)),
                    (float) (pos.z + RandomUtil.getRandom(-25f, 25f)),
                    (float) RandomUtil.getRandom(-0.2f, 0.2f) ,
                    (float) RandomUtil.getRandom(-0.1f, 0.1f) ,
                    (float) RandomUtil.getRandom(-0.2f, 0.2f) ));
        }

        for (int j = particles.size(); j < particlesCount; j++) {
            particles.add(new ParticleBase(
                    (float) (pos.x + RandomUtil.getRandom(-15f, 15f)),
                    (float) (pos.y + RandomUtil.getRandom(2, 15f)),
                    (float) (pos.z + RandomUtil.getRandom(-15f, 15f)),
                    !fly ? 0 : (float) RandomUtil.getRandom(-0.4f, 0.4f),
                    !fly ? (float)RandomUtil.getRandom(-0.2f, -0.05f) : (float) RandomUtil.getRandom(-0.1f, 0.1f),
                    !fly ? 0 : (float) RandomUtil.getRandom(-0.4f, 0.4f)));
        }

    }


    @SubscribeEvent
    public void onRender3D(PostRender3DEvent event) {
        if (RenderHelperComponent.render3DData == null) return;

        boolean usingDepth = GLES20.glIsEnabled(GLES20.GL_DEPTH_TEST);
        boolean usingCull = GLES20.glIsEnabled(GLES20.GL_CULL_FACE);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
        if(player == null) return;
        Vec3f cameraPos = RenderHelperComponent.render3DData.cameraPos;
        Vec2f rot = player.getRotation();
        float size = particlesSize.getCurrentValue().floatValue();
        float partialTicks = RenderHelperComponent.render3DData.partialTicks;
        GLES20.glDepthMask(false);

        for(ParticleBase p : particles){
            float[] clone = new float[16];
            System.arraycopy(RenderHelperComponent.render3DData.mvpMatrix.m,0,clone,0,16);

            float x = p.prevposX + (p.posX - p.prevposX) * partialTicks - cameraPos.x;
            float y = p.prevposY + (p.posY - p.prevposY) * partialTicks - cameraPos.y;
            float z = p.prevposZ + (p.posZ - p.prevposZ) * partialTicks - cameraPos.z;


            android.opengl.Matrix.translateM(clone, 0, x,y,z);
            android.opengl.Matrix.rotateM(clone, 0, -rot.y, 0, 1, 0);
            android.opengl.Matrix.rotateM(clone, 0, rot.x, 1, 0, 0);
            android.opengl.Matrix.translateM(clone, 0, -x,-y,-z);


            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,GLES20.GL_ONE);
            RenderHelperComponent.imageXY(RenderHelperComponent.getTexture(particleType.getCurrentValue()),x,y,z,size,size, ColorUtil.applyOpacity(RenderInterface.applyColor(p.age),(float)p.age/(float)p.maxAge),clone);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,GLES20.GL_ONE_MINUS_SRC_ALPHA);
        }

        float size2 = fireSize.getCurrentValue().floatValue();
        for(FireFly p : fireParticles){
            if(!p.trails.isEmpty()){
                for(FireTrail trail : p.trails){
                    float[] clone = new float[16];
                    System.arraycopy(RenderHelperComponent.render3DData.mvpMatrix.m,0,clone,0,16);
                    Vec3f pos = trail.interpolate(1f);
                    float x = pos.x - cameraPos.x;
                    float y = pos.y - cameraPos.y;
                    float z = pos.z - cameraPos.z;
                    android.opengl.Matrix.translateM(clone, 0, x,y,z);
                    android.opengl.Matrix.rotateM(clone, 0, -rot.y, 0, 1, 0);
                    android.opengl.Matrix.rotateM(clone, 0, rot.x, 1, 0, 0);
                    android.opengl.Matrix.translateM(clone, 0, -x,-y,-z);

                    GLES20.glEnable(GLES20.GL_BLEND);
                    GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,GLES20.GL_ONE);
                    float percent = ((float)p.age/(float)p.maxAge)*(float)trail.animation(partialTicks);
                    RenderHelperComponent.imageXY(RenderHelperComponent.fireFlyTexture,x,y,z,size2,size2, ColorUtil.applyOpacity(RenderInterface.applyColor((int) (percent*360)),percent),clone);
                    GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,GLES20.GL_ONE_MINUS_SRC_ALPHA);
                }
            }
        }

        GLES20.glDepthMask(true);

        if(!usingDepth){
            GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        }
        if(usingCull){
            GLES20.glEnable(GLES20.GL_CULL_FACE);
        }

    }


    public class FireTrail {
        private final Vec3f from;
        private final Vec3f to;
        public final int color;
        private int ticks, prevTicks;

        public FireTrail(Vec3f from, Vec3f to, int color) {
            this.from = from;
            this.to = to;
            this.ticks = 25;
            this.color = color;
        }

        public Vec3f interpolate(float pt) {
            float x = from.x + ((to.x - from.x) * pt);
            float y = from.y + ((to.y - from.y) * pt);
            float z = from.z + ((to.z - from.z) * pt);
            return new Vec3f(x, y, z);
        }

        public double animation(float pt) {
            return (this.prevTicks + (this.ticks - this.prevTicks) * pt) / 10.;
        }

        public boolean update() {
            this.prevTicks = this.ticks;
            return this.ticks-- <= 0;
        }


    }
    public class FireFly extends ParticleBase {

        public final List<FireTrail> trails = new ArrayList<>();

        public FireFly(float posX, float posY, float posZ, float motionX, float motionY, float motionZ) {
            super(posX, posY, posZ, motionX, motionY, motionZ);
        }

        @Override
        public boolean tick(TickEvent event, boolean fly) {

            EntityLocalPlayer player = event.getPlayer();
            Vec3f pos = player.getPos();
            if (distance(pos,new Vec3f(posX, posY, posZ)) > 100) age -= 8;
            else age--;
            if (age < 0)
                return true;

            trails.removeIf(new Predicate<FireTrail>() {
                @Override
                public boolean test(FireTrail trail) {
                    return trail.update();
                }
            });

            prevposX = posX;
            prevposY = posY;
            prevposZ = posZ;

            posX += motionX;
            posY += motionY;
            posZ += motionZ;

            trails.add(new FireTrail(new Vec3f(prevposX, prevposY, prevposZ), new Vec3f(posX, posY, posZ), RenderInterface.applyColor(age)));


            motionX *= 0.99f;
            motionY *= 0.99f;
            motionZ *= 0.99f;

            return false;
        }
    }


    public class ParticleBase {

        public float prevposX, prevposY, prevposZ, posX, posY, posZ, motionX, motionY, motionZ;
        public int age, maxAge;


        public ParticleBase(float posX, float posY, float posZ, float motionX, float motionY, float motionZ) {
            this.posX = posX;
            this.posY = posY;
            this.posZ = posZ;
            prevposX = posX;
            prevposY = posY;
            prevposZ = posZ;
            this.motionX = motionX;
            this.motionY = motionY;
            this.motionZ = motionZ;
            age = (int) RandomUtil.getRandom(100, 300);
            maxAge = age;
        }

        public boolean tick(TickEvent event, boolean fly) {
            EntityLocalPlayer player = event.getPlayer();
            Vec3f pos = player.getPos();
            if (distance(pos,new Vec3f(posX, posY, posZ)) > 4096) age -= 8;
            else age--;
            if (age < 0)
                return true;

            prevposX = posX;
            prevposY = posY;
            prevposZ = posZ;

            posX += motionX;
            posY += motionY;
            posZ += motionZ;

            motionX *= 0.9f;
            if (fly)
                motionY *= 0.9f;

            motionY -= 0.001f;
            motionZ *= 0.9f;

            return false;
        }


        public double distance(Vec3f pos1,Vec3f pos2) {
            float dx = pos1.x - pos2.x;
            float dy = pos1.y - pos2.y;
            float dz = pos1.z - pos2.z;
            return Math.sqrt((double)(dx * dx + dy * dy + dz * dz));
        }
    }


}
