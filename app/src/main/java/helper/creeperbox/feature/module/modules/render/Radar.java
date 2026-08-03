package helper.creeperbox.feature.module.modules.render;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.GL_VIEWPORT;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glGetIntegerv;

import android.graphics.Color;

import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.component.RenderHelperComponent;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.Render2DEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.module.modules.combat.Target;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.sdk.entity.type.EntityActor;
import helper.creeperbox.sdk.entity.type.EntityItem;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.entity.type.EntityPlayer;
import helper.creeperbox.sdk.math.Vec3f;
import helper.creeperbox.utils.render.MatrixUtil;
import helper.creeperbox.utils.render.Render2DUtil;
import helper.creeperbox.utils.render.StencilUtil;

@ModuleInfo(name = "雷达", category = Category.Render)
public class Radar extends Module {


    private final NumberValue xPercent = new NumberValue("左右比例", this, 0.5f, 0f, 1f, 0.01f);
    private final NumberValue yPercent = new NumberValue("上下比例", this, 0.5f, 0f, 1f, 0.01f);

    public float degree = 0;

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


        RenderHelperComponent.roundImage(RenderHelperComponent.radarTexture,startX,startY,300,300,40);
        degree++;
        if(degree>=360) degree = 0;


//        StencilUtil.initStencil();
//        StencilUtil.writeStencilBuffer();
//        RenderHelperComponent.drawGradientRound(startX,startY,300,300,40, Color.WHITE,Color.WHITE,true);
//        StencilUtil.readStencilBuffer();

        RenderHelperComponent.scissorStart(startX,startY,300,300);
        Render2DUtil.startRotation(startX,startY,300,300,degree);
        RenderHelperComponent.drawLight(RenderHelperComponent.lightTexture,startX-50,startY-50,400,400,Color.rgb(45,184,98));
        Render2DUtil.endRotation();

        RenderHelperComponent.bloomUtil.addTask(new Runnable() {
            @Override
            public void run() {
                RenderHelperComponent.drawGradientRound(startX,startY,300, 300,40f,RenderInterface.applyColor(270),RenderInterface.applyColor(0),RenderInterface.applyColor(180),RenderInterface.applyColor(90));
            }
        });

        MatrixUtil.pushMatrix();
        MatrixUtil.transtale(startX+150,startY+150,0);
        MatrixUtil.rotate(-player.getRotation().y+180,0,0,1);
        Vec3f pos = player.getPos();
        glBlendFunc(GL_SRC_ALPHA, GL_ONE);
        int color = -1;
        ItemData armor = player.getArmor(0).getItemData();
        if(armor.getTag() != null){
            color = armor.getTag().getInt("customColor",-1);
        }
        boolean onlyPlayer = Target.getINSTANCE().target.getCurrentValue().equals("玩家");
        for(EntityActor actor : player.getLevel().getRuntimeActorList()){
            if(actor instanceof EntityLocalPlayer) continue;
            if(actor instanceof EntityItem) continue;
            if(!actor.isAlive()) continue;
            if(onlyPlayer && !(actor instanceof EntityPlayer)){
                continue;
            }
            boolean isEnemy = true;
            if(actor instanceof EntityPlayer){
                ItemData enArmor = actor.getArmor(0).getItemData();
                if(color!= -1 && enArmor.getTag() != null && enArmor.getTag().getInt("customColor",-1)==color){
                    isEnemy = false;
                }
            }
            if(distance(player, actor) < 100){
                Vec3f targetPos = actor.getPos();
                drawTarget((targetPos.x-pos.x)*4f,(targetPos.z-pos.z)*4f,isEnemy);
            }
        }
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        RenderHelperComponent.scissorEnd();
        MatrixUtil.popMatrix();

    }


    private static double distance(EntityActor a1, EntityActor a2) {
        Vec3f pos1 = a1.getPos();
        Vec3f pos2 = a2.getPos();
        float dx = pos1.x - pos2.x;
        float dy = pos1.y - pos2.y;
        float dz = pos1.z - pos2.z;
        return Math.sqrt((double)(dx * dx + dy * dy + dz * dz));
    }


    public void drawTarget(float x,float y,boolean isEnemy){
        RenderHelperComponent.image(RenderHelperComponent.targetTexture,x-15f,y-15f,30,30,isEnemy?0xFFFF0000:0xFF00FF00);
    }


}
