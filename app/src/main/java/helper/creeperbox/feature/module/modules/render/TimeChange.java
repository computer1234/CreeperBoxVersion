package helper.creeperbox.feature.module.modules.render;

import org.cloudburstmc.protocol.bedrock.packet.SetTimePacket;

import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.PacketReceiveEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.ListValue;
import helper.creeperbox.utils.mc.StopWatch;


@ModuleInfo(name = "设置时间", category = Category.Render)
public class TimeChange extends Module {

    private final ListValue mode = new ListValue("模式",this,"夜晚")
            .addSubList("日出")
            .addSubList("白日")
            .addSubList("夜晚")
            .addSubList("日落");


    private StopWatch updateWatch = new StopWatch();


    private int getTime(){
        switch (mode.getCurrentValue()){
            case "日出":
                return 95000;
            case "白日":
                return 145000;
            case "夜晚":
                return 18000;
            default:
                return 156000;
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent event){
        if(updateWatch.finished(1000)){
            SetTimePacket packet = new SetTimePacket();
            packet.setTime(getTime());
            event.getPlayer().receivePacketNoEvent(packet);
            updateWatch.reset();
        }
    }

    @SubscribeEvent
    public void onPacket(PacketReceiveEvent event){
        if(event.getPacket() instanceof SetTimePacket){
            ((SetTimePacket) event.getPacket()).setTime(getTime());
        }
    }


    @Override
    public String getTag() {
        switch (mode.getCurrentValue()){
            case "日出":
                return "Sunrise";
            case "白日":
                return "Day";
            case "夜晚":
                return "Night";
            default:
                return "Sunset";
        }
    }
}
