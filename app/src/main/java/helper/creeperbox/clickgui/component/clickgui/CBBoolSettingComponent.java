package helper.creeperbox.clickgui.component.clickgui;

import helper.creeperbox.clickgui.font.CustomFontRenderer;
import helper.creeperbox.clickgui.font.Fonts;
import helper.creeperbox.feature.settings.BooleanValue;
import helper.creeperbox.utils.render.ColorUtil;
import helper.creeperbox.utils.render.Render2DUtil;
import helper.creeperbox.utils.render.animation.Animation;
import helper.creeperbox.utils.render.animation.Easing;

public class CBBoolSettingComponent extends CBSettingComponent {

    private Animation toggleAnimation = new Animation(Easing.Decelerate,500);
    private static CustomFontRenderer font = Fonts.getFontRenderer("pingfang-thin.ttf",30);

    private BooleanValue value;
    public CBBoolSettingComponent(BooleanValue value) {
        super(60f);
        this.value = value;
    }

    private static boolean inside(float x,float y,float width,float height,float posX,float posY){
        return posX >= x && posX <= x+width && posY >= y && posY <= y+height;
    }


    @Override
    public void onClick(float x, float y, long time) {
        if(inside(this.x+width-150f,this.y+5f,95f,50f,x,y)){
            value.setValue(!value.getCurrentValue());
        }
    }


    @Override
    public void render() {
        font.drawString(value.getName(),x+40f,y+font.getMiddle(height), 0xffa9a9a9);
        float fromX = this.x + this.width - 140f;
        float toX = this.x + this.width - 100f;
        int fromColor = 0xffc8c4c0;
        int toColor = 0xffffffff;
        int fromColor2 = 0xff3b3934;
        int toColor2 = 0xff6f6f6f;
        toggleAnimation.run(value.getCurrentValue()?1f:0f);
        float value = toggleAnimation.getValue();
        float x = fromX + (toX - fromX) * value;
        Render2DUtil.drawRound(this.x+width-150f,y+5f,95f,50f,15, ColorUtil.interpolateColor(fromColor2,toColor2,value));
        Render2DUtil.drawRound(x,y+12.5f,35f,35f,10f, ColorUtil.interpolateColor(fromColor,toColor,value));
    }

}
