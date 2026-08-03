package helper.creeperbox.feature.module.modules.combat;

import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.utils.mc.StopWatch;

@ModuleInfo(name = "自杀光环", category = Category.Combat)
public class SelfAttack extends Module {

    private StopWatch attackTicks = new StopWatch();
    private final NumberValue cps = new NumberValue("频率", this,10,1, 100, 1);
    private final NumberValue multiple = new NumberValue("倍数", this,1,1, 100, 1);


    @Override
    public void onEnable() {
        attackTicks.reset();
    }


    @SubscribeEvent
    public void onTick(TickEvent event) {
        int cps = multiple.getCurrentValue().intValue()*this.cps.getCurrentValue().intValue();
        double preTick = 1000d / cps;
        if(attackTicks.finished((long) preTick)){
            int count = cps>20?(int) (attackTicks.getElapsedTime() / preTick):1;
            for(int i = 0 ; i < count ; i++){
                event.getPlayer().attack(event.getPlayer());
            }
            event.getPlayer().swing();
            attackTicks.reset();
        }
    }
}
