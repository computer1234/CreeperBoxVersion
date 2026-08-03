package helper.creeperbox.feature.module.modules.render;

import static android.opengl.GLES10.glBindTexture;
import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_NEAREST;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_REPEAT;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.GL_VIEWPORT;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGetIntegerv;
import static android.opengl.GLES20.glTexParameteri;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityEventType;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;
import org.cloudburstmc.protocol.bedrock.data.inventory.transaction.InventoryTransactionType;
import org.cloudburstmc.protocol.bedrock.data.skin.ImageData;
import org.cloudburstmc.protocol.bedrock.data.skin.SerializedSkin;
import org.cloudburstmc.protocol.bedrock.packet.EntityEventPacket;
import org.cloudburstmc.protocol.bedrock.packet.InventoryTransactionPacket;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.component.RenderHelperComponent;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.PacketReceiveEvent;
import helper.creeperbox.feature.event.events.PacketSendEvent;
import helper.creeperbox.feature.event.events.Render2DEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.BooleanValue;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.sdk.ClientInstance;
import helper.creeperbox.sdk.entity.Attributes;
import helper.creeperbox.sdk.entity.type.EntityActor;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.entity.type.EntityServerPlayer;
import helper.creeperbox.sdk.item.ItemStack;
import helper.creeperbox.sdk.math.Vec4f;
import helper.creeperbox.sdk.render.UIRenderContext;
import helper.creeperbox.utils.math.MathUtil;
import helper.creeperbox.utils.math.RandomUtil;
import helper.creeperbox.utils.mc.StopWatch;
import helper.creeperbox.utils.render.ColorUtil;
import helper.creeperbox.utils.render.MatrixUtil;
import helper.creeperbox.utils.render.Render2DUtil;
import helper.creeperbox.utils.render.animation.Animation;
import helper.creeperbox.utils.render.animation.Easing;
import helper.creeperbox.utils.render.shader.ShaderProgram;

@ModuleInfo(name = "人物面板渲染", category = Category.Render)
public class TargetHud extends Module {

    private final BooleanValue follow = new BooleanValue("跟随",this,false);
    private final NumberValue xPercent = new NumberValue("左右比例", this, 0.5f, 0f, 1f, 0.01f);
    private final NumberValue yPercent = new NumberValue("上下比例", this, 0.5f, 0f, 1f, 0.01f);


    public int darker(int color) {
        return Color.argb(Color.alpha(color),Math.max((int)(Color.red(color) * 0.7), 0),
                Math.max((int)(Color.green(color) * 0.7), 0),
                Math.max((int)(Color.blue(color) * 0.7), 0));
    }

    public Animation healthAnimation = new Animation(Easing.EASE_IN_OUT_QUAD, 300);

    public static Bitmap createBitmapFromRGBA(byte[] rgba, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(rgba));
        return bitmap;
    }
    public static HashMap<Long,Integer> hurtMap = new HashMap<>();
    @SubscribeEvent
    public void handleHurt(PacketReceiveEvent event){
        if(event.getPacket() instanceof EntityEventPacket){
            EntityEventPacket packet = (EntityEventPacket) event.getPacket();
            if(packet.getType() == EntityEventType.HURT) hurtMap.put(packet.getRuntimeEntityId(),10);
        }
    }

    @SubscribeEvent
    public void onHurtCheck(TickEvent event){
        Iterator<Map.Entry<Long, Integer>> iterator = hurtMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, Integer> entry = iterator.next();
            Long key = entry.getKey();
            int hurtTime = entry.getValue();
            if (hurtTime <= 0) {
                iterator.remove();
            } else {
                hurtMap.put(key, hurtTime - 1);
            }
        }
    }

    @Override
    public void onEnable() {
        targetWatch.reset();
    }

    public final ArrayList<Particle2D> particles = new ArrayList<>();
    public long attackEid;
    private static final Pattern COLOR_CODE_PATTERN = Pattern.compile("§[0-9a-fk-or]");

    public static String formatName(String text){
        return COLOR_CODE_PATTERN.matcher(text).replaceAll("");
    }

    public StopWatch targetWatch = new StopWatch();
    private Animation openingAnimation = new Animation(Easing.EASE_OUT_ELASTIC, 1000);


    @SubscribeEvent
    public void renderSkin(Render2DEvent event){

        EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
        if(player == null || RenderHelperComponent.render3DData == null) return;

        EntityActor target = null;
        boolean finish = targetWatch.finished(1000);
        openingAnimation.run(finish ? 0f : 1f);

        if(finish && openingAnimation.isFinished()) return;

        for(EntityActor actor : player.getLevel().getPlayers()){
            if(actor.getRuntimeID() == attackEid){
                target = actor;
            }
        }


        if(target!=null){

            Vec4f position = RenderHelperComponent.projectionsData.get(attackEid);
            if(position == null) return;
            EntityServerPlayer serverPlayer = new EntityServerPlayer(target.getCppPointer());
            SerializedSkin skin = serverPlayer.getSkin();
            ImageData imageData = skin.getSkinData();
            if(imageData.getHeight()==0 || imageData.getWidth() == 0){
                skin = player.getSkin();
                imageData = skin.getSkinData();
            }

            Bitmap bitmap = createBitmapFromRGBA(imageData.getImage(),imageData.getWidth(),imageData.getHeight());
            int texture = createSkinTexture(bitmap);
            bitmap.recycle();
            float width = 320;
            float height = 120;

            float startX = 0f;
            float startY = 0f;
            if(follow.getCurrentValue()){
                startX = position.z;
                startY = position.w - (position.w - position.y) / 2 - height / 2f;
            }else {
                int[] viewport = new int[4];
                glGetIntegerv(GL_VIEWPORT, viewport, 0);
                startX = viewport[2]*xPercent.getCurrentValue().floatValue();
                startY = viewport[3]*yPercent.getCurrentValue().floatValue();
            }

            float skinMargin = 5f;
            float skinWidth = height-skinMargin*2;
            float radius = 23f;
            float scale = Math.max(0f,Math.min(1f,openingAnimation.getValue()));
            Render2DUtil.startScale(startX+width/2f,startY+height/2f,scale);
            glDisable(GL_DEPTH_TEST);

            float finalStartX = startX;
            float finalStartY = startY;
            RenderHelperComponent.bloomUtil.addTask(new Runnable() {
                @Override
                public void run() {
                    Render2DUtil.startScale(finalStartX + width/2f, finalStartY +height/2f,scale);
                    RenderHelperComponent.drawGradientRound(finalStartX, finalStartY,width,height,radius,RenderInterface.applyColor(270),RenderInterface.applyColor(0),RenderInterface.applyColor(180),RenderInterface.applyColor(90));
                    Render2DUtil.endScale();
                }
            });

            RenderHelperComponent.drawGradientRound(startX,startY,width,height,radius,0x80000000,0x80000000,false);

            int hurtTime = hurtMap.getOrDefault(serverPlayer.getRuntimeID(),0);
            renderHead(texture,startX+skinMargin+hurtTime*0.7f,startY+skinMargin+hurtTime*0.7f,skinWidth-hurtTime*1.4f,skinWidth-hurtTime*1.4f,radius, ColorUtil.interpolateColor(Color.WHITE,Color.RED,hurtTime/10f), MatrixUtil.mvpMatrix());
            if(hurtTime!=0){
                for(int i = 0 ; i<1 ; i++){
                    if(System.currentTimeMillis()%2 == 0){
                        particles.add(new Particle2D(0,0, (float) RandomUtil.getRandom(-10f,10f), (float) RandomUtil.getRandom(-10f,10f),30, RenderInterface.applyColor((int) RandomUtil.getRandom(1,228))));
                    }
                }
            }

            particles.removeIf(new Predicate<Particle2D>() {
                @Override
                public boolean test(Particle2D particleBase) {
                    return particleBase.update();
                }
            });

            if(!particles.isEmpty()) {
                glEnable(GL_BLEND);
                glBlendFunc(GL_SRC_ALPHA, GL_ONE);
                for (Particle2D p : particles) {
                    RenderHelperComponent.imageXY(RenderHelperComponent.fireFlyTexture, startX + skinMargin + skinWidth / 2f + p.x - p.size / 2f, startY + skinMargin + skinWidth / 2f + p.y - p.size / 2f, 0, p.size, p.size, ColorUtil.applyOpacity(p.color, p.opacity), MatrixUtil.mvpMatrix());
                }
                glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);
            }

            RenderHelperComponent.pingfang20.drawString(formatName(serverPlayer.getNameTag()),startX+skinWidth+15,startY+13f,Color.WHITE);

            RenderHelperComponent.loadOpenGLCtx();
            float itemX = startX+skinWidth+15;
            if(Math.abs(1f-scale)<0.1){
                UIRenderContext ctx = event.getCtx();
                renderTargetItem(ctx,serverPlayer.getItemInHand(),itemX,startY+40f);
                for(int i = 0 ; i < 4 ; i++){
                    itemX += 35f*scale;
                    renderTargetItem(ctx,serverPlayer.getArmor(i),itemX,startY+40f);
                }
            }
            RenderHelperComponent.saveOpenGLCtx();

            glDisable(GL_DEPTH_TEST);
            RenderHelperComponent.drawGradientRound( startX+skinWidth+15, startY+80f, width-skinWidth-30f, 30, 10f, darker(darker(RenderInterface.applyColor(0))),darker(darker(darker(darker(RenderInterface.applyColor(270))))),true);
            float percent = Math.min(1f,target.getAttributeCurrentValue(Attributes.HEALTH)/target.getAttributeMaxValue(Attributes.HEALTH));;
            healthAnimation.run(percent);
            RenderHelperComponent.drawGradientRound( startX+skinWidth+15, startY+80f, (width-skinWidth-30f)*healthAnimation.getValue(), 30, 10f, RenderInterface.applyColor(0),RenderInterface.applyColor(270),false);
            Render2DUtil.endScale();

        }

    }




    public void renderTargetItem(UIRenderContext ctx, ItemStack item, float x, float y){

        float ratio = ClientInstance.getGuiScale();
        float def = 5/ratio;
        float scale = 0.4f * def;
        ItemData data = item.getItemData();

        if (!data.isValid()) return;
        boolean ench = data.getTag()!=null && !data.getTag().getList("ench", NbtType.COMPOUND).isEmpty();

        ctx.renderItem(item,x/ratio,y/ratio,1f,1f,scale,ench);

    }

    public static int hsvToArgb(float hue, float saturation, float value, int alpha) {
        int i = (int)(hue * 6.0F) % 6;
        float f = hue * 6.0F - (float)i;
        float g = value * (1.0F - saturation);
        float h = value * (1.0F - f * saturation);
        float j = value * (1.0F - (1.0F - f) * saturation);
        float k;
        float l;
        float m;
        switch (i) {
            case 0:
                k = value;
                l = j;
                m = g;
                break;
            case 1:
                k = h;
                l = value;
                m = g;
                break;
            case 2:
                k = g;
                l = value;
                m = j;
                break;
            case 3:
                k = g;
                l = h;
                m = value;
                break;
            case 4:
                k = j;
                l = g;
                m = value;
                break;
            case 5:
                k = value;
                l = g;
                m = h;
                break;
            default:
                throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + hue + ", " + saturation + ", " + value);
        }

        return Color.argb(alpha, (int) MathUtil.clamp((int)(k * 255.0F), 0, 255), (int) MathUtil.clamp((int)(l * 255.0F), 0, 255), (int) MathUtil.clamp((int)(m * 255.0F), 0, 255));
    }
    @SubscribeEvent
    public void onAttack(PacketSendEvent event){
        if(event.getPacket() instanceof InventoryTransactionPacket){
            InventoryTransactionPacket packet = (InventoryTransactionPacket) event.getPacket();
            if(packet.getTransactionType() == InventoryTransactionType.ITEM_USE_ON_ENTITY && packet.getActionType() == 1){
                attackEid = packet.getRuntimeEntityId();
                targetWatch.reset();
            }
        }
    }



    public static int createSkinTexture(Bitmap bitmap){
        IntBuffer buffer = IntBuffer.allocate(1);
        glGenTextures(1,buffer);
        int textureID = buffer.get();

        glBindTexture(GL_TEXTURE_2D,textureID);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_S,GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_T,GL_REPEAT);

        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_NEAREST);

        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
        glBindTexture(GL_TEXTURE_2D,0);
        return textureID;
    }

    public void renderHead(int texture,final float x, final float y,final float width, final float height,float radius,int color,float[] mvpMatrix){
        ShaderProgram skinProgram = RenderHelperComponent.skinProgram;
        if(skinProgram == null) return;
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);

        float uStart = 0.125f;
        float vStart = 0.125f;

        float uEnd = 0.25f;
        float vEnd = 0.25f;

        skinProgram.useShader();
        glEnable(GL_TEXTURE_2D);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D,texture);
        skinProgram.setUniformi("uSampler",0);
        skinProgram.setUniformf("u_size",width,height);
        skinProgram.setColor(color);
        skinProgram.setUniformf("u_radius",radius);
        skinProgram.setUniformf("uvStart",uStart,vStart);
        skinProgram.setUniformf("uvEnd",uEnd,vEnd);


        int pos = GLES20.glGetUniformLocation(skinProgram.getProgramId(), "u_MVPMatrix");
        GLES20.glUniformMatrix4fv(pos, 1, false, mvpMatrix, 0);


        float[] texCoords = new float[]{
                uStart, vEnd,
                uEnd, vEnd,
                uEnd, vStart,
                uStart, vStart
        };


        float[] vertices = new float[]{
                x, y + height, 0,
                x + width, y + height, 0,
                x + width, y, 0,
                x, y, 0
        };


        float[] combinedData = new float[vertices.length + texCoords.length];

        for (int i = 0; i < vertices.length / 3; i++) {
            combinedData[i * 5] = vertices[i * 3];
            combinedData[i * 5 + 1] = vertices[i * 3 + 1];
            combinedData[i * 5 + 2] = vertices[i * 3 + 2];
            combinedData[i * 5 + 3] = texCoords[i * 2];
            combinedData[i * 5 + 4] = texCoords[i * 2 + 1];
        }

        int[] vbo = new int[1];
        GLES20.glGenBuffers(1, vbo, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
        FloatBuffer vertexBuffer = Render2DUtil.getFloatBuffer(combinedData);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GLES20.GL_DYNAMIC_DRAW);

        int vPosition = GLES20.glGetAttribLocation(skinProgram.getProgramId(), "vPosition");
        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttribPointer(vPosition, 3, GLES20.GL_FLOAT, false, 5 * 4, 0);

        int texCoordHandle = GLES20.glGetAttribLocation(skinProgram.getProgramId(), "aUv");
        GLES20.glEnableVertexAttribArray(texCoordHandle);
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 5 * 4, 3 * 4);

        glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
        glDisable(GL_TEXTURE_2D);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glDeleteBuffers(1, vbo, 0);
        glBindTexture(GL_TEXTURE_2D,0);
        skinProgram.unUseShader(false);

    }

    public static class Particle2D {
        public float x, y, deltaX, deltaY, size, opacity;
        public int color;

        public int tick;

        public boolean update() {
            x += deltaX;
            y += deltaY;

            deltaY *= 0.95f;
            deltaX *= 0.95f;

            opacity -= 0.01f;
            size /= 1.01f;

            if (opacity < 0.01f)
                opacity = 0.01f;

            tick--;
            return tick<=0;
        }

        public Particle2D(final float x, final float y, final float deltaX, final float deltaY, final float size, final int color) {
            this.x = x;
            this.y = y;
            this.deltaX = deltaX;
            this.deltaY = deltaY;
            this.size = size;
            this.opacity = 1f;
            this.color = color;
            this.tick = 50;
        }

    }
}
