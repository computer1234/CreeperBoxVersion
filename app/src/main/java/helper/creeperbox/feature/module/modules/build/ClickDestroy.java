package helper.creeperbox.feature.module.modules.build;


import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.BlockDestroyEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.sdk.math.Vec3i;

@ModuleInfo(name = "点击破坏", category = Category.Build)
public class ClickDestroy extends Module {


    private final NumberValue range = new NumberValue("范围", this,5,1,10,1);



    @SubscribeEvent
    public void onBreak(BlockDestroyEvent event){
        Vec3i pos = event.getBlock().getPos();
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
