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

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.component.RenderHelperComponent;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.Render2DEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.math.Vec3i;
import helper.creeperbox.utils.render.animation.Animation;
import helper.creeperbox.utils.render.animation.Easing;
@ModuleInfo(name = "药水界面小窗", category = Category.Render)
public class EffectHud extends Module {

    private final NumberValue xPercent = new NumberValue("左右比例", this, 0.5f, 0f, 1f, 0.01f);
    private final NumberValue yPercent = new NumberValue("上下比例", this, 0.1f, 0f, 1f, 0.01f);

    private final Animation heightAnimation = new Animation(Easing.Decelerate,500);

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
        float startX = viewport[2]*xPercent.getCurrentValue().floatValue();
        float startY = viewport[3]*yPercent.getCurrentValue().floatValue();

        Vec3i[] effects = player.getEffectList();

        float width = 350f;
        float fontHeight = 50f;
        float height = fontHeight;
        int count = 0;
        for(Vec3i effect : effects){
            int index = effect.x-1;
            if(index>=0 && index<RenderHelperComponent.effectIdToName.length){
                height += 60f;
                count++;
            }
        }
        heightAnimation.run(height);
        height = heightAnimation.getValue();
        RenderHelperComponent.scissorStart(startX, startY, width, height);
        String text = "药水效果";
        float fontX = startX+(width-RenderHelperComponent.pingfang30.getStringWidth(text))/2;
        float fontY = startY+RenderHelperComponent.pingfang30.getMiddle(fontHeight);
        int color1 = RenderInterface.applyColor(0);
        int color2 = RenderInterface.applyColor(180);
        for(int i = 0 ; i < text.length() ; i ++){
            String s = String.valueOf(text.charAt(i));
            int color = getGradientOffset(color1,color2,i*5.7f);
            fontX = RenderHelperComponent.pingfang30.drawString(s,fontX,fontY,color);
        }

        RenderHelperComponent.drawGradientRound(startX,startY,width, height,20f+count*2f,0x80000000,0x80000000,false);

        float finalHeight = height;
        int finalCount = count;
        RenderHelperComponent.bloomUtil.addTask(new Runnable() {
            @Override
            public void run() {
                RenderHelperComponent.drawGradientRound(startX,startY,width, finalHeight,20f+ finalCount *2f,RenderInterface.applyColor(270),RenderInterface.applyColor(0),RenderInterface.applyColor(180),RenderInterface.applyColor(90));
            }
        });

        float y = startY+50f;


        for(Vec3i effect : effects){
            int id = effect.x;
            int index = id-1;
            int duration = effect.y;
            int amplifier = effect.z;
            if(index>=0 && index<RenderHelperComponent.effectIdToName.length){
                String effectString = RenderHelperComponent.effectIdToName[index];
                int texture = RenderHelperComponent.effectIdToTexture[index];
                if(texture == -1) continue;
                RenderHelperComponent.image(texture,startX+15f,y+10f,40,40,Color.WHITE);
                float fontPosX = RenderHelperComponent.pingfang_medium_30.drawString(effectString,startX+70f,y+RenderHelperComponent.pingfang_medium_30.getMiddle(60f),Color.rgb(213,223,212));
                fontPosX += 5f;
                RenderHelperComponent.pingfang30.drawString(String.valueOf(amplifier+1),fontPosX,y+RenderHelperComponent.pingfang30.getMiddle(60f),Color.rgb(255,94,94));
                int totalSeconds = duration / 20;
                int minutes = totalSeconds / 60;
                int seconds = totalSeconds % 60;
                String time = String.format("%d : %02d",minutes,seconds);
                float x = startX + width-RenderHelperComponent.pingfang30.getStringWidth(time)-20f;
                RenderHelperComponent.pingfang30.drawString(time,x,y+RenderHelperComponent.pingfang30.getMiddle(60f),Color.WHITE);
                y+=60f;
            }

        }


        RenderHelperComponent.scissorEnd();

    }

    public static int getGradientOffset(int color1, int color2, double index) {
        double offs = Math.abs(System.currentTimeMillis() / 16.0D) / 60.0D + index;
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
