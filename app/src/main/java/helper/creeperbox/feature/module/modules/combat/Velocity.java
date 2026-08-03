package helper.creeperbox.feature.module.modules.combat;

import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.packet.SetEntityMotionPacket;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.PacketReceiveEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;


@ModuleInfo(name = "反击退", category = Category.Combat)
public class Velocity extends Module {


    private final NumberValue vertical = new NumberValue("垂直", this,0,0, 1, 0.01);
    private final NumberValue horizontal = new NumberValue("水平", this,0,0, 1, 0.01);


    @SubscribeEvent
    public void onPacket(PacketReceiveEvent event){
        if(event.getPacket() instanceof SetEntityMotionPacket){
            SetEntityMotionPacket packet = (SetEntityMotionPacket) event.getPacket();
            EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
            if(player!=null && packet.getRuntimeEntityId() == player.getRuntimeID()){
                Vector3f motion = packet.getMotion();
                packet.setMotion(Vector3f.from(horizontal.getCurrentValue().floatValue()*motion.getX(),vertical.getCurrentValue().floatValue()*motion.getY(),horizontal.getCurrentValue().floatValue()*motion.getZ()));
            }
        }
    }



}
