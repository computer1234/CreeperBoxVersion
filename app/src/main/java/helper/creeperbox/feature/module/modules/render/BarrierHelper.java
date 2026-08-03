package helper.creeperbox.feature.module.modules.render;

import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.data.PlayerActionType;
import org.cloudburstmc.protocol.bedrock.packet.PlayerActionPacket;

import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.sdk.entity.type.EntityActor;
import helper.creeperbox.sdk.math.AxisAlignedBB;
import helper.creeperbox.utils.mc.StopWatch;
import helper.creeperbox.utils.mc.TargetUtil;

@ModuleInfo(name = "虚影屏障", category = Category.Render)
public class BarrierHelper extends Module {


    private NumberValue speed = new NumberValue("速率",this,500,0,1000,20);

    private StopWatch watch = new StopWatch();

    @Override
    public void onEnable() {
        watch.reset();
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {

        int speed = this.speed.getCurrentValue().intValue();
        if(!watch.finished(speed)) return;
        watch.reset();


        PlayerActionPacket packet = new PlayerActionPacket();
        packet.setAction(PlayerActionType.BUILD_DENIED);
        packet.setFace(-1);
        packet.setRuntimeEntityId(event.getPlayer().getRuntimeID());

        for(EntityActor actor : TargetUtil.findTarget(event.getPlayer(),Integer.MAX_VALUE)){
            AxisAlignedBB aabb = actor.getAABB().offset(0,1f,0f).expand(2f,0.5f,2f);
            int minX = (int)Math.floor(aabb.minX);
            int maxX = (int)Math.floor(aabb.maxX + (double)1.0F);
            int minY = (int)Math.floor(aabb.minY);
            int maxY = (int)Math.floor(aabb.maxY + (double)1.0F);
            int minZ = (int)Math.floor(aabb.minZ);
            int maxZ = (int)Math.floor(aabb.maxZ + (double)1.0F);

            for(int x = minX; x < maxX; ++x) {
                for(int y = minY; y < maxY; ++y) {
                    for(int z = minZ; z < maxZ; ++z) {
                        Vector3i pos = Vector3i.from(x,y,z);
                        packet.setBlockPosition(pos);
                        packet.setResultPosition(pos);
                        event.getPlayer().sendPacket(packet);
                    }
                }
            }
        }

    }

}
