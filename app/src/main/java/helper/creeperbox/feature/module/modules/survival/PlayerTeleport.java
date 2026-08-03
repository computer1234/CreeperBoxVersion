package helper.creeperbox.feature.module.modules.survival;

import android.util.Pair;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.cloudburstmc.protocol.bedrock.data.command.CommandOriginData;
import org.cloudburstmc.protocol.bedrock.data.command.CommandOriginType;
import org.cloudburstmc.protocol.bedrock.data.command.CommandOutputMessage;
import org.cloudburstmc.protocol.bedrock.packet.CommandOutputPacket;
import org.cloudburstmc.protocol.bedrock.packet.ModalFormRequestPacket;
import org.cloudburstmc.protocol.bedrock.packet.ModalFormResponsePacket;
import org.cloudburstmc.protocol.bedrock.packet.SetTitlePacket;
import org.cloudburstmc.protocol.bedrock.packet.TextPacket;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.PacketReceiveEvent;
import helper.creeperbox.feature.event.events.PacketSendEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.sdk.entity.type.EntityActor;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.math.Vec3f;
import helper.creeperbox.sdk.network.packet.PyRpcPacket;
import helper.creeperbox.sdk.network.packet.modify.NeteaseCommandRequestPacket;
import helper.creeperbox.sdk.network.packet.modify.NeteasePlayerAuthInputPacket;
import helper.creeperbox.sdk.network.packet.modify.NeteaseTextPacket;


@ModuleInfo(name = "玩家传送", category = Category.Survival)
public class PlayerTeleport extends Module {

    private int status = 0;     //0 list 1 wait response



    public static byte[] hexToByteArray(String hex) {
        int length = hex.length();
        byte[] byteArray = new byte[length / 2];

        for (int i = 0; i < length; i += 2) {
            String subStr = hex.substring(i, i + 2);
            byteArray[i / 2] = (byte) Integer.parseInt(subStr, 16);
        }

        return byteArray;
    }

    private Pair<Long,String>[] pairs = new Pair[]{};
    private void getList(EntityLocalPlayer player){
        pairs = player.getLevel().g();
        if(pairs.length == 0){
            ModalFormRequestPacket modal = new ModalFormRequestPacket();
            modal.setFormData("{\"type\":\"form\",\"title\":\"执行失败\",\"content\":\"§c当前服务器没有更多玩家\",\"buttons\":[{\"type\":\"button\",\"text\":\"确认\"}]}");
            modal.setFormId(0);
            player.receivePacket(modal);
        }else{
            status = 1;
            ModalFormRequestPacket modal = new ModalFormRequestPacket();
            StringBuilder sb = new StringBuilder("{\"type\":\"form\",\"title\":\"请选择玩家\",\"content\":\"§c当前服务器有");
            sb.append(pairs.length);
            sb.append("名玩家\",\"buttons\":[");
            for (int i = 0; i < pairs.length; i++) {
                sb.append("{\"text\":\"");
                sb.append(pairs[i].second);
                sb.append(" §a§l[点我搜索]");
                sb.append("\"}");
                if(i != pairs.length - 1){
                    sb.append(",");
                }
            }
            sb.append("]}");
            modal.setFormData(sb.toString());
            modal.setFormId(0);
            player.receivePacket(modal);
        }
    }




    @SubscribeEvent
    public void onPress(PacketSendEvent event) {

        if(event.getPacket() instanceof NeteasePlayerAuthInputPacket){
            ((NeteasePlayerAuthInputPacket) event.getPacket()).setCameraDeparted(true);
        }

        if(event.getPacket() instanceof ModalFormResponsePacket){
            ModalFormResponsePacket packet = (ModalFormResponsePacket) event.getPacket();
            event.setCancelled();

            if(status == 1 && packet.getFormId() == 0){
                int index = Integer.parseInt(packet.getFormData().trim());
                if(Integer.parseInt(packet.getFormData().trim())<=pairs.length){
                    long uid = pairs[index].first;
                    PyRpcPacket pyRpcPacket = new PyRpcPacket();
                    pyRpcPacket.setMsgId(98247598);
                    pyRpcPacket.setData(hexToByteArray("93c401729200c4314d696e6563726166743a7065743a7065745f736b696c6c5f667269656e645f6332735f6765745f667269656e645f706f73c0"));
                    EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
                    player.sendPacket(pyRpcPacket);

                    String other = String.valueOf(uid);
                    String c2s_self_pos = "93c40163920082c407706c617965727391";
                    String c2s_pos = "c40b726571506c617965724964";

                    String id = String.valueOf(player.getUniqueID());
                    String self_id = String.format("%s%s%s","c4",String.format("%02x", id.length()),stringToHex(id));
                    String other_id = String.format("%s%s%s","c4",String.format("%02x", other.length()),stringToHex(other));
                    String c2s_get_pos = String.format("%s%s%s%s%s",c2s_self_pos,other_id,c2s_pos,self_id,"c0");
                    pyRpcPacket.setData(hexToByteArray(c2s_get_pos));
                    player.sendPacket(pyRpcPacket);
                    status = 2;
                }else{
                    status = 0;
                }
            }
        }
    }




    @Override
    public void onEnable() {
        status = 0;
        EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
        if(player != null){
            getList(player);
        }
    }


    private static String stringToHex(String input) {
        StringBuilder hex = new StringBuilder();
        for (byte b : input.getBytes(StandardCharsets.UTF_8)) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }

    @SubscribeEvent
    public void onReceive(PacketReceiveEvent event){
        if(event.getPacket() instanceof PyRpcPacket && status == 2){
            if(new String(((PyRpcPacket) event.getPacket()).getData()).contains("isSameDimensionMap")){
                status = 0;

                JsonArray rootArray = null;
                try {
                    rootArray = JsonParser.parseString(((PyRpcPacket) event.getPacket()).generatorUnpacker().unpackValue().toString()).getAsJsonArray();
                } catch (IOException e) {
                    return;
                }
                JsonArray innerArray = rootArray.get(1).getAsJsonArray();
                JsonObject posMapObj = innerArray.get(1).getAsJsonObject();

                JsonObject posMap = posMapObj.getAsJsonObject("posMap");
                String key = posMap.keySet().iterator().next();
                JsonObject tupleObj = posMap.getAsJsonObject(key);
                JsonArray coordinates = tupleObj.getAsJsonArray("value");

                double x = coordinates.get(0).getAsDouble();
                double y = coordinates.get(1).getAsDouble();
                double z = coordinates.get(2).getAsDouble();

                EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
                EntityActor vehicle = player.getVehicle();
                if(vehicle!=null){
                    vehicle.setPos(new Vec3f((float) x, (float) y+1.62f, (float) z));
                }else{
                    player.setPos(new Vec3f((float) x, (float) y+1.62f, (float) z));
                }
            }
        }
    }

}
