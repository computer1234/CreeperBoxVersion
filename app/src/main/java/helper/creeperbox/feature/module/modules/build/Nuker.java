package helper.creeperbox.feature.module.modules.build;

import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.BlockDestroyEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.sdk.math.Vec3f;
import helper.creeperbox.sdk.math.Vec3i;

@ModuleInfo(name = "范围破坏", category = Category.Build)
public class Nuker extends Module {

    private final NumberValue range = new NumberValue("范围", this,5,1,10,1);



    @SubscribeEvent
    public void onTick(TickEvent event){
        Vec3f playerPos = event.getPlayer().getPos();
        Vec3i pos = new Vec3i((int)Math.floor(playerPos.x),(int)Math.floor(playerPos.y),(int)Math.floor(playerPos.z));
        int reach = range.getCurrentValue().intValue();
        for (int x = -reach;x <= reach; x++) {
            for (int y = -reach;y <= reach; y++) {
                for (int z = -reach;z <= reach; z++) {
                    Vec3i blockPos = new Vec3i(pos.x+x,pos.y+y, pos.z+z);
                    event.getPlayer().destroyBlock(blockPos,0);
                }
            }
        }
    }



}
