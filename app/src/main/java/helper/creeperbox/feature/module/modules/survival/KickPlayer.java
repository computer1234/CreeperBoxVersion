package helper.creeperbox.feature.module.modules.survival;

import android.util.Log;

import org.cloudburstmc.protocol.bedrock.data.skin.SerializedSkin;
import org.cloudburstmc.protocol.bedrock.packet.ModalFormRequestPacket;
import org.cloudburstmc.protocol.bedrock.packet.PlayerListPacket;
import org.cloudburstmc.protocol.bedrock.packet.UnknownPacket;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.PacketReceiveEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.network.packet.ConfirmSkinPacket;


@ModuleInfo(name = "踢人光环", category = Category.Survival)
public class KickPlayer extends Module {

    public long ONE_HOUR = 60 * 60 * 1000;
    public long ONE_DAY = 24 * ONE_HOUR;
    public long ONE_YEAR = 365 * ONE_DAY;


    @Override
    public void onEnable() {
        // -1 表示永久用户，应该允许使用
        if(CreeperBox.y() != -1 && CreeperBox.y() <= ONE_YEAR){
            EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
            if(player!=null){
                ModalFormRequestPacket packet = new ModalFormRequestPacket();
                packet.setFormData("{\"type\":\"form\",\"title\":\"踢人开启失败\",\"content\":\"§c此功能仅为永久用户使用\",\"buttons\":[{\"type\":\"button\",\"text\":\"确认\"}]}");
                packet.setFormId(0);
                player.receivePacket(packet);
            }
        } else {
            CreeperBox.z(true);
        }
    }



    @Override
    public void onDisable() {
        CreeperBox.z(false);
    }

}
