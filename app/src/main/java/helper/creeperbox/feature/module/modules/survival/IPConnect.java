package helper.creeperbox.feature.module.modules.survival;

import android.util.Log;

import org.cloudburstmc.protocol.bedrock.packet.ModalFormRequestPacket;
import org.cloudburstmc.protocol.bedrock.packet.TransferPacket;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.utils.ResourceUtil;


@ModuleInfo(name = "IP外进", category = Category.Survival)
public class IPConnect extends Module {

    public static final String PATH = "ip.txt";

    @Override
    public void onEnable() {
        EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
        if(player != null){

            String ip = readIP();

            String[] ipWithData = ip.trim().split("\n",-1);

            if(ipWithData.length != 2){
                player.displayClientMessage("读取文件失败 请尝试重新保存");
                return;
            }

            String[] ipWithPort = ipWithData[0].trim().split(":");

            int port = -1;
            try {
                port = Integer.parseInt(ipWithPort[1]);
            }catch (Exception ex){
                player.displayClientMessage("读取文件失败 请尝试重新保存");
                return;
            }

            CreeperBox.INSTANCE.setRoomSid(ipWithData[1]);

            TransferPacket packet = new TransferPacket();
            packet.setAddress(ipWithPort[0]);
            packet.setPort(port);
            player.receivePacket(packet);
        }

    }



    public String readIP() {
        try {
            File file = new File(CreeperBox.INSTANCE.activity.getExternalFilesDir(null), PATH);

            FileInputStream fileInputStream = new FileInputStream(file);

            InputStream decryptedStream = ResourceUtil.decryptStream(fileInputStream);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(decryptedStream));
            StringBuilder content = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                content.append(line).append("\n");
            }

            bufferedReader.close();
            decryptedStream.close();
            fileInputStream.close();

            return content.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
