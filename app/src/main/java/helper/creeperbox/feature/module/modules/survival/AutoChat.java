package helper.creeperbox.feature.module.modules.survival;

import org.cloudburstmc.protocol.bedrock.data.command.CommandOriginData;
import org.cloudburstmc.protocol.bedrock.data.command.CommandOriginType;
import org.cloudburstmc.protocol.bedrock.packet.ModalFormRequestPacket;
import org.cloudburstmc.protocol.bedrock.packet.TextPacket;

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
import helper.creeperbox.sdk.network.packet.modify.NeteaseTextPacket;
import helper.creeperbox.utils.mc.StopWatch;

@ModuleInfo(name = "自动发言", category = Category.Survival)
public class AutoChat extends Module {


    private final ListValue mode = new ListValue("模式",this,"循环")
            .addSubList("循环")
            .addSubList("单次");

    private final NumberValue count = new NumberValue("次数", this,10,1, 100, 1);


    public static final String PATH = "chat.txt";


    private List<String> chat = new ArrayList<>();
    private StopWatch chatTick = new StopWatch();



    @Override
    public void onEnable() {
        EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
        if(player!=null){
            chatTick.reset();
            String cmd = readChat();
            if(cmd.isEmpty()){
                File file = new File(CreeperBox.INSTANCE.activity.getExternalFilesDir(null),PATH);
                ModalFormRequestPacket packet = new ModalFormRequestPacket();
                packet.setFormData("{\"type\":\"form\",\"title\":\"文件读取失败\",\"content\":\"§c聊天文件不存在,请去 "+file.getAbsolutePath()+ "添加运行指令\",\"buttons\":[{\"type\":\"button\",\"text\":\"确认\"}]}");
                packet.setFormId(0);
                player.receivePacket(packet);
                this.chat.clear();
                return;
            }

            this.chat = Arrays.asList(cmd.split("\n"));

            if(mode.getCurrentValue().equalsIgnoreCase("单次")){

                NeteaseTextPacket packet = new NeteaseTextPacket();
                packet.setHasExtra(true);
                packet.setType(TextPacket.Type.CHAT);
                packet.setXuid("");
                packet.setAuthor("");
                packet.setPlatformChatId("");
                packet.setSourceName(player.getNameTag());

                for(String s : this.chat){
                    packet.setMessage(s);
                    player.sendPacket(packet);
                }
            }
        }else{
            this.chat.clear();
        }
    }


    @SubscribeEvent
    public void onTick(TickEvent event){
        if(mode.getCurrentValue().equalsIgnoreCase("单次")){
            return;
        }
        int cps = this.count.getCurrentValue().intValue();
        double preTick = 1000d / cps;
        if(chatTick.finished((long) preTick)){
            int count = cps>20?(int) (chatTick.getElapsedTime() / preTick):1;
            NeteaseTextPacket packet = new NeteaseTextPacket();
            packet.setHasExtra(true);
            packet.setType(TextPacket.Type.CHAT);
            packet.setXuid("");
            packet.setAuthor("");
            packet.setPlatformChatId("");
            packet.setSourceName(event.getPlayer().getNameTag());
            for(int i = 0 ; i < count ; i++){
                for(String s : this.chat){
                    packet.setMessage(s);
                    event.getPlayer().sendPacket(packet);
                }
            }
            chatTick.reset();
        }
    }



    public String readChat() {
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
