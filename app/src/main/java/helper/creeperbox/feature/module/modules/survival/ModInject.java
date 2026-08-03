package helper.creeperbox.feature.module.modules.survival;

import org.cloudburstmc.protocol.bedrock.packet.ResourcePackStackPacket;

import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.PacketReceiveEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.BooleanValue;

@ModuleInfo(name = "模组注入", category = Category.Survival)
public class ModInject extends Module {

    private final BooleanValue shadow = new BooleanValue("光影",this,false);


    private final BooleanValue motionBlur = new BooleanValue("动态模糊",this,false);


    private final BooleanValue action = new BooleanValue("更多动作",this,false);

    @SubscribeEvent
    public void onPacket(PacketReceiveEvent event) {
        if(event.getPacket() instanceof ResourcePackStackPacket){

            ResourcePackStackPacket packet = (ResourcePackStackPacket) event.getPacket();

            if(shadow.getCurrentValue()){
                ResourcePackStackPacket.Entry entry1 = new ResourcePackStackPacket.Entry("10221024-1024-2048-4096-200000000000","0.0.1","");
                packet.getResourcePacks().add(entry1);

                ResourcePackStackPacket.Entry entry2 = new ResourcePackStackPacket.Entry("1022c64c-157b-4418-9053-bbc72ca72c28","0.0.1","");
                packet.getBehaviorPacks().add(entry2);

                ResourcePackStackPacket.Entry entry3 = new ResourcePackStackPacket.Entry("02286bcd-bbfd-4b2d-9b5d-ab8dfbbd4bed","0.0.1","");
                packet.getResourcePacks().add(entry3);
            }

            if(motionBlur.getCurrentValue()){
                ResourcePackStackPacket.Entry entry4 = new ResourcePackStackPacket.Entry("513f2e9a-94c7-4ab3-bba1-b862ddcd2b29","0.0.3","");
                packet.getBehaviorPacks().add(entry4);

                ResourcePackStackPacket.Entry entry5 = new ResourcePackStackPacket.Entry("f87bcb22-d15a-4a6e-a45b-7994df5f8da4","0.0.3","");
                packet.getResourcePacks().add(entry5);
            }

            if(action.getCurrentValue()){
                ResourcePackStackPacket.Entry entry6 = new ResourcePackStackPacket.Entry("147c2c31-3b13-41df-b11a-24ed188f650f","3.2.0","");
                packet.getBehaviorPacks().add(entry6);

                ResourcePackStackPacket.Entry entry7 = new ResourcePackStackPacket.Entry("29f65651-0851-4081-8e3c-ebaa140d387c","3.2.0","");
                packet.getResourcePacks().add(entry7);
            }

        }
    }




}
