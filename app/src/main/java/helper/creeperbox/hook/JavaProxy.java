package helper.creeperbox.hook;

import android.util.Log;

import helper.creeperbox.clickgui.ClickGUIRenderer;
import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.event.events.BlockDestroyEvent;
import helper.creeperbox.feature.event.events.KeyEvent;
import helper.creeperbox.feature.event.events.LoginFinishEvent;
import helper.creeperbox.feature.event.events.PacketReceiveEvent;
import helper.creeperbox.feature.event.events.PacketSendEvent;
import helper.creeperbox.feature.event.events.PostMoveEvent;
import helper.creeperbox.feature.event.events.PostRender3DEvent;
import helper.creeperbox.feature.event.events.Render2DEvent;
import helper.creeperbox.feature.event.events.Render3DEvent;
import helper.creeperbox.feature.event.events.RenderHandEvent;
import helper.creeperbox.feature.event.events.RenderItem3DEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleManager;
import helper.creeperbox.sdk.InstanceGenerator;
import helper.creeperbox.sdk.block.Block;
import helper.creeperbox.sdk.entity.type.EntityItem;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.math.Vec3i;
import helper.creeperbox.sdk.network.packet.SyncSkinPacket;
import helper.creeperbox.sdk.network.packet.modify.NeteasePlayerAuthInputPacket;
import helper.creeperbox.sdk.render.LevelRenderer;
import helper.creeperbox.sdk.render.ScreenContext;
import helper.creeperbox.sdk.render.ScreenView;
import helper.creeperbox.sdk.render.UIRenderContext;

import org.cloudburstmc.protocol.bedrock.packet.AvailableCommandsPacket;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.MoveEntityAbsolutePacket;
import org.cloudburstmc.protocol.bedrock.packet.MoveEntityDeltaPacket;
import org.cloudburstmc.protocol.bedrock.packet.SetEntityDataPacket;
import org.cloudburstmc.protocol.bedrock.packet.UnknownPacket;

public class JavaProxy {


    //onLocalPlayerTick
    public static void a(long player){
        EntityLocalPlayer p = new EntityLocalPlayer(player);
        CreeperBox.INSTANCE.getEventManager().callEvent(new TickEvent(p));
    }




    //onKeyEvent
    public static void b(int keyCode,int action){
        ModuleManager moduleManager = CreeperBox.INSTANCE.getModuleManager();
        if(moduleManager!=null && action == KeyEvent.AKEY_EVENT_ACTION_DOWN){
            for(Module module :moduleManager.getRegisteredModule()){
                module.onKeyDown(keyCode);
            }
        }
    }


    //onDestroyBlock
    public static float c(long player, long block, int x,int y,int z,float progress){
        Block b = new Block(block,new Vec3i(x,y,z));
        EntityLocalPlayer p = new EntityLocalPlayer(player);
        BlockDestroyEvent event = new BlockDestroyEvent(b,p,progress);
        CreeperBox.INSTANCE.getEventManager().callEvent(event);
        return event.getProgress();
    }


    //preRender2D
    public static void d(long screenView,long renderCtx){
        CreeperBox.INSTANCE.getEventManager().callEvent(new Render2DEvent(new ScreenView(screenView),new UIRenderContext(renderCtx)));
    }


    //onActorTick
    public static void e(long actor){
        CreeperBox.INSTANCE.getEventManager().callEvent(new LoginFinishEvent(actor));
    }


    //onAttack
    public static void f(long gamemode,long other){}



    //onPacketSend
    public static byte[] g(byte[] data){
        try {
            BedrockPacket packet = InstanceGenerator.generatorPacket(data);
            PacketSendEvent event = new PacketSendEvent(packet);
            CreeperBox.INSTANCE.getEventManager().callEvent(event);
            if(event.isCancelled()) return new byte[0];
            return InstanceGenerator.decodePacket(packet);
        } catch (Exception ex){
            ex.printStackTrace();
            return data;
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }

    //preRender3D
    public static void h(long levelRenderer,long screenContext){
        CreeperBox.INSTANCE.getEventManager().callEvent(new Render3DEvent(new LevelRenderer(levelRenderer),new ScreenContext(screenContext)));
    }

    //onPacketReceive
    public static byte[] i(byte[] data){
        try {
            return data;
//            BedrockPacket packet = InstanceGenerator.generatorPacket(data);
//
//            if(packet instanceof AvailableCommandsPacket || packet instanceof SetEntityDataPacket){
//                return data;
//            }
//            PacketReceiveEvent event = new PacketReceiveEvent(packet);
//            if(packet instanceof MoveEntityAbsolutePacket || packet instanceof MoveEntityDeltaPacket) return data;
//            CreeperBox.INSTANCE.getEventManager().callEvent(event);
//            if(event.isCancelled()) return new byte[0];
//
//            return InstanceGenerator.decodePacket(packet);
        } catch (Exception e){
            return data;
        }
    }



    //postRender3D
    public static void j(){
        CreeperBox.INSTANCE.getEventManager().callEvent(new PostRender3DEvent());
    }


    //onPress return false to cancel
    public static boolean k(int action,float x,float y,int count) {
        return ClickGUIRenderer.onKey(action, x, y,count);
    }

    public static float[] l(float[] matrix){
        RenderHandEvent event = new RenderHandEvent(matrix);
        CreeperBox.INSTANCE.getEventManager().callEvent(event);
        return event.matrix;
    }

    public static float[] m(float particleTick,long actor,float[] matrix){
        RenderItem3DEvent event = new RenderItem3DEvent(matrix,new EntityItem(actor),particleTick);
        CreeperBox.INSTANCE.getEventManager().callEvent(event);
        return event.matrix;
    }


    public static void n(long screenView, long renderCtx) {
        CreeperBox.INSTANCE.getEventManager().callEvent(new Render2DEvent(new ScreenView(screenView),new UIRenderContext(renderCtx)));
    }

    public static void o(long player){
        CreeperBox.INSTANCE.getEventManager().callEvent(new PostMoveEvent(new EntityLocalPlayer(player)));
    }





}
