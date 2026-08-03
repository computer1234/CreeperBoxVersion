package helper.creeperbox.feature.module.modules.combat;

import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.sdk.entity.type.EntityActor;
import helper.creeperbox.sdk.math.Vec3f;

@ModuleInfo(name = "水晶光环", category = Category.Combat)
public class CrystalAura extends Module {


    @SubscribeEvent
    public void onTick(TickEvent event) {
        for(EntityActor actor : event.getPlayer().getLevel().getRuntimeActorList()){
            if(actor.getNameSpace().equals("minecraft:ender_crystal")){
                if(distance(actor,event.getPlayer()) <= 6f){
                    event.getPlayer().attack(actor);
                    event.getPlayer().swing();
                }
            }
        }
    }


    private static double distance(EntityActor a1, EntityActor a2) {
        Vec3f pos1 = a1.getPos();
        Vec3f pos2 = a2.getPos();
        float dx = pos1.x - pos2.x;
        float dy = pos1.y - pos2.y;
        float dz = pos1.z - pos2.z;
        return Math.sqrt((double)(dx * dx + dy * dy + dz * dz));
    }

}
