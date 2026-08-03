package helper.creeperbox.feature.component;


import static helper.creeperbox.utils.mc.RotationUtil.smoothRotation;

import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.packet.MovePlayerPacket;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.event.EventPriority;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.PacketSendEvent;
import helper.creeperbox.feature.event.events.PostRender3DEvent;
import helper.creeperbox.feature.event.events.Render3DEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.math.Vec2f;
import helper.creeperbox.sdk.network.packet.modify.NeteasePlayerAuthInputPacket;
import helper.creeperbox.utils.math.RandomUtil;

public class RotationComponent {

    private static Vec2f rotations,tempRotations,tempLastRotations,targetRotations,prevRotation,lastRotations,serverRotations;
    private static boolean active;
    private static float rotationSpeed;
    private static boolean back;
    private static boolean fixMove;
    public static void setRotation(EntityLocalPlayer player, Vec2f targetRotations, float rotationSpeed){
        setRotation(player,targetRotations,rotationSpeed,false);
    }

    public static Vec2f getServerRotations() {
        return serverRotations;
    }

    public static void setRotation(EntityLocalPlayer player,Vec2f targetRotations, float rotationSpeed, boolean fixMove){
        RotationComponent.targetRotations = targetRotations;
        RotationComponent.rotationSpeed = rotationSpeed;
        RotationComponent.fixMove = fixMove;
        RotationComponent.prevRotation = player.getRotation();
        active = true;
        back = false;
    }

    @SubscribeEvent(value = EventPriority.VERY_LOW)
    public void onTick(TickEvent event){

        EntityLocalPlayer player = event.getPlayer();
        Vec2f currentRotation = player.getRotation();

        if(!active || rotations == null || targetRotations == null || prevRotation == null){
            rotations = targetRotations = lastRotations = prevRotation = currentRotation;
        }

        if(active){
            prevRotation = rotations;
            rotations = new Vec2f(smoothRotation(prevRotation.x,targetRotations.x + (float) RandomUtil.getRandom(-0.5,0.5),rotationSpeed),smoothRotation(prevRotation.y,targetRotations.y + (float)RandomUtil.getRandom(-0.5,0.5),rotationSpeed));

            if (Math.abs((rotations.x - targetRotations.x) % 360) < 1 && Math.abs((rotations.y - targetRotations.y) % 360) < 1) {
                if(!back){
                    setRotation(player,lastRotations,rotationSpeed,fixMove);
                    back = true;
                }else {
                    active = false;
                }
            }
        }

    }
    @SubscribeEvent
    public void onPlayerRot(Render3DEvent event) {
        if(active){
            EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
            tempRotations = player.getRotation();
            tempLastRotations = player.getRotationPrev();
            player.setMobBodyRotationYaw(rotations.y);
            player.setActorHeadRotationYaw(rotations.y);
            player.setRotation(new Vec2f(rotations.x,rotations.y));
            player.setRotationPrev(new Vec2f(rotations.x,rotations.y));
        }
    }

    @SubscribeEvent(EventPriority.VERY_LOW)
    public void onPlayerRot(PostRender3DEvent event) {
        if(active && !fixMove){
            EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
            player.setRotation(new Vec2f(tempRotations.x, tempRotations.y));
            player.setRotationPrev(new Vec2f(tempLastRotations.x, tempLastRotations.y));
        }
    }

    @SubscribeEvent(value = EventPriority.VERY_LOW)
    public void onPacketSend(PacketSendEvent event){
        if(event.getPacket() instanceof MovePlayerPacket){
            MovePlayerPacket packet = (MovePlayerPacket) event.getPacket();
            if(active){
                packet.setRotation(Vector3f.from(rotations.x,rotations.y,rotations.y));
            }
        }else if (event.getPacket() instanceof NeteasePlayerAuthInputPacket){
            NeteasePlayerAuthInputPacket packet = (NeteasePlayerAuthInputPacket) event.getPacket();
            if(active){
                packet.setRotation(Vector3f.from(rotations.x,rotations.y,rotations.y));
            }
        }
    }


    @SubscribeEvent(value = EventPriority.SYSTEM)
    public void onPacket(PacketSendEvent event) {
        if(event.getPacket() instanceof MovePlayerPacket){
            MovePlayerPacket packet = (MovePlayerPacket) event.getPacket();
            serverRotations = new Vec2f(packet.getRotation().getX(),packet.getRotation().getY());
        }
    }



}
