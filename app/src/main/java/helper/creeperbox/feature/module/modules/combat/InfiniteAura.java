package helper.creeperbox.feature.module.modules.combat;

import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.data.PlayerActionType;
import org.cloudburstmc.protocol.bedrock.data.inventory.transaction.InventoryTransactionType;
import org.cloudburstmc.protocol.bedrock.packet.InventoryTransactionPacket;
import org.cloudburstmc.protocol.bedrock.packet.PlayerActionPacket;
import org.cloudburstmc.protocol.bedrock.packet.PlayerAuthInputPacket;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.PacketSendEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.ListValue;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.sdk.entity.Abilities;
import helper.creeperbox.sdk.entity.Attributes;
import helper.creeperbox.sdk.entity.type.EntityActor;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.entity.type.EntityServerPlayer;
import helper.creeperbox.sdk.math.Vec3f;
import helper.creeperbox.sdk.network.packet.PyRpcPacket;
import helper.creeperbox.sdk.network.packet.modify.NeteasePlayerAuthInputPacket;
import helper.creeperbox.utils.mc.StopWatch;
import helper.creeperbox.utils.mc.TargetUtil;

@ModuleInfo(name = "百米大刀", category = Category.Combat)
public class InfiniteAura extends Module {

    private final NumberValue cps = new NumberValue("频率", this,10,1, 100, 1);
    private final NumberValue range = new NumberValue("范围", this,400,10, 400, 0.1);
    private final ListValue mode = new ListValue("模式",this,"轮换")
            .addSubList("单体")
            .addSubList("群体")
            .addSubList("轮换");
    private StopWatch switchChangeTicks = new StopWatch();
    private EntityActor currentTarget;
    private StopWatch attackTicks = new StopWatch();
    public StopWatch attackWatch = new StopWatch();

    @Override
    public void onEnable() {
        attackTicks.reset();
        switchChangeTicks.reset();
        attackWatch.reset();
        lastAuth = null;
    }


    private List<EntityActor> targets;

    private static double distance(EntityActor a1, EntityActor a2){
        Vec3f pos1 = a1.getPos();
        Vec3f pos2 = a2.getPos();
        float dx = pos1.x - pos2.x;
        float dy = pos1.y - pos2.y;
        float dz = pos1.z - pos2.z;
        return Math.sqrt(dx*dx+dy*dy+dz*dz);
    }


    private void findEntity(EntityLocalPlayer player) {

        targets = new ArrayList<>();

        for (EntityActor en : TargetUtil.findTarget(player,range.getCurrentValue().floatValue())){
            if(en instanceof EntityLocalPlayer) continue;
            if(en.isAlive()){
                targets.add(en);
            }
        }

        if(targets.isEmpty()){
            return;
        }

        switch(mode.getCurrentValue()){
            case "单体":
                currentTarget = targets.get(0);
                targets = new ArrayList<>();
                targets.add(currentTarget);
                break;
            case "轮换":
                if(!switchChangeTicks.finished(100)){
                    targets = new ArrayList<>();
                    targets.add(currentTarget);
                    break;
                }
                if(targets.contains(currentTarget)) {
                    int index = targets.indexOf(currentTarget)+1;
                    if (index > 0 && index < targets.size()){
                        currentTarget = targets.get(index);
                    }else{
                        currentTarget = targets.get(0);
                    }
                }else{
                    currentTarget = targets.get(0);
                }
                targets = new ArrayList<>();
                targets.add(currentTarget);
                switchChangeTicks.reset();
                break;
            case "群体":
            default:
                break;
        }
    }
    private PlayerAuthInputPacket lastAuth;

    @SubscribeEvent
    public void onTick(TickEvent event){

        EntityLocalPlayer player = event.getPlayer();

        if(!attackWatch.finished(400)) return;

        if(lastAuth == null) return;

        findEntity(player);

        if(targets.isEmpty()){
            return;
        }

        attackEntity(player);
    }


    @SubscribeEvent
    public void onPacket(PacketSendEvent event){
        if(event.getPacket() instanceof NeteasePlayerAuthInputPacket){
            ((NeteasePlayerAuthInputPacket) event.getPacket()).setCameraDeparted(true);
            lastAuth = (PlayerAuthInputPacket) event.getPacket();
        }
    }

    private void attackEntity(EntityLocalPlayer player) {
        if(targets.isEmpty()){
            return;
        }

        int cps = this.cps.getCurrentValue().intValue();
        double preTick = 1000d / cps;

        if(attackTicks.finished((long) preTick)){
            int count = cps >20 ? (int) (attackTicks.getElapsedTime() / preTick):1;
            attackAll(player,count);
            attackTicks.reset();
        }

    }



    private static float offsetY = 0.8f;
    private void attackAll(EntityLocalPlayer player,int count){

        Vector3f temp = lastAuth.getPosition();

        for(EntityActor en : targets){
            Vec3f pos = en.getPos();
            lastAuth.setPosition(Vector3f.from(pos.x,pos.y + offsetY,pos.z));
            player.sendPacket(lastAuth);
            for(int i = 0 ; i < count ; i ++){
                InventoryTransactionPacket packet = new InventoryTransactionPacket();
                packet.setTransactionType(InventoryTransactionType.ITEM_USE_ON_ENTITY);
                packet.setActionType(1);
                packet.setRuntimeEntityId(en.getRuntimeID());
                packet.setHotbarSlot(player.getInventory().getSelected());
                packet.setItemInHand(player.getItemInHand().getItemData());
                packet.setPlayerPosition(Vector3f.from(pos.x, pos.y + offsetY , pos.z));
                packet.setClickPosition(Vector3f.ZERO);
                player.sendPacket(packet);
                player.swing();
            }
        }

        lastAuth.setPosition(temp.add(0,offsetY,0));
        player.sendPacket(lastAuth);
    }

    @SubscribeEvent
    public void onPacket2(PacketSendEvent event){
        if(event.getPacket() instanceof NeteasePlayerAuthInputPacket){
            EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
            PlayerActionPacket packet = new PlayerActionPacket();
            packet.setRuntimeEntityId(player.getRuntimeID());
            packet.setAction(PlayerActionType.RESPAWN);
            packet.setBlockPosition(Vector3i.ZERO);
            packet.setResultPosition(Vector3i.ZERO);
            player.sendPacket(packet);
        }
    }


    @Override
    public String getTag() {
        switch(mode.getCurrentValue()) {
            case "单体":
                return "Single";
            case "群体":
                return "Multi";
            default:
                return "Switch";
        }
    }

}
