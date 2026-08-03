package helper.creeperbox.feature.module.modules.combat;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.math.Vec2f;
import helper.creeperbox.utils.mc.TargetUtil;

@ModuleInfo(name = "碰撞箱", category = Category.Combat)
public class HitBox extends Module {

    private final NumberValue sizeX = new NumberValue("横向", this,3,1, 6, 0.1);

    private final NumberValue sizeY = new NumberValue("纵向", this,3,1, 6, 0.1);


    @SubscribeEvent
    public void onTick(TickEvent event){
        EntityLocalPlayer player = event.getPlayer();
        float x = sizeX.getCurrentValue().floatValue();
        float y = sizeY.getCurrentValue().floatValue();
        TargetUtil.findTarget(player,100).forEach(en->{
            en.setHitBoxSize(new Vec2f(x,y));
        });
    }


    @Override
    public void onDisable() {
        EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
        if(player!=null){
            TargetUtil.findTarget(player,Integer.MAX_VALUE).forEach(en->{
                en.setHitBoxSize(new Vec2f(0.6f,1.8f));
            });
        }
    }

}
