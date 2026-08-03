package helper.creeperbox.feature.module.modules.render;

import android.opengl.Matrix;

import org.cloudburstmc.protocol.bedrock.data.inventory.transaction.InventoryTransactionType;
import org.cloudburstmc.protocol.bedrock.packet.AnimatePacket;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.InventoryTransactionPacket;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.PacketSendEvent;
import helper.creeperbox.feature.event.events.RenderHandEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.ListValue;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.item.ItemStack;
import helper.creeperbox.utils.mc.StopWatch;
import helper.creeperbox.utils.mc.TargetUtil;
import helper.creeperbox.utils.render.animation.Animation;
import helper.creeperbox.utils.render.animation.Easing;

@ModuleInfo(name = "挥手动画", category = Category.Render)
public class SwingAnimation extends Module {

    private final ListValue mode = new ListValue("模式",this,"无")
            .addSubList("无")
            .addSubList("Java1.7")
            .addSubList("平滑")
            .addSubList("转刀")
            .addSubList("泄露")
            .addSubList("欣欣")
            .addSubList("突击2")
            .addSubList("下拉")
            .addSubList("安拉")
            .addSubList("突击");

    private final ListValue blockingTiming = new ListValue("格挡时机",this,"Always")
            .addSubList("一直")
            .addSubList("玩家靠近")
            .addSubList("攻击时");


    private NumberValue swingSpeed = new NumberValue( "挥手速度",this, 1000, 100, 2000, 1);

    private NumberValue blockingRange = new NumberValue( "格挡距离",this, 4, 3, 10, 0.1);


    public SwingAnimation(){
        blockAnimation = new Animation(Easing.EASE_IN_QUAD, swingSpeed.getCurrentValue().longValue());
    }

    private boolean animate = false;

    private Animation blockAnimation;

    private StopWatch attackWatch = new StopWatch();


    @SubscribeEvent
    public void onTick(TickEvent event) {
        EntityLocalPlayer player = event.getPlayer();

        switch (blockingTiming.getCurrentValue()){
            case "一直":
                blocking = true;
                break;
            case "玩家靠近":
                blocking = !TargetUtil.findTarget(event.getPlayer(),blockingRange.getCurrentValue().floatValue()).isEmpty();
                break;
            case "攻击时":
                blocking = !attackWatch.finished(swingSpeed.getCurrentValue().longValue());
                break;
        }

        if(blocking && player.getItemInHand().getNameSpace().contains("sword") && EntityLocalPlayer.getView() == 0){
            player.setSwingTick(-1);
        }

    }

    @Override
    public void onEnable() {
        attackWatch.reset();
    }


    private boolean isAttackPacket(BedrockPacket packet){
        if(packet instanceof InventoryTransactionPacket){
            InventoryTransactionPacket inventoryTransactionPacket = (InventoryTransactionPacket) packet;
            if(inventoryTransactionPacket.getTransactionType() == InventoryTransactionType.ITEM_USE_ON_ENTITY && inventoryTransactionPacket.getActionType() == 1){
                return true;
            }
        }
        return false;
    }


    @SubscribeEvent
    public void onAttack(PacketSendEvent event){
        if(isAttackPacket(event.getPacket())) attackWatch.reset();

        if(event.getPacket() instanceof InventoryTransactionPacket){
            doSwing();
        }

        if(event.getPacket() instanceof AnimatePacket){
            AnimatePacket packet = (AnimatePacket) event.getPacket();
            if(packet.getAction() == AnimatePacket.Action.SWING_ARM){
                doSwing();
            }
        }
    }



    public void doSwing(){
        if(!animate || blockAnimation.getProgress() > 0.8){
            blockAnimation = new Animation(Easing.EASE_IN_QUAD, swingSpeed.getCurrentValue().longValue());
            animate = true;
        }
    }






    private void transformFirstPersonItem(float swingProgress,float[] matrix){
        Matrix.translateM(matrix,0,0.56F, -0.52F, -0.71999997F);
        Matrix.rotateM(matrix,0,45.0F, 0.0F, 1.0F, 0.0F);
        final float f = (float) Math.sin(swingProgress * swingProgress * (float) Math.PI);
        final float f1 = (float) Math.sin(Math.sqrt(swingProgress) * (float) Math.PI);
        Matrix.rotateM(matrix,0,f * -20.0F, 0.0F, 1.0F, 0.0F);
        Matrix.rotateM(matrix,0,f1 * -20.0F, 0.0F, 0.0F, 1.0F);
        Matrix.rotateM(matrix,0,f1 * -80.0F, 1.0F, 0.0F, 0.0F);
        Matrix.scaleM(matrix,0,0.4F, 0.4F, 0.4F);
    }



    private void blockTransformation(float[] matrix){
        Matrix.translateM(matrix,0,-0.5F, 0.2F, 0.0F);
        Matrix.rotateM(matrix,0,30.0F, 0.0F, 1.0F, 0.0F);
        Matrix.rotateM(matrix,0,-80.0F, 1.0F, 0.0F, 0.0F);
        Matrix.rotateM(matrix,0,60.0F, 0.0F, 1.0F, 0.0F);
    }




    private void endTransformation(float[] matrix){
        Matrix.scaleM(matrix,0,2.5f, 2.5f, 2.5f);
        Matrix.rotateM(matrix,0,-45.0F, 0.0F, 1.0F, 0.0F);
        Matrix.translateM(matrix,0,-0.56F, 0.52F, 0.71999997F);
    }

    public static boolean blocking = true;

    @SubscribeEvent
    public void onRender(RenderHandEvent event) {

        if (!blocking) return;

        EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
        if (player != null && player.getItemInHand().getNameSpace().contains("sword")) {
            if (animate) {
                blockAnimation.run(1f);
                if (blockAnimation.isFinished()) animate = false;
            }

            float swingProgress = blockAnimation.getValue();
            float convertedProgress = (float) Math.sin(Math.sqrt(swingProgress) * (float) Math.PI);

            switch (mode.getCurrentValue()) {
                case "无":
                    transformFirstPersonItem(0, event.matrix);
                    blockTransformation(event.matrix);
                    break;
                case "Java1.7":
                    transformFirstPersonItem(swingProgress, event.matrix);
                    blockTransformation(event.matrix);
                    break;
                case "平滑":
                    transformFirstPersonItem(0, event.matrix);
                    final float y = -convertedProgress * 2.0F;
                    Matrix.translateM(event.matrix, 0, 0.0F, y / 10.0F + 0.1F, 0.0F);
                    Matrix.rotateM(event.matrix, 0, y * 10.0F, 0.0F, 1.0F, 0.0F);
                    Matrix.rotateM(event.matrix, 0, 250, 0.2F, 1.0F, -0.6F);
                    Matrix.rotateM(event.matrix, 0, -10.0F, 1.0F, 0.5F, 1.0F);
                    Matrix.rotateM(event.matrix, 0, -y * 20.0F, 1.0F, 0.5F, 1.0F);
                    break;
                case "转刀":
                    transformFirstPersonItem(0, event.matrix);
                    Matrix.translateM(event.matrix, 0, 0, 0.2F, -1);
                    Matrix.rotateM(event.matrix, 0, -59, -1, 0, 3);
                    Matrix.rotateM(event.matrix, 0, -(System.currentTimeMillis() / 2 % 360), 1, 0, 0.0F);
                    Matrix.rotateM(event.matrix, 0, 60.0F, 0.0F, 1.0F, 0.0F);
                    break;
                case "泄露":
                    transformFirstPersonItem(0, event.matrix);
                    Matrix.translateM(event.matrix, 0, 0.0f, 0.1F, 0.0F);
                    blockTransformation(event.matrix);
                    Matrix.rotateM(event.matrix, 0, convertedProgress * 35.0F / 2.0F, 0.0F, 1.0F, 1.5F);
                    Matrix.rotateM(event.matrix, 0, -convertedProgress * 135.0F / 4.0F, 1.0f, 1.0F, 0.0F);
                    break;
                case "突击":
                    float spin = (float) Math.sin(Math.sqrt(swingProgress) * (float) Math.PI);
                    Matrix.translateM(event.matrix, 0, 0.6f, 0.3f, -0.6f + -spin * 0.7f);
                    Matrix.rotateM(event.matrix, 0, 6090, 0.0f, 0.0f, 0.1f);
                    Matrix.rotateM(event.matrix, 0, 6085, 0.0f, 0.1f, 0.0f);
                    Matrix.rotateM(event.matrix, 0, 6110, 0.1f, 0.0f, 0.0f);
                    transformFirstPersonItem(0, event.matrix);
                    blockTransformation(event.matrix);
                    break;
                case "突击2":
                    spin = (float) Math.sin(Math.sqrt(swingProgress) * (float) Math.PI);
                    transformFirstPersonItem(0, event.matrix);
                    Matrix.translateM(event.matrix, 0, 0, 0.2f, -spin * 0.7f);
                    Matrix.rotateM(event.matrix, 0, 210f, 0f, 0.0f, 1.0f);
                    blockTransformation(event.matrix);
                    break;
                case "下拉":
                    transformFirstPersonItem(0, event.matrix);
                    Matrix.rotateM(event.matrix, 0, -convertedProgress * 31.0F, 1.0F, 0.0F, 2.0F);
                    Matrix.rotateM(event.matrix, 0, -convertedProgress * 33.0F, 1.5F, (convertedProgress / 1.1F), 0.0F);
                    blockTransformation(event.matrix);
                    break;
                case "欣欣":
                    transformFirstPersonItem(0, event.matrix);
                    Matrix.rotateM(event.matrix, 0, -(System.currentTimeMillis() / 2 % 360), 0, 1, 0.0F);
                    break;
                case "安拉":
                    Matrix.scaleM(event.matrix, 0, 0.6f, 0.6f, 0.6f);
                    Matrix.translateM(event.matrix, 0, 0, -convertedProgress * 0.1f, 0);
                    transformFirstPersonItem(swingProgress, event.matrix);
                    blockTransformation(event.matrix);
                    break;
            }
            endTransformation(event.matrix);
        }
    }



}
