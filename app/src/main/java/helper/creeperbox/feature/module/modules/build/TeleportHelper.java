package helper.creeperbox.feature.module.modules.build;

import org.cloudburstmc.protocol.bedrock.data.command.CommandOriginData;
import org.cloudburstmc.protocol.bedrock.data.command.CommandOriginType;
import org.cloudburstmc.protocol.bedrock.packet.TextPacket;

import java.util.UUID;

import helper.creeperbox.VerifyManager;
import helper.creeperbox.clickgui.ChatGUI;
import helper.creeperbox.clickgui.TeleportGUI;
import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.sdk.entity.type.EntityActor;
import helper.creeperbox.sdk.math.Vec3f;
import helper.creeperbox.sdk.network.packet.modify.NeteaseCommandRequestPacket;
import helper.creeperbox.sdk.network.packet.modify.NeteaseTextPacket;

@ModuleInfo(name = "坐标传送", category = Category.Build)
public class TeleportHelper extends Module {

    private String msg;


    public void setMsg(String msg) {
        this.msg = msg;
    }


    @Override
    public void onEnable() {
        if(!VerifyManager.isVerify) return;
        msg = null;
        CreeperBox.INSTANCE.activity.runOnUiThread(()->{
            new TeleportGUI(this);
        });
    }



    @SubscribeEvent
    public void onTick(TickEvent event){
        if(msg != null && !msg.isEmpty()){
            String[] pos = msg.split(" ");
            if(pos.length != 3){
                event.getPlayer().displayClientMessage("不正确的坐标格式,如 1 1 1");
            }

            try {
                float x = Float.parseFloat(pos[0]);
                float y = Float.parseFloat(pos[1]);
                float z = Float.parseFloat(pos[2]);
                EntityActor actor = event.getPlayer().getVehicle();
                if(actor != null) actor.setPos(new Vec3f(x,y,z));
                else event.getPlayer().setPos(new Vec3f(x,y,z));
            }catch (Exception e){

            }

            msg = null;
        }
    }


}
