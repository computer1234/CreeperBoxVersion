package helper.creeperbox.feature.module.modules.survival;

import android.util.Log;

import org.cloudburstmc.protocol.bedrock.packet.ModalFormRequestPacket;
import org.cloudburstmc.protocol.bedrock.packet.TransferPacket;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.utils.ResourceUtil;


@ModuleInfo(name = "IP获取", category = Category.Survival)
public class IPPrint extends Module {
    public static final String PATH = "ip.txt";

    @Override
    public void onEnable() {
        EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
        if(player != null){
            File file = new File(CreeperBox.INSTANCE.activity.getExternalFilesDir(null),PATH);
            StringBuilder sb = new StringBuilder();
            sb.append(CreeperBox.INSTANCE.getIP());
            sb.append("\n");
            sb.append(CreeperBox.INSTANCE.getRoomSid());
            writeIP(sb.toString());
            player.displayClientMessage("当前服务器信息已保存到"+file.getAbsolutePath());
        }
    }


    public boolean writeIP(String content) {
        try {
            File file = new File(CreeperBox.INSTANCE.activity.getExternalFilesDir(null), PATH);

            byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(contentBytes);

            InputStream encryptedStream = ResourceUtil.encryptStream(byteArrayInputStream);

            FileOutputStream fileOutputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = encryptedStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, length);
            }

            encryptedStream.close();
            byteArrayInputStream.close();
            fileOutputStream.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
