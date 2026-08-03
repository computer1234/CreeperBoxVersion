package helper.creeperbox.feature.component;

import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.RequestNetworkSettingsPacket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.event.EventPriority;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.PacketSendEvent;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;

public class BlinkComponent {
    public static final ConcurrentLinkedQueue<BedrockPacket> packets = new ConcurrentLinkedQueue<>();

    public static boolean blink = false;
    public static ArrayList<Class<?>> allowPackets = new ArrayList<>();
    public static ArrayList<Class<?>> exemptedPackets = new ArrayList<>();
    private static boolean exemptedMode = false;

    private static boolean dispatch = false;

    public static void setAllowPackets(Class<?>... packets) {
        allowPackets = new ArrayList<>(Arrays.asList(packets));
        exemptedMode = false;
    }


    public static void setExemptedPackets(Class<?>... packets) {
        exemptedPackets = new ArrayList<>(Arrays.asList(packets));
        exemptedMode = true;
    }

    public static void dispatch() {
        dispatch = true;
    }

    public static void clear(){
        packets.clear();
    }



    @SubscribeEvent(EventPriority.VERY_LOW)
    public void onPacketSend(PacketSendEvent event) {

        //join
        if(event.getPacket() instanceof RequestNetworkSettingsPacket) {
            packets.clear();
            return;
        }



        if(blink && !dispatch){
            BedrockPacket packet = event.getPacket();
            if(exemptedMode){
                if(exemptedPackets.contains(packet.getClass())){
                    return;
                }
                packets.add(packet);
                event.setCancelled();
            }else{
                for(Class clazz : allowPackets){
                    if(clazz == packet.getClass()){
                        packets.add(packet);
                        event.setCancelled();
                        return;
                    }
                }
            }
        }


        if(dispatch){
            EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
            if(player == null) return;
            for(BedrockPacket packet: packets){
                player.sendPacketNoEvent(packet);
            }
            dispatch = false;
            packets.clear();
        }


    }

}
