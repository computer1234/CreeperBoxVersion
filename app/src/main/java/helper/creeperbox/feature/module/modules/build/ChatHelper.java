package helper.creeperbox.feature.module.modules.build;

import org.cloudburstmc.protocol.bedrock.data.command.CommandOriginData;
import org.cloudburstmc.protocol.bedrock.data.command.CommandOriginType;
import org.cloudburstmc.protocol.bedrock.packet.TextPacket;

import java.util.UUID;

import helper.creeperbox.VerifyManager;
import helper.creeperbox.clickgui.ChatGUI;
import helper.creeperbox.clickgui.Login4399GUI;
import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.sdk.network.packet.modify.NeteaseCommandRequestPacket;
import helper.creeperbox.sdk.network.packet.modify.NeteaseTextPacket;

@ModuleInfo(name = "内置发言", category = Category.Build)
public class ChatHelper extends Module {

    private String msg;


    public void setMsg(String msg) {
        this.msg = msg;
    }



    @Override
    public void onEnable() {
        if(!VerifyManager.isVerify) return;
        msg = null;
        CreeperBox.INSTANCE.activity.runOnUiThread(()->{
            new ChatGUI(this);
        });
    }



    @SubscribeEvent
    public void onTick(TickEvent event){
        if(msg != null && !msg.isEmpty()){
            if(!msg.startsWith("/")){
                NeteaseTextPacket packet = new NeteaseTextPacket();
                packet.setHasExtra(true);
                packet.setType(TextPacket.Type.CHAT);
                packet.setXuid("");
                packet.setAuthor("");
                packet.setPlatformChatId("");
                packet.setSourceName(event.getPlayer().getNameTag());
                packet.setMessage(msg);
                event.getPlayer().sendPacket(packet);
            }else {
                NeteaseCommandRequestPacket packet = new NeteaseCommandRequestPacket();
                packet.setCommandOriginData(new CommandOriginData(CommandOriginType.PLAYER, UUID.randomUUID(),"",0));
                packet.setInternal(false);
                packet.setVersion(36);
                packet.setHasExtra(true);
                packet.setCommand(msg);
                event.getPlayer().sendPacket(packet);
            }
            msg = null;
        }
    }

}
