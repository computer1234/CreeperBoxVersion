package helper.creeperbox.feature.module.modules.render;

import android.opengl.GLES20;

import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;

import java.util.regex.Pattern;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.component.RenderHelperComponent;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.Render2DEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.sdk.ClientInstance;
import helper.creeperbox.sdk.entity.Attributes;
import helper.creeperbox.sdk.entity.type.EntityActor;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.item.ItemStack;
import helper.creeperbox.sdk.math.Vec3f;
import helper.creeperbox.sdk.math.Vec4f;
import helper.creeperbox.sdk.render.UIRenderContext;
import helper.creeperbox.utils.render.MatrixUtil;

@ModuleInfo(name = "玩家标签", category = Category.Render)
public class NameTag extends Module {


    @SubscribeEvent
    public void onRender2D(Render2DEvent event){
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
        if (player!=null){
            for(EntityActor actor : player.getLevel().getPlayers()){
                Long rid = actor.getRuntimeID();
                if(RenderHelperComponent.projectionsData.containsKey(rid)) {
                    Vec4f position = RenderHelperComponent.projectionsData.get(rid);
                    float posX = position.x;
                    float posY = position.y;
                    float endPosX = position.z;
                    float centerX = posX + (endPosX-posX)/2f;
                    String name = formatName(actor.getNameTag());
                    if(name.isEmpty()){
                        name = "赵梓洛大端";
                    }

                    float fontWidth = RenderHelperComponent.pingfang25.getStringWidth(name);
                    float margin = 10f;
                    RenderHelperComponent.drawGradientRound(centerX-fontWidth/2f-margin,posY-50f,fontWidth+margin*2,40f,20f,0x3f000000,0x3f000000,true);
                    RenderHelperComponent.drawShadow(centerX-fontWidth/2f-margin,posY-50f,fontWidth+margin*2,40f,20f,0x3f000000,0x3f000000,true, MatrixUtil.mvpMatrix());
                    RenderHelperComponent.pingfang25.drawCenteredString(name,centerX,posY-50f+RenderHelperComponent.pingfang25.getMiddle(40f),0xffff5555);


                    float leftX = centerX-fontWidth/2f-margin*2f;
                    String health = String.valueOf((int) actor.getAttributeCurrentValue(Attributes.HEALTH));
                    float healthWidth = RenderHelperComponent.pingfang25.getStringWidth(health) + 5f;
                    RenderHelperComponent.drawGradientRound(leftX - 50f - healthWidth,posY-50f,50f+healthWidth,40f,20f,0x3f000000,0x3f000000,true);
                    RenderHelperComponent.drawShadow(leftX - 50f - healthWidth,posY-50f,50f+healthWidth,40f,20f,0x3f000000,0x3f000000,true, MatrixUtil.mvpMatrix());
                    RenderHelperComponent.imageXY(RenderHelperComponent.healthTexture,leftX-40f - healthWidth,posY-45f,0f,30f,30f,0xffcccccc,MatrixUtil.mvpMatrix());
                    RenderHelperComponent.pingfang25.drawString(health,leftX - 5f - healthWidth,posY-50f+RenderHelperComponent.pingfang25.getMiddle(40f),0xffaaaaaa);


                    StringBuilder sb = new StringBuilder();
                    sb.append((int)distance(player,actor));
                    sb.append("m");
                    float rightX = centerX+fontWidth/2f+margin*2f;
                    float distanceWidth = RenderHelperComponent.pingfang25.getStringWidth(sb.toString());
                    RenderHelperComponent.drawGradientRound(rightX,posY-50f,distanceWidth+20f,40f,20f,0x3f000000,0x3f000000,true);
                    RenderHelperComponent.drawShadow(rightX,posY-50f,distanceWidth+20f,40f,20f,0x3f000000,0x3f000000,true, MatrixUtil.mvpMatrix());
                    RenderHelperComponent.pingfang25.drawString(sb.toString(),rightX+10f,posY-50f+RenderHelperComponent.pingfang25.getMiddle(40f),0xffaaaaaa);

                    RenderHelperComponent.loadOpenGLCtx();
                    float y = posY - 110f;
                    float x = centerX - 150f;
                    renderItem(event.getCtx(),actor.getItemInHand(),x,y);
                    x+=50f;
                    for(int i = 0 ; i < 4 ; i ++){
                        renderItem(event.getCtx(),actor.getArmor(i),x,y);
                        x+=50f;
                    }
                    renderItem(event.getCtx(),actor.getItemOffHand(),x,y);
                    RenderHelperComponent.saveOpenGLCtx();
                    RenderHelperComponent.loadOpenGLCtx();
                }

            }
        }
    }

    public void renderItem(UIRenderContext ctx, ItemStack item, float x, float y){
        float ratio = ClientInstance.getGuiScale();
        float def = 5/ratio;
        float scale = 0.6f * def;
        ItemData data = item.getItemData();

        if (!data.isValid()) return;
        boolean ench = data.getTag()!=null && !data.getTag().getList("ench", NbtType.COMPOUND).isEmpty();

        ctx.renderItem(item,x/ratio,y/ratio,1f,1f,scale,ench);
    }


    private static double distance(EntityActor a1, EntityActor a2) {
        Vec3f pos1 = a1.getPos();
        Vec3f pos2 = a2.getPos();
        float dx = pos1.x - pos2.x;
        float dy = pos1.y - pos2.y;
        float dz = pos1.z - pos2.z;
        return Math.sqrt((double)(dx * dx + dy * dy + dz * dz));
    }

    private static final Pattern COLOR_CODE_PATTERN = Pattern.compile("§[0-9a-fk-or]");

    public static String formatName(String text){
        return COLOR_CODE_PATTERN.matcher(text).replaceAll("");
    }


}
