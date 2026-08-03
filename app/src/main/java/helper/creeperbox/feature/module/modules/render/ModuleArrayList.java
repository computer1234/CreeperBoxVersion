package helper.creeperbox.feature.module.modules.render;


import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnable;
import static helper.creeperbox.utils.render.MatrixUtil.mvpMatrix;

import android.graphics.Color;
import android.opengl.GLES20;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import helper.creeperbox.clickgui.ClickGUIRenderer;
import helper.creeperbox.clickgui.component.BasicComponent;
import helper.creeperbox.clickgui.font.CustomFontRenderer;
import helper.creeperbox.clickgui.font.Fonts;
import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.utils.render.ColorUtil;
import helper.creeperbox.utils.render.Render2DUtil;
import helper.creeperbox.utils.render.StencilUtil;
import helper.creeperbox.utils.render.animation.Animation;
import helper.creeperbox.utils.render.shader.ShaderProgram;
import helper.creeperbox.utils.render.shader.ShaderUtil;

@ModuleInfo(name = "功能列表", category = Category.Render)
public class ModuleArrayList extends Module {

    public static ArrayListComponent component;

    public static int[] colors = {0xFFDA18A9,0xFFCA2829,0xFFF08102,0xFFFFCC00,0xFF67CA55,0xFF21A9CC,0xFF6335CE};

    private static CustomFontRenderer font = Fonts.getFontRenderer("pianfang.ttf",35);

    public ModuleArrayList(){
        component = new ArrayListComponent(this);
    }

    public static void draw(){
        if(component.parent.isEnable()){
            component.draw();
        }
    }

    class ArrayListComponent extends BasicComponent {

        private ModuleArrayList parent;
        public ArrayListComponent(ModuleArrayList parent) {
            super(0, 0);
            this.parent = parent;
        }

        @Override
        public void measure() {
            float maxWidth = 0f;
            float textHeight = 40f;
            float startY = y + 20f;
            for (Module module : CreeperBox.INSTANCE.getModuleManager().getRegisteredModule()) {
                Animation anim = module.animation;
                anim.run(module.isEnable()?1f:0f);
                if(!module.isEnable() && anim.isFinished()) continue;
                float width = font.getStringWidth(module.hasTag()?module.getName()+" "+module.getTag():module.getName());
                if(maxWidth<width){
                    maxWidth = width;
                }
                startY+=textHeight;
            }
            this.width = maxWidth + 20f;
            this.height = startY;

            this.x = ClickGUIRenderer.width - maxWidth - 40f;
        }

        @Override
        public void render() {

            float textHeight = 40f;
            float startY = y+20f;
            int count = 0;
            List<Module> list = CreeperBox.INSTANCE.getModuleManager().getRegisteredModule().stream()
                    .filter(module -> module.isEnable() || !module.animation.isFinished())
                    .sorted(Comparator.comparingDouble(module -> -1 * font.getStringWidth(module.hasTag()?module.getName()+" "+module.getTag():module.getName())))
                    .collect(Collectors.toList());

            ArrayList<Float> widthList = new ArrayList<>();
            for (Module module : list) {
                widthList.add(font.getStringWidth(module.hasTag()?module.getName()+" "+module.getTag():module.getName()));
            }
            float startPercent = 0f;
            float percentStep = 0.05f;
            for (Module module : list) {
                Animation anim = module.animation;
                float width = widthList.get(count);
                float nextWidth = 0f;
                if(count+1 < widthList.size()) nextWidth = widthList.get(count+1);

                float margin = 8f;
                float startX;
                if(!anim.isFinished()){
                    startX = x+(1-anim.getValue())*width;
                    Render2DUtil.scissorStart(x,startY,this.width+margin,textHeight+5f);
                } else {
                    startX = x;
                }

                int textColor1 = getGradientColor(startPercent);
                int textColor2 = getGradientColor(startPercent+percentStep);

                //background
                if(list.size() == 1) Render2DUtil.rectangle(startX+this.width - width - 20f,startY,width+20f,textHeight, 0xD8000000);
                else if(width-nextWidth < 10) Render2DUtil.rectangle(startX+this.width - width - 20f,startY,width+20f,textHeight, 0xD8000000);
                else if(count == 0) Render2DUtil.drawRoundOld(startX+this.width - width - 20f,startY,width+20f,textHeight, 0f,0f,10f,10f,0xD8000000);
                else Render2DUtil.drawRoundOld(startX+this.width - width - 20f,startY,width+20f,textHeight, 0f,0f,0f,10f,0xD8000000);

                //left padding
                StencilUtil.initStencil();
                StencilUtil.writeStencilBuffer();
                Render2DUtil.drawGradientRoundOld(this.x+this.width - 22f,y+20f,30f,height-20f,10f, Color.WHITE,Color.WHITE,true);
                StencilUtil.readStencilBuffer();
                Render2DUtil.drawGradientRoundOld(x+this.width,startY,margin,textHeight,0f,textColor1,textColor2,true);
                StencilUtil.unUseStencil();


//                float finalStartY = startY;
//                ClickGUIRenderer.bloomUtil.addTask(()->{
//                    font.drawGradientString(module.getName(), startX+this.width - width - 10f, finalStartY + font.getMiddle(textHeight),textColor1,textColor2,false,true);
//                    font.drawString(" "+module.getTag(),startX+this.width-width-10f+font.getStringWidth(module.getName()), finalStartY + font.getMiddle(textHeight),0xFFABABAB);
//                });

                font.drawGradientString(module.getName(), startX+this.width - width - 10f,startY+ font.getMiddle(textHeight),textColor1,textColor2,false,true);
                font.drawString(" "+module.getTag(),startX+this.width-width-10f+font.getStringWidth(module.getName()),startY+ font.getMiddle(textHeight),0xFFABABAB);
                startY += textHeight*anim.getValue();
                count++;
                startPercent+=percentStep;
                if(!anim.isFinished()){
                    Render2DUtil.scissorEnd();
                }

            }
        }
    }





    public static int getGradientColor(float basePercent) {
        int timeCycle = 10000;
        int colorCount = colors.length;

        float timePercent = (System.currentTimeMillis() % timeCycle) / (float)timeCycle;

        float dynamicPosition = (basePercent + timePercent) * colorCount;

        dynamicPosition %= colorCount;

        int currentIndex = (int)dynamicPosition;
        int nextIndex = (currentIndex + 1) % colorCount;

        float t = dynamicPosition - currentIndex;
        return ColorUtil.interpolateColor(colors[currentIndex], colors[nextIndex], t);
    }



}
