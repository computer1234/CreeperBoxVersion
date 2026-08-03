package helper.creeperbox.feature.module.modules.render;

import android.graphics.Color;
import android.opengl.GLES20;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.component.RenderHelperComponent;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.PostRender3DEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.BooleanValue;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.sdk.entity.type.EntityActor;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.entity.type.EntityServerPlayer;
import helper.creeperbox.sdk.math.Vec3f;
import helper.creeperbox.utils.render.Render2DUtil;

@ModuleInfo(name = "拖尾", category = Category.Render)
public class Trail extends Module {


    public BooleanValue self = new BooleanValue("自己",this,true);
    public BooleanValue other = new BooleanValue("其他人",this,true);
    public NumberValue fadeTime = new NumberValue("消失时间",this, 300, 100, 1000, 1);
    public HashMap<Long, List<Rect>> map = new HashMap<Long,List<Rect>>();

    @SubscribeEvent
    public void onRender3D(PostRender3DEvent event) {
        if(RenderHelperComponent.render3DData==null) return;

        boolean usingDepth = GLES20.glIsEnabled(GLES20.GL_DEPTH_TEST);
        boolean usingCull = GLES20.glIsEnabled(GLES20.GL_CULL_FACE);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDisable(GLES20.GL_CULL_FACE);

        float partialTicks = RenderHelperComponent.render3DData.partialTicks;
        float[] mvpMatrix = RenderHelperComponent.render3DData.mvpMatrix.m;

        EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
        if(player == null) return;

        for(EntityActor p : player.getLevel().getPlayers()){

            if(!self.getCurrentValue() && p instanceof EntityLocalPlayer) continue;
            if(!other.getCurrentValue() && p instanceof EntityServerPlayer) continue;


            List<Rect> list;
            Vec3f playerPos = p.getPos();
            Vec3f oldPos = p.getPosPrev();
            float x = oldPos.x + (playerPos.x - oldPos.x) * partialTicks;
            float y = oldPos.y + (playerPos.y - oldPos.y) * partialTicks;
            float z = oldPos.z + (playerPos.z - oldPos.z) * partialTicks;

            long rid = p.getRuntimeID();
            if(!map.containsKey(rid)){
                list = new ArrayList<Rect>();
                list.add(new Rect(x,y-1.6f,z));
                map.put(rid,list);
            }else{
                list = map.get(rid);
                list.add(new Rect(x,y-1.6f,z));
                map.put(rid,list);
            }
        }

        Vec3f cameraPos = RenderHelperComponent.render3DData.cameraPos;


        map.forEach(new BiConsumer<Long, List<Rect>>() {
            @Override
            public void accept(Long runtimeID, List<Rect> value) {

                float[] d = new float[value.size()*2*(3+4)];

                int i = 0;
                for (Rect r : new ArrayList<>(value)) {

                    float alpha = (float) (1-smoothStep(r.getTime(),r.getTime()+fadeTime.getCurrentValue().intValue(),System.currentTimeMillis()));

                    if(alpha <= 0){
                        value.remove(r);
                    }

                    float ratio = (float) (i+1) /value.size();
                    int mixed = RenderInterface.applyColor((int) (ratio*360f));
                    int start = i*2*(3+4);

                    d[start] = r.getX() - cameraPos.x;
                    d[start+1] = r.getY() - cameraPos.y;
                    d[start+2] = r.getZ() - cameraPos.z;
                    d[start+3] = Color.red(mixed)/255f;
                    d[start+4] = Color.green(mixed)/255f;
                    d[start+5] = Color.blue(mixed)/255f;
                    d[start+6] = ratio;


                    d[start+7] = r.getX() - cameraPos.x;
                    d[start+8] = r.getY() - cameraPos.y + 1.6f;
                    d[start+9] = r.getZ() - cameraPos.z;
                    d[start+10] = Color.red(mixed)/255f;
                    d[start+11] = Color.green(mixed)/255f;
                    d[start+12] = Color.blue(mixed)/255f;
                    d[start+13] = ratio;

                    i++;
                }

                map.put(runtimeID,value);


                RenderHelperComponent.trailProgram.useShader();
                int pos = GLES20.glGetUniformLocation(RenderHelperComponent.trailProgram.getProgramId(), "u_MVPMatrix");
                GLES20.glUniformMatrix4fv(pos, 1, false, mvpMatrix, 0);

                int[] vbo = new int[1];
                GLES20.glGenBuffers(1, vbo, 0);
                GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
                FloatBuffer vertexBuffer = Render2DUtil.getFloatBuffer(d);
                GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GLES20.GL_DYNAMIC_DRAW);
                GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);


                GLES20.glEnableVertexAttribArray(0);
                GLES20.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, 7*4, 0);


                GLES20.glEnableVertexAttribArray(1);
                GLES20.glVertexAttribPointer(1, 4, GLES20.GL_FLOAT, false, 7*4, 3*4);


                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, value.size()*2);
                GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
                GLES20.glDeleteBuffers(1, vbo, 0);
                RenderHelperComponent.trailProgram.unUseShader(false);
            }
        });

        if(!usingDepth){
            GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        }
        if(usingCull){
            GLES20.glEnable(GLES20.GL_CULL_FACE);
        }

    }


    public double smoothStep(double edge0, double edge1, double x) {
        double t = clamp((x - edge0) / (edge1 - edge0), 0.0, 1.0);
        return t * t * (3.0 - 2.0 * t);
    }
    public double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(value, max));
    }


    public static class Rect {
        private final float x,y,z;

        private long time;


        public Rect(double x, double y, double z) {
            this.x = (float) x;
            this.y = (float) y;
            this.z = (float) z;
            this.time = System.currentTimeMillis();
        }
        public long getTime(){
            return time;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public float getZ() {
            return z;
        }
    }

}
