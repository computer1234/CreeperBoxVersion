package helper.creeperbox.feature.module.modules.render;

import android.opengl.Matrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.RenderItem3DEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.sdk.entity.type.EntityActor;
import helper.creeperbox.sdk.entity.type.EntityItem;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.level.Level;
import helper.creeperbox.sdk.math.Vec3f;
import helper.creeperbox.sdk.math.Vec3i;


@ModuleInfo(name = "物理掉落", category = Category.Render)
public class ItemPhysics extends Module {

    public HashMap<Long,Float> itemMap = new HashMap<>();
    public ItemPhysics() {
    }


    @Override
    public void onEnable() {
        EntityLocalPlayer.setItemNoRot(true);
    }

    @Override
    public void onDisable() {
        EntityLocalPlayer.setItemNoRot(false);
    }


    @SubscribeEvent
    public void updateOnGround(TickEvent event){

        Level level = event.getPlayer().getLevel();
        List<Long> activeUids = new ArrayList<>();

        for(EntityActor actor : level.getRuntimeActorList()){
            if(actor instanceof EntityItem){
                long uid = actor.getRuntimeID();
                activeUids.add(uid);

                Vec3f pos = actor.getPos();
                Vec3i blockPos = new Vec3i((int) Math.floor(pos.x), (int) (actor.getAABB().minY-1.1f), (int) Math.floor(pos.z));
                if(!level.getMaterial(blockPos).isAir()){
                    actor.setOnGround(true);
                }
            }
        }

        itemMap.keySet().removeIf(new Predicate<Long>() {
            @Override
            public boolean test(Long uid) {
                return !activeUids.contains(uid);
            }
        });


    }

    @SubscribeEvent
    public void onRenderItem(RenderItem3DEvent event){
        EntityItem item = event.getItem();

        long rid = item.getRuntimeID();
        float yaw = item.getRotation().y;
        float pitch = item.isOnGround() ? 90 : itemMap.getOrDefault(item.getRuntimeID(),0f);
        pitch+=5f;
        if(pitch > 180){
            pitch-=180;
        }

        itemMap.put(rid,pitch);
        Matrix.rotateM(event.matrix,0,pitch,1,0,0);
        Matrix.rotateM(event.matrix,0,yaw,0,0,1);
    }


}
