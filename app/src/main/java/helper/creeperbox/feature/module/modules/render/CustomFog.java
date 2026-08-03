package helper.creeperbox.feature.module.modules.render;

import android.annotation.SuppressLint;
import android.graphics.Color;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.component.PythonCallerComponent;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.BooleanValue;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.utils.render.ColorUtil;


@ModuleInfo(name = "迷雾渲染", category = Category.Render)
public class CustomFog extends Module {

    private final BooleanValue isStatic = new BooleanValue("静态",this,false);
    private final NumberValue far = new NumberValue("最远距离", this,20,0, 100, 1);
    private final NumberValue near = new NumberValue("最近距离", this,13,0, 100, 1);

    @SuppressLint("DefaultLocale")
    public void setFog(float r, float g, float b, float a, int near, int far) {
        CreeperBox.INSTANCE.runPython(String.format("import mod.client.extraClientApi as clientApi\n" +
                "CompFactory = clientApi.GetEngineCompFactory()\n" +
                "comp = CompFactory.CreateFog(clientApi.GetLevelId())\n" +
                "comp.SetFogColor((%.4f,%.4f,%.4f,%.4f))\n" +
                "comp.SetFogLength(%d, %d)",r,g,b,a,near,far));
    }

    public void resetFog() {
        PythonCallerComponent.addQueue("import mod.client.extraClientApi as clientApi\n" +
                "CompFactory = clientApi.GetEngineCompFactory()\n" +
                "comp = CompFactory.CreateFog(clientApi.GetLevelId())\n" +
                "if comp.GetUseFogColor():\n" +
                "\tcomp.ResetFogColor()\n" +
                "if comp.GetUseFogLength():\n" +
                "\tcomp.ResetFogLength()");
    }


    @SubscribeEvent
    public void onTick(TickEvent event){
        int color = isStatic.getCurrentValue()? ColorUtil.interpolateColor(RenderInterface.getColor1(),RenderInterface.getColor2(),0.5f) :RenderInterface.applyColor(0);
        int far = this.far.getCurrentValue().intValue();
        int near = this.near.getCurrentValue().intValue();
        setFog(Color.red(color) / 255.0F,Color.green(color) / 255.0F,Color.blue(color) / 255.0F,1f,Math.min(far,near),Math.max(far,near));
    }


    @Override
    public void onDisable() {
        resetFog();
    }

    @Override
    public String getTag() {
        return isStatic.getCurrentValue()?"Static":"Dynamic";
    }
}
