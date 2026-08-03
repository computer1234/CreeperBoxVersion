package helper.creeperbox.feature.module.modules.movement;

import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.data.PlayerActionType;
import org.cloudburstmc.protocol.bedrock.packet.InventoryTransactionPacket;
import org.cloudburstmc.protocol.bedrock.packet.PlayerActionPacket;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.PacketSendEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.ListValue;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.math.Vec3f;
import helper.creeperbox.sdk.network.packet.modify.NeteasePlayerAuthInputPacket;
import helper.creeperbox.utils.math.RandomUtil;

@ModuleInfo(name = "上帝模式", category = Category.Movement)
public class GodMode extends Module {


    private final NumberValue height = new NumberValue("高度", this,300,0, 10000, 100);

    public static GodMode INSTANCE;

    public GodMode(){
        INSTANCE = this;
    }


    private final ListValue mode = new ListValue("模式",this,"租赁服")
            .addSubList("租赁服")
            .addSubList("花雨庭")
            .addSubList("EC");

    private NeteasePlayerAuthInputPacket lastAuth;
    private float lastY;

    private boolean isHyt = false;

    @Override
    public void onEnable() {
        isHyt = mode.getCurrentValue().equalsIgnoreCase("花雨庭");
        lastAuth = null;
    }



    @SubscribeEvent
    public void onPacket(PacketSendEvent event){


        if(isHyt && event.getPacket() instanceof NeteasePlayerAuthInputPacket){
            NeteasePlayerAuthInputPacket packet = (NeteasePlayerAuthInputPacket) event.getPacket();
            if(packet.getTick()%2 == 0){
                Vector3f pos = packet.getPosition();
                packet.setPosition(Vector3f.from(pos.getX(),900,pos.getZ()));
            }
            return;
        }

        if(!isHyt && event.getPacket() instanceof NeteasePlayerAuthInputPacket && mode.getCurrentValue().equalsIgnoreCase("租赁服")){
            NeteasePlayerAuthInputPacket packet = (NeteasePlayerAuthInputPacket) event.getPacket();
            packet.setCameraDeparted(true);
            Vector3f pos = packet.getPosition();
            lastY = pos.getY();
            packet.setPosition(Vector3f.from(pos.getX(),height.getCurrentValue().floatValue(),pos.getZ()));
            lastAuth = packet;
        }


        if(!isHyt && event.getPacket() instanceof InventoryTransactionPacket && mode.getCurrentValue().equalsIgnoreCase("租赁服")){
            InventoryTransactionPacket packet = (InventoryTransactionPacket) event.getPacket();
            EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
            if(lastAuth!=null && player!=null){
                Vector3f pos = packet.getPlayerPosition();
                lastAuth.setPosition(Vector3f.from(pos.getX(),pos.getY()+3.62f,pos.getZ()));
                player.sendPacketNoEvent(lastAuth);
            }
        }

        if(!isHyt && event.getPacket() instanceof NeteasePlayerAuthInputPacket && mode.getCurrentValue().equalsIgnoreCase("EC")){
            lastAuth = (NeteasePlayerAuthInputPacket) event.getPacket();
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent event){
        if(!isHyt && mode.getCurrentValue().equalsIgnoreCase("租赁服")){
            EntityLocalPlayer player = event.getPlayer();
            PlayerActionPacket packet = new PlayerActionPacket();
            packet.setRuntimeEntityId(player.getRuntimeID());
            packet.setAction(PlayerActionType.RESPAWN);
            packet.setBlockPosition(Vector3i.ZERO);
            packet.setResultPosition(Vector3i.ZERO);
            player.sendPacket(packet);
        }

        if(!isHyt && mode.getCurrentValue().equalsIgnoreCase("EC") && lastAuth !=null){
            Vec3f pos = event.getPlayer().getPos();
            lastAuth.setPosition(Vector3f.from(5000f, pos.y+RandomUtil.getRandom(300,350),5000f));
            event.getPlayer().sendPacketNoEvent(lastAuth);
        }
    }


    @Override
    public void onDisable() {
        if(!isHyt && mode.getCurrentValue().equalsIgnoreCase("租赁服")){
            EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();

            if(player!=null && lastAuth !=null){
                lastAuth.setPosition(Vector3f.from(lastAuth.getPosition().getX(),lastY+1f,lastAuth.getPosition().getZ()));
                player.sendPacketNoEvent(lastAuth);
                try {
                    Thread.sleep(60);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            lastAuth = null;
            lastY = 0;
        }

    }

    @Override
    public String getTag() {
        switch(mode.getCurrentValue()) {
            case "租赁服":
                return "Rental";
            case "EC":
                return "EC";
            default:
                return "HYT";
        }
    }

}
