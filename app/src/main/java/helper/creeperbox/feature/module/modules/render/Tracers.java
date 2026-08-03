package helper.creeperbox.feature.module.modules.render;

import static android.opengl.GLES20.GL_VIEWPORT;
import static android.opengl.GLES20.glGetIntegerv;

import android.graphics.Color;
import android.opengl.GLES20;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.component.Render3DData;
import helper.creeperbox.feature.component.RenderHelperComponent;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.PostRender3DEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.sdk.entity.type.EntityActor;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.math.Vec3f;
import helper.creeperbox.utils.mc.TargetUtil;



@ModuleInfo(name = "射线", category = Category.Render)
public class Tracers extends Module {
    private boolean firstPer;
    @SubscribeEvent
    public void checkPer(TickEvent event) {
        firstPer = EntityLocalPlayer.getView()==0;
    }


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

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glDisable(GLES20.GL_CULL_FACE);

        float[] clone = new float[16];
        System.arraycopy(RenderHelperComponent.render3DData.mvpMatrix.m,0,clone,0,16);
        float partialTicks = RenderHelperComponent.render3DData.partialTicks;
        Vec3f cameraPos = RenderHelperComponent.render3DData.cameraPos;

        int[] viewport = new int[4];
        glGetIntegerv(GL_VIEWPORT, viewport, 0);
        Vec3f screen;
        if(firstPer){
            screen = screenToWorld(RenderHelperComponent.render3DData,viewport[2]/2f,viewport[3]/2f,1f);
        }else{
            Vec3f pos = player.getPos();
            Vec3f posPrev = player.getPosPrev();
            float toX = posPrev.x + (pos.x - posPrev.x) * partialTicks - cameraPos.x;
            float toY = posPrev.y + (pos.y - posPrev.y) * partialTicks - cameraPos.y ;
            float toZ = posPrev.z + (pos.z - posPrev.z) * partialTicks - cameraPos.z;
            screen = new Vec3f(toX,toY,toZ);
        }
        for(EntityActor actor : TargetUtil.findTarget(player,200)){
            Vec3f toPos = actor.getPos();
            Vec3f toPrev = actor.getPosPrev();
            float toX = toPrev.x + (toPos.x - toPrev.x) * partialTicks - cameraPos.x;
            float toY = toPrev.y + (toPos.y - toPrev.y) * partialTicks - cameraPos.y ;
            float toZ = toPrev.z + (toPos.z - toPrev.z) * partialTicks - cameraPos.z;
            RenderHelperComponent.drawLine(screen.x,screen.y,screen.z,toX,toY,toZ, Color.WHITE,clone);
        }

        if(usingDepth){
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        }
        if(usingCull){
            GLES20.glEnable(GLES20.GL_CULL_FACE);
        }
    }




    public static Vec3f screenToWorld(Render3DData data, float screenX, float screenY, float depth) {
        int[] viewport = new int[4];
        glGetIntegerv(GL_VIEWPORT, viewport, 0);

        float ndcX = (screenX - viewport[0]) / viewport[2] * 2.0f - 1.0f;
        float ndcY = 1.0f - (screenY - viewport[1]) / viewport[3] * 2.0f;
        float ndcZ = depth * 2.0f - 1.0f;

        float[] clipPos = {ndcX, ndcY, ndcZ, 1.0f};

        float[] viewPos = new float[4];
        float[] invProj = new float[16];
        android.opengl.Matrix.invertM(invProj, 0, data.proMatrix.m, 0);
        android.opengl.Matrix.multiplyMV(viewPos, 0, invProj, 0, clipPos, 0);

        float[] worldPos = new float[4];
        float[] invView = new float[16];
        android.opengl.Matrix.invertM(invView, 0, data.viewMatrix.m, 0);
        android.opengl.Matrix.multiplyMV(worldPos, 0, invView, 0, viewPos, 0);

        if (worldPos[3] != 0) {
            return new Vec3f(
                    worldPos[0] / worldPos[3],
                    worldPos[1] / worldPos[3],
                    worldPos[2] / worldPos[3]
            );
        }

        return null;
    }
}
