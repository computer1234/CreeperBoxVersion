package helper.creeperbox.feature.module.modules.render;

import helper.creeperbox.feature.component.RotationComponent;
import helper.creeperbox.feature.event.EventPriority;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.KeyEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.ListValue;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.sdk.math.Vec2f;

@ModuleInfo(name = "大陀螺", category = Category.Render)
public class Derp extends Module {

    private int counter = 0;

    private final NumberValue speed = new NumberValue("速度", this,4,1, 60, 1);

    private final ListValue mode = new ListValue("模式",this,"身体")
            .addSubList("身体")
            .addSubList("头")
            .addSubList("全部");


    @Override
    public void onEnable() {
        counter = 0;
    }


    @SubscribeEvent(EventPriority.VERY_LOW)
    public void onTick(TickEvent event) {

        counter+=speed.getCurrentValue().intValue();
        if(counter >= 360) counter -= 360;

        switch(mode.getCurrentValue()) {
            case "身体":
                RotationComponent.setRotation(event.getPlayer(),new Vec2f(0,counter),180);
                return;
            case "头":
                RotationComponent.setRotation(event.getPlayer(),new Vec2f(counter,0),180);
                return;
            default:
                RotationComponent.setRotation(event.getPlayer(),new Vec2f(counter,counter),180);
                return;
        }

    }


    @Override
    public String getTag() {
        switch(mode.getCurrentValue()) {
            case "身体":
                return "Body";
            case "头":
                return "Head";
            default:
                return "Both";
        }
    }

}
