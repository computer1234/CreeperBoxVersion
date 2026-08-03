package helper.creeperbox.feature.module.modules.render;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.component.FovComponent;
import helper.creeperbox.feature.component.GameDataComponent;
import helper.creeperbox.feature.component.PythonCallerComponent;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.Render2DEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.utils.render.animation.Animation;
import helper.creeperbox.utils.render.animation.Easing;

@ModuleInfo(name = "放大镜", category = Category.Render)
public class Zoom extends Module {


    @Override
    public void onEnable() {
        FovComponent.setToggle(true);
    }


    @Override
    public void onDisable() {
        FovComponent.setToggle(false);
    }
}
