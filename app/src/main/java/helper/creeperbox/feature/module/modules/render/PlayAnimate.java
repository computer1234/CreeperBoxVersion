package helper.creeperbox.feature.module.modules.render;

import org.cloudburstmc.protocol.bedrock.packet.AnimateEntityPacket;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.ListValue;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.utils.mc.StopWatch;

@ModuleInfo(name = "玩家动画", category = Category.Render)
public class PlayAnimate extends Module {

    private final ListValue mode = new ListValue("模式",this,"坐下")
            .addSubList("坐下")
            .addSubList("游泳")
            .addSubList("躺下")
            .addSubList("青蛙跳");

    private boolean isFrog = false;

    private StopWatch frogWatch = new StopWatch();

    @Override
    public void onEnable() {
        EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
        isFrog = mode.getCurrentValue().equals("青蛙跳");

        String name = "";
        switch (mode.getCurrentValue()){
            case "坐下":
                name = "animation.player.riding.legs";
                break;
            case "游泳":
                name = "animation.player.move.ss_s2star_skin_g_swim";
                break;
            case "躺下":
                name = "animation.player.sleeping";
                break;
            default:
                name = "animation.xy_frogjump";
                isFrog = true;
                frogWatch.reset();
                break;
        }
        if(player != null) {
            AnimateEntityPacket packet = new AnimateEntityPacket();
            packet.setAnimation(name);
            packet.setNextState("move");
            packet.setStopExpression("query.any_animation_finished");
            packet.setStopExpressionVersion(16777216);
            packet.setController("__runtime_controller");
            packet.setBlendOutTime(9999);
            packet.getRuntimeEntityIds().add(player.getRuntimeID());
            player.receivePacket(packet);
        }
    }


    @Override
    public void onDisable() {
        EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
        if(player != null) {
            AnimateEntityPacket packet = new AnimateEntityPacket();
            packet.setAnimation("animation.humanoid.riding.legs");
            packet.setNextState("move");
            packet.setStopExpression("query.any_animation_finished");
            packet.setStopExpressionVersion(16777216);
            packet.setController("__runtime_controller");
            packet.setBlendOutTime(0);
            packet.getRuntimeEntityIds().add(player.getRuntimeID());
            player.receivePacket(packet);
        }
    }


    @SubscribeEvent
    public void onTick(TickEvent event) {
        if(isFrog && frogWatch.finished(7000)){
            frogWatch.reset();
            AnimateEntityPacket packet = new AnimateEntityPacket();
            packet.setAnimation("animation.xy_frogjump");
            packet.setNextState("move");
            packet.setStopExpression("query.any_animation_finished");
            packet.setStopExpressionVersion(16777216);
            packet.setController("__runtime_controller");
            packet.setBlendOutTime(9999);
            packet.getRuntimeEntityIds().add(event.getPlayer().getRuntimeID());
            event.getPlayer().receivePacket(packet);
        }
    }


}
