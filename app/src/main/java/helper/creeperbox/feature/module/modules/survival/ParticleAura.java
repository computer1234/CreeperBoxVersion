package helper.creeperbox.feature.module.modules.survival;

import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.BooleanValue;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.sdk.entity.type.EntityActor;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.network.packet.modify.NeteaseAnimatePacket;
import helper.creeperbox.utils.mc.StopWatch;

@ModuleInfo(name = "粒子光环", category = Category.Survival)
public class ParticleAura extends Module {
    private BooleanValue self = new BooleanValue("自我粒子",this,true);

    private BooleanValue canSee = new BooleanValue("自己可见",this,true);

    private NumberValue count = new NumberValue("每秒粒子数",this,100,0,100,1);
    private NumberValue time = new NumberValue("每秒粒子倍数",this,1,1,100,1);
    private StopWatch watch = new StopWatch();

    @Override
    public void onEnable() {
        watch.reset();
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {
        boolean self = this.self.getCurrentValue();
        boolean canSee = this.canSee.getCurrentValue();
        int cps = this.count.getCurrentValue().intValue()*this.time.getCurrentValue().intValue();
        EntityActor[] actorList = event.getPlayer().getLevel().getPlayers();
        double preTick = (double)1000.0F / (double)cps;
        int count = cps > 20 ? (int)((double)this.watch.getElapsedTime() / preTick) : 1;

        for(EntityActor actor : actorList){
            if(self && actor instanceof EntityLocalPlayer) continue;

            long id = actor.getRuntimeID();
            NeteaseAnimatePacket packet = new NeteaseAnimatePacket();
            packet.setAction(NeteaseAnimatePacket.Action.CRITICAL_HIT);
            packet.setHasExtra(true);
            packet.setExtraCriticalEntityId(id);
            packet.setRowingTime(-1);
            packet.setRuntimeEntityId(id);

            for(int i = 0 ; i < count ; i++){
                event.getPlayer().sendPacket(packet);
                if(canSee) event.getPlayer().receivePacketNoEvent(packet);
            }

        }
    }


}
