package helper.creeperbox.feature.module.modules.render;

import static android.opengl.GLES10.glBindTexture;
import static android.opengl.GLES20.GL_ACTIVE_TEXTURE;
import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_CURRENT_PROGRAM;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_BINDING_2D;
import static android.opengl.GLES20.GL_VIEWPORT;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glGetIntegerv;
import static android.opengl.GLES20.glUseProgram;

import android.graphics.Color;
import android.util.Log;

import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.component.RenderHelperComponent;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.Render2DEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.sdk.ClientInstance;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.item.ItemStack;
import helper.creeperbox.sdk.render.UIRenderContext;
import helper.creeperbox.utils.math.MathUtil;
import helper.creeperbox.utils.render.MatrixUtil;

@ModuleInfo(name = "装备信息", category = Category.Render)
public class ArmorHud extends Module {

    private final NumberValue xPercent = new NumberValue("左右比例", this, 0.5f, 0f, 1f, 0.01f);
    private final NumberValue yPercent = new NumberValue("上下比例", this, 0.5f, 0f, 1f, 0.01f);

    @SubscribeEvent
    public void renderHud(Render2DEvent event){


        EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
        if(player == null) return;

        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        float height = 40f;
        ItemStack hand = player.getItemInHand();
        if(hand.isValid()){
            height += 60f;
        }
        ItemStack offHand = player.getItemOffHand();
        if(offHand.isValid()){
            height += 60f;
        }
        for(int i = 0 ; i < 4 ; i++) {
            ItemStack armor = player.getArmor(i);
            if(armor.isValid()){
                height += 60f;
            }
        }
        if(height == 40f) return;

        int[] viewport = new int[4];
        glGetIntegerv(GL_VIEWPORT, viewport, 0);
        float startX = viewport[2]*xPercent.getCurrentValue().floatValue();
        float startY = viewport[3]*yPercent.getCurrentValue().floatValue();

        float finalHeight = height;
        RenderHelperComponent.bloomUtil.addTask(new Runnable() {
            @Override
            public void run() {
                RenderHelperComponent.drawGradientRound(startX,startY,260, finalHeight,40f,RenderInterface.applyColor(270),RenderInterface.applyColor(0),RenderInterface.applyColor(180),RenderInterface.applyColor(90));
            }
        });

        RenderHelperComponent.drawGradientRound(startX,startY,260,height,40f,0x80000000,0x80000000,false);

        RenderHelperComponent.loadOpenGLCtx();
        float itemX = startX + 20f;
        float itemY = startY + 20f;

        if(hand.isValid()){
            renderTargetItem(event.getCtx(),hand,itemX,itemY);
            itemY += 60f;
        }

        if(offHand.isValid()){
            renderTargetItem(event.getCtx(),offHand,itemX,itemY);
            itemY += 60f;
        }

        for(int i = 0 ; i < 4 ; i++) {
            ItemStack armor = player.getArmor(i);
            if(armor.isValid()){
                renderTargetItem(event.getCtx(),armor,itemX,itemY);
                itemY += 60f;
            }
        }


        RenderHelperComponent.saveOpenGLCtx();




    }


    public void renderTargetItem(UIRenderContext ctx, ItemStack item, float x, float y){

        float ratio = ClientInstance.getGuiScale();
        float def = 5/ratio;
        float scale = def * 0.7f;
        ItemData data = item.getItemData();

        if (!data.isValid()) return;

        boolean ench = data.getTag()!=null && !data.getTag().getList("ench", NbtType.COMPOUND).isEmpty();

        ctx.renderItem(item,x/ratio,y/ratio,1f,1f,scale,ench);

        int count = data.getCount();
        scale = 0.7f;

        if(count>1){
            MatrixUtil.pushMatrix();
            MatrixUtil.loadIdentity();
            float size = 38*scale;
            int[] currentProgram = new int[1];
            glGetIntegerv(GL_CURRENT_PROGRAM, currentProgram, 0);

            int[] currentActiveTexture = new int[1];
            glGetIntegerv(GL_ACTIVE_TEXTURE, currentActiveTexture, 0);

            glActiveTexture(GL_TEXTURE0);
            int[] currentTexture = new int[1];
            glGetIntegerv(GL_TEXTURE_BINDING_2D, currentTexture, 0);

            float width = RenderHelperComponent.pingfang25.getStringWidth(String.valueOf(count));
            RenderHelperComponent.pingfang25.drawString(String.valueOf(count),x+size+40*scale-width+5f,y+size+5f, Color.WHITE);

            glBindTexture(GL_TEXTURE_2D, currentTexture[0]);
            glActiveTexture(currentActiveTexture[0]);
            glUseProgram(currentProgram[0]);

            MatrixUtil.popMatrix();
        }

        int maxDamage = item.getMaxDamage();
        int damage = data.getTag()==null?0:data.getTag().getInt("Damage",0);

        if(maxDamage>0 && damage>0){
            MatrixUtil.pushMatrix();
            MatrixUtil.loadIdentity();

            int[] currentProgram = new int[1];
            glGetIntegerv(GL_CURRENT_PROGRAM, currentProgram, 0);

            int[] currentActiveTexture = new int[1];
            glGetIntegerv(GL_ACTIVE_TEXTURE, currentActiveTexture, 0);

            glActiveTexture(GL_TEXTURE0);
            int[] currentTexture = new int[1];
            glGetIntegerv(GL_TEXTURE_BINDING_2D, currentTexture, 0);


            glDisable(GL_DEPTH_TEST);
            float size = 64*scale;
            RenderHelperComponent.rectangle(x+4f,y+size,size,4f,Color.BLACK);
            int color = hsvToArgb(Math.max(0.0F, ((float)maxDamage - (float)damage) / (float)maxDamage) / 3.0F, 1.0F, 1.0F,255);
            float step = (float) MathUtil.clamp(Math.round(size - (float)damage * size / (float)maxDamage), 0, size);
            RenderHelperComponent.rectangle(x+4f,y+size,step,2f,color);

            glBindTexture(GL_TEXTURE_2D, currentTexture[0]);
            glActiveTexture(currentActiveTexture[0]);
            glUseProgram(currentProgram[0]);

            MatrixUtil.popMatrix();
            glEnable(GL_BLEND);
        }


        MatrixUtil.pushMatrix();
        MatrixUtil.loadIdentity();

        int[] currentProgram = new int[1];
        glGetIntegerv(GL_CURRENT_PROGRAM, currentProgram, 0);

        int[] currentActiveTexture = new int[1];
        glGetIntegerv(GL_ACTIVE_TEXTURE, currentActiveTexture, 0);

        glActiveTexture(GL_TEXTURE0);
        int[] currentTexture = new int[1];
        glGetIntegerv(GL_TEXTURE_BINDING_2D, currentTexture, 0);

        float size = 64*scale;
        if(maxDamage>0 && damage>=0){
            RenderHelperComponent.pingfang_medium_30.drawCenteredString(String.format("%d/%d",maxDamage-damage,maxDamage),x+size+100f,y+RenderHelperComponent.pingfang_medium_30.getMiddle(60f),Color.WHITE);
        }else{
            RenderHelperComponent.pingfang_medium_30.drawCenteredString("0/0",x+size+100f,y+RenderHelperComponent.pingfang_medium_30.getMiddle(60f),Color.WHITE);
        }


        glBindTexture(GL_TEXTURE_2D, currentTexture[0]);
        glActiveTexture(currentActiveTexture[0]);
        glUseProgram(currentProgram[0]);
        MatrixUtil.popMatrix();
        glEnable(GL_BLEND);

    }

    public static int hsvToArgb(float hue, float saturation, float value, int alpha) {
        int i = (int)(hue * 6.0F) % 6;
        float f = hue * 6.0F - (float)i;
        float g = value * (1.0F - saturation);
        float h = value * (1.0F - f * saturation);
        float j = value * (1.0F - (1.0F - f) * saturation);
        float k;
        float l;
        float m;
        switch (i) {
            case 0:
                k = value;
                l = j;
                m = g;
                break;
            case 1:
                k = h;
                l = value;
                m = g;
                break;
            case 2:
                k = g;
                l = value;
                m = j;
                break;
            case 3:
                k = g;
                l = h;
                m = value;
                break;
            case 4:
                k = j;
                l = g;
                m = value;
                break;
            case 5:
                k = value;
                l = g;
                m = h;
                break;
            default:
                throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + hue + ", " + saturation + ", " + value);
        }

        return Color.argb(alpha, (int) MathUtil.clamp((int)(k * 255.0F), 0, 255), (int) MathUtil.clamp((int)(l * 255.0F), 0, 255), (int) MathUtil.clamp((int)(m * 255.0F), 0, 255));
    }


}
