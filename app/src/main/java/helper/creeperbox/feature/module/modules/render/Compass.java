package helper.creeperbox.feature.module.modules.render;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.GL_VIEWPORT;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glGetIntegerv;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.component.RenderHelperComponent;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.Render2DEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.utils.math.MathUtil;


@ModuleInfo(name = "指南针", category = Category.Render)
public class Compass extends Module {

    private static List<String> degreeData = new ArrayList<>();

    static {
        degreeData.add("N");
        degreeData.add("NE");
        degreeData.add("E");
        degreeData.add("SE");
        degreeData.add("S");
        degreeData.add("SW");
        degreeData.add("W");
        degreeData.add("NW");
    }


    @SubscribeEvent
    public void renderHud(Render2DEvent event){

        EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
        if(player == null) return;

        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);


        int[] viewport = new int[4];
        glGetIntegerv(GL_VIEWPORT, viewport, 0);
        float startY = 80f;
        float width = 800f;
        float height = 110f;
        float center = viewport[2]/2f;
        float startX = (viewport[2]-width)/2f;
        RenderHelperComponent.drawGradientRound(startX,startY,width,height,30f,0x80000000,0x80000000,true);
        RenderHelperComponent.scissorStart(startX,startY,width,height);

        RenderHelperComponent.bloomUtil.addTask(new Runnable() {
            @Override
            public void run() {
                RenderHelperComponent.drawGradientRound(startX,startY,width, height,30f,RenderInterface.applyColor(270),RenderInterface.applyColor(0),RenderInterface.applyColor(180),RenderInterface.applyColor(90));
            }
        });


        float degree = MathUtil.wrapAngleTo180_float(player.getRotation().y) + 180;
        int margin = 40;
        int step = 5;

        float minPixelsPerDegree = margin / (float)step;
        float pixelsPerDegree = Math.max(minPixelsPerDegree, width / 360f);
        float centerX = width / 2f;
        float visibleStartDegree = degree - (centerX / pixelsPerDegree);
        float visibleEndDegree = degree + (centerX / pixelsPerDegree);
        float firstTickDegree = (float)(Math.ceil(visibleStartDegree / step) * step);

        for (float currentDegree = firstTickDegree; currentDegree <= visibleEndDegree; currentDegree += step) {
            double wrappedDegree = wrapDegree(currentDegree);
            float xPosition = centerX + (currentDegree - degree) * pixelsPerDegree + startX;
            if(wrappedDegree%15 == 0){
                if(wrappedDegree%45 == 0){
                    RenderHelperComponent.rectangle(xPosition-1.5f, startY + 23.7f,3f,43.2f, Color.rgb(111,111,111));
                    RenderHelperComponent.pingfang25.drawCenteredString(degreeData.get((int) (wrappedDegree/45)),xPosition, startY+60f, Color.WHITE);
                }else{
                    RenderHelperComponent.rectangle(xPosition-1.5f, startY + 31.5f,3f,12f, Color.GRAY);
                    RenderHelperComponent.pingfang25.drawCenteredString(String.valueOf(wrappedDegree),xPosition, startY+60f, Color.GRAY);
                }
            }else{
                RenderHelperComponent.rectangle(xPosition-1.5f, startY + 31.5f,3f,12f,Color.GRAY);
            }
        }
        RenderHelperComponent.scissorEnd();


    }

    public static double wrapDegree(double degree){
        while (degree< 0){
            degree+=360;
        }
        while (degree>= 360){
            degree-=360;
        }
        return degree;
    }


}
