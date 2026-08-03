package helper.creeperbox.feature.event.events;

import helper.creeperbox.feature.event.CancellableEvent;

import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

public class PacketSendEvent extends CancellableEvent {
    private BedrockPacket packet;

    public PacketSendEvent(BedrockPacket packet){
        this.packet = packet;
    }

    public BedrockPacket getPacket() {
        return packet;
    }


}
