package helper.creeperbox.feature.module.modules.build;

import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.ListValue;


@ModuleInfo(name = "游戏模式", category = Category.Build)
public class GameModeChange extends Module {

    private final ListValue mode = new ListValue("模式",this,"生存")
            .addSubList("生存")
            .addSubList("创造")
            .addSubList("冒险");


    @SubscribeEvent
    public void onTick(TickEvent event){
        int mode = 0;
        switch (this.mode.getCurrentValue()){
            case "创造":
                mode = 1;
                break;
            case "冒险":
                mode = 2;
                break;
            default:
                break;
        }
        event.getPlayer().setGameType(mode);
    }

    @Override
    public String getTag() {
        switch (this.mode.getCurrentValue()){
            case "创造":
                return "Creative";
            case "冒险":
                return "Adventure";
            default:
                return "Survival";
        }
    }
}
