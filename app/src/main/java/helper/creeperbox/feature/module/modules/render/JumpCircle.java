package helper.creeperbox.feature.module.modules.render;

import android.opengl.GLES20;

import java.util.ArrayList;
import java.util.List;

import helper.creeperbox.feature.component.RenderHelperComponent;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.PostRender3DEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.math.Vec3f;
import helper.creeperbox.utils.mc.StopWatch;
import helper.creeperbox.utils.render.animation.Animation;
import helper.creeperbox.utils.render.animation.Easing;

@ModuleInfo(name = "跳跃光环", category = Category.Render)
public class JumpCircle extends Module {

    private List<Circle> circle =  new ArrayList<Circle>();
    private int delay;

    public JumpCircle(){

    }
    @Override
    public void onEnable() {
        delay = 0;
    }

    public NumberValue biggerSpeed = new NumberValue( "变大速度",this, 3, 1, 5, 0.1);
    public NumberValue rotateSpeed = new NumberValue( "旋转速度",this, 3, 0, 5, 0.1);
    public NumberValue waitSpeed = new NumberValue("等待速度",this, 3, 0, 5, 0.1);


    @SubscribeEvent
    public void onJump(TickEvent event) {
        delay--;
        EntityLocalPlayer player = event.getPlayer();
        if(player.isOnGround() && player.getMoveInput().isJumpDown && delay < 0){
            delay = 5;
            Vec3f pos = player.getPos();
            circle.add(new Circle(pos.x,pos.y-1.62f,pos.z,biggerSpeed.getCurrentValue().floatValue(),rotateSpeed.getCurrentValue().floatValue(),waitSpeed.getCurrentValue().floatValue()));
        }
    }



    @SubscribeEvent
    public void renderJumpCircle(PostRender3DEvent event) {

        if(RenderHelperComponent.render3DData==null) return;

        boolean usingDepth = GLES20.glIsEnabled(GLES20.GL_DEPTH_TEST);
        boolean usingCull = GLES20.glIsEnabled(GLES20.GL_CULL_FACE);

        GLES20.glDisable(GLES20.GL_CULL_FACE);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        Vec3f cameraPos = RenderHelperComponent.render3DData.cameraPos;

        for (Circle c : new ArrayList<>(circle)) {
            c.update();

            float radius = 2f;

            float scale = 0.4f + c.getScale()*0.6f;
            float calcScaleX = 1f-scale;
            float calcScaleZ = 1f-scale;
            float xVal = c.x - cameraPos.x;
            float yVal = c.y - cameraPos.y;
            float zVal = c.z - cameraPos.z;

            float calcTranslateX = xVal / 2F * calcScaleX;
            float calcTranslateZ = zVal / 2F * calcScaleZ;

            float[] clone = new float[16];
            System.arraycopy(RenderHelperComponent.render3DData.mvpMatrix.m,0,clone,0,16);

            android.opengl.Matrix.translateM(clone,0,calcTranslateX, 0f, calcTranslateZ);
            android.opengl.Matrix.scaleM(clone,0,scale, 1f, scale);

            android.opengl.Matrix.translateM(clone,0,xVal,yVal,zVal);
            android.opengl.Matrix.rotateM(clone,0,c.getRotate(), 0, 1, 0);
            android.opengl.Matrix.translateM(clone,0,-xVal,-yVal,-zVal);

            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,GLES20.GL_ONE);
            RenderHelperComponent.gradientImageXZ(RenderHelperComponent.circleTexture,xVal-radius,yVal,zVal-radius,radius*2,radius*2,RenderInterface.applyColor(270),RenderInterface.applyColor(0),RenderInterface.applyColor(180),RenderInterface.applyColor(90),clone);


            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,GLES20.GL_ONE_MINUS_SRC_ALPHA);

            if(c.finish()){
                circle.remove(c);
            }
        }

        if(usingDepth){
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        }

        if(usingCull){
            GLES20.glEnable(GLES20.GL_CULL_FACE);
        }
    }




    private static class Circle {

        public final float x;
        public final float y;
        public final float z;


        private final float rotateSpeed;

        public Animation biggerAnimation;

        private StopWatch waitWatch;
        private long waitTime;
        private int state;      //0 forward  1 backward  2 fade  3 finish
        private float start;

        public Circle(double x, double y, double z,float biggerSpeed, float rotateSpeed,float waitTime) {
            this.x = (float) x;
            this.y = (float) y;
            this.z = (float) z;
            this.start = 0;
            state = 0;
            biggerAnimation = new Animation(Easing.EASE_IN_OUT_QUAD, (long) (biggerSpeed*1000));
            this.rotateSpeed = rotateSpeed*4;
            this.waitTime = (long) (waitTime*1000);
        }

        public boolean finish(){
            return state == 3;
        }


        public float getScale(){
            return biggerAnimation.getValue();
        }

        public float getRotate() {
            return start;
        }


        public void update(){
            if(state == 0){
                biggerAnimation.run(1f);
                if(biggerAnimation.isFinished()){
                    state = 1;
                }
                start+=rotateSpeed*(1f-biggerAnimation.getValue());
                while(start>= 360){
                    start -= 360;
                }

                while(start <= 0){
                    start += 360;
                }

                waitWatch = new StopWatch();
            }else if(state == 1){
                if(waitWatch.finished(waitTime)) {
                    state = 2;
                    biggerAnimation.reset();
                }
            }else if(state == 2){

                biggerAnimation.run(0f);

                if(biggerAnimation.isFinished()){
                    state = 3;
                }

                start-=rotateSpeed*(1f-biggerAnimation.getValue());

                while(start >= 360){
                    start -= 360;
                }

                while(start <= 0){
                    start += 360;
                }

            }

        }
    }

}
