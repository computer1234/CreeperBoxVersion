package helper.creeperbox.feature.module.modules.survival;

import java.nio.charset.StandardCharsets;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.network.packet.PyRpcPacket;


@ModuleInfo(name = "召唤坐骑", category = Category.Survival)
public class HorseSpawn extends Module {


    private static String stringToHex(String input) {
        StringBuilder hex = new StringBuilder();
        for (byte b : input.getBytes(StandardCharsets.UTF_8)) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }



    public static byte[] hexToByteArray(String hex) {
        int length = hex.length();
        byte[] byteArray = new byte[length / 2];

        for (int i = 0; i < length; i += 2) {
            String subStr = hex.substring(i, i + 2);
            byteArray[i / 2] = (byte) Integer.parseInt(subStr, 16);
        }

        return byteArray;
    }



    @Override
    public void onEnable() {

        PyRpcPacket packet = new PyRpcPacket();
        packet.setMsgId(98247598);
        packet.setData(hexToByteArray("93c40163920681c4057374617274cf0000018fb5671c15c0"));
        EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
        if(player != null) {
            player.sendPacket(packet);
            packet.setData(hexToByteArray("93c401729208c4244d696e6563726166743a7065743a74656c65706f72745f6d6f756e745f72657175657374c0"));
            player.sendPacket(packet);
            String horseSummonRpc = String.valueOf(player.getUniqueID());
            String lengthHex = String.format("%02x", horseSummonRpc.length());
            String rpcHex = stringToHex(horseSummonRpc);
            String horseSummon = new StringBuilder("c4").append(lengthHex).append(rpcHex).toString();
            packet.setData(hexToByteArray("93c40163920881c408706c617965724964c40b2d34323934393637323935c0".replace("c40b2d34323934393637323935",horseSummon)));
            player.sendPacket(packet);
        }
    }


}
