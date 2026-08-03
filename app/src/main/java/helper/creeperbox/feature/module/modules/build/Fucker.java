package helper.creeperbox.feature.module.modules.build;

import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.data.PlayerActionType;
import org.cloudburstmc.protocol.bedrock.data.PlayerAuthInputData;
import org.cloudburstmc.protocol.bedrock.data.PlayerBlockActionData;
import org.cloudburstmc.protocol.bedrock.data.inventory.transaction.InventoryTransactionType;
import org.cloudburstmc.protocol.bedrock.packet.InventoryTransactionPacket;
import org.cloudburstmc.protocol.bedrock.packet.MobEquipmentPacket;
import org.cloudburstmc.protocol.bedrock.packet.PlayerActionPacket;

import java.util.ArrayList;
import java.util.List;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.event.EventPriority;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.PacketSendEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.BooleanValue;
import helper.creeperbox.feature.settings.ListValue;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.sdk.block.Block;
import helper.creeperbox.sdk.block.EnumFacing;
import helper.creeperbox.sdk.block.Material;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.inventory.PlayerInventory;
import helper.creeperbox.sdk.level.Level;
import helper.creeperbox.sdk.math.Vec3f;
import helper.creeperbox.sdk.math.Vec3i;
import helper.creeperbox.sdk.network.UnknownBlockDef;
import helper.creeperbox.sdk.network.packet.modify.NeteasePlayerAuthInputPacket;

@ModuleInfo(name = "自动破床", category = Category.Build)
public class Fucker extends Module {


    private static final List<Vec3i> bedFacing = new ArrayList<>();

    private static final List<Vec3i> blockFacing = new ArrayList<>();

    static {
        bedFacing.add(EnumFacing.NORTH.getValue());
        bedFacing.add(EnumFacing.SOUTH.getValue());
        bedFacing.add(EnumFacing.WEST.getValue());
        bedFacing.add(EnumFacing.EAST.getValue());



        blockFacing.add(EnumFacing.NORTH.getValue());
        blockFacing.add(EnumFacing.SOUTH.getValue());
        blockFacing.add(EnumFacing.WEST.getValue());
        blockFacing.add(EnumFacing.EAST.getValue());
        blockFacing.add(EnumFacing.UP.getValue());
    }


    private static final String BED = "minecraft:bed";

    private final NumberValue range = new NumberValue("挖床距离", this,5,1,10,0.1);
    private final ListValue mode = new ListValue("模式",this,"布吉岛")
            .addSubList("EC")
            .addSubList("布吉岛")
            .addSubList("花雨庭");

    private Vector3i breakBlock;
    private int tool;
    private int breakTime = -1;

    private double distance(EntityLocalPlayer player){
        Vec3f pos1 = player.getPos();
        float dx = pos1.x - breakBlock.getX();
        float dy = pos1.y - breakBlock.getY();
        float dz = pos1.z - breakBlock.getZ();
        return Math.sqrt(dx*dx+dy*dy+dz*dz);
    }

    private double distance(Vec3i pos1, Vec3f pos2){
        float dx = pos1.x - pos2.x;
        float dy = pos1.y - pos2.y;
        float dz = pos1.z - pos2.z;

        return Math.sqrt(dx*dx+dy*dy+dz*dz);
    }

    @Override
    public void onEnable() {
        blockPos = null;
    }

    private Vec3i blockPos;

    private BestResult findBestTool(EntityLocalPlayer player, Block block){
        int hand = player.getInventory().getSelected();
        PlayerInventory inventory = player.getInventory();
        BestResult result = new BestResult(hand,player.getDestroyRate(block));
        for(int i = 0 ; i <9 ; i ++){
            if(i != hand){
                inventory.setSelected(i);
                float value = player.getDestroyRate(block);
                if(result.getValue()<value){
                    result = new BestResult(i,value);
                }
            }
        }
        inventory.setSelected(hand);
        return result;
    }




    @SubscribeEvent(EventPriority.LOW)
    public void onPacket(PacketSendEvent event){
        if(mode.getCurrentValue().equals("布吉岛")){
            return;
        }
        if(event.getPacket() instanceof NeteasePlayerAuthInputPacket){
            NeteasePlayerAuthInputPacket packet = (NeteasePlayerAuthInputPacket) event.getPacket();

            EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
            if(player == null || blockPos == null) return;
            Vec3f pos = player.getPos();

            packet.getInputData().add(PlayerAuthInputData.PERFORM_BLOCK_ACTIONS);
            if(distance(blockPos,pos)>range.getCurrentValue().floatValue() || !player.getLevel().getBlock(blockPos).getNameSpace().equals(BED)){
                PlayerBlockActionData absorb = new PlayerBlockActionData();
                absorb.setAction(PlayerActionType.ABORT_BREAK);
                absorb.setFace(1);
                absorb.setBlockPosition(Vector3i.from(blockPos.x,blockPos.y,blockPos.z));
                packet.getPlayerActions().add(absorb);
                blockPos = null;
            }else{
                Vector3i p = Vector3i.from(blockPos.x,blockPos.y,blockPos.z);
                PlayerBlockActionData start = new PlayerBlockActionData();
                start.setAction(PlayerActionType.START_BREAK);
                start.setFace(1);
                start.setBlockPosition(p);
                packet.getPlayerActions().add(start);

                PlayerBlockActionData con = new PlayerBlockActionData();
                con.setAction(PlayerActionType.BLOCK_CONTINUE_DESTROY);
                con.setFace(1);
                con.setBlockPosition(p);
                packet.getPlayerActions().add(con);

                PlayerBlockActionData predict = new PlayerBlockActionData();
                predict.setAction(PlayerActionType.BLOCK_PREDICT_DESTROY);
                predict.setFace(1);
                predict.setBlockPosition(p);
                packet.getPlayerActions().add(predict);
            }
        }
    }

    List<Vec3i> getSurround(EntityLocalPlayer player,Vec3i pos){
        Level world = player.getLevel();

        List<Vec3i> surround = new ArrayList<>();
        Vec3i facing = null;

        for(Vec3i f : bedFacing){
            Vec3i blockPos = new Vec3i(pos.x+f.x,pos.y+f.y,pos.z+f.z);
            if(world.getBlock(blockPos).getNameSpace().equals(BED)){
                facing = f;
                break;
            }
        }

        if(facing == null){
            return surround;
        }

        for(Vec3i f : blockFacing){
            Vec3i blockPos = new Vec3i(pos.x+f.x,pos.y+f.y,pos.z+f.z);
            if(!world.getBlock(blockPos).getNameSpace().equals(BED)){
                surround.add(blockPos);
            }
        }

        for(Vec3i f : blockFacing){
            Vec3i blockPos = new Vec3i(pos.x+f.x+facing.x,pos.y+f.y+facing.y,pos.z+f.z+facing.z);
            if(!world.getBlock(blockPos).getNameSpace().equals(BED)){
                boolean flag = false;
                for(Vec3i e : surround){
                    if(e.x == blockPos.x && e.y == blockPos.y && e.z == blockPos.z){
                        flag = true;
                    }
                }
                if(!flag){
                    surround.add(blockPos);
                }
            }
        }

        return surround;
    }

    private void doFindAndStart(EntityLocalPlayer player){
        Level world = player.getLevel();
        Vec3f pos = player.getPos();
        Vector3i Vec3iPos = Vector3i.from((int)Math.floor(pos.x),(int)Math.floor(pos.y),(int)Math.floor(pos.z));
        int reach = range.getCurrentValue().intValue();
        for (int x = -reach;x <= reach; x++) {
            for (int y = -reach;y <= reach; y++) {
                for (int z = -reach;z <= reach; z++) {
                    Vector3i blockPos  = Vec3iPos.add(x,y,z);
                    Block block = world.getBlock(new Vec3i(blockPos.getX(),blockPos.getY(),blockPos.getZ()));
                    if(block.getNameSpace().equals(BED) && blockPos.distance(pos.x,pos.y,pos.z)<=range.getCurrentValue().floatValue()) {
                        breakBlock = blockPos;

                        Vector3i bedPos = breakBlock;
                        List<Vec3i> surround = getSurround(player,new Vec3i(breakBlock.getX(),breakBlock.getY(),breakBlock.getZ()));

                        BestResult result = new BestResult(0,0);
                        for(Vec3i p : surround){
                            Block b = world.getBlock(p);

                            Material m = world.getMaterial(p);
                            if(m.isAir()){
                                result = new BestResult(-1,player.getDestroyRate(block));
                                breakBlock = bedPos;
                                break;
                            }
                            BestResult bestResult = findBestTool(player,b);
                            if(bestResult.getValue()>result.getValue()){
                                result = bestResult;
                                breakBlock = Vector3i.from(p.x,p.y,p.z);
                            }

                            tool = result.slot;


                            PlayerActionPacket packet = new PlayerActionPacket();
                            packet.setBlockPosition(breakBlock);
                            packet.setResultPosition(Vector3i.ZERO);
                            packet.setAction(PlayerActionType.START_BREAK);
                            packet.setFace(1);
                            player.sendPacketNoEvent(packet);
                            breakTime = Math.round(1f/result.getValue());
                            breakTime--;
                            return;
                        }

                        PlayerActionPacket packet = new PlayerActionPacket();
                        packet.setBlockPosition(breakBlock);
                        packet.setResultPosition(Vector3i.ZERO);
                        packet.setAction(PlayerActionType.START_BREAK);
                        packet.setFace(1);
                        player.sendPacketNoEvent(packet);

                        breakTime = Math.round(1f/player.getDestroyRate(block));
                        breakTime--;
                        tool = -1;
                        return;
                    }
                }
            }
        }
    }


    private void doBreakAndStop(EntityLocalPlayer player){
        Level world = player.getLevel();
        Material material = world.getMaterial(new Vec3i(breakBlock.getX(),breakBlock.getY(),breakBlock.getZ()));

        if(material.isAir()){
            breakBlock = null;
            return;
        }


        if(breakTime<=0){
            double distance = distance(player);

            if(distance>10f){
                breakBlock = null;
                return;
            }

            if(distance>=6f){
                return;
            }


            PlayerInventory inv = player.getInventory();
            int select = inv.getSelected();

            if(tool!=-1){
                MobEquipmentPacket switchPacket = new MobEquipmentPacket();
                switchPacket.setContainerId(0);
                switchPacket.setHotbarSlot(tool);
                switchPacket.setInventorySlot(tool);
                switchPacket.setRuntimeEntityId(player.getRuntimeID());
                switchPacket.setItem(player.getInventory().getContainer().getItemStack(tool).getItemData());
                player.sendPacket(switchPacket);
            }


            PlayerActionPacket packet = new PlayerActionPacket();
            packet.setBlockPosition(breakBlock);
            packet.setResultPosition(Vector3i.ZERO);
            packet.setAction(PlayerActionType.CONTINUE_BREAK);
            packet.setFace(1);
            player.sendPacketNoEvent(packet);

            InventoryTransactionPacket stop = new InventoryTransactionPacket();
            stop.setTransactionType(InventoryTransactionType.ITEM_USE);
            stop.setActionType(2);
            stop.setItemInHand(inv.getContainer().getItemStack(tool!=-1?tool:select).getItemData());
            stop.setHotbarSlot(select);
            Vec3f pos = player.getPos();
            stop.setPlayerPosition(Vector3f.from(pos.x,pos.y,pos.z));
            stop.setClickPosition(Vector3f.ZERO);
            stop.setBlockDefinition(new UnknownBlockDef(0));
            stop.setBlockFace(1);
            stop.setRuntimeEntityId(player.getRuntimeID());
            stop.setBlockPosition(breakBlock);
            player.sendPacket(stop);

            if (tool!=-1) {
                MobEquipmentPacket switchPacket = new MobEquipmentPacket();
                switchPacket.setContainerId(0);
                switchPacket.setHotbarSlot(select);
                switchPacket.setInventorySlot(select);
                switchPacket.setRuntimeEntityId(player.getRuntimeID());
                switchPacket.setItem(player.getInventory().getContainer().getItemStack(select).getItemData());
                player.sendPacket(switchPacket);
            }

            breakBlock = null;
        }else {

            double distance = distance(player);

            if(distance>20f){
                breakBlock = null;
                return;
            }


            PlayerInventory inv = player.getInventory();
            int select = inv.getSelected();

            if(tool!=-1){
                MobEquipmentPacket switchPacket = new MobEquipmentPacket();
                switchPacket.setContainerId(0);
                switchPacket.setHotbarSlot(tool);
                switchPacket.setInventorySlot(tool);
                switchPacket.setRuntimeEntityId(player.getRuntimeID());
                switchPacket.setItem(player.getInventory().getContainer().getItemStack(tool).getItemData());
                player.sendPacket(switchPacket);
            }


            PlayerActionPacket packet = new PlayerActionPacket();
            packet.setBlockPosition(breakBlock);
            packet.setResultPosition(Vector3i.ZERO);
            packet.setAction(PlayerActionType.CONTINUE_BREAK);
            packet.setFace(1);
            player.sendPacketNoEvent(packet);

            if (tool!=-1) {
                MobEquipmentPacket switchPacket = new MobEquipmentPacket();
                switchPacket.setContainerId(0);
                switchPacket.setHotbarSlot(select);
                switchPacket.setInventorySlot(select);
                switchPacket.setRuntimeEntityId(player.getRuntimeID());
                switchPacket.setItem(player.getInventory().getContainer().getItemStack(select).getItemData());
                player.sendPacket(switchPacket);
            }

            breakTime--;
        }

        player.swing();


    }


    @SubscribeEvent
    public void onTick(TickEvent event){

        if(mode.getCurrentValue().equals("布吉岛")){
            EntityLocalPlayer player = event.getPlayer();
            if(breakBlock!=null){
                doBreakAndStop(player);
            }else{
                doFindAndStart(player);
            }
            return;
        }

        EntityLocalPlayer player = event.getPlayer();
        Level world = player.getLevel();
        Vec3f pos = player.getPos();
        if(blockPos != null) return;

        Vector3i Vec3iPos = Vector3i.from((int)Math.floor(pos.x),(int)Math.floor(pos.y),(int)Math.floor(pos.z));
        int reach = range.getCurrentValue().intValue();
        for (int x = -reach;x <= reach; x++) {
            for (int y = -reach;y <= reach; y++) {
                for (int z = -reach;z <= reach; z++) {
                    Vector3i blockPos  = Vec3iPos.add(x,y,z);
                    Block block = world.getBlock(new Vec3i(blockPos.getX(),blockPos.getY(),blockPos.getZ()));
                    if(block.getNameSpace().equals(BED) && blockPos.distance(pos.x,pos.y,pos.z)<=range.getCurrentValue().floatValue()) {
                        this.blockPos = new Vec3i(blockPos.getX(),blockPos.getY(),blockPos.getZ());
                    }
                }
            }
        }
    }


    class BestResult {
        private int slot;
        private float value;

        public BestResult(int slot, float value) {
            this.slot = slot;
            this.value = value;
        }

        public int getSlot() {
            return slot;
        }

        public void setSlot(int slot) {
            this.slot = slot;
        }

        public float getValue() {
            return value;
        }

        public void setValue(float value) {
            this.value = value;
        }
    };


    @Override
    public String getTag() {
        switch (mode.getCurrentValue()){
            case "EC":
                return "EC";
            case "布吉岛":
                return "BJD";
            default:
                return "Hyt";
        }
    }
}
