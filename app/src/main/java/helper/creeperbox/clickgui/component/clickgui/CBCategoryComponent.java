package helper.creeperbox.clickgui.component.clickgui;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

import helper.creeperbox.clickgui.component.FixedComponent;
import helper.creeperbox.clickgui.component.interfaces.TouchListener;
import helper.creeperbox.clickgui.font.CustomFontRenderer;
import helper.creeperbox.clickgui.font.Fonts;
import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.utils.render.ColorUtil;
import helper.creeperbox.utils.render.Render2DUtil;
import helper.creeperbox.utils.render.StencilUtil;
import helper.creeperbox.utils.render.animation.Animation;
import helper.creeperbox.utils.render.animation.Easing;

public class CBCategoryComponent extends FixedComponent {
    private boolean isMainMenu;
    private int index;
    private Category category;
    private String name;

    private float downY,tempY;
    private static final float slideSlop = 30f;

    private float scrollY;
    public boolean isScroll;
    private Animation splashAnim = new Animation(Easing.EASE_OUT_EXPO,500);

    private static CustomFontRenderer font = Fonts.getFontRenderer("pingfang-medium.ttf",40);

    private static CustomFontRenderer thinFont = Fonts.getFontRenderer("pingfang-thin.ttf",25);

    private List<CBModuleComponent> modules = new ArrayList<>();


    public CBCategoryComponent(Category category,String name,int index) {
        super(0,0,778,750);
        this.isMainMenu = false;
        this.index = index;
        this.category = category;
        this.name = name;
        for(Module m : CreeperBox.INSTANCE.getModuleManager().getModule(category)){
            CBModuleComponent c = new CBModuleComponent(m,this);
            addChildComponent(c);
            this.modules.add(c);
        }

        addTouchListener(new TouchListener() {
            @Override
            public boolean onPress(float x, float y) {
                isPress = true;
                downY = tempY = y - CBCategoryComponent.this.y;
                return false;
            }

            @Override
            public void onRelese(float x, float y) {
                isPress = false;
                isScroll = false;
            }

            @Override
            public boolean onMove(float x, float y) {
                if(!isPress) return true;
                float deltaY = tempY - (y - CBCategoryComponent.this.y);
                tempY = y - CBCategoryComponent.this.y;

                if(Math.abs(downY+CBCategoryComponent.this.y-y)>slideSlop){
                    isScroll = true;
                    scrollY += deltaY;
                    return false;
                }
                return true;
            }
        });

    }

    public CBCategoryComponent(int index,String name){
        super(0,0,778,750);
        this.isMainMenu = true;
        this.index = index;
        this.name = name;
    }

    public void toggle(){
        splashAnim = new Animation(Easing.EASE_OUT_EXPO,500);
    }
    private float moduleHeight;


    @Override
    public void render() {
        splashAnim.run(1f);
        float progress = splashAnim.getValue();
        int alpha = (int)(255*progress);
        Render2DUtil.startScale(x+width/2f,y+height/2f,0.8f+0.2f*progress);
        drawPanel(alpha);
        Render2DUtil.endScale();
    }

    @Override
    public void measure() {

        if(!isMainMenu){
            scrollY = Math.min(Math.max(0,scrollY),Math.max(moduleHeight-height,0));
            moduleHeight = 80f;
            float x = this.x + 19.25f;
            float y = this.y + 80f - scrollY;
            int index = 0;
            for(CBModuleComponent c : modules){
                c.setPos(x,y);
                x+=252.25f;
                index++;
                if(index == 1){
                    moduleHeight += 252.25f;
                }
                if(index == 3){
                    index = 0;
                    y += 252.25f;
                    x = this.x + 19.25f;
                }
            }
        }
    }

    public static String timeLeft = "2025-1-32";

    private void drawPanel(int alpha){

        font.drawString(this.name,this.x+15f,y+15f,Color.argb(alpha,255,255,255));

        if(isMainMenu){
            Render2DUtil.drawRound(x+20f,y+100f,733f,180f,30f, ColorUtil.interpolateColor(0x0,0xc0121212,alpha/255f));
            font.drawString("Galaxy",x+50f,y+120f,Color.argb(alpha,255,255,255));
            thinFont.drawString("欢迎使用Galaxy,此作品仅供学习,请勿用于任何犯罪用途",x+50f,y+165f,Color.argb(alpha,255,255,255));
            thinFont.drawString("使用者的任何行为与开发者无关",x+50f,y+195f,Color.argb(alpha,255,255,255));
            String text = "您的卡密将于"+timeLeft+"到期 |";
            float textWidth = thinFont.getStringWidth(text);
            thinFont.drawString(text,x+width-20f-textWidth,y+290f,Color.argb(alpha,255,255,255));
        }else {
            StencilUtil.initStencil();
            StencilUtil.writeStencilBuffer();
            Render2DUtil.drawRound(x-482f,y+80f,width+482f,height-80f,0,90f,0f,90f,Color.WHITE);
            StencilUtil.readStencilBuffer();
            for(CBModuleComponent c : modules){
                c.alpha = alpha;
                c.render();
            }
            StencilUtil.unUseStencil();
        }
    }


}
