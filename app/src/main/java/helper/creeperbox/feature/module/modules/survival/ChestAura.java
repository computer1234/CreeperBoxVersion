package helper.creeperbox.feature.module.modules.survival;

import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.packet.InventoryTransactionPacket;
import org.cloudburstmc.protocol.bedrock.packet.SetSpawnPositionPacket;

import java.util.ArrayList;
import java.util.List;

import helper.creeperbox.feature.component.InventoryComponent;
import helper.creeperbox.feature.component.RotationComponent;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.KeyEvent;
import helper.creeperbox.feature.event.events.PacketReceiveEvent;
import helper.creeperbox.feature.event.events.PacketSendEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.sdk.block.EnumFacing;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.level.Level;
import helper.creeperbox.sdk.math.Vec2f;
import helper.creeperbox.sdk.math.Vec3f;
import helper.creeperbox.sdk.math.Vec3i;
import helper.creeperbox.sdk.network.packet.modify.NeteaseContainerOpenPacket;
import helper.creeperbox.utils.mc.RotationUtil;

@ModuleInfo(name = "箱子光环", category = Category.Survival,key = KeyEvent.KEYCODE_C)
public class ChestAura extends Module {
    public List<Vector3i> found = new ArrayList<>();
    private final NumberValue range = new NumberValue("距离", this,4,1,10,0.1);

    private Vector3i current;


    @Override
    public void onEnable() {
        found.clear();
        current = null;
    }


    private int calcFace(EntityLocalPlayer player, Vec3i blockPos){
        double minDistance = 100;
        int f = 0;
        Vec3f playerPos = player.getPos();
        for(EnumFacing facing:EnumFacing.values()){
            Vec3i pos = new Vec3i(blockPos.x+facing.getValue().x, blockPos.y+facing.getValue().y, blockPos.z+facing.getValue().z);
            if(distance(pos,playerPos)<minDistance){
                minDistance = distance(pos,playerPos);
                f = facing.index;
            }
        }
        return f;
    }


    private double distance(Vec3i pos1, Vec3f pos2) {
        float dx = pos1.x - pos2.x;
        float dy = pos1.y - pos2.y;
        float dz = pos1.z - pos2.z;
        return Math.sqrt((double)(dx * dx + dy * dy + dz * dz));
    }


    @SubscribeEvent
    public void onPacketReceive(PacketReceiveEvent event) {


        if(event.getPacket() instanceof SetSpawnPositionPacket){
            found.clear();
        }


        if(event.getPacket() instanceof NeteaseContainerOpenPacket){
            NeteaseContainerOpenPacket packet = (NeteaseContainerOpenPacket) event.getPacket();

            if(current!= null && packet.getId()!=0){
                found.add(current);
                current = null;
            }
        }
    }
    @SubscribeEvent
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacket() instanceof InventoryTransactionPacket) {
            InventoryTransactionPacket packet = (InventoryTransactionPacket) event.getPacket();
            packet.setClickPosition(Vector3f.ZERO);
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent event){
        EntityLocalPlayer player = event.getPlayer();
        if(InventoryComponent.getOpenContainer() != null) return;

        if(current==null){
            doFind(player);
            if(current!=null) doClick(player);
        }else{
            doClick(player);
        }
    }

    private void doFind(EntityLocalPlayer player){

        Level world = player.getLevel();
        Vec3f pos = player.getPos();
        Vector3i Vec3iPos = Vector3i.from((int)Math.floor(pos.x),(int)Math.floor(pos.y),(int)Math.floor(pos.z));
        int reach = range.getCurrentValue().intValue();
        for (int x = -reach;x <= reach; x++) {
            for (int y = -reach;y <= reach; y++) {
                for (int z = -reach;z <= reach; z++) {
                    Vector3i blockPos  = Vec3iPos.add(x,y,z);

                    if(found.contains(blockPos)){
                        continue;
                    }

                    if(world.getBlock(new Vec3i(blockPos.getX(),blockPos.getY(),blockPos.getZ())).getNameSpace().equals("minecraft:chest") && blockPos.distance(pos.x,pos.y,pos.z)<=range.getCurrentValue().floatValue()){
                        current = blockPos;
                        return;
                    }
                }
            }
        }
    }



    private void doClick(EntityLocalPlayer player){

        if(current== null || found.contains(current) || !player.getLevel().getBlock(new Vec3i(current.getX(),current.getY(),current.getZ())).getNameSpace().equals("minecraft:chest")){
            current = null;
            return;
        }

        Vec3f pos = player.getPos();
        if(current.distance(pos.x,pos.y,pos.z)>range.getCurrentValue().doubleValue()){
            current = null;
            return;
        }

        Vec2f need = RotationUtil.calcRotation(player,new Vec3f(current.getX(),current.getY(),current.getZ()));
        RotationComponent.setRotation(player,need,360f);

        player.buildBlock(new Vec3i(current.getX(),current.getY(),current.getZ()),calcFace(player,new Vec3i(current.getX(),current.getY(),current.getZ())),true);
    }

}
