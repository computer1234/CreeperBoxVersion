package helper.creeperbox.hook;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.cloudburstmc.protocol.bedrock.packet.TextPacket;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import helper.creeperbox.NeteaseManager;
import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.event.events.ClientTickEvent;
import helper.creeperbox.feature.event.events.LoginEvent;
import helper.creeperbox.feature.event.events.SetCameraEvent;
import helper.creeperbox.feature.module.modules.survival.ChatBypass;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.math.Vec3f;
import helper.creeperbox.sdk.network.packet.modify.NeteaseTextPacket;


public class HookProxy {


    //onLocalPlayerTick
    public static void a(long player){
        JavaProxy.a(player);
    }

    //onKeyEvent
    public static void b(int keyCode,int action){
        JavaProxy.b(keyCode,action);
    }


    //onDestroyBlock
    public static float c(long player, long block, int x,int y,int z,float progress){
      return JavaProxy.c(player, block, x, y, z, progress);
    }

    //preRender2D
    public static void d(long screenView,long renderCtx){
        JavaProxy.d(screenView,renderCtx);
    }

    //onActorTick
    public static void e(long actor){
        JavaProxy.e(actor);
    }

    //onAttack
    public static void f(long gamemode,long other){
        JavaProxy.f(gamemode, other);
    }


    //onPacketSend
    public static byte[] g(byte[] data){
       return JavaProxy.g(data);
    }


    //preRender3D
    public static void h(long levelRenderer,long screenContext){
        JavaProxy.h(levelRenderer,screenContext);
    }


    //onPacketReceive
    public static byte[] i(byte[] data){
        return JavaProxy.i(data);
    }



    //postRender3D
    public static void j(){
        JavaProxy.j();
    }



    //onPress return false to cancel
    public static boolean k(int action,float x,float y,int count) {
        return JavaProxy.k(action, x, y,count);
    }

    //onRenderItem
    public static float[] l(float[] matrix){
        return JavaProxy.l(matrix);
    }


    //onRenderItem3DMethod
    public static float[] m(float particleTick,long actor,float[] matrix){
        return JavaProxy.m(particleTick, actor, matrix);
    }


    //PostRender2D
    public static void n(long screenView,long renderCtx){
        JavaProxy.n(screenView,renderCtx);
    }

    //onMove
    public static void o(long player){
        JavaProxy.o(player);
    }

    //onClientInstance tick
    public static void p(){
        if(!HopeHookEntrance.hasInit) return;
        CreeperBox.INSTANCE.getEventManager().callEvent(new ClientTickEvent());
    }

    //onSetCamera
    public static float[] q(float x,float y,float z){
        SetCameraEvent event = new SetCameraEvent(new Vec3f(x,y,z));
        CreeperBox.INSTANCE.getEventManager().callEvent(event);
        return new float[]{event.cameraPos.x,event.cameraPos.y,event.cameraPos.z};
    }

    //onChat return true to cancel
    public static boolean r(String message){
        if(!ChatBypass.bypass) return false;
        EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
        if(player!=null){
            NeteaseTextPacket packet = new NeteaseTextPacket();
            packet.setHasExtra(true);
            packet.setType(TextPacket.Type.CHAT);
            packet.setXuid("");
            packet.setAuthor("");
            packet.setPlatformChatId("");
            packet.setSourceName(player.getNameTag());
            packet.setMessage(message);
            player.sendPacket(packet);
            return true;
        }
        return false;
    }


    public static String s(String content){

//        if(content.contains("sauth_json")){
//            JsonObject root = JsonParser.parseString(content).getAsJsonObject();
//            String sauthJson = root.getAsJsonObject("sauth_json").toString();
//
//            String seed = String.valueOf(UUID.randomUUID());
//            String engine_version = "3.4.5.272725";
//            String patch_version = "3.4.118.273808";
//            String libminecraftpe_md5 = "8496013eea6d450019160b20adc7c124";
//            String package_sign_md5 = "2b3e7ca013bb30a74d822579860c042b";
//            String vanilla_mcp_md5 = "e28465bde2a9815781e9590ba339ae97";
//            String rn_bundle_md5 = "618f393e167b347c2809cc01a94f717e";
//
//            MessageDigest combineMD5 = null;
//            try {
//                combineMD5 = MessageDigest.getInstance("MD5");
//            } catch (NoSuchAlgorithmException e) {
//                throw new RuntimeException("Error while calculate message. Reason: an error occurred during calculating "+e.getMessage());
//            }
//
//            combineMD5.update(vanilla_mcp_md5.getBytes());
//            combineMD5.update(rn_bundle_md5.getBytes());
//            String patchMd5 = byteArrayToHex(combineMD5.digest());
//
//            String message = String.format("%s%s%s%s%s%s",engine_version,libminecraftpe_md5,patch_version,patchMd5,package_sign_md5,seed);
//            String sign = NeteaseManager.encryptMessage(message);
//
//            JsonObject requestJson = new JsonObject();
//
//            requestJson.addProperty("engine_version",engine_version);
//            requestJson.addProperty("extra_param","extra");
//            requestJson.addProperty("message", message);
//            requestJson.addProperty("patch_version", patch_version);
//            requestJson.addProperty("pay_channel", "dashen_cloudgame");
//            requestJson.addProperty("sa_data",buildSaData(patch_version, "HONOR BVL-AN00", "263ba5cf7bbfa252", "02:00:00:00:00:00"));
//            requestJson.addProperty("sauth_json", sauthJson);
//            requestJson.addProperty("seed", seed);
//            requestJson.addProperty("sign", sign);
//
//            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
//            String json = gson.toJson(requestJson);
//            return json;
//        }

        return content;
    }


    public static String t(String content){
        LoginEvent event = new LoginEvent(content);
        CreeperBox.INSTANCE.getEventManager().callEvent(event);
        return event.loginData;
    }

    public static String byteArrayToHex(byte[] bytes) {
        return byteArrayToHex(bytes,false);
    }

    public static String byteArrayToHex(byte[] bytes,boolean upperCase) {
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes) {
            if(upperCase) hex.append(String.format("%02X", b));
            else hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }


    private static String buildSaData(String patch_version,String device_model,String udid,String mac_addr){
        JsonObject saData = new JsonObject();
        saData.addProperty("app_channel", "netease");
        saData.addProperty("app_ver", patch_version);
        saData.addProperty("core_num", "4");
        saData.addProperty("cpu_digit", "64");
        saData.addProperty("cpu_hz", "3200000");
        saData.addProperty("cpu_name", "placeholder");
        saData.addProperty("device_height", "1920");
        saData.addProperty("device_model", device_model);
        saData.addProperty("device_width", "1080");
        saData.addProperty("disk", "");
        saData.addProperty("emulator", 0);
        saData.addProperty("first_udid", udid);
        saData.addProperty("is_guest", 1);
        saData.addProperty("launcher_type", "PE_C++");
        saData.addProperty("mac_addr", mac_addr);
        saData.addProperty("network", "CHANNEL_UNKNOW");
        saData.addProperty("os_name", "android");
        saData.addProperty("os_ver", "12");
        saData.addProperty("ram", "6238916608");
        saData.addProperty("rom", "134208294912");
        saData.addProperty("root", false);
        saData.addProperty("sdk_ver", "5.9.0");
        saData.addProperty("start_type", "default");
        saData.addProperty("udid", udid);
        return saData.toString();
    }



}
