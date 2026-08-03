package helper.creeperbox.feature.module.modules.render;

import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.glBlendFunc;

import android.graphics.Color;
import android.opengl.GLES20;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.component.RenderHelperComponent;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.PostRender3DEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.math.Vec2f;
import helper.creeperbox.sdk.math.Vec3f;
import helper.creeperbox.utils.render.animation.Animation;
import helper.creeperbox.utils.render.animation.Easing;


@ModuleInfo(name = "光环", category = Category.Render)
public class PlayerHolo extends Module {


    private Animation floatingAnimation = new Animation(Easing.EASE_IN_OUT_QUAD,1000);
    boolean animationBackwards;


    @SubscribeEvent
    public void onRender3D(PostRender3DEvent event) {

        if(RenderHelperComponent.render3DData==null) {
            return;
        }

        EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
        if(player == null) {
            return;
        }

        boolean usingDepth = GLES20.glIsEnabled(GLES20.GL_DEPTH_TEST);
        boolean usingCull = GLES20.glIsEnabled(GLES20.GL_CULL_FACE);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDisable(GLES20.GL_CULL_FACE);


        float[] clone = new float[16];

        float partialTicks = RenderHelperComponent.render3DData.partialTicks;
        Vec3f cameraPos = RenderHelperComponent.render3DData.cameraPos;



        Vec2f rot = player.getRotation();
        Vec3f pos = player.getPos();
        Vec3f prev = player.getPosPrev();
        float x = prev.x + (pos.x - prev.x) * partialTicks - cameraPos.x;
        float y = prev.y + (pos.y - prev.y) * partialTicks - cameraPos.y;
        float z = prev.z + (pos.z - prev.z) * partialTicks - cameraPos.z;

        float size = 1.5f;
        System.arraycopy(RenderHelperComponent.render3DData.mvpMatrix.m,0,clone,0,16);

        android.opengl.Matrix.translateM(clone, 0, x,y+0.5f,z);
        android.opengl.Matrix.rotateM(clone, 0, rot.y, 0.0f, -1.0f, 0.0f);
        android.opengl.Matrix.rotateM(clone, 0, 90f, 1.0f, 0.0f, 0.0f);


        final float animationHeight = 0.5f;
        this.floatingAnimation.run(this.animationBackwards ? 0.0f : animationHeight);
        if(this.floatingAnimation.isFinished()){
            this.animationBackwards = !this.animationBackwards;
        }

        android.opengl.Matrix.rotateM(clone, 0, -90f, 1.0f, 0.0f, 0.0f);
        android.opengl.Matrix.translateM(clone, 0, 0,floatingAnimation.getValue(),0);
        android.opengl.Matrix.rotateM(clone, 0, 90f, 1.0f, 0.0f, 0.0f);

        GLES20.glDepthMask(false);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFuncSeparate(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA,GLES20.GL_ONE,GLES20.GL_ZERO);
        RenderHelperComponent.imageXY(RenderHelperComponent.layer1Texture,size*-0.5f,size*-0.5f,0,size,size, Color.WHITE,clone);
        android.opengl.Matrix.rotateM(clone, 0, -90f, 1.0f, 0.0f, 0.0f);
        android.opengl.Matrix.translateM(clone, 0, 0,0.06f,0);
        android.opengl.Matrix.rotateM(clone, 0, 90f, 1.0f, 0.0f, 0.0f);
        RenderHelperComponent.imageXY(RenderHelperComponent.layer2Texture,size*-0.5f,size*-0.5f,0,size,size, Color.WHITE,clone);
        android.opengl.Matrix.rotateM(clone, 0, -90f, 1.0f, 0.0f, 0.0f);
        android.opengl.Matrix.translateM(clone, 0, 0,0.06f,0);
        android.opengl.Matrix.rotateM(clone, 0, 90f, 1.0f, 0.0f, 0.0f);
        RenderHelperComponent.imageXY(RenderHelperComponent.layer3Texture,size*-0.5f,size*-0.5f,0,size,size, Color.WHITE,clone);
        glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glDepthMask(true);



        if(!usingDepth){
            GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        }
        if(usingCull){
            GLES20.glEnable(GLES20.GL_CULL_FACE);
        }

    }



}
