package helper.creeperbox.feature.module.modules.build;

import static helper.creeperbox.utils.mc.RotationUtil.getFacing;

import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.packet.InventoryTransactionPacket;
import org.cloudburstmc.protocol.bedrock.packet.MobEquipmentPacket;
import org.cloudburstmc.protocol.bedrock.packet.MovePlayerPacket;
import org.cloudburstmc.protocol.bedrock.packet.PlayerHotbarPacket;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.component.RotationComponent;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.KeyEvent;
import helper.creeperbox.feature.event.events.PacketReceiveEvent;
import helper.creeperbox.feature.event.events.PacketSendEvent;
import helper.creeperbox.feature.event.events.PostMoveEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.BooleanValue;
import helper.creeperbox.feature.settings.ListValue;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.sdk.block.EnumFacing;
import helper.creeperbox.sdk.block.Material;
import helper.creeperbox.sdk.component.MoveInputComponent;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.inventory.PlayerInventory;
import helper.creeperbox.sdk.level.Level;
import helper.creeperbox.sdk.math.AxisAlignedBB;
import helper.creeperbox.sdk.math.Vec2f;
import helper.creeperbox.sdk.math.Vec3f;
import helper.creeperbox.sdk.math.Vec3i;
import helper.creeperbox.utils.math.MathUtil;

@ModuleInfo(name = "自动搭路", category = Category.Build,key = KeyEvent.KEYCODE_F)
public class Scaffold extends Module {

    private ListValue mode = new ListValue("模式",this,"梯子")
            .addSubList("锁Y")
            .addSubList("梯子")
            .addSubList("宝马加速");
    private BooleanValue spoof = new BooleanValue("剑铺",this,true);

    private NumberValue extend = new NumberValue("延伸",this,1,0,4,1);


    private static Scaffold INSTANCE;
    public Scaffold(){
        INSTANCE = this;
    }

    public static Scaffold getINSTANCE() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
        if (player!=null) {
            startY = calcBelowBlock(player).y;
        }
    }
    private int slot;
    private int startY = 0;
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

    @SubscribeEvent
    public void onPacketSend(PacketSendEvent event) {
        if(event.getPacket() instanceof InventoryTransactionPacket){
            InventoryTransactionPacket packet = (InventoryTransactionPacket) event.getPacket();
            packet.setClickPosition(Vector3f.ZERO);
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent event){
        EntityLocalPlayer player = event.getPlayer();
        doRotation(player);
        for(int i = 0 ; i < 5 ; i++){
            if(searchBlock(player) && switchBlock(player)){
                place(player);
            }
        }
    }

    private void doRotation(EntityLocalPlayer player) {
        if(extend.getCurrentValue().intValue() <= 1){
            RotationComponent.setRotation(player,new Vec2f(80f, MathUtil.wrapAngleTo180_float(player.getRotation().y+180)),40f);
        }
    }

    private boolean searchBlock(EntityLocalPlayer player){
        Vec3i blockPos = calcBelowBlock(player);

        if(mode.getCurrentValue().equals("锁Y")){
            blockPos = new Vec3i(blockPos.x,startY,blockPos.z);
        }

        Vec3i facing = getFacing(player);
        int extend = this.extend.getCurrentValue().intValue();
        for (int i = 0; i <= extend; i++) {

            boolean result = doSearch(player, blockPos);

            if (result) {
                if (!player.getAABB().intersectsWith(new AxisAlignedBB(this.blockPos.x, this.blockPos.y, this.blockPos.z, this.blockPos.x + 1, this.blockPos.y + 1, this.blockPos.z + 1).expand(-0.01, -0.01, -0.01))) {
                    return true;
                }
            }

            if (facing.x != 0 && facing.z != 0 && i + 1 != extend) {
                i++;
            }

            blockPos = new Vec3i(blockPos.x + facing.x, blockPos.y + facing.y, blockPos.z + facing.z);
        }
        return false;
    }



    private boolean switchBlock(EntityLocalPlayer player) {
        slot = searchBlockInHotBar(player);
        if (spoof.getCurrentValue()) {
            return slot != -1;
        } else {
            if (slot == -1) return false;
            player.getInventory().setSelected(slot);
            return true;
        }
    }

    private void place(EntityLocalPlayer player){

        int selected = player.getInventory().getSelected();
        boolean needSwitch = selected!=slot;

        if(spoof.getCurrentValue() && needSwitch){
            player.getInventory().setSelected(slot);
            MobEquipmentPacket packet = new MobEquipmentPacket();
            packet.setContainerId(0);
            packet.setHotbarSlot(slot);
            packet.setInventorySlot(slot);
            packet.setRuntimeEntityId(player.getRuntimeID());
            packet.setItem(player.getInventory().getContainer().getItemStack(slot).getItemData());
            player.sendPacket(packet);
        }

        player.buildBlock(blockPos,direction.index,false);

        if(spoof.getCurrentValue() && needSwitch){
            player.getInventory().setSelected(selected);
            MobEquipmentPacket packet = new MobEquipmentPacket();
            packet.setContainerId(0);
            packet.setHotbarSlot(selected);
            packet.setInventorySlot(selected);
            packet.setRuntimeEntityId(player.getRuntimeID());
            packet.setItem(player.getInventory().getContainer().getItemStack(selected).getItemData());
            player.sendPacket(packet);
        }
    }

    private Vec3i blockPos;
    private EnumFacing direction;

    private boolean doSearch(EntityLocalPlayer player,Vec3i pos){
        Level world = player.getLevel();
        if (world.getMaterial(pos).isAir()) {
            for (int x = 0; x < 4; x++) {
                for (int z = 0; z < 4; z++) {
                    for (int i = 1; i > -3; i -= 2) {
                        Vec3i blockPos = new Vec3i(pos.x+x*i,pos.y,pos.z+z*i);
                        Material blockMaterial = world.getMaterial(blockPos);
                        if(blockMaterial.isAir()){
                            for (EnumFacing direction : EnumFacing.values()) {
                                Vec3i dir = direction.getValue();
                                Vec3i clickPos = new Vec3i(blockPos.x + dir.x, blockPos.y + dir.y, blockPos.z + dir.z);
                                Material oppoMaterial = world.getMaterial(clickPos);
                                if(oppoMaterial.isSolid() && !oppoMaterial.isAir()){
                                    this.blockPos = clickPos;
                                    this.direction = direction;
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }


    private Vec3i calcBelowBlock(EntityLocalPlayer player){
        Vec3f down = player.getPos().sub(0,2,0);
        return new Vec3i((int)Math.floor(down.x), (int)Math.floor(down.y), (int)Math.floor(down.z));
    }

    private int tick = 0;

    @SubscribeEvent
    public void onFlag(PacketReceiveEvent event){
        if(event.getPacket() instanceof MovePlayerPacket){
            EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
            if(player == null) return;
            MovePlayerPacket packet = ((MovePlayerPacket) event.getPacket());

            if(packet.getRuntimeEntityId() == player.getRuntimeID()){
                tick = 0;
            }
        }

        if(event.getPacket() instanceof MobEquipmentPacket && spoof.getCurrentValue()){
            EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
            MobEquipmentPacket packet = (MobEquipmentPacket) event.getPacket();
            if(player!=null && player.getRuntimeID() == packet.getRuntimeEntityId() && packet.getContainerId() == 0){
                PlayerInventory inv = player.getInventory();
                int selected = inv.getSelected();
                if(selected!=packet.getHotbarSlot()){
                    MobEquipmentPacket switchPacket = new MobEquipmentPacket();
                    switchPacket.setRuntimeEntityId(packet.getRuntimeEntityId());
                    switchPacket.setContainerId(0);
                    switchPacket.setHotbarSlot(selected);
                    switchPacket.setInventorySlot(selected);
                    switchPacket.setItem(inv.getContainer().getItemStack(selected).getItemData());
                    event.setCancelled();
                    player.sendPacket(switchPacket);
                }
            }
        }

    }

    @SubscribeEvent
    public void onMove(PostMoveEvent event){

        if(mode.getCurrentValue().equals("宝马加速")){
            EntityLocalPlayer player = event.getPlayer();
            MoveInputComponent moveInput = player.getMoveInput();
            Vec3f motion = player.getMotion();
            if(!player.isOnGround()) return;
            if(moveInput.isJumpDown){
                player.setMotion(new Vec3f(motion.x,0.42f,motion.z));
                return;
            }
            float yaw = player.getRotation().y;
            float calcYaw = (yaw + 90) * (float) (3.1415926 / 180);
            float s = (float) Math.sin(calcYaw);
            float c = (float) Math.cos(calcYaw);
            float forward = moveInput.moveForward;
            float side = moveInput.moveSide;

            float speed = Math.min(0.5f + tick / 100f, 0.8f);

            float moveX = (forward * c + side * s) * speed;
            float moveZ = (forward * s - side * c) * speed;
            float motionY = motion.y;

            if (player.isOnGround()) {
                motionY = 0.005f;
            }

            player.setMotion(new Vec3f(moveX, motionY, moveZ));

            if(moveInput.moveForward!=0 || moveInput.moveSide !=0){
                tick = 0;
            }

        }

    }


    @Override
    public String getTag() {
        switch(mode.getCurrentValue()) {
            case "梯子":
                return "Normal";
            case "锁Y":
                return "Same Y";
            default:
                return "BMW";
        }
    }


}
