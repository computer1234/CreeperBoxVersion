package helper.creeperbox.clickgui.component.clickgui;

import android.graphics.Color;

import helper.creeperbox.clickgui.ClickGUIRenderer;
import helper.creeperbox.clickgui.component.FixedComponent;
import helper.creeperbox.clickgui.component.interfaces.TouchListener;
import helper.creeperbox.clickgui.font.CustomFontRenderer;
import helper.creeperbox.clickgui.font.Fonts;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.utils.mc.StopWatch;
import helper.creeperbox.utils.render.ColorUtil;
import helper.creeperbox.utils.render.Render2DUtil;
import helper.creeperbox.utils.render.animation.Animation;
import helper.creeperbox.utils.render.animation.Easing;

public class CBModuleComponent extends FixedComponent {
    private Module module;

    public int alpha;

    private Animation animation = new Animation(Easing.Decelerate,500);
    private static CustomFontRenderer font = Fonts.getFontRenderer("pingfang-thin.ttf",28);
    private static CustomFontRenderer font2 = Fonts.getFontRenderer("pingfang-thin.ttf",25);

    private CBCategoryComponent parent;
    private String icon;

    private boolean isPress;
    private StopWatch watch  = new StopWatch();
    public CBModuleComponent(Module module,CBCategoryComponent parent) {
        super(0,0,233f,233f);
        this.module = module;
        this.parent = parent;
        this.icon = "icon/"+module.getName()+".png";

        addTouchListener(new TouchListener() {
            @Override
            public boolean onPress(float x, float y) {
                if(y-parent.getY() > 80){
                    isPress = true;
                    watch.reset();
                }
                return true;
            }

            @Override
            public void onRelese(float x, float y) {
                isPress = false;
            }

            @Override
            public boolean onMove(float x, float y) {
                return true;
            }
        });
    }

    @Override
    public void onClick(float x, float y, long time) {
        if(y-parent.getY() > 80){
            if(!parent.isScroll){
                if(time < 300){
                    module.toggle();
                }
            }
        }
    }


    @Override
    public void render() {

        if(watch.finished(300) && isPress && !parent.isScroll){
            isPress = false;
            ClickGUIRenderer.toggleSetting(module);
            watch.reset();
        }

        boolean isEnable = module.isEnable();
        animation.run(isEnable?1f:0f);
        float value = animation.getValue();
        int color = ColorUtil.interpolateColor(ColorUtil.interpolateColor(0x0,0xd0121212,alpha/255f),Color.argb(alpha,255,255,255),value);
        Render2DUtil.drawRound(x,y,width,height,40f, color);
        int rgb = (int)(255*(1f-value));
        font.drawString(module.getName(),x+20f,y+150f,Color.argb(alpha,rgb,rgb,rgb));
        font2.drawString(isEnable?"开启":"关闭",x+20f,y+186f,Color.argb(alpha,0x88,0x88,0x8C));
        Render2DUtil.startRotation(x+20f,y+20f,60f,60f,-value*45f);
        Render2DUtil.image(icon,x+20f,y+20f,60f,60f,Color.argb(alpha,255,255,255));
        Render2DUtil.endRotation();

    }

}
