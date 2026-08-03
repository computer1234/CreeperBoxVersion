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

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.component.RenderHelperComponent;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.Render2DEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;

@ModuleInfo(name = "水印", category = Category.Render)
public class WaterMark extends Module {

    @SubscribeEvent
    public void renderHud(Render2DEvent event){

        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        float startX = 50f;
        float startY = 50f;
        float height = 60f;

        int fps = CreeperBox.getFPS();
        String remoteText = "Galaxy.cool";
        String total = String.format("Galaxy | FPS%d | %s",fps,remoteText);

        float finalStartX = startX;
        RenderHelperComponent.bloomUtil.addTask(new Runnable() {
            @Override
            public void run() {
                RenderHelperComponent.drawGradientRound(finalStartX,startY,RenderHelperComponent.anta40.getStringWidth(total)+45f,height,15f,RenderInterface.applyColor(270),RenderInterface.applyColor(0),RenderInterface.applyColor(180),RenderInterface.applyColor(90));
            }
        });

        RenderHelperComponent.drawGradientRound(startX,startY,RenderHelperComponent.anta40.getStringWidth(total)+45f,height,15f,0x80000000,0x80000000,false);

        String text = "Galaxy";
        startX += 20f;

        float fontY = startY+RenderHelperComponent.anta40.getMiddle(height);

        for(int i = 0 ; i < text.length() ; i ++){
            String s = String.valueOf(text.charAt(i));
            int color = getGradientOffset(Color.rgb(255,118,118),Color.rgb(109,0,0),i*16.1);
            startX = RenderHelperComponent.anta40.drawString(s,startX,fontY,color);
        }

        startX = RenderHelperComponent.anta40.drawString(" | ",startX,fontY,Color.argb(25,255,255,255));
        startX = RenderHelperComponent.anta40.drawString(String.format("FPS:%d",CreeperBox.getFPS()),startX,fontY,Color.WHITE);
        startX = RenderHelperComponent.anta40.drawString(" | ",startX,fontY,Color.argb(25,255,255,255));
        RenderHelperComponent.anta40.drawString(remoteText,startX,fontY,Color.WHITE);
    }

    public static int getGradientOffset(int color1, int color2, double index) {
        double offs = Math.abs(System.currentTimeMillis() / 16.0D) / 300.0D + index;
        if (offs > 1.0D) {
            double left = offs % 1.0D;
            int off = (int)offs;
            offs = (off % 2 == 0) ? left : (1.0D - left);
        }
        double inverse_percent = 1.0D - offs;
        int redPart = (int)(Color.red(color1) * inverse_percent + Color.red(color2) * offs);
        int greenPart = (int)(Color.green(color1) * inverse_percent + Color.green(color2) * offs);
        int bluePart = (int)(Color.blue(color1) * inverse_percent + Color.blue(color2) * offs);
        int alphaPart = (int)(Color.alpha(color1) * inverse_percent + Color.alpha(color2) * offs);
        return Color.argb(alphaPart,redPart, greenPart, bluePart);
    }


}
