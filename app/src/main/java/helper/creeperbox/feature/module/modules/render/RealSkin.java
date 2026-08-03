package helper.creeperbox.feature.module.modules.render;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.cloudburstmc.protocol.bedrock.packet.ModalFormRequestPacket;
import org.cloudburstmc.protocol.bedrock.packet.ModalFormResponsePacket;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.LoginEvent;
import helper.creeperbox.feature.event.events.PacketSendEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.network.packet.PyRpcPacket;
import helper.creeperbox.utils.FileUtil;
import helper.creeperbox.utils.LogUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

@ModuleInfo(name = "皮肤美化", category = Category.Render)
public class RealSkin extends Module {

    // 角色
    private static final String[][] ROLES = {
            {"无", ""},
            {"星霜蝶梦", "0d09a886-9fe3-4119-86cc-3b20c2b881a5"},
            {"晶魄霜华", "1db22f15-8451-43e4-989e-0a671a793b07"},
            {"夜樱鬼舞", "adb16c35-0858-4da0-88e4-c2a3f1d66edc"},
            {"变异传说魔法师", "de391fab-ce6d-4f05-bdab-4267302d5ad2"},
            {"神话帝皇铠甲", "bc4c6fcc-93c0-4bd0-84d0-64b4f9611645"},
            {"神话魔君", "39f87e5d-d856-4cd7-85f8-2df3a721eab5"},
            {"神话仙君", "f1459932-69d7-4fe6-9385-ce5d97dd026e"},
            {"暗域守望", "0f6565bb-f40f-43b1-bac2-554a231d306d"},
            {"碧瑶龙影", "e1056fc3-1d2f-48b0-aa62-4880d22a4604"},
            {"轩辕龙灵", "dce11ce7-c260-4cba-b82f-1b3e07143b4c"},
            {"海神王子", "6635ed53-4416-448b-a5d3-d8db8ba38557"},
            {"白虎雷子", "b2e1c7fa-9850-4fea-8029-a425daa334d6"},
            {"诸葛亮", "97b91cb0-a317-469d-9e1a-a420a93da53b"},
            {"兔神凌霄", "3e3609e7-5004-453d-8cf1-c4863620ce39"},
            {"女娲", "6b292fd5-13dd-462f-a102-e1208f8c25e4"},
            {"盘古", "2cf97572-35d5-4560-b25e-d1aadaec2f75"},
            {"魔龙使", "adb2b5d4-89db-4f6f-8af6-8274b464e947"},
            {"海神", "6635ed53-4416-448b-a5d3-d8db8ba38557"},
            {"胡耀太阳", "4cdd343c-795c-40a4-9813-04ff8298464d"},
            {"神医骑士", "c6af9ba2-25af-4d39-abfa-c8702ed2b212"},
            {"星河夜月", "0e0f5dec-d21e-49db-b9f3-c755884de905"},
            {"多啦爱梦", "c4b149c5-aeee-46d9-8370-fd7c62ed9e85"},
            {"轩辕龙腾", "dce11ce7-c260-4cba-b82f-1b3e07143b4c"},
            {"静香", "e4dd5b4d-e561-4dac-a6ba-ac4bc07371e9"},
            {"琉璃九尾", "35af4fdc-7e6e-4de6-9b0b-5989aa0d7a85"},
            {"潮汐公主", "e26ff0b0-9f46-4b3b-b1f0-e2023fdfed4d"},
            {"璀璨潮汐公主", "f9ba0c55-b264-40d2-a4f6-e8cc7c4dad7f"},
            {"帝皇铠甲", "bc4c6fcc-93c0-4bd0-84d0-64b4f9611645"},
            {"琉音", "adb16c35-0858-4da0-88e4-c2a3f1d66edc"},
            {"仙帝", "f1459932-69d7-4fe6-9385-ce5d97dd026e"},
            {"魔君", "39f87e5d-d856-4cd7-85f8-2df3a721eab5"},
            {"丝提亚", "2f5287ff-9acf-4a35-8324-366d2d5dd63a"},
            {"鸣月", "6fe39205-d2e7-4e07-a19c-02ed0943e9d3"},
            {"末影魔龙", "f30305c8-f726-43df-aa00-779baf2fc2c9"},
            {"糖果熊", "32da1900-654b-40c0-a056-4c97a51f6e98"}
    };

    // 法阵
    private static final String[][] CIRCLES = {
            {"无", ""},
            {"金游龙阵", "66d4bfd6-3469-4285-a9c7-0d51a7f217b2"},
            {"蓝游龙阵", "b6d96438-0b6b-48e0-88b6-b316e4988ec6"},
            {"神灵法阵", "990ae403-2566-4708-b988-f5f4bde57c88"},
            {"都市之光", "9a9261e0-1bb6-454f-9172-b1e274af4f17"},
            {"金雷异兽", "72a24391-bffe-4545-b1a6-8495a83f6325"},
            {"浪套汹涌", "4e8d632d-9663-4048-8c70-e480cc6a0788"}
    };

    // 坐骑
    private static final String[][] MOUNTS = {
            {"无", ""},
            {"九霄神龙坐骑", "f64c15d9-09cc-4ea0-9d0a-0b526b76c204"},
            {"变异坐骑", "b0992573-9974-4397-8ff9-95538751c67"},
            {"瑞麒麟坐骑", "9d9dec4c-50a6-466e-ac45-cb3b889a844e"},
            {"九尾灵狐坐骑", "81d52982-1413-439d-8326-693f25496124"},
            {"凤凰坐骑", "b5061642-dba6-4fed-9c2d-142ddf385fb3"},
            {"乖巧小狗坐骑", "3c03a04f-5b10-4bae-b31a-b77f1446d950"},
            {"梦幻珍珠号坐骑", "6afda474-52db-46e1-bac6-409cec3463ee"},
            {"炎龙之尽坐骑", "c12c8d31-6246-43aa-8816-32708eda83c7"},
            {"梦幻战车坐骑", "d82b9286-43c3-4eb6-96f3-c6c4200e9d8f"},
            {"幽冥主宰坐骑", "a4d06864-8b84-4d33-a7b3-c2604b00ed26"},
            {"末影之尽坐骑", "b35fd2c5-bcbe-430c-aaa5-7c1739747e91"},
            {"机械魔龙坐骑", "15c39742-640b-49d4-9728-be813d445eed"},
            {"梦幻蝶影坐骑", "7989df50-f16f-49a2-b2b8-959e60a91b5e"},
            {"梦幻天马坐骑", "ff55aa78-3cf3-4526-a5bb-04a8feea1720"}
    };


    // 盔甲
    private static final String[][] ARMORS = {
            {"无", ""},
            {"神龙之友盔甲", "d6a043cc-8547-4bd5-8768-82a0bbe77717"},
            {"天风之翼", "43c62ad0-4ae8-4b0a-b3ba-a820621d2324"},
            {"雷鸣铠甲", "801f0063-31e2-456b-a26b-122471344fa0"}
    };

    // 剑
    private static final String[][] SWORDS = {
            {"无", ""},
            {"轩辕神剑", "ddf26316-8d37-4236-acfb-08081a306d5c"},
            {"神话三叉戟", "109e7484-3f9f-4c05-b2b8-aea7c4aabdf1"},
            {"星斗长矛", "ab5f9e37-ae28-4d14-9fcd-8dfb6a50e89b"},
            {"宇宙之辉", "4d50ea73-1c76-4721-9d39-04f108c1a7ce"}
    };

    // 记忆上次选择
    private static int lastRoleIndex = 0;
    private static int lastCircleIndex = 0;
    private static int lastMountIndex = 0;
    private static int lastArmorIndex = 0;
    private static int lastSwordIndex = 0;
    public long ONE_HOUR = 60 * 60 * 1000;
    public long ONE_DAY = 24 * ONE_HOUR;
    public long SEVEN_DAYS_ONE_HOUR = 7 * ONE_DAY + ONE_HOUR;

    @Override
    public void onEnable() {
        showSkinForm();
    }

    private void showSkinForm() {
        EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
        if(player==null) return;
        // -1 表示永久用户，应该允许使用
        if(CreeperBox.y() != -1 && CreeperBox.y() <= SEVEN_DAYS_ONE_HOUR){
            ModalFormRequestPacket packet = new ModalFormRequestPacket();
            packet.setFormData("{\"type\":\"form\",\"title\":\"修改皮肤失败\",\"content\":\"§c有效期小于七天,此功能仅为月卡及以上用户使用\",\"buttons\":[{\"type\":\"button\",\"text\":\"确认\"}]}");
            packet.setFormId(0);
            player.receivePacket(packet);
            return;
        }
        JsonObject form = new JsonObject();
        form.addProperty("type", "custom_form");
        form.addProperty("title", "§b皮肤选择");

        JsonArray content = new JsonArray();
        content.add(createDropdown("§a角色", ROLES, lastRoleIndex));
        content.add(createDropdown("§e法阵", CIRCLES, lastCircleIndex));
        content.add(createDropdown("§d坐骑", MOUNTS, lastMountIndex));
        content.add(createDropdown("§6盔甲", ARMORS, lastArmorIndex));
        content.add(createDropdown("§c剑", SWORDS, lastSwordIndex));
        form.add("content", content);
        form.addProperty("closed", false);

        ModalFormRequestPacket packet = new ModalFormRequestPacket();
        packet.setFormId(88888);
        packet.setFormData(new Gson().toJson(form));
        player.receivePacket(packet);
    }

    private JsonObject createDropdown(String text, String[][] data, int defaultIndex) {
        JsonObject dropdown = new JsonObject();
        dropdown.addProperty("type", "dropdown");
        dropdown.addProperty("text", text);
        JsonArray options = new JsonArray();
        for (String[] item : data) {
            options.add(item[0]);
        }
        dropdown.add("options", options);
        dropdown.addProperty("default", defaultIndex);
        return dropdown;
    }


    @SubscribeEvent
    public void onPacket(PacketSendEvent event) {
        if (event.getPacket() instanceof ModalFormResponsePacket) {
            ModalFormResponsePacket response = (ModalFormResponsePacket) event.getPacket();
            if (response.getFormId() == 88888) {
                event.setCancelled(true);
                handleFormResponse(response.getFormData());
            }
        }
    }

    private void handleFormResponse(String formData) {
        try {
            JsonArray arr = JsonParser.parseString(formData).getAsJsonArray();
            int roleIndex = arr.get(0).getAsInt();
            int circleIndex = arr.get(1).getAsInt();
            int mountIndex = arr.get(2).getAsInt();
            int armorIndex = arr.get(3).getAsInt();
            int swordIndex = arr.get(4).getAsInt();

            // 记忆选择
            lastRoleIndex = roleIndex;
            lastCircleIndex = circleIndex;
            lastMountIndex = mountIndex;
            lastArmorIndex = armorIndex;
            lastSwordIndex = swordIndex;

            List<String> uuids = new ArrayList<>();
            if (roleIndex > 0) uuids.add(ROLES[roleIndex][1]);
            if (circleIndex > 0) uuids.add(CIRCLES[circleIndex][1]);
            if (mountIndex > 0) uuids.add(MOUNTS[mountIndex][1]);
            if (armorIndex > 0) uuids.add(ARMORS[armorIndex][1]);
            if (swordIndex > 0) uuids.add(SWORDS[swordIndex][1]);

            if (!uuids.isEmpty()) {
                sendSyncUsingMod(uuids);
            }
        } catch (Exception e) {
        }
    }

    private void sendSyncUsingMod(List<String> uuids) {
        byte[] data = buildSyncUsingMod(uuids, "4672593875093455504");
        PyRpcPacket packet = new PyRpcPacket();
        packet.setMsgId(98247598);
        packet.setData(data);
        CreeperBox.INSTANCE.getLocalPlayer().sendPacket(packet);
    }

    private byte[] buildSyncUsingMod(List<String> modUuids, String skinId) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeByte(0x93);
        writeBinString(buf, "SyncUsingMod");
        buf.writeByte(0x90 + 7);

        buf.writeByte(0x90 + modUuids.size());
        for (String uuid : modUuids) writeBinString(buf, uuid);

        writeBinString(buf, modUuids.get(0));
        writeBinString(buf, skinId);
        buf.writeByte(0xc3);
        buf.writeByte(0x80);
        writeBinString(buf, skinId);

        buf.writeByte(0x83);
        writeBinString(buf, "is_skin_try_on_mod");
        buf.writeByte(0xc3);
        writeBinString(buf, "usingMods");
        buf.writeByte(0x90 + modUuids.size());
        for (String uuid : modUuids) writeBinString(buf, uuid);
        writeBinString(buf, "skinId");
        writeBinString(buf, skinId);

        buf.writeByte(0xc0);

        byte[] result = new byte[buf.readableBytes()];
        buf.readBytes(result);
        buf.release();
        return result;
    }

    private void writeBinString(ByteBuf buf, String str) {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        buf.writeByte(0xc4);
        buf.writeByte(bytes.length);
        buf.writeBytes(bytes);
    }



}
