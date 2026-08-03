package helper.creeperbox.feature.module.modules.render;

import android.graphics.Color;
import android.opengl.GLES20;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.component.RenderHelperComponent;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.Render2DEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.sdk.entity.Attributes;
import helper.creeperbox.sdk.entity.type.EntityActor;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.math.Vec4f;
import helper.creeperbox.utils.render.MatrixUtil;

@ModuleInfo(name = "绘制", category = Category.Render)
public class ESP extends Module {


    @SubscribeEvent
    public void onRender2D(Render2DEvent event){
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
        if (player!=null){
            for(EntityActor actor : player.getLevel().getPlayers()){
                Long rid = actor.getRuntimeID();
                if(RenderHelperComponent.projectionsData.containsKey(rid)){
                    Vec4f position = RenderHelperComponent.projectionsData.get(rid);
                    float posX = position.x;
                    float posY = position.y;
                    float endPosX = position.z;
                    float endPosY = position.w;
                    float width = 3f;
                    int color = Color.WHITE;
                    float lengthX = (endPosX - posX) / 5f;
                    float lengthY = (endPosY - posY) / 5f;

                    RenderHelperComponent.drawGradientRoundOld(posX,posY,lengthX+width,width,0f,color,color,false);
                    RenderHelperComponent.drawGradientRoundOld(posX,posY,width,lengthY+width,0f,color,color,true);

                    //Left bottom
                    RenderHelperComponent.drawGradientRoundOld(posX,endPosY,lengthX+width,width,0f,color,color,false);
                    RenderHelperComponent.drawGradientRoundOld(posX,endPosY-lengthY,width,lengthY,0f,color,color,true);


                    //Right Top
                    RenderHelperComponent.drawGradientRoundOld(endPosX-lengthX,posY,lengthX,width,0f,color,color,false);
                    RenderHelperComponent.drawGradientRoundOld(endPosX,posY,width,lengthY+width,0f,color,color,true);

                    //Right bottom
                    RenderHelperComponent.drawGradientRoundOld(endPosX-lengthX,endPosY,lengthX+width,width,0f,color,color,false);
                    RenderHelperComponent.drawGradientRoundOld(endPosX,endPosY-lengthY,width,lengthY,0f,color,color,true);


                    lengthY = endPosY - posY;
                    lengthX = endPosX - posX;

                    //Health
                    float healthPrograss = Math.min(1f,actor.getAttributeCurrentValue(Attributes.HEALTH)/actor.getAttributeMaxValue(Attributes.HEALTH));

                    float startY = posY + lengthY*(1-healthPrograss);
                    RenderHelperComponent.drawGradientRoundOld(posX - 11f,posY-1f,7f,lengthY+2f,0f, Color.BLACK,Color.BLACK,true);
                    RenderHelperComponent.drawGradientRoundOld(posX - 10f,posY,5f,lengthY*(1-healthPrograss),0f, Color.BLACK,Color.BLACK,true);
                    RenderHelperComponent.drawGradientRoundOld(posX - 10f,startY,5f,lengthY*healthPrograss,0f,0xff00bf00,0xff00bf00,true);
                }
            }
        }
    }


}
