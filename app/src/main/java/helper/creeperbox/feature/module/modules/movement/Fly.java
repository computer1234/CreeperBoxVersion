package helper.creeperbox.feature.module.modules.movement;

import org.cloudburstmc.math.vector.Vector3f;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.component.HytComponent;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.PacketSendEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.ListValue;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.sdk.component.MoveInputComponent;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.math.Vec3f;
import helper.creeperbox.sdk.network.packet.modify.NeteasePlayerAuthInputPacket;

@ModuleInfo(name = "平飞", category = Category.Movement)
public class Fly extends Module {

    public static Fly INSTANCE;
    public Fly() {
        INSTANCE = this;
    }
    private final ListValue mode = new ListValue("飞行模式",this,"花雨庭")
            .addSubList("花雨庭")
            .addSubList("普通");
    private final NumberValue verticalSpeed = new NumberValue("水平", this,1,0, 20, 0.1);
    private final NumberValue horizontalSpeed = new NumberValue("垂直", this,1,0, 20, 0.1);

    private boolean first;

    @Override
    public void onEnable() {
        first = true;
    }

    @SubscribeEvent
    public void onTick(TickEvent event){
        EntityLocalPlayer player = event.getPlayer();
        MoveInputComponent moveInput = player.getMoveInput();
        float yaw = player.getRotation().y;
        float calcYaw = (yaw + 90) * (float)(3.1415926 / 180);
        float s = (float) Math.sin(calcYaw);
        float c = (float) Math.cos(calcYaw);
        float forward = moveInput.moveForward;
        float side = moveInput.moveSide;
        float speed = horizontalSpeed.getCurrentValue().floatValue();
        float moveX = (forward * c + side * s) * speed;
        float moveZ = (forward * s - side * c) * speed;
        float motionY = 0;

        float vSpeed = verticalSpeed.getCurrentValue().floatValue();


        if(moveInput.isJumpDown || moveInput.isFlyingAscendDown){
            motionY+=vSpeed;
        }
        if(moveInput.isSneakDown|| moveInput.isFlyingDescendDown){
            motionY-=vSpeed;
        }

        player.setMotion(new Vec3f(moveX,motionY,moveZ));
    }

    @SubscribeEvent
    public void onPacket(PacketSendEvent event){
        if(event.getPacket() instanceof NeteasePlayerAuthInputPacket && mode.getCurrentValue().equals("花雨庭")){

            NeteasePlayerAuthInputPacket packet = (NeteasePlayerAuthInputPacket) event.getPacket();

            EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
            if(player == null) return;

            //断定能否起飞
            if(first){
                HytComponent.teleportBelow(player);
                HytComponent.moveToY(player, HytComponent.maxY);
                first = false;
            }

            //不是传送包
            if(!packet.getDelta().equals(Vector3f.ZERO)){
                HytComponent.teleportPath(player,packet.getPosition());
            }

            Vector3f pos = packet.getPosition();
            packet.setPosition(Vector3f.from(pos.getX(),HytComponent.getCurrentY()-HytComponent.fallHeight,pos.getZ()));

        }

    }


    @Override
    public void onDisable() {
        if(mode.getCurrentValue().equals("花雨庭")) {
            EntityLocalPlayer p = CreeperBox.INSTANCE.getLocalPlayer();
            if(p !=null){
                p.setMotion(new Vec3f(0,0,0));
            }
        }
    }



    @Override
    public String getTag() {
        switch(mode.getCurrentValue()) {
            case "花雨庭":
                return "HYT";
            default:
                return "Normal";
        }
    }

}
