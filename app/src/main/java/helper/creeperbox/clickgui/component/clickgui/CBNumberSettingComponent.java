package helper.creeperbox.clickgui.component.clickgui;

import androidx.core.math.MathUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

import helper.creeperbox.clickgui.component.FixedComponent;
import helper.creeperbox.clickgui.component.interfaces.TouchListener;
import helper.creeperbox.clickgui.font.CustomFontRenderer;
import helper.creeperbox.clickgui.font.Fonts;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.utils.mc.StopWatch;
import helper.creeperbox.utils.render.Render2DUtil;
import helper.creeperbox.utils.render.animation.Animation;
import helper.creeperbox.utils.render.animation.Easing;

public class CBNumberSettingComponent extends CBSettingComponent {
    private NumberValue value;
    private CBNumberSliderSettingComponent slider;
    private boolean decimal;
    private static CustomFontRenderer font = Fonts.getFontRenderer("pingfang-thin.ttf",25);
    private static CustomFontRenderer font2 = Fonts.getFontRenderer("pingfang-thin.ttf",40);

    private StopWatch pressWatch = new StopWatch();
    private boolean pressUp;
    private boolean pressDown;
    public CBNumberSettingComponent(NumberValue value) {
        super(50f);
        this.value = value;
        addChildComponent(slider = new CBNumberSliderSettingComponent(this));
        decimal = value.getDecimalPlaces().doubleValue()%1!=0;

        addTouchListener(new TouchListener() {
            @Override
            public boolean onPress(float posX, float posY) {
                if(inside(x+603f,y+5f,30f,40f,posX,posY)){
                    pressWatch.reset();
                    value.setValue(value.getCurrentValue().doubleValue()+value.getDecimalPlaces().doubleValue());
                    pressUp = true;
                }else if(inside(x+673f,y+5f,30f,40f,posX,posY)){
                    pressWatch.reset();
                    value.setValue(value.getCurrentValue().doubleValue()-value.getDecimalPlaces().doubleValue());
                    pressDown = true;
                }
                return true;
            }

            @Override
            public void onRelese(float x, float y) {
                pressUp = false;
                pressDown = false;
            }

            @Override
            public boolean onMove(float posX, float posY) {
                return true;
            }
        });

    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Override
    public void measure() {
        slider.setPos(x+160f,y+5f);

        if(pressWatch.finished(100)){
            pressWatch.reset();
            if(pressUp){
                value.setValue(value.getCurrentValue().doubleValue()+value.getDecimalPlaces().doubleValue());
            }else if(pressDown){
                value.setValue(value.getCurrentValue().doubleValue()-value.getDecimalPlaces().doubleValue());
            }
        }
    }

    private static boolean inside(float x,float y,float width,float height,float posX,float posY){
        return posX >= x && posX <= x+width && posY >= y && posY <= y+height;
    }


    @Override
    public void render() {
        String v = decimal?String.valueOf(round(value.getCurrentValue().doubleValue(),2)):String.valueOf(value.getCurrentValue().intValue());
        String text = value.getName()+v;
        float textWidth = font.getStringWidth(text);
        font.drawString(text,x+140f-textWidth,y+font.getMiddle(height),0xffa9a9a9);
        slider.render();
        font2.drawString("+",x+606f,y+font2.getMiddle(height),0xffffffff);
        font2.drawString("|",x+646f,y+font2.getMiddle(height),0xffa9a9a9);
        font2.drawString("-",x+676f,y+font2.getMiddle(height),0xffffffff);
    }

    private class CBNumberSliderSettingComponent extends FixedComponent {
        private CBNumberSettingComponent parent;
        private Animation moveAnimation = new Animation(Easing.SMOOTH,200);
        public CBNumberSliderSettingComponent(CBNumberSettingComponent parent) {
            super(0,0,426f,40f);
            this.parent = parent;

            this.addTouchListener(new TouchListener() {
                @Override
                public boolean onPress(float x, float y) {
                    onMove(x,y);
                    return false;
                }

                @Override
                public void onRelese(float x, float y) {
                }

                @Override
                public boolean onMove(float posX, float posY) {
                    posX = MathUtils.clamp(posX,x,x+width);
                    float percentage = (posX - x)/width;
                    parent.value.setValue(percentage*(parent.value.getMax().floatValue() - parent.value.getMin().floatValue())+parent.value.getMin().floatValue());
                    return false;
                }
            });
        }

        @Override
        public void render() {
            float widthPercentage = (parent.value.getCurrentValue().floatValue() - parent.value.getMin().floatValue()) / (parent.value.getMax().floatValue() - parent.value.getMin().floatValue());
            moveAnimation.run(widthPercentage);
            float value = moveAnimation.getValue();
            Render2DUtil.rectangle(x,y+17f,426f,6f,0xffa0a0a0);
            Render2DUtil.circle(x+width*value,y+20f,10f,0xffa0a0a0);
        }
    }
}
