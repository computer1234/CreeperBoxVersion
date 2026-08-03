package helper.creeperbox.clients;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Log;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import helper.creeperbox.VerifyManager;
import helper.creeperbox.feature.event.EventManager;
import helper.creeperbox.feature.module.ModuleManager;
import helper.creeperbox.file.ConfigManager;
import helper.creeperbox.hotfix.UpdateManager;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.utils.ResourceUtil;

public enum CreeperBox {
    INSTANCE;

    public Activity activity;
    public VerifyManager getVerifyManager() {
        return verifyManager;
    }
    public String accountPath;
    public static boolean isInit = false;
    public Context context;
    public boolean debug = true;
    private EventManager eventManager;
    private ModuleManager moduleManager;
    private UpdateManager updateManager;
    private ConfigManager configManager;
    private VerifyManager verifyManager;

    public native EntityLocalPlayer getLocalPlayer();

    public static String generatorUdid() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[8];
        random.nextBytes(bytes);

        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    public static String generateRandomMac() {
        Random random = new Random();
        byte[] macBytes = new byte[6];
        random.nextBytes(macBytes);

        macBytes[0] = (byte) (macBytes[0] & (byte) 254);
        macBytes[0] = (byte) (macBytes[0] | 0x02);

        StringBuilder macBuilder = new StringBuilder();
        for (int i = 0; i < macBytes.length; i++) {
            macBuilder.append(String.format("%02X", macBytes[i]));
            if (i < macBytes.length - 1 ) {
                macBuilder.append(":");
            }
        }
        return macBuilder.toString();
    }


    public String getPackageSignMd5(Context context, String packageName) {
        try {

            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
                    packageName,
                    PackageManager.GET_SIGNATURES
            );


            Signature[] signatures = packageInfo.signatures;
            if (signatures == null || signatures.length == 0) {
                return null;
            }

            byte[] certBytes = signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = md.digest(certBytes);

            StringBuilder hexString = new StringBuilder();
            for (byte b : md5Bytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressLint("SdCardPath")
    public void init() {
        if(!isInit){
            isInit = true;

            CreeperBox.v(generatorUdid());
            CreeperBox.w(generateRandomMac());

            this.eventManager = new EventManager();
            this.moduleManager = new ModuleManager();
            if(debug){
                this.updateManager = new UpdateManager();
            }

            this.configManager = new ConfigManager();
            this.verifyManager = new VerifyManager();

            ResourceUtil.decryptModel();
        }
    }

    private String getCookie(){
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

        if (clipboard.hasPrimaryClip()) {
            ClipData clipData = clipboard.getPrimaryClip();
            if (clipData != null && clipData.getItemCount() > 0) {
                ClipData.Item item = clipData.getItemAt(0);
                String text = item.getText().toString();
                return text;
            }
        }
        return "";
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public UpdateManager getUpdateManager() {
        return updateManager;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public boolean runPython(String cmd){
        return a(cmd);
    }

    public native boolean a(String cmd);

    public native void b(String cookie);

    public boolean getShowProgress(){
        return c();
    }
    public native boolean c();

    public void handleDestroyOrAttackButtonPress(){
        d();
    }
    public void handleBuildOrInteractButtonPress(){
        e();
    }

    public native void e();

    public native void d();

    public int getPointerX(){
        return f();
    }

    public native int f();
    public int getPointerY(){
        return g();
    }

    public native int g();

    public String getIP(){
        return h();
    }

    public native String h();
    public String getRoomSid(){
        return i();
    }

    public native String i();

    public void setRoomSid(String sid){
        j(sid);
    }

    public native void j(String sid);

    public void setNextIP(String ip){
        k(ip);
    }

    public void setNextPort(int port){
        l(port);
    }


    public native void k(String ip);

    public native void l(int port);

    public static native int m();

    public static int getNextRequestID(){
        return m();
    }

    public static native void n(int id);

    public static void setNextRequestID(int id){
        n(id);
    }

    public static native void o(boolean doXray);

    public static void setXRay(boolean doXray){
        o(doXray);
    }

    public static native void p(boolean doXray);

    public static void setChestXRay(boolean doXray){
        p(doXray);
    }

    public static native void q(String name);

    public static void addXRayBlock(String name){
        q(name);
    }

    public static native void r(String name);

    public static void removeXRayBlock(String name){
        r(name);
    }

    public static native int s();

    public static int getFPS(){
        return s();
    }

    public native static void t(float gamma);

    public static void setGamma(float gamma){
        t(gamma);
    }

    public native static void u();

    public native static void v(String udid);
    public native static void w(String macaddr);
    public native static boolean x();
    public native static long y();

    public native static void z(boolean crash);

    public native static void a1(int type);

    // 验证接口 JNI 方法
    public native static boolean b1(String username, String password, String hwid);
    public native static String c1();
    public native static String d1();

    // 包装方法（更易读的API）
    public static boolean verify(String card, String password, String hwid) {
        return b1(card, password, hwid);
    }
    public static String getVerifyStatus() {
        return c1();
    }
    public static String getVerifyError() {
        return d1();
    }
}
