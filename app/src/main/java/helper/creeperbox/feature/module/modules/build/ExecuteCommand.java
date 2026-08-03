package helper.creeperbox.feature.module.modules.build;

import org.cloudburstmc.protocol.bedrock.data.command.CommandOriginData;
import org.cloudburstmc.protocol.bedrock.data.command.CommandOriginType;
import org.cloudburstmc.protocol.bedrock.packet.ModalFormRequestPacket;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.ListValue;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.network.packet.modify.NeteaseCommandRequestPacket;
import helper.creeperbox.utils.mc.StopWatch;

@ModuleInfo(name = "执行指令", category = Category.Build)
public class ExecuteCommand extends Module {


    private final ListValue mode = new ListValue("模式",this,"循环")
            .addSubList("循环")
            .addSubList("单次");

    private final NumberValue count = new NumberValue("次数", this,10,1, 100, 1);


    public static final String PATH = "command.txt";


    private List<String> cmd = new ArrayList<>();
    private StopWatch commandTick = new StopWatch();



    @Override
    public void onEnable() {
        EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
        if(player!=null){
            commandTick.reset();
            String cmd = readCommand();
            if(cmd.isEmpty()){
                File file = new File(CreeperBox.INSTANCE.activity.getExternalFilesDir(null),PATH);
                ModalFormRequestPacket packet = new ModalFormRequestPacket();
                packet.setFormData("{\"type\":\"form\",\"title\":\"文件读取失败\",\"content\":\"§c指令文件不存在,请去 "+file.getAbsolutePath()+ "添加运行指令\",\"buttons\":[{\"type\":\"button\",\"text\":\"确认\"}]}");
                packet.setFormId(0);
                player.receivePacket(packet);
                this.cmd.clear();
                return;
            }

            this.cmd = Arrays.asList(cmd.split("\n"));

            if(mode.getCurrentValue().equalsIgnoreCase("单次")){
                NeteaseCommandRequestPacket packet = new NeteaseCommandRequestPacket();
                packet.setCommandOriginData(new CommandOriginData(CommandOriginType.PLAYER, UUID.randomUUID(),"",0));
                packet.setInternal(false);
                packet.setVersion(36);
                packet.setHasExtra(true);

                for(String s : this.cmd){
                    packet.setCommand(s);
                    player.sendPacket(packet);
                }
            }
        }else{
            this.cmd.clear();
        }

    }


    @SubscribeEvent
    public void onTick(TickEvent event){
        if(mode.getCurrentValue().equalsIgnoreCase("单次")){
            return;
        }
        int cps = this.count.getCurrentValue().intValue();
        double preTick = 1000d / cps;
        if(commandTick.finished((long) preTick)){
            int count = cps>20?(int) (commandTick.getElapsedTime() / preTick):1;
            NeteaseCommandRequestPacket packet = new NeteaseCommandRequestPacket();
            packet.setCommandOriginData(new CommandOriginData(CommandOriginType.PLAYER, UUID.randomUUID(),"",0));
            packet.setInternal(false);
            packet.setVersion(36);
            packet.setHasExtra(true);
            for(int i = 0 ; i < count ; i++){
                for(String s : this.cmd){
                    packet.setCommand(s);
                    event.getPlayer().sendPacket(packet);
                }
            }
            commandTick.reset();
        }
    }



    public String readCommand() {
        StringBuilder python = new StringBuilder();
        try {
            File file = new File(CreeperBox.INSTANCE.activity.getExternalFilesDir(null),PATH);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                python.append(line).append("\n");
            }
            bufferedReader.close();
        } catch (IOException e) {
            return "";
        }
        return python.toString();
    }


    @Override
    public String getTag() {
        if(mode.getCurrentValue().equalsIgnoreCase("单次")){
            return "Once";
        }
        return "Loop";
    }
}
