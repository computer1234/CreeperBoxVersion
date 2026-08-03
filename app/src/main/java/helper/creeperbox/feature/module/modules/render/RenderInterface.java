package helper.creeperbox.feature.module.modules.render;

import android.graphics.Color;

import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.utils.render.ColorUtil;

@ModuleInfo(name = "调色盘", category = Category.Render)
public class RenderInterface extends Module {
    private NumberValue color1R = new NumberValue("颜色一R",this,231,0,255,1);
    private NumberValue color1G = new NumberValue("颜色一G",this,169,0,255,1);
    private NumberValue color1B = new NumberValue("颜色一B",this,255,0,255,1);


    private NumberValue color2R = new NumberValue("颜色二R",this,147,0,255,1);
    private NumberValue color2G = new NumberValue("颜色二G",this,185,0,255,1);
    private NumberValue color2B = new NumberValue("颜色二B",this,255,0,255,1);
    private final NumberValue colorSpeed = new NumberValue("渐变速度", this,18,2, 54, 1);


    private static RenderInterface INSTANCE;
    public RenderInterface(){
        INSTANCE = this;
    }



    public static int getColor1(){
        return Color.rgb(INSTANCE.color1R.getCurrentValue().intValue(),INSTANCE.color1G.getCurrentValue().intValue(),INSTANCE.color1B.getCurrentValue().intValue());
    }

    public static int getColor2() {
        return Color.rgb(INSTANCE.color2R.getCurrentValue().intValue(),INSTANCE.color2G.getCurrentValue().intValue(),INSTANCE.color2B.getCurrentValue().intValue());
    }

    public static int applyColor(int count){
        int color1 = getColor1();
        int color2 = getColor2();
        return doubleColor(color1,color2,INSTANCE.colorSpeed.getCurrentValue().doubleValue(),count);
    }

    public static int doubleColor(int cl1, int cl2, double speed, double count) {
        int angle = (int) (((System.currentTimeMillis()) / speed + count) % 360);
        angle = (angle >= 180 ? 360 - angle : angle) * 2;
        return ColorUtil.interpolateColor(cl1, cl2, angle / 360f);
    }


}
