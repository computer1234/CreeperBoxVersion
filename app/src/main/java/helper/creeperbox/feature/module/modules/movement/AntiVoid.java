package helper.creeperbox.feature.module.modules.movement;

import org.cloudburstmc.protocol.bedrock.packet.MobEquipmentPacket;
import org.cloudburstmc.protocol.bedrock.packet.MovePlayerPacket;

import helper.creeperbox.feature.component.BlinkComponent;
import helper.creeperbox.feature.component.GameDataComponent;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.ListValue;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.sdk.block.EnumFacing;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.math.Vec3f;
import helper.creeperbox.sdk.math.Vec3i;
import helper.creeperbox.sdk.network.packet.modify.NeteasePlayerAuthInputPacket;
import helper.creeperbox.utils.mc.PlayerUtil;
import helper.creeperbox.utils.mc.StopWatch;
@ModuleInfo(name = "反虚空", category = Category.Movement)
public class AntiVoid extends Module {
    private final NumberValue distance = new NumberValue("距离",this, 5, 0, 10, 1);

    private final ListValue mode = new ListValue("模式",this,"瞬移")
            .addSubList("空气放置")
            .addSubList("传送")
            .addSubList("瞬移");

    private final NumberValue blinkSecond = new NumberValue("瞬移等待时长",this, 2, 0, 10, 1);

    private StopWatch blinkTicks = new StopWatch();

    private Vec3f safePos;
    private Vec3f safeMotion;
    private boolean startBlink = false;

    private boolean wasBlink = false;

    private static int tick = 0;

    public AntiVoid(){
    }

    @Override
    public void onDisable() {
        if(startBlink) BlinkComponent.blink = false;
        startBlink = false;
        wasBlink = false;
    }

    private int searchBlockInHotBar(EntityLocalPlayer player){
        if(player.getItemInHand().isBlock()){
            return player.getInventory().getSelected();
        }
        for(int i = 0 ; i< 9 ; i ++){
            if(player.getInventory().getContainer().getItemStack(i).isBlock()){
                return i;
            }
        }
        return -1;
    }

    private boolean doAirPlace(EntityLocalPlayer player){
        int slot = searchBlockInHotBar(player);
        if(slot == -1) return false;

        Vec3f pos = player.getPos();
        Vec3i clickPos = new Vec3i((int) Math.floor(pos.x), (int) (Math.floor(pos.y)-3), (int) Math.floor(pos.z));
        Vec3i blockPos = new Vec3i((int) Math.floor(pos.x), (int) (Math.floor(pos.y)-2), (int) Math.floor(pos.z));

        if(!player.getLevel().getMaterial(blockPos).isAir()){
            return true;
        }

        int selected = player.getInventory().getSelected();
        boolean needSwitch = selected!=slot;

        if(needSwitch){
            player.getInventory().setSelected(slot);
            MobEquipmentPacket packet = new MobEquipmentPacket();
            packet.setContainerId(0);
            packet.setHotbarSlot(slot);
            packet.setInventorySlot(slot);
            packet.setRuntimeEntityId(player.getRuntimeID());
            packet.setItem(player.getInventory().getContainer().getItemStack(slot).getItemData());
            player.sendPacket(packet);
        }

        player.buildBlock(clickPos, EnumFacing.UP.index, false);

        if(needSwitch){
            player.getInventory().setSelected(selected);
            MobEquipmentPacket packet = new MobEquipmentPacket();
            packet.setContainerId(0);
            packet.setHotbarSlot(selected);
            packet.setInventorySlot(selected);
            packet.setRuntimeEntityId(player.getRuntimeID());
            packet.setItem(player.getInventory().getContainer().getItemStack(selected).getItemData());
            player.sendPacket(packet);
        }

        return true;
    }

    private Vec3i calcBelowBlock(EntityLocalPlayer player){
        Vec3f down = player.getPos().sub(0,2,0);
        return new Vec3i((int)Math.floor(down.x), (int)Math.floor(down.y), (int)Math.floor(down.z));
    }

    @SubscribeEvent
    public void onTick(TickEvent event){

        if(GameDataComponent.tick < 20*10){
            return;
        }

        EntityLocalPlayer player = event.getPlayer();
        double distanceValue = mode.getCurrentValue().equals("瞬移")?0:distance.getCurrentValue().doubleValue();

        Vec3f motion = player.getMotion();
        if(player.getFallDistance()>=distanceValue && !player.isOnGround() && PlayerUtil.getCollidingBoundingBoxes(player,player.getAABB().expand(0,50,0).offset(0,-50,0).offset(motion.x,motion.y,motion.z)).isEmpty()){
            //In void
            switch (mode.getCurrentValue()){
                case "空气放置":
                    doAirPlace(player);
                    break;
                case "传送":
                    break;
                case "瞬移":
                    if(!startBlink){
                        if(BlinkComponent.blink){
                            wasBlink = true;
                        }else{
                            BlinkComponent.blink = true;
                            BlinkComponent.setAllowPackets(MovePlayerPacket.class, NeteasePlayerAuthInputPacket.class);
                            blinkTicks.reset();
                        }
                        startBlink = true;
                    }else{
                        if(!wasBlink && blinkTicks.finished(blinkSecond.getCurrentValue().longValue()*1000)){
                            player.setPos(safePos);
                            BlinkComponent.blink = false;
                            BlinkComponent.clear();
                            if(!doAirPlace(player)) player.setMotion(safeMotion);
                            startBlink = false;
                        }
                    }
                    break;
                default:
                    player.setPos(safePos);
                    break;
            }
        }else if(player.isOnGround()){
            safePos = player.getPos();
            safeMotion = player.getMotion();
            if(mode.getCurrentValue().equals("瞬移") && startBlink && !wasBlink){
                BlinkComponent.blink = false;
                BlinkComponent.dispatch();
                startBlink = false;
            }
        }
    }

    @Override
    public String getTag() {
        switch (this.mode.getCurrentValue()){
            case "空气放置":
                return "AirPlace";
            case "传送":
                return "Teleport";
            default:
                return "Blink";
        }
    }


}
