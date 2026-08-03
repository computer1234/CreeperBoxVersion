package helper.creeperbox.feature.module.modules.combat;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.BooleanValue;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.utils.mc.StopWatch;

@ModuleInfo(name = "自动点击", category = Category.Combat)
public class AutoClicker extends Module {
    private final NumberValue cps = new NumberValue("频率", this,10,1, 20, 1);

    private final BooleanValue left = new BooleanValue("左键",this,true);

    private final BooleanValue right = new BooleanValue("右键",this,true);

    private StopWatch leftWatch = new StopWatch();
    private StopWatch rightWatch = new StopWatch();

    @Override
    public void onEnable() {
        leftWatch.reset();
        rightWatch.reset();
    }

    @SubscribeEvent
    public void onTick(TickEvent event){
        if(left.getCurrentValue()){
            int cps = this.cps.getCurrentValue().intValue();
            double preTick = 1000d / cps;
            if(leftWatch.finished((long)preTick)){
                CreeperBox.INSTANCE.handleDestroyOrAttackButtonPress();
                leftWatch.reset();
            }
        }

        if(right.getCurrentValue()){
            int cps = this.cps.getCurrentValue().intValue();
            double preTick = 1000d / cps;
            if(rightWatch.finished((long)preTick)){
                CreeperBox.INSTANCE.handleBuildOrInteractButtonPress();
                rightWatch.reset();
            }
        }
    }


    @Override
    public String getTag() {
        boolean l = left.getCurrentValue();
        boolean r = right.getCurrentValue();
        if(r && l){
            return "All";
        }
        if(r){
            return "Right";
        }

        if(l){
            return "Left";
        }

        return "None";
    }


}
