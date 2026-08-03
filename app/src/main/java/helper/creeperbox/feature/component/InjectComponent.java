package helper.creeperbox.feature.component;

import org.cloudburstmc.protocol.bedrock.packet.ResourcePackStackPacket;

import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.PacketReceiveEvent;

public class InjectComponent {

    @SubscribeEvent
    public void onPacket(PacketReceiveEvent event) {
        if(event.getPacket() instanceof ResourcePackStackPacket){
            ResourcePackStackPacket packet = (ResourcePackStackPacket) event.getPacket();
            ResourcePackStackPacket.Entry entry1 = new ResourcePackStackPacket.Entry("f22415f6-49e6-4180-a136-a2e34c96a7e0","1.0.0","");
            packet.getResourcePacks().add(entry1);
        }
    }


}
