package helper.creeperbox.feature.component;


import org.cloudburstmc.protocol.bedrock.data.AuthoritativeMovementMode;
import org.cloudburstmc.protocol.bedrock.packet.NetworkSettingsPacket;
import org.cloudburstmc.protocol.bedrock.packet.RequestNetworkSettingsPacket;

import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.PacketReceiveEvent;
import helper.creeperbox.feature.event.events.PacketSendEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.sdk.network.packet.modify.NeteaseStartGamePacket;

public class GameDataComponent {

    private static final boolean isDefaultNukkit = true;

    public static boolean movementServerAuthoritative = isDefaultNukkit;
    public static boolean blockBreakingServerAuthoritative = isDefaultNukkit;

    public static boolean inventoriesServerAuthoritative = !isDefaultNukkit;


    public static long tick = 0;

    @SubscribeEvent
    public void onPacket(PacketSendEvent event){
        if(event.getPacket() instanceof RequestNetworkSettingsPacket) {
            tick = 0;
        }
    }


    @SubscribeEvent
    public void onPacket(PacketReceiveEvent event){
        if(event.getPacket() instanceof NetworkSettingsPacket){
            tick = 0;
        }

        if(event.getPacket() instanceof NeteaseStartGamePacket) {
            NeteaseStartGamePacket packet = (NeteaseStartGamePacket) event.getPacket();
            movementServerAuthoritative = packet.getAuthoritativeMovementMode() != AuthoritativeMovementMode.CLIENT;
            blockBreakingServerAuthoritative = packet.isServerAuthoritativeBlockBreaking();
            inventoriesServerAuthoritative = packet.isInventoriesServerAuthoritative();
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent event){
        tick++;
    }

}
