package helper.creeperbox.feature.module.modules.survival;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.PacketSendEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;

@ModuleInfo(name = "重复发包", category = Category.Survival)
public class RepeatPacket extends Module {

    private final NumberValue time = new NumberValue("次数", this,20,1,100,1);

    @SubscribeEvent
    public void onPacket(PacketSendEvent event){
        EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
        if(player==null) return;
        int time = this.time.getCurrentValue().intValue();
        for(int i = 0 ; i < time ; i++){
            player.sendPacketNoEvent(event.getPacket());
        }
    }


}
